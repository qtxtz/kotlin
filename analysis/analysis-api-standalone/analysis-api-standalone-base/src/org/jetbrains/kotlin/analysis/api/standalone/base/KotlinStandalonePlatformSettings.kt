/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.api.standalone.base

import org.jetbrains.kotlin.analysis.api.platform.KotlinDeserializedDeclarationsOrigin
import org.jetbrains.kotlin.analysis.api.platform.KotlinPlatformSettings

open class KotlinStandalonePlatformSettings : KotlinPlatformSettings {
    override val deserializedDeclarationsOrigin: KotlinDeserializedDeclarationsOrigin
        get() = KotlinDeserializedDeclarationsOrigin.BINARIES

    override val allowUseSiteLibraryModuleAnalysis: Boolean
        get() = false
}
