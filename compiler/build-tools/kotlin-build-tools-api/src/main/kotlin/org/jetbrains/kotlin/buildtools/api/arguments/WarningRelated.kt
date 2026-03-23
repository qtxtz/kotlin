/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.buildtools.api.arguments

/**
 * @since 2.4.0
 */
@ExperimentalCompilerArgument
public class WarningLevel(
    public val warningName: String,
    public val severity: Severity,
) {

    public enum class Severity(
        public val stringValue: String,
    ) {
        ERROR(stringValue = "error"),
        WARNING(stringValue = "warning"),
        DISABLED(stringValue = "disabled");
    }
}
