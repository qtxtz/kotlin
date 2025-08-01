/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.jvm.compiler

import org.jetbrains.kotlin.cli.common.arguments.CommonCompilerArguments
import org.jetbrains.kotlin.cli.common.arguments.cliArgument
import org.jetbrains.kotlin.cli.js.K2JSCompiler
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.config.LanguageVersion
import org.jetbrains.kotlin.config.forcesPreReleaseBinariesIfEnabled
import org.jetbrains.kotlin.utils.PathUtil
import org.jetbrains.kotlin.util.toJvmMetadataVersion
import java.io.File
import java.util.jar.JarFile

class FirCompileKotlinAgainstCustomBinariesTest : AbstractCompileKotlinAgainstCustomBinariesTest() {
    override val languageVersion: LanguageVersion
        get() = maxOf(LanguageVersion.LATEST_STABLE, LanguageVersion.KOTLIN_2_0)

    override fun muteForK2(test: () -> Unit) {
        try {
            test()
        } catch (e: Throwable) {
            return
        }
        fail("Looks like this test can be unmuted. Remove the call to `muteForK2`.")
    }

    fun testHasStableParameterNames() {
        compileKotlin("source.kt", tmpdir, listOf(compileLibrary("library")))
    }

    fun testDeserializedAnnotationReferencesJava() {
        // Only Java
        val libraryAnnotation = compileLibrary("libraryAnnotation")
        // Specifically, use K1
        val libraryUsingAnnotation = compileLibrary(
            "libraryUsingAnnotation",
            additionalOptions = listOf(
                CommonCompilerArguments::languageVersion.cliArgument, "1.9",
                CommonCompilerArguments::suppressVersionWarnings.cliArgument,
            ),
            extraClassPath = listOf(libraryAnnotation)
        )

        compileKotlin(
            "usage.kt",
            output = tmpdir,
            classpath = listOf(libraryAnnotation, libraryUsingAnnotation),
        )
    }

    fun testStrictMetadataVersionSemanticsOldVersion() {
        val nextMetadataVersion = languageVersion.toJvmMetadataVersion().next()
        val library = compileLibrary(
            "library", additionalOptions = listOf("-Xgenerate-strict-metadata-version", "-Xmetadata-version=$nextMetadataVersion")
        )
        compileKotlin("source.kt", tmpdir, listOf(library))
    }

    fun testPreReleaseFlagIsConsistentBetweenBootstrapAndCurrentCompiler() {
        val bootstrapCompiler = JarFile(PathUtil.kotlinPathsForCompiler.compilerPath)
        val classFromBootstrapCompiler = bootstrapCompiler.getEntry(LanguageFeature::class.java.name.replace(".", "/") + ".class")
        checkPreReleaseness(
            bootstrapCompiler.getInputStream(classFromBootstrapCompiler).readBytes(),
            KotlinCompilerVersion.isPreRelease()
        )
    }

    fun testPreReleaseFlagIsConsistentBetweenStdlibAndCurrentCompiler() {
        val stdlib = JarFile(PathUtil.kotlinPathsForCompiler.stdlibPath)
        val classFromStdlib = stdlib.getEntry(KotlinVersion::class.java.name.replace(".", "/") + ".class")
        checkPreReleaseness(
            stdlib.getInputStream(classFromStdlib).readBytes(),
            KotlinCompilerVersion.isPreRelease()
        )
    }

    fun testReleaseCompilerAgainstPreReleaseFeatureJs() {
        val arbitraryPoisoningFeature = LanguageFeature.entries.firstOrNull { it.forcesPreReleaseBinariesIfEnabled() } ?: return

        val poisonedLibrary = compileJsLibrary(
            libraryName = "poisonedLibrary",
            additionalOptions = listOf("-XXLanguage:+$arbitraryPoisoningFeature",)
        ) {}

        val library = compileJsLibrary(
            libraryName = "library"
        ) {}

        compileKotlin(
            fileName = "source.kt",
            output = File(tmpdir, "usage.js"),
            classpath = listOf(poisonedLibrary, library),
            compiler = K2JSCompiler()
        ) { compilerOutput ->
            compilerOutput.replace(arbitraryPoisoningFeature.name, "<!POISONING_LANGUAGE_FEATURE!>")
        }
    }

    fun testReleaseCompilerWithoutUsageOfPreReleaseFeatureJs() {
        val arbitraryPoisoningFeature = LanguageFeature.entries.firstOrNull { it.forcesPreReleaseBinariesIfEnabled() } ?: return

        val poisonedLibrary = compileJsLibrary(
            libraryName = "poisonedLibrary",
            additionalOptions = listOf("-XXLanguage:+$arbitraryPoisoningFeature",)
        ) {}

        val library = compileJsLibrary(
            libraryName = "library"
        ) {}

        compileKotlin(
            fileName = "source.kt",
            output = File(tmpdir, "usage.js"),
            classpath = listOf(poisonedLibrary, library),
            compiler = K2JSCompiler()
        ) { compilerOutput ->
            compilerOutput.replace(arbitraryPoisoningFeature.name, "<!POISONING_LANGUAGE_FEATURE!>")
        }
    }

    fun testDataClassCompiledWith1_0_5Compiler() {
        val library = File(testDataDirectory, "VeryOldLibraryWithDataClass.jar")
        compileKotlin("source.kt", tmpdir, listOf(library), K2JVMCompiler())
    }
}
