/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.backend.js.transformers.irToJs

import org.jetbrains.kotlin.backend.common.ir.isTmpForInline
import org.jetbrains.kotlin.ir.backend.js.lower.ES6_DELEGATING_CONSTRUCTOR_CALL_REPLACEMENT
import org.jetbrains.kotlin.ir.backend.js.lower.EXTERNAL_SUPER_ACCESSORS_ORIGIN
import org.jetbrains.kotlin.ir.backend.js.utils.JsGenerationContext
import org.jetbrains.kotlin.ir.backend.js.utils.emptyScope
import org.jetbrains.kotlin.ir.backend.js.utils.isTheLastReturnStatementIn
import org.jetbrains.kotlin.ir.backend.js.utils.isUnitInstanceFunction
import org.jetbrains.kotlin.ir.backend.js.wasMovedFromItsDeclarationPlace
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrReturnableBlockSymbol
import org.jetbrains.kotlin.ir.types.isAny
import org.jetbrains.kotlin.ir.util.constructedClassType
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.irError
import org.jetbrains.kotlin.ir.util.render
import org.jetbrains.kotlin.js.backend.ast.*
import org.jetbrains.kotlin.js.backend.ast.metadata.synthetic
import org.jetbrains.kotlin.js.backend.ast.metadata.wasMovedFromItsDeclarationPlace
import org.jetbrains.kotlin.utils.toSmartList

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class IrElementToJsStatementTransformer : BaseIrElementToJsNodeTransformer<JsStatement, JsGenerationContext>() {

    override fun visitFunction(declaration: IrFunction, data: JsGenerationContext): JsStatement {
        irError("All functions must be already lowered") {
            withIrEntry("declaration", declaration)
        }
    }

    override fun visitBlockBody(body: IrBlockBody, context: JsGenerationContext): JsStatement {
        return JsBlock(body.statements.map { it.accept(this, context) }.toSmartList()).withSource(body, context, container = context.currentFunction)
    }

    override fun visitReturnableBlock(expression: IrReturnableBlock, context: JsGenerationContext): JsStatement {
        val inlinedBlock = expression.statements.singleOrNull() as? IrInlinedFunctionBlock
        val newContext = if (inlinedBlock != null) {
            context.newInlineFunction(inlinedBlock.inlinedFunctionFileEntry, inlinedBlock.inlinedFunctionSymbol?.owner)
        } else {
            context
        }

        val container = inlinedBlock?.statements ?: expression.statements
        val statements = container.map { it.accept(this, newContext) }.toSmartList()

        val label = context.getNameForReturnableBlock(expression)
        val wrappedStatements = statements.wrapInCommentsInlineFunctionCall(inlinedBlock)

        return if (label != null) {
            JsLabel(label, JsBlock(wrappedStatements))
        } else {
            JsCompositeBlock(wrappedStatements)
        }.withSource(expression, context)
    }

    override fun visitBlock(expression: IrBlock, context: JsGenerationContext): JsStatement {
        val statements = expression.statements.map { it.accept(this, context) }.toSmartList()
        return JsBlock(statements).withSource(expression, context)
    }

    private fun List<JsStatement>.wrapInCommentsInlineFunctionCall(inlinedBlock: IrInlinedFunctionBlock?): List<JsStatement> {
        val inlinedFunction = inlinedBlock?.inlinedFunctionSymbol?.owner ?: return this
        val correspondingProperty = (inlinedFunction as? IrSimpleFunction)?.correspondingPropertySymbol
        val owner = correspondingProperty?.owner ?: inlinedFunction
        val funName = owner.fqNameWhenAvailable ?: owner.name
        return listOf(JsSingleLineComment(" Inline function '$funName' call")) + this
    }

    override fun visitComposite(expression: IrComposite, context: JsGenerationContext): JsStatement {
        return if (expression.statements.isEmpty()) {
            JsEmpty
        } else {
            JsBlock(expression.statements.map { it.accept(this, context) }.toSmartList()).withSource(expression, context)
        }
    }

    override fun visitExpression(expression: IrExpression, context: JsGenerationContext): JsStatement {
        return expression.accept(IrElementToJsExpressionTransformer(), context).makeStmt()
    }

    override fun visitFunctionExpression(expression: IrFunctionExpression, context: JsGenerationContext): JsStatement {
        // If IrFunctionExpression is not used (i. e. the function expression is also a statement),
        // the generated function cannot be anonymous, so we don't erase its name, unlike in IrElementToJsExpressionTransformer
        return expression.function.accept(IrFunctionToJsTransformer(), context).makeStmt()
    }

    override fun visitBreak(jump: IrBreak, context: JsGenerationContext): JsStatement {
        return JsBreak(context.getNameForLoop(jump.loop)?.let { JsNameRef(it) }).withSource(jump, context)
    }

    override fun visitContinue(jump: IrContinue, context: JsGenerationContext): JsStatement {
        return JsContinue(context.getNameForLoop(jump.loop)?.let { JsNameRef(it) }).withSource(jump, context)
    }

    private fun IrExpression.maybeOptimizeIntoSwitch(
        context: JsGenerationContext,
        transformer: (() -> JsExpression) -> JsStatement
    ): JsStatement {
        if (this is IrWhen) {
            val stmtTransformer: (() -> JsStatement) -> JsStatement = {
                transformer {
                    val stmt = it()
                    assert(stmt is JsExpressionStatement) { "${render()} is not a statement $stmt" }
                    (stmt as JsExpressionStatement).expression
                }
            }
            SwitchOptimizer(context, isExpression = true, stmtTransformer).tryOptimize(this)?.let { return it }
        }

        return transformer { accept(IrElementToJsExpressionTransformer(), context) }
    }

    override fun visitSetField(expression: IrSetField, context: JsGenerationContext): JsStatement {
        val fieldName = context.getNameForField(expression.symbol.owner)
        val expressionTransformer = IrElementToJsExpressionTransformer()
        val dest = jsElementAccess(fieldName, expression.receiver?.accept(expressionTransformer, context))
        return expression.value.maybeOptimizeIntoSwitch(context) { jsAssignment(dest, it()).withSource(expression, context).makeStmt() }
    }

    override fun visitSetValue(expression: IrSetValue, context: JsGenerationContext): JsStatement {
        val owner = expression.symbol.owner
        val ref = JsNameRef(context.getNameForValueDeclaration(owner))
        return expression.value.maybeOptimizeIntoSwitch(context) { jsAssignment(ref, it()).withSource(expression, context).makeStmt() }
    }

    override fun visitReturn(expression: IrReturn, context: JsGenerationContext): JsStatement {
        val lastStatementTransformer: (() -> JsExpression) -> JsStatement =
            when (val targetSymbol = expression.returnTargetSymbol) {
                is IrReturnableBlockSymbol -> {
                    // TODO assert that value is Unit?
                    {
                        context.getNameForReturnableBlock(targetSymbol.owner)
                            .takeIf { !expression.isTheLastReturnStatementIn(targetSymbol) }
                            ?.run { JsBreak(makeRef()) } ?: JsEmpty
                    }
                }
                is IrFunctionSymbol -> {
                    { JsReturn(it()) }
                }
            }

        return expression.value
            .maybeOptimizeIntoSwitch(context, lastStatementTransformer)
            .withSource(expression, context)
    }

    override fun visitThrow(expression: IrThrow, context: JsGenerationContext): JsStatement {
        return expression.value.maybeOptimizeIntoSwitch(context) { JsThrow(it()) }.withSource(expression, context)
    }

    override fun visitVariable(declaration: IrVariable, context: JsGenerationContext): JsStatement {
        val varName = context.getNameForValueDeclaration(declaration)
        val value = declaration.initializer

        if (value is IrWhen) {
            val varRef = varName.makeRef()
            val transformer: (() -> JsStatement) -> JsStatement = {
                val expr = (it() as JsExpressionStatement).expression
                JsBinaryOperation(JsBinaryOperator.ASG, varRef, expr).makeStmt()
            }

            SwitchOptimizer(context, isExpression = true, transformer).tryOptimize(value)?.let {
                return JsBlock(JsVars(JsVars.JsVar(varName)), it).withSource(declaration, context)
            }
        }

        val jsInitializer = value?.accept(IrElementToJsExpressionTransformer(), context)

        val syntheticVariable = when (declaration.origin) {
            IrDeclarationOrigin.IR_TEMPORARY_VARIABLE -> true
            EXTERNAL_SUPER_ACCESSORS_ORIGIN -> true
            ES6_DELEGATING_CONSTRUCTOR_CALL_REPLACEMENT -> true
            else -> declaration.isTmpForInline
        }

        val variable = JsVars.JsVar(varName, jsInitializer).apply {
            withSource(declaration, context, useNameOf = declaration)
            synthetic = syntheticVariable
            wasMovedFromItsDeclarationPlace = declaration.wasMovedFromItsDeclarationPlace
        }
        return JsVars(variable).apply { synthetic = syntheticVariable }
    }

    override fun visitDelegatingConstructorCall(expression: IrDelegatingConstructorCall, context: JsGenerationContext): JsStatement {
        if (expression.symbol.owner.constructedClassType.isAny()) {
            return JsEmpty
        }
        return expression.accept(IrElementToJsExpressionTransformer(), context).makeStmt()
    }

    override fun visitCall(expression: IrCall, data: JsGenerationContext): JsStatement {
        if (expression.symbol.isUnitInstanceFunction(data.staticContext.backendContext)) {
            return JsEmpty
        }
        if (data.checkIfJsCode(expression.symbol) || data.checkIfHasAssociatedJsCode(expression.symbol)) {
            return JsCallTransformer(expression, data).generateStatement()
        }
        return translateCall(expression, data, IrElementToJsExpressionTransformer()).withSource(expression, data).makeStmt()
    }

    override fun visitInstanceInitializerCall(expression: IrInstanceInitializerCall, context: JsGenerationContext): JsStatement {
        // TODO: implement
        return JsEmpty
    }

    override fun visitTry(aTry: IrTry, context: JsGenerationContext): JsStatement {

        val jsTryBlock = aTry.tryResult.accept(this, context).asBlock()

        val jsCatch = aTry.catches.singleOrNull()?.let {
            val name = context.getNameForValueDeclaration(it.catchParameter)
            val jsCatchBlock = it.result.accept(this, context)
            JsCatch(emptyScope, name.ident, jsCatchBlock).withSource(it, context)
        }

        val jsFinallyBlock = aTry.finallyExpression?.accept(this, context)?.asBlock()

        return JsTry(jsTryBlock, jsCatch, jsFinallyBlock).withSource(aTry, context)
    }

    override fun visitWhen(expression: IrWhen, context: JsGenerationContext): JsStatement {
        return SwitchOptimizer(context).tryOptimize(expression) ?: expression.toJsNode(this, context, ::JsIf) ?: JsEmpty
    }

    override fun visitWhileLoop(loop: IrWhileLoop, context: JsGenerationContext): JsStatement {
        //TODO what if body null?
        val label = context.getNameForLoop(loop)
        val loopStatement = JsWhile(
            loop.condition.accept(IrElementToJsExpressionTransformer(), context),
            loop.body?.accept(this, context) ?: JsEmpty
        )
        return label?.let { JsLabel(it, loopStatement) } ?: loopStatement
    }

    override fun visitDoWhileLoop(loop: IrDoWhileLoop, context: JsGenerationContext): JsStatement {
        //TODO what if body null?
        val label = context.getNameForLoop(loop)
        val loopStatement =
            JsDoWhile(loop.condition.accept(IrElementToJsExpressionTransformer(), context), loop.body?.accept(this, context) ?: JsEmpty)
        return label?.let { JsLabel(it, loopStatement) } ?: loopStatement
    }
}
