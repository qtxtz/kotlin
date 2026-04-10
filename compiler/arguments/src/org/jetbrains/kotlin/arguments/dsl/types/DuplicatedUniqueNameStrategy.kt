/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.arguments.dsl.types

import kotlinx.serialization.Serializable
import org.jetbrains.kotlin.arguments.dsl.base.KotlinReleaseVersion
import org.jetbrains.kotlin.arguments.dsl.base.KotlinReleaseVersionLifecycle
import org.jetbrains.kotlin.arguments.dsl.base.WithKotlinReleaseVersionsMetadata
import org.jetbrains.kotlin.arguments.serialization.json.KotlinDuplicatedUniqueNameStrategyAsNameSerializer

@Serializable(with = KotlinDuplicatedUniqueNameStrategyAsNameSerializer::class)
enum class DuplicatedUniqueNameStrategy(
    val strategyName: String,
    override val releaseVersionsMetadata: KotlinReleaseVersionLifecycle,
) : WithKotlinReleaseVersionsMetadata, WithStringRepresentation {
    DENY(
        strategyName = "deny",
        releaseVersionsMetadata = KotlinReleaseVersionLifecycle(
            introducedVersion = KotlinReleaseVersion.v2_1_0,
        )
    ),
    ALLOW_ALL_WITH_WARNING(
        strategyName = "allow-all-with-warning",
        releaseVersionsMetadata = KotlinReleaseVersionLifecycle(
            introducedVersion = KotlinReleaseVersion.v2_1_0,
        )
    ),
    ALLOW_FIRST_WITH_WARNING(
        strategyName = "allow-first-with-warning",
        releaseVersionsMetadata = KotlinReleaseVersionLifecycle(
            introducedVersion = KotlinReleaseVersion.v2_1_0,
        )
    );

    override val stringRepresentation: String
        get() = strategyName
}
