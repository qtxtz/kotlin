/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.arguments.dsl.types

import kotlinx.serialization.Serializable
import org.jetbrains.kotlin.arguments.dsl.base.KotlinReleaseVersion
import org.jetbrains.kotlin.arguments.dsl.base.KotlinReleaseVersionLifecycle
import org.jetbrains.kotlin.arguments.dsl.base.WithKotlinReleaseVersionsMetadata
import org.jetbrains.kotlin.arguments.serialization.json.KotlinJsModuleKindAsNameSerializer

@Serializable(with = KotlinJsModuleKindAsNameSerializer::class)
enum class JsModuleKind(
    val kindName: String,
    override val releaseVersionsMetadata: KotlinReleaseVersionLifecycle,
) : WithKotlinReleaseVersionsMetadata, WithStringRepresentation {
    PLAIN(
        kindName = "plain",
        releaseVersionsMetadata = KotlinReleaseVersionLifecycle(
            introducedVersion = KotlinReleaseVersion.v1_0_4,
            stabilizedVersion = KotlinReleaseVersion.v1_0_4,
        )
    ),
    AMD(
        kindName = "amd",
        releaseVersionsMetadata = KotlinReleaseVersionLifecycle(
            introducedVersion = KotlinReleaseVersion.v1_0_4,
            stabilizedVersion = KotlinReleaseVersion.v1_0_4,
        )
    ),
    COMMONJS(
        kindName = "commonjs",
        releaseVersionsMetadata = KotlinReleaseVersionLifecycle(
            introducedVersion = KotlinReleaseVersion.v1_0_4,
            stabilizedVersion = KotlinReleaseVersion.v1_0_4,
        )
    ),
    UMD(
        kindName = "umd",
        releaseVersionsMetadata = KotlinReleaseVersionLifecycle(
            introducedVersion = KotlinReleaseVersion.v1_0_4,
            stabilizedVersion = KotlinReleaseVersion.v1_0_4,
        )
    ),
    ES(
        kindName = "es",
        releaseVersionsMetadata = KotlinReleaseVersionLifecycle(
            introducedVersion = KotlinReleaseVersion.v1_0_4,
            stabilizedVersion = KotlinReleaseVersion.v1_0_4,
        )
    );

    override val stringRepresentation: String
        get() = kindName
}
