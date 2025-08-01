/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.jps.statistic

import org.jetbrains.kotlin.build.report.metrics.BuildAttribute
import org.jetbrains.kotlin.build.report.metrics.DynamicBuildTimeKey
import org.jetbrains.kotlin.build.report.metrics.JpsBuildPerformanceMetric
import org.jetbrains.kotlin.build.report.metrics.JpsBuildTime
import org.jetbrains.kotlin.build.report.statistics.CompileStatisticsData
import org.jetbrains.kotlin.build.report.statistics.StatTag

class JpsCompileStatisticsData(
    private val projectName: String?,
    private val label: String?,
    private val taskName: String,
    private val taskResult: String?,
    private val startTimeMs: Long,
    private val durationMs: Long,
    private val tags: Set<StatTag>,
    private val changes: List<String>,
    private val buildUuid: String = "Unset",
    private val kotlinVersion: String,
    private val kotlinLanguageVersion: String?,
    private val hostName: String? = "Unset",
    private val finishTime: Long,
    private val compilerArguments: List<String>,
    private val nonIncrementalAttributes: Set<BuildAttribute>,
    private val buildTimesMetrics: Map<JpsBuildTime, Long>,
    private val dynamicBuildTimesMetrics: Map<DynamicBuildTimeKey, Long>,
    private val performanceMetrics: Map<JpsBuildPerformanceMetric, Long>,
    private val gcTimeMetrics: Map<String, Long>?,
    private val gcCountMetrics: Map<String, Long>?,
    private val type: String,
    private val fromKotlinPlugin: Boolean?,
    private val compiledSources: List<String> = emptyList(),
    private val skipMessage: String?,
    private val icLogLines: List<String>,
) : CompileStatisticsData<JpsBuildTime, JpsBuildPerformanceMetric> {
    override fun getProjectName(): String? = projectName
    override fun getLabel(): String? = label

    override fun getTaskName(): String = taskName

    override fun getTaskResult(): String? = taskResult

    override fun getStartTimeMs(): Long = startTimeMs

    override fun getDurationMs(): Long = durationMs

    override fun getTags(): Set<StatTag> = tags

    override fun getChanges(): List<String> = changes

    override fun getKotlinVersion(): String = kotlinVersion

    override fun getKotlinLanguageVersion(): String? = kotlinLanguageVersion

    override fun getFinishTime(): Long = finishTime

    override fun getCompilerArguments(): List<String> = compilerArguments

    override fun getNonIncrementalAttributes(): Set<BuildAttribute> = nonIncrementalAttributes

    override fun getBuildTimesMetrics(): Map<JpsBuildTime, Long> = buildTimesMetrics

    override fun getDynamicBuildTimeMetrics(): Map<DynamicBuildTimeKey, Long> = dynamicBuildTimesMetrics

    override fun getPerformanceMetrics(): Map<JpsBuildPerformanceMetric, Long> = performanceMetrics

    override fun getGcTimeMetrics(): Map<String, Long>? = gcTimeMetrics

    override fun getGcCountMetrics(): Map<String, Long>? = gcCountMetrics

    override fun getFromKotlinPlugin(): Boolean? = fromKotlinPlugin

    override fun getSkipMessage(): String? = skipMessage

    override fun getIcLogLines(): List<String> = icLogLines
}