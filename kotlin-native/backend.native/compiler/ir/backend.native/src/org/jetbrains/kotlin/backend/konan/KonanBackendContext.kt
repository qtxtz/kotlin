/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.konan

import org.jetbrains.kotlin.backend.common.CommonBackendContext
import org.jetbrains.kotlin.backend.common.ir.SharedVariablesManager
import org.jetbrains.kotlin.backend.konan.driver.BasicPhaseContext
import org.jetbrains.kotlin.backend.konan.ir.KonanSharedVariablesManager
import org.jetbrains.kotlin.backend.konan.ir.KonanSymbols
import org.jetbrains.kotlin.builtins.konan.KonanBuiltIns
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrFactory
import org.jetbrains.kotlin.ir.declarations.impl.IrFactoryImpl

internal abstract class KonanBackendContext(config: KonanConfig) : BasicPhaseContext(config), CommonBackendContext {
    abstract val builtIns: KonanBuiltIns

    abstract override val symbols: KonanSymbols

    override val sharedVariablesManager: SharedVariablesManager by lazy {
        // Creating lazily because builtIns module seems to be incomplete during `link` test;
        // TODO: investigate this.
        KonanSharedVariablesManager(irBuiltIns, symbols)
    }

    override val irFactory: IrFactory = IrFactoryImpl

    override val messageCollector: MessageCollector
        get() = super<BasicPhaseContext>.messageCollector
}
