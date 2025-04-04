/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

// This file was generated automatically. See compiler/ir/ir.tree/tree-generator/ReadMe.md.
// DO NOT MODIFY IT MANUALLY.

@file:Suppress("DuplicatedCode")

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.expressions.IrBreak
import org.jetbrains.kotlin.ir.expressions.IrLoop
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.IrElementConstructorIndicator

class IrBreakImpl internal constructor(
    @Suppress("UNUSED_PARAMETER") constructorIndicator: IrElementConstructorIndicator?,
    override var startOffset: Int,
    override var endOffset: Int,
    override var type: IrType,
    override var loop: IrLoop,
) : IrBreak() {
    override var attributeOwnerId: IrElement = this

    override var label: String? = null
}
