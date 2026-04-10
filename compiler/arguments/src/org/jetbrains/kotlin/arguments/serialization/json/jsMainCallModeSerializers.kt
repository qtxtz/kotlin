/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.arguments.serialization.json

import org.jetbrains.kotlin.arguments.dsl.base.ExperimentalArgumentApi
import org.jetbrains.kotlin.arguments.dsl.types.JsMainCallMode
import org.jetbrains.kotlin.arguments.dsl.types.JsMainCallModeType
import org.jetbrains.kotlin.arguments.serialization.json.base.AllNamedTypeSerializer
import org.jetbrains.kotlin.arguments.serialization.json.base.NamedTypeSerializer
import org.jetbrains.kotlin.arguments.serialization.json.base.SetTypeSerializer
import org.jetbrains.kotlin.arguments.serialization.json.base.typeFinder

private const val SERIAL_NAME = "org.jetbrains.kotlin.arguments.JsMainCallMode"
private const val SET_SERIAL_NAME = "org.jetbrains.kotlin.arguments.SetJsMainCallMode"

object KotlinJsMainCallModeAsNameSerializer : NamedTypeSerializer<JsMainCallMode>(
    serialName = SERIAL_NAME,
    nameAccessor = JsMainCallMode::modeName,
    typeFinder = JsMainCallMode::modeName.typeFinder()
)

private object AllJsMainCallModeSerializer : AllNamedTypeSerializer<JsMainCallMode>(
    serialName = SERIAL_NAME,
    jsonElementNameForName = "name",
    nameAccessor = JsMainCallMode::modeName,
    typeFinder = JsMainCallMode::modeName.typeFinder()
)

@OptIn(ExperimentalArgumentApi::class)
object AllDetailsJsMainCallModeSerializer : SetTypeSerializer<JsMainCallMode>(
    typeSerializer = AllJsMainCallModeSerializer,
    valueTypeQualifiedNamed = JsMainCallModeType::class.qualifiedName!!,
    serialName = SET_SERIAL_NAME,
)
