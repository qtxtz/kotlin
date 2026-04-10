/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.arguments.serialization.json

import org.jetbrains.kotlin.arguments.dsl.base.ExperimentalArgumentApi
import org.jetbrains.kotlin.arguments.dsl.types.SourceMapEmbedSources
import org.jetbrains.kotlin.arguments.dsl.types.SourceMapEmbedSourcesType
import org.jetbrains.kotlin.arguments.serialization.json.base.AllNamedTypeSerializer
import org.jetbrains.kotlin.arguments.serialization.json.base.NamedTypeSerializer
import org.jetbrains.kotlin.arguments.serialization.json.base.SetTypeSerializer
import org.jetbrains.kotlin.arguments.serialization.json.base.typeFinder

private const val SERIAL_NAME = "org.jetbrains.kotlin.arguments.SourceMapEmbedSources"
private const val SET_SERIAL_NAME = "org.jetbrains.kotlin.arguments.SetSourceMapEmbedSources"

object KotlinSourceMapEmbedSourcesAsNameSerializer : NamedTypeSerializer<SourceMapEmbedSources>(
    serialName = SERIAL_NAME,
    nameAccessor = SourceMapEmbedSources::modeName,
    typeFinder = SourceMapEmbedSources::modeName.typeFinder()
)

private object AllSourceMapEmbedSourcesSerializer : AllNamedTypeSerializer<SourceMapEmbedSources>(
    serialName = SERIAL_NAME,
    jsonElementNameForName = "name",
    nameAccessor = SourceMapEmbedSources::modeName,
    typeFinder = SourceMapEmbedSources::modeName.typeFinder()
)

@OptIn(ExperimentalArgumentApi::class)
object AllDetailsSourceMapEmbedSourcesSerializer : SetTypeSerializer<SourceMapEmbedSources>(
    typeSerializer = AllSourceMapEmbedSourcesSerializer,
    valueTypeQualifiedNamed = SourceMapEmbedSourcesType::class.qualifiedName!!,
    serialName = SET_SERIAL_NAME,
)
