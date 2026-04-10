/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.arguments.serialization.json

import org.jetbrains.kotlin.arguments.dsl.base.ExperimentalArgumentApi
import org.jetbrains.kotlin.arguments.dsl.types.WasmTarget
import org.jetbrains.kotlin.arguments.dsl.types.WasmTargetType
import org.jetbrains.kotlin.arguments.serialization.json.base.AllNamedTypeSerializer
import org.jetbrains.kotlin.arguments.serialization.json.base.NamedTypeSerializer
import org.jetbrains.kotlin.arguments.serialization.json.base.SetTypeSerializer
import org.jetbrains.kotlin.arguments.serialization.json.base.typeFinder

private const val SERIAL_NAME = "org.jetbrains.kotlin.arguments.WasmTarget"
private const val SET_SERIAL_NAME = "org.jetbrains.kotlin.arguments.SetWasmTarget"

object KotlinWasmTargetAsNameSerializer : NamedTypeSerializer<WasmTarget>(
    serialName = SERIAL_NAME,
    nameAccessor = WasmTarget::targetName,
    typeFinder = WasmTarget::targetName.typeFinder()
)

private object AllWasmTargetSerializer : AllNamedTypeSerializer<WasmTarget>(
    serialName = SERIAL_NAME,
    jsonElementNameForName = "name",
    nameAccessor = WasmTarget::targetName,
    typeFinder = WasmTarget::targetName.typeFinder()
)

@OptIn(ExperimentalArgumentApi::class)
object AllDetailsWasmTargetSerializer : SetTypeSerializer<WasmTarget>(
    typeSerializer = AllWasmTargetSerializer,
    valueTypeQualifiedNamed = WasmTargetType::class.qualifiedName!!,
    serialName = SET_SERIAL_NAME,
)
