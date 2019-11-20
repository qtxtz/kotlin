/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.backend.js

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.backend.js.export.isExported
import org.jetbrains.kotlin.ir.backend.js.utils.getJsName
import org.jetbrains.kotlin.ir.backend.js.utils.getJsNameOrKotlinName
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrBlockBodyImpl
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.classifierOrFail
import org.jetbrains.kotlin.ir.types.classifierOrNull
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.*
import java.util.*
import kotlin.collections.ArrayList

fun eliminateDeadDeclarations(
    module: IrModuleFragment,
    context: JsIrBackendContext,
    mainFunction: IrSimpleFunction?
) {

    val allRoots = buildRoots(module, context, mainFunction)

    val usefulDeclarations = usefulDeclarations(allRoots, context)

    removeUselessDeclarations(module, usefulDeclarations)
}

private fun buildRoots(module: IrModuleFragment, context: JsIrBackendContext, mainFunction: IrSimpleFunction?): Iterable<IrDeclaration> {
    val rootDeclarations =
        (module.files + context.packageLevelJsModules + context.externalPackageFragment.values).flatMapTo(mutableListOf()) { file ->
            file.declarations.filter {
                it is IrField && it.initializer != null && it.fqNameWhenAvailable?.asString()?.startsWith("kotlin") != true
                        || it.isExported(context)
                        || it.isEffectivelyExternal()
                        || it is IrField && it.correspondingPropertySymbol?.owner?.isExported(context) == true
                        || it is IrSimpleFunction && it.correspondingPropertySymbol?.owner?.isExported(context) == true
            }
        }

    if (context.hasTests) rootDeclarations += context.testContainer

    if (mainFunction != null) {
        rootDeclarations += mainFunction
        if (mainFunction.isSuspend) {
            rootDeclarations += context.coroutineEmptyContinuation.owner
        }
    }

    return rootDeclarations
}

private fun removeUselessDeclarations(module: IrModuleFragment, usefulDeclarations:Set<IrDeclaration>) {
    module.files.forEach {
        it.acceptVoid(object : IrElementVisitorVoid {
            override fun visitElement(element: IrElement) {
                element.acceptChildrenVoid(this)
            }

            override fun visitFile(declaration: IrFile) {
                process(declaration)
            }

            override fun visitClass(declaration: IrClass) {
                process(declaration)
            }

            override fun visitConstructor(declaration: IrConstructor) {
                if (declaration !in usefulDeclarations) {
                    // Keep the constructor declaration without body in order to declare the JS constructor function
                    declaration.body = IrBlockBodyImpl(UNDEFINED_OFFSET, UNDEFINED_OFFSET, emptyList())
                }
            }

            private fun process(container: IrDeclarationContainer) {
                container.declarations.transformFlat { member ->
                    if (member !in usefulDeclarations && member !is IrConstructor) {
                        emptyList()
                    } else {
                        member.acceptVoid(this)
                        null
                    }
                }
            }
        })
    }
}

