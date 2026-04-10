/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.arguments.serialization.json

import org.jetbrains.kotlin.arguments.dsl.base.ExperimentalArgumentApi
import org.jetbrains.kotlin.arguments.dsl.types.JsModuleKind
import org.jetbrains.kotlin.arguments.dsl.types.JsModuleKindType
import org.jetbrains.kotlin.arguments.serialization.json.base.AllNamedTypeSerializer
import org.jetbrains.kotlin.arguments.serialization.json.base.NamedTypeSerializer
import org.jetbrains.kotlin.arguments.serialization.json.base.SetTypeSerializer
import org.jetbrains.kotlin.arguments.serialization.json.base.typeFinder

private const val SERIAL_NAME = "org.jetbrains.kotlin.arguments.JsModuleKind"
private const val SET_SERIAL_NAME = "org.jetbrains.kotlin.arguments.SetJsModuleKind"

object KotlinJsModuleKindAsNameSerializer : NamedTypeSerializer<JsModuleKind>(
    serialName = SERIAL_NAME,
    nameAccessor = JsModuleKind::kindName,
    typeFinder = JsModuleKind::kindName.typeFinder()
)

private object AllJsModuleKindSerializer : AllNamedTypeSerializer<JsModuleKind>(
    serialName = SERIAL_NAME,
    jsonElementNameForName = "name",
    nameAccessor = JsModuleKind::kindName,
    typeFinder = JsModuleKind::kindName.typeFinder()
)

@OptIn(ExperimentalArgumentApi::class)
object AllDetailsJsModuleKindSerializer : SetTypeSerializer<JsModuleKind>(
    typeSerializer = AllJsModuleKindSerializer,
    valueTypeQualifiedNamed = JsModuleKindType::class.qualifiedName!!,
    serialName = SET_SERIAL_NAME,
)
