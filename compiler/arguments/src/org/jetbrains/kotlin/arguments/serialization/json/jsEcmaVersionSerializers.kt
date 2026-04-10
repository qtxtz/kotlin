/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.arguments.serialization.json

import org.jetbrains.kotlin.arguments.dsl.base.ExperimentalArgumentApi
import org.jetbrains.kotlin.arguments.dsl.types.JsEcmaVersion
import org.jetbrains.kotlin.arguments.dsl.types.JsEcmaVersionType
import org.jetbrains.kotlin.arguments.serialization.json.base.AllNamedTypeSerializer
import org.jetbrains.kotlin.arguments.serialization.json.base.NamedTypeSerializer
import org.jetbrains.kotlin.arguments.serialization.json.base.SetTypeSerializer
import org.jetbrains.kotlin.arguments.serialization.json.base.typeFinder

private const val SERIAL_NAME = "org.jetbrains.kotlin.arguments.JsEcmaVersion"
private const val SET_SERIAL_NAME = "org.jetbrains.kotlin.arguments.SetJsEcmaVersion"

object KotlinJsEcmaVersionAsNameSerializer : NamedTypeSerializer<JsEcmaVersion>(
    serialName = SERIAL_NAME,
    nameAccessor = JsEcmaVersion::versionName,
    typeFinder = JsEcmaVersion::versionName.typeFinder()
)

private object AllJsEcmaVersionSerializer : AllNamedTypeSerializer<JsEcmaVersion>(
    serialName = SERIAL_NAME,
    jsonElementNameForName = "name",
    nameAccessor = JsEcmaVersion::versionName,
    typeFinder = JsEcmaVersion::versionName.typeFinder()
)

@OptIn(ExperimentalArgumentApi::class)
object AllDetailsJsEcmaVersionSerializer : SetTypeSerializer<JsEcmaVersion>(
    typeSerializer = AllJsEcmaVersionSerializer,
    valueTypeQualifiedNamed = JsEcmaVersionType::class.qualifiedName!!,
    serialName = SET_SERIAL_NAME,
)