fun usefulDeclarations(roots: Iterable<IrDeclaration>, context: JsIrBackendContext): Set<IrDeclaration> {
    val queue = ArrayDeque<IrDeclaration>()
    val result = hashSetOf<IrDeclaration>()
    val constructedClasses = hashSetOf<IrClass>()

    fun IrDeclaration.enqueue() {
        if (this !in result) {
            result.add(this)
            queue.addLast(this)
            if (this is IrConstructor) {
                constructedClass.enqueue()
                constructedClasses += constructedClass
                constructedClass.declarations.forEach {
                    // A hack to support `toJson` and other js-specific members
                    if (it.getJsName() != null ||
                        it is IrField && it.correspondingPropertySymbol?.owner?.getJsName() != null ||
                        it is IrSimpleFunction && it.correspondingPropertySymbol?.owner?.getJsName() != null
                    ) {
                        it.enqueue()
                    }
                }
            }
        }
    }

    // Add nested declarations within roots
    for (declaration in roots) {
        declaration.acceptVoid(object : IrElementVisitorVoid {
            override fun visitElement(element: IrElement) {
                element.acceptChildrenVoid(this)
            }

            override fun visitBody(body: IrBody) {
                // Skip
            }

            override fun visitDeclaration(declaration: IrDeclaration) {
                super.visitDeclaration(declaration)
                declaration.enqueue()
            }
        })
    }

    val toStringMethod =
        context.irBuiltIns.anyClass.owner.declarations.filterIsInstance<IrFunction>().single { it.name.asString() == "toString" }
    val equalsMethod =
        context.irBuiltIns.anyClass.owner.declarations.filterIsInstance<IrFunction>().single { it.name.asString() == "equals" }
    val hashCodeMethod =
        context.irBuiltIns.anyClass.owner.declarations.filterIsInstance<IrFunction>().single { it.name.asString() == "hashCode" }

    fun IrOverridableDeclaration<*>.overridesUsefulFunction(): Boolean {
        return this.overriddenSymbols.any {
            (it.owner as? IrOverridableDeclaration<*>)?.let {
                it in result || it.overridesUsefulFunction()
            } ?: false
        }
    }

    while (queue.isNotEmpty()) {
        while (queue.isNotEmpty()) {
            val declaration = queue.pollFirst()

            // Statics are not lowered yet
            (declaration.parent as? IrDeclaration)?.enqueue()

            if (declaration is IrClass) {
                declaration.superTypes.forEach {
                    (it.classifierOrNull as? IrClassSymbol)?.owner?.enqueue()
                }

                // Special hack for `IntrinsicsJs.kt` support
                if (declaration.superTypes.any { it.isSuspendFunctionTypeOrSubtype() }) {
                    declaration.declarations.forEach {
                        if (it is IrSimpleFunction && it.name.asString().startsWith("invoke")) {
                            it.enqueue()
                        }
                    }
                }

                // TODO find out how `doResume` gets removed
                if (declaration.name.asString() == "CoroutineImpl") {
                    declaration.declarations.forEach {
                        if (it is IrSimpleFunction && it.name.asString() == "doResume") {
                            it.enqueue()
                        }
                    }
                }
            }

            if (declaration is IrSimpleFunction) {
                if (declaration.origin == IrDeclarationOrigin.FAKE_OVERRIDE) {
                    declaration.overriddenSymbols.forEach {
                        it.owner.enqueue()
                    }
                }
            }

            val body = when (declaration) {
                is IrFunction -> declaration.body
                is IrField -> declaration.initializer
                is IrVariable -> declaration.initializer
                else -> null
            }

            body?.acceptVoid(object : IrElementVisitorVoid {
                override fun visitElement(element: IrElement) {
                    element.acceptChildrenVoid(this)
                }

                override fun visitFunctionAccess(expression: IrFunctionAccessExpression) {
                    super.visitFunctionAccess(expression)

                    expression.symbol.owner.enqueue()
                }

                override fun visitVariableAccess(expression: IrValueAccessExpression) {
                    super.visitVariableAccess(expression)

                    expression.symbol.owner.enqueue()
                }

                override fun visitFieldAccess(expression: IrFieldAccessExpression) {
                    super.visitFieldAccess(expression)

                    expression.symbol.owner.enqueue()
                }

                override fun visitCall(expression: IrCall) {
                    super.visitCall(expression)

                    expression.superQualifierSymbol?.owner?.enqueue()

                    when (expression.symbol) {
                        context.intrinsics.jsBoxIntrinsic -> {
                            val inlineClass = expression.getTypeArgument(0)!!.getInlinedClass()!!
                            val constructor = inlineClass.declarations.filterIsInstance<IrConstructor>().single { it.isPrimary }
                            constructor.enqueue()
                        }
                        context.intrinsics.jsClass -> {
                            (expression.getTypeArgument(0)?.classifierOrNull as? IrClassSymbol)?.owner?.enqueue()
                        }
                        context.intrinsics.jsObjectCreate.symbol -> {
                            val classToCreate = expression.getTypeArgument(0)!!.classifierOrFail.owner as IrClass
                            classToCreate.enqueue()
                            constructedClasses += classToCreate
                        }
                        context.intrinsics.jsEquals -> {
                            equalsMethod.enqueue()
                        }
                        context.intrinsics.jsToString -> {
                            toStringMethod.enqueue()
                        }
                        context.intrinsics.jsHashCode -> {
                            hashCodeMethod.enqueue()
                        }
                        context.intrinsics.jsPlus -> {
                            if (expression.getValueArgument(0)?.type?.classOrNull == context.irBuiltIns.stringClass) {
                                toStringMethod.enqueue()
                            }
                        }
                    }
                }

                override fun visitGetObjectValue(expression: IrGetObjectValue) {
                    super.visitGetObjectValue(expression)

                    expression.symbol.owner.let {
                        constructedClasses += it
                        it.constructors.find { it.isPrimary }?.enqueue()
                    }
                }

                override fun visitStringConcatenation(expression: IrStringConcatenation) {
                    super.visitStringConcatenation(expression)

                    toStringMethod.enqueue()
                }
            })
        }

        for (klass in constructedClasses) {
            for (declaration in ArrayList(klass.declarations)) {
                if (declaration in result) continue

                if (declaration is IrOverridableDeclaration<*> && declaration.overridesUsefulFunction()) {
                    declaration.enqueue()
                }
                if (declaration is IrSimpleFunction && declaration.getJsNameOrKotlinName().asString() == "valueOf") {
                    declaration.enqueue()
                }
            }
        }
    }

    return result
}