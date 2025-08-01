/*
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.jvm.intrinsics

import org.jetbrains.kotlin.backend.jvm.codegen.BlockInfo
import org.jetbrains.kotlin.backend.jvm.codegen.ClassCodegen
import org.jetbrains.kotlin.backend.jvm.codegen.ExpressionCodegen
import org.jetbrains.kotlin.backend.jvm.mapping.mapClass
import org.jetbrains.kotlin.codegen.AsmUtil
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.isVararg
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.util.substitute
import org.jetbrains.kotlin.resolve.jvm.jvmSignature.JvmMethodSignature
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

abstract class IntrinsicFunction(
    val expression: IrFunctionAccessExpression,
    val signature: JvmMethodSignature,
    val classCodegen: ClassCodegen,
    val argsTypes: List<Type>,
) {
    abstract fun genInvokeInstruction(v: InstructionAdapter)

    fun invoke(
        v: InstructionAdapter,
        codegen: ExpressionCodegen,
        data: BlockInfo,
        expression: IrFunctionAccessExpression,
    ) {
        loadArguments(codegen, data)
        with(codegen) { expression.markLineNumber(startOffset = true) }
        genInvokeInstruction(v)
    }

    private fun loadArguments(codegen: ExpressionCodegen, data: BlockInfo) {
        for ((parameter, argument) in expression.symbol.owner.parameters zip expression.arguments) {
            if (argument != null) {
                genArg(argument, codegen, parameter.indexInParameters, data)
            } else if (parameter.isVararg) {
                // TODO: is there an easier way to get the substituted type of an empty vararg argument?
                val arrayType = codegen.typeMapper.mapType(
                    parameter.type.substitute(expression.symbol.owner.typeParameters, expression.typeArguments.map { it!! })
                )
                codegen.mv.aconst(0)
                codegen.mv.newarray(AsmUtil.correctElementType(arrayType))
            } else {
                error("Unknown parameter ${parameter.name} in: ${expression.dump()}")
            }
        }
    }

    private fun genArg(expression: IrExpression, codegen: ExpressionCodegen, index: Int, data: BlockInfo) {
        codegen.gen(expression, argsTypes[index], expression.type, data)
    }

    companion object {
        fun create(
            expression: IrFunctionAccessExpression,
            signature: JvmMethodSignature,
            classCodegen: ClassCodegen,
            argsTypes: List<Type> = expression.argTypes(classCodegen),
            invokeInstruction: IntrinsicFunction.(InstructionAdapter) -> Unit,
        ): IntrinsicFunction =
            object : IntrinsicFunction(expression, signature, classCodegen, argsTypes) {
                override fun genInvokeInstruction(v: InstructionAdapter) = invokeInstruction(v)
            }
    }
}

internal fun IrFunctionAccessExpression.argTypes(classCodegen: ClassCodegen): List<Type> {
    val callee = symbol.owner
    val signature = classCodegen.methodSignatureMapper.mapSignatureSkipGeneric(callee)
    return arrayListOf<Type>().apply {
        if (dispatchReceiver != null) {
            add(classCodegen.typeMapper.mapClass(callee.parentAsClass))
        }
        addAll(signature.asmMethod.argumentTypes)
    }
}
