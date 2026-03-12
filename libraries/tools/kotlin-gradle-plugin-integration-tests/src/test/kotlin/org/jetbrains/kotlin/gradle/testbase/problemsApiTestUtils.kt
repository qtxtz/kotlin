/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.testbase

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.gradle.testkit.runner.BuildResult
import java.io.File
import java.net.URI
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal object ProblemsApiTestUtils {
    fun extractProblemReportUrl(output: String): String? {
        val urlRegex = """file:///[^\s]+problems-report\.html""".toRegex()
        return urlRegex.find(output)?.value
    }

    fun readProblemReportContent(urlString: String): String {
        val uri = URI(urlString)
        val file = File(uri)

        return if (file.exists()) {
            file.readText()
        } else {
            "File not found: $urlString"
        }
    }

    fun parseProblemReportFromScript(scriptContent: String): ProblemReport {
        val jsonRegex = """// begin-report-data\s*(\{.*\})\s*// end-report-data""".toRegex(RegexOption.DOT_MATCHES_ALL)
        val matchResult = jsonRegex.find(scriptContent)
            ?: throw IllegalArgumentException("Could not extract JSON from script")

        val json = Json {
            isLenient = true
            ignoreUnknownKeys = true
        }

        return json.decodeFromString(matchResult.groupValues[1])
    }
}

internal fun BuildResult.assertProblemsReportContainsDiagnostic(
    expectedProblemId: String,
    expectedMessageSubstring: String,
) {
    val reportUrl = ProblemsApiTestUtils.extractProblemReportUrl(output)
    assertNotNull(reportUrl, "Problems API HTML report URL not found in build output")

    val reportContent = ProblemsApiTestUtils.readProblemReportContent(reportUrl)
    val report = ProblemsApiTestUtils.parseProblemReportFromScript(reportContent)

    val matchingDiagnostics = report.diagnostics.filter { diagnostic ->
        diagnostic.problemId.any { it.name == expectedProblemId } &&
                (diagnostic.problemDetails.any { it.text.contains(expectedMessageSubstring) } ||
                        diagnostic.contextualLabel.contains(expectedMessageSubstring))
    }

    assertTrue(
        matchingDiagnostics.isNotEmpty(),
        "Expected Problems API HTML report to contain a '$expectedProblemId' diagnostic " +
                "with message containing '$expectedMessageSubstring', but none found.\n" +
                "Report diagnostics: ${report.diagnostics.map { d -> "${d.problemId.map { it.name }} -> details: ${d.problemDetails.map { it.text }}" }}"
    )
}

@Serializable
internal data class ProblemReport(
    val diagnostics: List<ProblemsApiDiagnostic>,
    val problemsReport: ProblemsReportSummary,
)

@Serializable
internal data class ProblemsApiDiagnostic(
    val locations: List<ProblemsApiLocation> = emptyList(),
    val problem: List<TextWrapper> = emptyList(),
    val severity: String = "",
    val problemDetails: List<TextWrapper> = emptyList(),
    val contextualLabel: String = "",
    val problemId: List<ProblemIdentifier> = emptyList(),
    val solutions: List<List<TextWrapper>> = emptyList(),
)

@Serializable
internal data class TextWrapper(val text: String)

@Serializable
internal data class ProblemsApiLocation(val pluginId: String = "")

@Serializable
internal data class ProblemIdentifier(
    val name: String,
    val displayName: String,
)

@Serializable
internal data class ProblemsReportSummary(
    val totalProblemCount: Int,
    val buildName: String,
    val requestedTasks: String,
    val documentationLink: String,
    val documentationLinkCaption: String,
    val summaries: List<String> = emptyList(),
)
