/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.arguments.serialization.json

import org.jetbrains.kotlin.arguments.dsl.base.ExperimentalArgumentApi
import org.jetbrains.kotlin.arguments.dsl.types.SourceMapNamesPolicy
import org.jetbrains.kotlin.arguments.dsl.types.SourceMapNamesPolicyType
import org.jetbrains.kotlin.arguments.serialization.json.base.AllNamedTypeSerializer
import org.jetbrains.kotlin.arguments.serialization.json.base.NamedTypeSerializer
import org.jetbrains.kotlin.arguments.serialization.json.base.SetTypeSerializer
import org.jetbrains.kotlin.arguments.serialization.json.base.typeFinder

private const val SERIAL_NAME = "org.jetbrains.kotlin.arguments.SourceMapNamesPolicy"
private const val SET_SERIAL_NAME = "org.jetbrains.kotlin.arguments.SetSourceMapNamesPolicy"

object KotlinSourceMapNamesPolicyAsNameSerializer : NamedTypeSerializer<SourceMapNamesPolicy>(
    serialName = SERIAL_NAME,
    nameAccessor = SourceMapNamesPolicy::policyName,
    typeFinder = SourceMapNamesPolicy::policyName.typeFinder()
)

private object AllSourceMapNamesPolicySerializer : AllNamedTypeSerializer<SourceMapNamesPolicy>(
    serialName = SERIAL_NAME,
    jsonElementNameForName = "name",
    nameAccessor = SourceMapNamesPolicy::policyName,
    typeFinder = SourceMapNamesPolicy::policyName.typeFinder()
)

@OptIn(ExperimentalArgumentApi::class)
object AllDetailsSourceMapNamesPolicySerializer : SetTypeSerializer<SourceMapNamesPolicy>(
    typeSerializer = AllSourceMapNamesPolicySerializer,
    valueTypeQualifiedNamed = SourceMapNamesPolicyType::class.qualifiedName!!,
    serialName = SET_SERIAL_NAME,
)
