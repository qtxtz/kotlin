/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(ExperimentalCompilerArgument::class)

package org.jetbrains.kotlin.buildtools.internal.arguments

import org.jetbrains.kotlin.buildtools.api.arguments.CompilerPlugin
import org.jetbrains.kotlin.buildtools.api.arguments.ExperimentalCompilerArgument
import org.jetbrains.kotlin.buildtools.api.arguments.enums.AbiStabilityMode
import org.jetbrains.kotlin.buildtools.api.arguments.enums.AnnotationDefaultTargetMode
import org.jetbrains.kotlin.buildtools.api.arguments.enums.AssertionsMode
import org.jetbrains.kotlin.buildtools.api.arguments.enums.CompatqualAnnotationsMode
import org.jetbrains.kotlin.buildtools.api.arguments.enums.ExplicitApiMode
import org.jetbrains.kotlin.buildtools.api.arguments.enums.HeaderMode
import org.jetbrains.kotlin.buildtools.api.arguments.enums.JdkRelease
import org.jetbrains.kotlin.buildtools.api.arguments.enums.JspecifyAnnotationsMode
import org.jetbrains.kotlin.buildtools.api.arguments.enums.JvmDefaultMode
import org.jetbrains.kotlin.buildtools.api.arguments.enums.JvmTarget
import org.jetbrains.kotlin.buildtools.api.arguments.enums.KotlinVersion
import org.jetbrains.kotlin.buildtools.api.arguments.enums.LambdasMode
import org.jetbrains.kotlin.buildtools.api.arguments.enums.NameBasedDestructuringMode
import org.jetbrains.kotlin.buildtools.api.arguments.enums.ReturnValueCheckerMode
import org.jetbrains.kotlin.buildtools.api.arguments.enums.SamConversionsMode
import org.jetbrains.kotlin.buildtools.api.arguments.enums.StringConcatMode
import org.jetbrains.kotlin.buildtools.api.arguments.enums.VerifyIrMode
import org.jetbrains.kotlin.buildtools.api.arguments.enums.WhenExpressionsMode
import java.nio.file.Path
import kotlin.collections.sortedBy

internal fun Boolean.transformToInput(): String = toString()

internal fun ExplicitApiMode.transformToInput(): String = name

internal fun String.transformToInput(): String = this

internal fun KotlinVersion.transformToInput(): String = toString()

internal fun HeaderMode.transformToInput(): String = name

internal fun ReturnValueCheckerMode.transformToInput(): String = name

internal fun AbiStabilityMode.transformToInput(): String = name

@JvmName("transformToInputListString")
internal fun List<String>.transformToInput(): String = joinToString(separator = ",")

internal fun AssertionsMode.transformToInput(): String = name

@JvmName("transformToInputListPath")
internal fun List<Path>.transformToInput(): String = joinToString(separator = ",") { it.absolutePathStringOrThrow() }

internal fun JdkRelease.transformToInput(): String = name

internal fun JspecifyAnnotationsMode.transformToInput(): String = name

internal fun LambdasMode.transformToInput(): String = name

internal fun SamConversionsMode.transformToInput(): String = name

internal fun StringConcatMode.transformToInput(): String = name

internal fun CompatqualAnnotationsMode.transformToInput(): String = name

internal fun WhenExpressionsMode.transformToInput(): String = name

internal fun Path.transformToInput(): String = absolutePathStringOrThrow()

internal fun JvmDefaultMode.transformToInput(): String = name

internal fun JvmTarget.transformToInput(): String = name

@JvmName("transformToInputListCompilerPlugin")
internal fun List<CompilerPlugin>.transformToInput(): String {
    val canonical = sortedBy { it.pluginId }.joinToString(";") { plugin ->
        buildString {
            append(plugin.pluginId)
            append(":")
            append(plugin.classpath.joinToString(",") { it.absolutePathStringOrThrow() })
            append(":")
            append(plugin.rawArguments.joinToString(",") { "${it.key}=${it.value}" })
            append(":")
            append(plugin.orderingRequirements.map { "${it.relation}:${it.otherPluginId}" }.sorted().joinToString(","))
        }
    }
    return canonical
}

internal fun Array<String>.transformToInput(): String = joinToString(separator = ",") { it }

internal fun NameBasedDestructuringMode.transformToInput(): String = name

internal fun VerifyIrMode.transformToInput(): String = name

internal fun AnnotationDefaultTargetMode.transformToInput(): String = name