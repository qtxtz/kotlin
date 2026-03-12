/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("FunctionName")

package org.jetbrains.kotlin.gradle.unitTests

import org.jetbrains.kotlin.buildtools.api.CompilerMessageRenderer.Severity
import org.jetbrains.kotlin.buildtools.api.CompilerMessageRenderer.SourceLocation
import org.jetbrains.kotlin.compilerRunner.btapi.ProblemsApiCompilerMessageRenderer
import org.jetbrains.kotlin.gradle.plugin.diagnostics.CompilerDiagnosticsProblemsReporter
import java.io.File
import kotlin.test.*

class ProblemsApiCompilerMessageRendererTest {

    private data class RecordedCall(
        val severity: Severity,
        val message: String,
        val location: SourceLocation?,
    )

    private class RecordingCompilerDiagnosticsProblemsReporter : CompilerDiagnosticsProblemsReporter {
        val calls = mutableListOf<RecordedCall>()

        override fun reportCompilerMessage(
            severity: Severity,
            message: String,
            location: SourceLocation?,
        ) {
            calls.add(RecordedCall(severity, message, location))
        }
    }

    @Test
    fun `render with location returns URI with line and column`() {
        val reporter = RecordingCompilerDiagnosticsProblemsReporter()
        val renderer = ProblemsApiCompilerMessageRenderer(reporter)

        val path = "/tmp/src/Main.kt"
        val location = SourceLocation(path, 10, 5, -1, -1, null)
        val result = renderer.render(Severity.ERROR, "Unresolved reference", location)

        val expectedUri = File(path).toPath().toUri()
        assertNotNull(result)
        assertTrue(result.contains("${expectedUri}:10:5"), "Expected URI with line:column, got: $result")
        assertTrue(result.endsWith("Unresolved reference"), "Expected message at end, got: $result")
    }

    @Test
    fun `render with null location returns message only`() {
        val reporter = RecordingCompilerDiagnosticsProblemsReporter()
        val renderer = ProblemsApiCompilerMessageRenderer(reporter)

        val result = renderer.render(Severity.INFO, "Compilation completed", null)

        assertEquals("Compilation completed", result)
    }

    @Test
    fun `render with zero line and column omits position`() {
        val reporter = RecordingCompilerDiagnosticsProblemsReporter()
        val renderer = ProblemsApiCompilerMessageRenderer(reporter)

        val path = "/tmp/src/Main.kt"
        val location = SourceLocation(path, 0, 0, -1, -1, null)
        val result = renderer.render(Severity.WARNING, "Something", location)

        val expectedUri = File(path).toPath().toUri().toString()
        assertNotNull(result)
        assertTrue(result.contains(expectedUri), "Expected file URI, got: $result")
        assertFalse(result.contains(":0:0"), "Should not contain :0:0, got: $result")
        assertTrue(result.endsWith("Something"), "Expected message at end, got: $result")
    }

    @Test
    fun `render delegates to problems reporter with correct arguments`() {
        val reporter = RecordingCompilerDiagnosticsProblemsReporter()
        val renderer = ProblemsApiCompilerMessageRenderer(reporter)

        val location = SourceLocation("/tmp/src/Main.kt", 10, 5, -1, -1, null)
        renderer.render(Severity.ERROR, "Unresolved reference", location)

        assertEquals(1, reporter.calls.size)
        val call = reporter.calls.first()
        assertEquals(Severity.ERROR, call.severity)
        assertEquals("Unresolved reference", call.message)
        assertEquals(location, call.location)
    }

    @Test
    fun `render delegates for each severity`() {
        val reporter = RecordingCompilerDiagnosticsProblemsReporter()
        val renderer = ProblemsApiCompilerMessageRenderer(reporter)

        val severities = Severity.entries
        severities.forEach { severity ->
            renderer.render(severity, "message for $severity", null)
        }

        assertEquals(severities.size, reporter.calls.size)
        severities.forEachIndexed { index, severity ->
            assertEquals(severity, reporter.calls[index].severity)
            assertEquals("message for $severity", reporter.calls[index].message)
        }
    }
}
