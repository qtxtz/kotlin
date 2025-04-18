/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.jvm.lower

import org.jetbrains.kotlin.backend.common.FileLoweringPass
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.phaser.PhaseDescription
import org.jetbrains.kotlin.backend.jvm.JvmBackendContext
import org.jetbrains.kotlin.backend.jvm.ir.createJvmIrBuilder
import org.jetbrains.kotlin.backend.jvm.ir.irArrayOf
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.fromSymbolOwner
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.fqNameForIrSerialization
import org.jetbrains.kotlin.ir.util.render
import org.jetbrains.kotlin.utils.addToStdlib.assignFrom

@PhaseDescription(name = "JvmBuiltInsLowering")
internal class JvmBuiltInsLowering(val context: JvmBackendContext) : FileLoweringPass {
    override fun lower(irFile: IrFile) {
        val transformer = object : IrElementTransformerVoidWithContext() {
            override fun visitCall(expression: IrCall): IrExpression {
                expression.transformChildren(this, null)

                val callee = expression.symbol.owner
                val parentClassName = callee.parent.fqNameForIrSerialization.asString()
                val functionName = callee.name.asString()
                if (parentClassName == "kotlin.CompareToKt" && functionName == "compareTo") {
                    val operandType = expression.arguments[0]!!.type
                    when {
                        operandType.isUInt() -> return expression.replaceWithCallTo(context.symbols.compareUnsignedInt)
                        operandType.isULong() -> return expression.replaceWithCallTo(context.symbols.compareUnsignedLong)
                    }
                }
                val jvm8Replacement = jvm8builtInReplacements[parentClassName to functionName]
                if (jvm8Replacement != null) {
                    return expression.replaceWithCallTo(jvm8Replacement)
                }

                return when {
                    callee.isArrayOf() ->
                        expression.arguments[0]
                            ?: throw AssertionError("Argument #0 expected: ${expression.dump()}")

                    callee.isEmptyArray() ->
                        context.createJvmIrBuilder(currentScope!!, expression).irArrayOf(expression.type)

                    else ->
                        expression
                }
            }
        }

        irFile.transformChildren(transformer, null)
    }

    private val jvm8builtInReplacements = mapOf(
        ("kotlin.UInt" to "compareTo") to context.symbols.compareUnsignedInt,
        ("kotlin.UInt" to "div") to context.symbols.divideUnsignedInt,
        ("kotlin.UInt" to "rem") to context.symbols.remainderUnsignedInt,
        ("kotlin.UInt" to "toString") to context.symbols.toUnsignedStringInt,
        ("kotlin.ULong" to "compareTo") to context.symbols.compareUnsignedLong,
        ("kotlin.ULong" to "div") to context.symbols.divideUnsignedLong,
        ("kotlin.ULong" to "rem") to context.symbols.remainderUnsignedLong,
        ("kotlin.ULong" to "toString") to context.symbols.toUnsignedStringLong
    )

    // Originals are so far only instance methods and extensions, while the replacements are
    // statics, so we copy dispatch and extension receivers to a value argument if needed.
    // If we can't coerce arguments to required types, keep original expression (see below).
    private fun IrCall.replaceWithCallTo(replacement: IrSimpleFunctionSymbol): IrExpression {
        val expectedType = this.type
        val intrinsicCallType = replacement.owner.returnType

        val intrinsicCall = IrCallImpl.fromSymbolOwner(
            startOffset,
            endOffset,
            intrinsicCallType,
            replacement
        ).also { newCall ->
            newCall.arguments.assignFrom(replacement.owner.parameters zip arguments) { (parameter, argument) ->
                argument!!.coerceIfPossible(parameter.type) ?: return this@replaceWithCallTo
            }
        }

        // Coerce intrinsic call result from JVM 'int' or 'long' to corresponding unsigned type if required.
        return if (intrinsicCallType.isInt() || intrinsicCallType.isLong()) {
            intrinsicCall.coerceIfPossible(expectedType)
                ?: throw AssertionError("Can't coerce '${intrinsicCallType.render()}' to '${expectedType.render()}'")
        } else {
            intrinsicCall
        }
    }

    private fun IrExpression.coerceIfPossible(toType: IrType): IrExpression? {
        // TODO maybe UnsafeCoerce could handle types with different, but coercible underlying representations.
        // See KT-43286 and related tests for details.
        val fromJvmType = context.defaultTypeMapper.mapType(type)
        val toJvmType = context.defaultTypeMapper.mapType(toType)
        return if (fromJvmType != toJvmType)
            null
        else
            IrCallImpl.fromSymbolOwner(startOffset, endOffset, toType, context.symbols.unsafeCoerceIntrinsic).also { call ->
                call.typeArguments[0] = type
                call.typeArguments[1] = toType
                call.arguments[0] = this
            }
    }
}
