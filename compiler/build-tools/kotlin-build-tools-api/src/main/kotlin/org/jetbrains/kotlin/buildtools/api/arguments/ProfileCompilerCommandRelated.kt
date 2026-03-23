/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.buildtools.api.arguments

import java.nio.file.Path

/**
 * @since 2.4.0
 */
@ExperimentalCompilerArgument
public class ProfileCompilerCommand(
    public val profilerPath: Path,
    public val command: String,
    public val outputDir: Path,
)