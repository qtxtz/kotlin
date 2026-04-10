/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.arguments.serialization.json

import org.jetbrains.kotlin.arguments.dsl.base.ExperimentalArgumentApi
import org.jetbrains.kotlin.arguments.dsl.types.PartialLinkageLogLevel
import org.jetbrains.kotlin.arguments.dsl.types.PartialLinkageLogLevelType
import org.jetbrains.kotlin.arguments.serialization.json.base.AllNamedTypeSerializer
import org.jetbrains.kotlin.arguments.serialization.json.base.NamedTypeSerializer
import org.jetbrains.kotlin.arguments.serialization.json.base.SetTypeSerializer
import org.jetbrains.kotlin.arguments.serialization.json.base.typeFinder

private const val SERIAL_NAME = "org.jetbrains.kotlin.arguments.PartialLinkageLogLevel"
private const val SET_SERIAL_NAME = "org.jetbrains.kotlin.arguments.SetPartialLinkageLogLevel"

object KotlinPartialLinkageLogLevelAsNameSerializer : NamedTypeSerializer<PartialLinkageLogLevel>(
    serialName = SERIAL_NAME,
    nameAccessor = PartialLinkageLogLevel::levelName,
    typeFinder = PartialLinkageLogLevel::levelName.typeFinder()
)

private object AllPartialLinkageLogLevelSerializer : AllNamedTypeSerializer<PartialLinkageLogLevel>(
    serialName = SERIAL_NAME,
    jsonElementNameForName = "name",
    nameAccessor = PartialLinkageLogLevel::levelName,
    typeFinder = PartialLinkageLogLevel::levelName.typeFinder()
)

@OptIn(ExperimentalArgumentApi::class)
object AllDetailsPartialLinkageLogLevelSerializer : SetTypeSerializer<PartialLinkageLogLevel>(
    typeSerializer = AllPartialLinkageLogLevelSerializer,
    valueTypeQualifiedNamed = PartialLinkageLogLevelType::class.qualifiedName!!,
    serialName = SET_SERIAL_NAME,
)
