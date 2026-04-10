/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.arguments.serialization.json

import org.jetbrains.kotlin.arguments.dsl.base.ExperimentalArgumentApi
import org.jetbrains.kotlin.arguments.dsl.types.DuplicatedUniqueNameStrategy
import org.jetbrains.kotlin.arguments.dsl.types.DuplicatedUniqueNameStrategyType
import org.jetbrains.kotlin.arguments.serialization.json.base.AllNamedTypeSerializer
import org.jetbrains.kotlin.arguments.serialization.json.base.NamedTypeSerializer
import org.jetbrains.kotlin.arguments.serialization.json.base.SetTypeSerializer
import org.jetbrains.kotlin.arguments.serialization.json.base.typeFinder

private const val SERIAL_NAME = "org.jetbrains.kotlin.arguments.DuplicatedUniqueNameStrategy"
private const val SET_SERIAL_NAME = "org.jetbrains.kotlin.arguments.SetDuplicatedUniqueNameStrategy"

object KotlinDuplicatedUniqueNameStrategyAsNameSerializer : NamedTypeSerializer<DuplicatedUniqueNameStrategy>(
    serialName = SERIAL_NAME,
    nameAccessor = DuplicatedUniqueNameStrategy::strategyName,
    typeFinder = DuplicatedUniqueNameStrategy::strategyName.typeFinder()
)

private object AllDuplicatedUniqueNameStrategySerializer : AllNamedTypeSerializer<DuplicatedUniqueNameStrategy>(
    serialName = SERIAL_NAME,
    jsonElementNameForName = "name",
    nameAccessor = DuplicatedUniqueNameStrategy::strategyName,
    typeFinder = DuplicatedUniqueNameStrategy::strategyName.typeFinder()
)

@OptIn(ExperimentalArgumentApi::class)
object AllDetailsDuplicatedUniqueNameStrategySerializer : SetTypeSerializer<DuplicatedUniqueNameStrategy>(
    typeSerializer = AllDuplicatedUniqueNameStrategySerializer,
    valueTypeQualifiedNamed = DuplicatedUniqueNameStrategyType::class.qualifiedName!!,
    serialName = SET_SERIAL_NAME,
)
