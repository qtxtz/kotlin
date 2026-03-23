/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.buildtools.api.arguments

/**
 * @since 2.4.0
 */
public class NullabilityAnnotation(
    public val fqName: String,
    public val mode: Mode,
) {

    public val annotationFqName: String = "@$fqName"

    public enum class Mode(
        public val stringValue: String,
    ) {
        IGNORE(stringValue = "ignore"),
        STRICT(stringValue = "strict"),
        WARN(stringValue = "warn");
    }
}