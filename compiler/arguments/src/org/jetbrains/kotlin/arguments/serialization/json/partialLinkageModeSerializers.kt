/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.arguments.serialization.json

import org.jetbrains.kotlin.arguments.dsl.base.ExperimentalArgumentApi
import org.jetbrains.kotlin.arguments.dsl.types.PartialLinkageMode
import org.jetbrains.kotlin.arguments.dsl.types.PartialLinkageModeType
import org.jetbrains.kotlin.arguments.serialization.json.base.AllNamedTypeSerializer
import org.jetbrains.kotlin.arguments.serialization.json.base.NamedTypeSerializer
import org.jetbrains.kotlin.arguments.serialization.json.base.SetTypeSerializer
import org.jetbrains.kotlin.arguments.serialization.json.base.typeFinder

private const val SERIAL_NAME = "org.jetbrains.kotlin.arguments.PartialLinkageMode"
private const val SET_SERIAL_NAME = "org.jetbrains.kotlin.arguments.SetPartialLinkageMode"

object KotlinPartialLinkageModeAsNameSerializer : NamedTypeSerializer<PartialLinkageMode>(
    serialName = SERIAL_NAME,
    nameAccessor = PartialLinkageMode::modeName,
    typeFinder = PartialLinkageMode::modeName.typeFinder()
)

private object AllPartialLinkageModeSerializer : AllNamedTypeSerializer<PartialLinkageMode>(
    serialName = SERIAL_NAME,
    jsonElementNameForName = "name",
    nameAccessor = PartialLinkageMode::modeName,
    typeFinder = PartialLinkageMode::modeName.typeFinder()
)

@OptIn(ExperimentalArgumentApi::class)
object AllDetailsPartialLinkageModeSerializer : SetTypeSerializer<PartialLinkageMode>(
    typeSerializer = AllPartialLinkageModeSerializer,
    valueTypeQualifiedNamed = PartialLinkageModeType::class.qualifiedName!!,
    serialName = SET_SERIAL_NAME,
)
