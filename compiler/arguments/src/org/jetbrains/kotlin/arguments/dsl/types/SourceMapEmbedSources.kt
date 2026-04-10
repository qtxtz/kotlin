/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.arguments.dsl.types

import kotlinx.serialization.Serializable
import org.jetbrains.kotlin.arguments.dsl.base.KotlinReleaseVersion
import org.jetbrains.kotlin.arguments.dsl.base.KotlinReleaseVersionLifecycle
import org.jetbrains.kotlin.arguments.dsl.base.WithKotlinReleaseVersionsMetadata
import org.jetbrains.kotlin.arguments.serialization.json.KotlinSourceMapEmbedSourcesAsNameSerializer

@Serializable(with = KotlinSourceMapEmbedSourcesAsNameSerializer::class)
enum class SourceMapEmbedSources(
    val modeName: String,
    override val releaseVersionsMetadata: KotlinReleaseVersionLifecycle,
) : WithKotlinReleaseVersionsMetadata, WithStringRepresentation {
    ALWAYS(
        modeName = "always",
        releaseVersionsMetadata = KotlinReleaseVersionLifecycle(
            introducedVersion = KotlinReleaseVersion.v1_1_4,
            stabilizedVersion = KotlinReleaseVersion.v1_1_4,
        )
    ),
    NEVER(
        modeName = "never",
        releaseVersionsMetadata = KotlinReleaseVersionLifecycle(
            introducedVersion = KotlinReleaseVersion.v1_1_4,
            stabilizedVersion = KotlinReleaseVersion.v1_1_4,
        )
    ),
    INLINING(
        modeName = "inlining",
        releaseVersionsMetadata = KotlinReleaseVersionLifecycle(
            introducedVersion = KotlinReleaseVersion.v1_1_4,
            stabilizedVersion = KotlinReleaseVersion.v1_1_4,
        )
    );

    override val stringRepresentation: String
        get() = modeName
}
