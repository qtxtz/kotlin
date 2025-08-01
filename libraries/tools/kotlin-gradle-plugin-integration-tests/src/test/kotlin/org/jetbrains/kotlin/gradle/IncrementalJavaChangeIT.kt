/*
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle

import org.gradle.api.logging.LogLevel
import org.gradle.util.GradleVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilerExecutionStrategy
import org.jetbrains.kotlin.gradle.testbase.*
import org.jetbrains.kotlin.gradle.util.replaceWithVersion
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import java.nio.file.Path
import kotlin.io.path.*
import kotlin.test.assertTrue

@DisplayName("Default incremental compilation with default precise java tracking")
abstract class IncrementalJavaChangeDefaultIT : IncrementalCompilationJavaChangesBase(usePreciseJavaTracking = null) {

    @DisplayName("Lib: tracked method signature ABI change")
    @GradleTest
    override fun testAbiChangeInLib_changeMethodSignature_tracked(gradleVersion: GradleVersion) {
        defaultProject(gradleVersion) {
            build("assemble")

            trackedJavaClassInLib.modify(changeMethodSignature)

            build("assemble") {
                val expectedSources = sourceFilesRelativeToProject(
                    listOf("foo/TrackedJavaClassChild.kt", "foo/useTrackedJavaClass.kt"),
                    subProjectName = "app"
                )
                assertCompiledKotlinSources(expectedSources, output)
            }
        }
    }

    @DisplayName("Lib: tracked method body non-ABI change")
    @GradleTest
    override fun testNonAbiChangeInLib_changeMethodBody_tracked(gradleVersion: GradleVersion) {
        defaultProject(gradleVersion) {
            build("assemble")

            trackedJavaClassInLib.modify(changeMethodBody)

            build("assemble") {
                assertCompiledKotlinSources(emptyList(), output)
            }
        }
    }

    @DisplayName("KT-38692: should clean all outputs after removing all Kotlin sources")
    @GradleTest
    fun testIncrementalWhenNoKotlinSources(gradleVersion: GradleVersion) {
        project("kotlinProject", gradleVersion) {
            build(":compileKotlin") {
                assertTasksExecuted(":compileKotlin")
            }

            // Remove all Kotlin sources and force non-incremental run
            projectPath.allKotlinFiles.forEach { it.deleteExisting() }
            javaSourcesDir().resolve("Sample.java").also {
                it.parent.createDirectories()
                it.writeText("public class Sample {}")
            }
            build("compileKotlin", "--rerun-tasks") {
                assertTasksExecuted(":compileKotlin")
                assertTrue(kotlinClassesDir().notExists())
            }
        }
    }

    @DisplayName("Type alias change is incremental")
    @GradleTest
    open fun testTypeAliasIncremental(gradleVersion: GradleVersion) {
        project("typeAlias", gradleVersion) {
            build("build")

            val curryKt = kotlinSourcesDir().resolve("Curry.kt")
            val useCurryKt = kotlinSourcesDir().resolve("UseCurry.kt")

            curryKt.modify {
                it.replace("class Curry", "internal class Curry")
            }

            build("build") {
                assertCompiledKotlinSources(
                    listOf(curryKt, useCurryKt).map { it.relativeTo(projectPath) },
                    output
                )
            }
        }
    }
}

@DisplayName("Default incremental compilation with default precise java tracking on K1")
class IncrementalK1JavaChangeDefaultIT : IncrementalJavaChangeDefaultIT() {
    override val defaultBuildOptions = super.defaultBuildOptions.copyEnsuringK1()
}

@DisplayName("Default incremental compilation with default precise java tracking on K2")
open class IncrementalK2JavaChangeDefaultIT : IncrementalJavaChangeDefaultIT() {
    override val defaultBuildOptions = super.defaultBuildOptions.copyEnsuringK2()

    @Disabled("KT-57147")
    @GradleTest
    override fun testAbiChangeInLib_changeMethodSignature_tracked(gradleVersion: GradleVersion) {
        super.testAbiChangeInLib_changeMethodSignature_tracked(gradleVersion)
    }

    @Disabled("KT-57147")
    @GradleTest
    override fun testNonAbiChangeInLib_changeMethodBody_tracked(gradleVersion: GradleVersion) {
        super.testNonAbiChangeInLib_changeMethodBody_tracked(gradleVersion)
    }

    @Disabled("KT-57147")
    @GradleTest
    override fun testAbiChangeInLib_changeMethodSignature(gradleVersion: GradleVersion) {
        super.testAbiChangeInLib_changeMethodSignature(gradleVersion)
    }

    @Disabled("KT-57147")
    @GradleTest
    override fun testNonAbiChangeInLib_changeMethodBody(gradleVersion: GradleVersion) {
        super.testNonAbiChangeInLib_changeMethodBody(gradleVersion)
    }
}

@DisplayName("Default incremental compilation with default precise java tracking on K2 using Fir Runner")
class IncrementalK2JavaChangeDefaultWithFirIT : IncrementalK2JavaChangeDefaultIT() {
    override val defaultBuildOptions = super.defaultBuildOptions.copy(useFirJvmRunner = true)
}

@DisplayName("Default incremental compilation via Build Tools API")
open class IncrementalK2JavaChangeBuildToolsApiDaemonIT : IncrementalJavaChangeDefaultIT() {
    override val defaultBuildOptions = super.defaultBuildOptions.copy(runViaBuildToolsApi = true, compilerExecutionStrategy = KotlinCompilerExecutionStrategy.DAEMON)

    @Disabled("KT-57147")
    @GradleTest
    override fun testAbiChangeInLib_changeMethodSignature_tracked(gradleVersion: GradleVersion) {
        super.testAbiChangeInLib_changeMethodSignature_tracked(gradleVersion)
    }

    @Disabled("KT-57147")
    @GradleTest
    override fun testNonAbiChangeInLib_changeMethodBody_tracked(gradleVersion: GradleVersion) {
        super.testNonAbiChangeInLib_changeMethodBody_tracked(gradleVersion)
    }

    @Disabled("KT-57147")
    @GradleTest
    override fun testAbiChangeInLib_changeMethodSignature(gradleVersion: GradleVersion) {
        super.testAbiChangeInLib_changeMethodSignature(gradleVersion)
    }

    @Disabled("KT-57147")
    @GradleTest
    override fun testNonAbiChangeInLib_changeMethodBody(gradleVersion: GradleVersion) {
        super.testNonAbiChangeInLib_changeMethodBody(gradleVersion)
    }
}

@DisplayName("Default incremental compilation via Build Tools API")
class IncrementalK2JavaChangeWithFirRunnerAndBuildToolsApiDaemonIT : IncrementalK2JavaChangeBuildToolsApiDaemonIT() {
    override val defaultBuildOptions: BuildOptions = super.defaultBuildOptions.copy(useFirJvmRunner = true)
}

@DisplayName("Incremental compilation via Build Tools API using in-process strategy")
open class IncrementalK2JavaChangeBuildToolsApiInProcessIT : IncrementalJavaChangeDefaultIT() {
    override val defaultBuildOptions = super.defaultBuildOptions.copy(runViaBuildToolsApi = true, compilerExecutionStrategy = KotlinCompilerExecutionStrategy.IN_PROCESS)

    @Disabled("KT-57147")
    @GradleTest
    override fun testAbiChangeInLib_changeMethodSignature_tracked(gradleVersion: GradleVersion) {
        super.testAbiChangeInLib_changeMethodSignature_tracked(gradleVersion)
    }

    @Disabled("KT-57147")
    @GradleTest
    override fun testNonAbiChangeInLib_changeMethodBody_tracked(gradleVersion: GradleVersion) {
        super.testNonAbiChangeInLib_changeMethodBody_tracked(gradleVersion)
    }

    @Disabled("KT-57147")
    @GradleTest
    override fun testAbiChangeInLib_changeMethodSignature(gradleVersion: GradleVersion) {
        super.testAbiChangeInLib_changeMethodSignature(gradleVersion)
    }

    @Disabled("KT-57147")
    @GradleTest
    override fun testNonAbiChangeInLib_changeMethodBody(gradleVersion: GradleVersion) {
        super.testNonAbiChangeInLib_changeMethodBody(gradleVersion)
    }
}

@DisplayName("Incremental compilation via Build Tools API using in-process strategy and FIR runner")
class IncrementalK2JavaChangeUsingFirRunnerBuildToolsApiInProcessIT : IncrementalK2JavaChangeBuildToolsApiInProcessIT() {
    override val defaultBuildOptions = super.defaultBuildOptions.copy(useFirJvmRunner = true)
}

@DisplayName("Default incremental compilation with disabled precise compilation outputs backup")
abstract class IncrementalJavaChangeWithoutPreciseCompilationBackupIT : IncrementalJavaChangeDefaultIT() {
    override val defaultBuildOptions = super.defaultBuildOptions.copy(usePreciseOutputsBackup = false, keepIncrementalCompilationCachesInMemory = false)
}

@DisplayName("Default incremental compilation with disabled precise compilation outputs backup on K1")
class IncrementalK1JavaChangeWithoutPreciseCompilationBackupIT : IncrementalJavaChangeWithoutPreciseCompilationBackupIT() {
    override val defaultBuildOptions = super.defaultBuildOptions.copyEnsuringK1()
}

@DisplayName("Default incremental compilation with disabled precise compilation outputs backup on K2")
open class IncrementalK2JavaChangeWithoutPreciseCompilationBackupIT : IncrementalJavaChangeWithoutPreciseCompilationBackupIT() {
    override val defaultBuildOptions = super.defaultBuildOptions.copyEnsuringK2()

    @Disabled("KT-57147")
    @GradleTest
    override fun testAbiChangeInLib_changeMethodSignature_tracked(gradleVersion: GradleVersion) {
        super.testAbiChangeInLib_changeMethodSignature_tracked(gradleVersion)
    }

    @Disabled("KT-57147")
    @GradleTest
    override fun testNonAbiChangeInLib_changeMethodBody_tracked(gradleVersion: GradleVersion) {
        super.testNonAbiChangeInLib_changeMethodBody_tracked(gradleVersion)
    }

    @Disabled("KT-57147")
    @GradleTest
    override fun testAbiChangeInLib_changeMethodSignature(gradleVersion: GradleVersion) {
        super.testAbiChangeInLib_changeMethodSignature(gradleVersion)
    }

    @Disabled("KT-57147")
    @GradleTest
    override fun testNonAbiChangeInLib_changeMethodBody(gradleVersion: GradleVersion) {
        super.testNonAbiChangeInLib_changeMethodBody(gradleVersion)
    }
}

@DisplayName("Default incremental compilation with FIR runner and disabled precise compilation outputs backup on K2")
class IncrementalK2JavaChangeWithFirRunnerAndWithoutPreciseCompilationBackupIT :
    IncrementalK2JavaChangeWithoutPreciseCompilationBackupIT() {
    override val defaultBuildOptions: BuildOptions = super.defaultBuildOptions.copy(useFirJvmRunner = true)
}

@DisplayName("Default incremental compilation with enabled precise java tracking")
class IncrementalJavaChangePreciseIT : IncrementalCompilationJavaChangesBase(
    usePreciseJavaTracking = true
) {
    @Disabled("KT-57147")
    @DisplayName("Lib: tracked method signature ABI change")
    @GradleTest
    override fun testAbiChangeInLib_changeMethodSignature_tracked(gradleVersion: GradleVersion) {
        defaultProject(gradleVersion) {
            build("assemble")

            trackedJavaClassInLib.modify(changeMethodSignature)

            build("assemble") {
                val expectedSources = sourceFilesRelativeToProject(
                    listOf("foo/TrackedJavaClassChild.kt", "foo/useTrackedJavaClass.kt"),
                    subProjectName = "app"
                )
                assertCompiledKotlinSources(expectedSources, output)
            }
        }
    }

    @Disabled("KT-57147")
    @DisplayName("Lib: tracked method body non-ABI change")
    @GradleTest
    override fun testNonAbiChangeInLib_changeMethodBody_tracked(gradleVersion: GradleVersion) {
        defaultProject(gradleVersion) {
            build("assemble")

            trackedJavaClassInLib.modify(changeMethodBody)

            build("assemble") {
                assertCompiledKotlinSources(emptyList(), output)
            }
        }
    }
}

@DisplayName("Default incremental compilation with disabled precise java tracking")
abstract class IncrementalJavaChangeDisablePreciseIT : IncrementalCompilationJavaChangesBase(
    usePreciseJavaTracking = false
) {
    @DisplayName("Lib: tracked method signature ABI change")
    @GradleTest
    override fun testAbiChangeInLib_changeMethodSignature_tracked(gradleVersion: GradleVersion) {
        defaultProject(gradleVersion) {
            build("assemble")

            trackedJavaClassInLib.modify(changeMethodSignature)

            build("assemble") {
                val expectedSources = sourceFilesRelativeToProject(
                    listOf(
                        "foo/TrackedJavaClassChild.kt",
                        "foo/useTrackedJavaClass.kt"
                    ),
                    subProjectName = "app"
                ) + sourceFilesRelativeToProject(
                    listOf("bar/useTrackedJavaClassSameModule.kt"),
                    subProjectName = "lib"
                )
                assertCompiledKotlinSources(expectedSources, output)
            }
        }
    }

    @DisplayName("Lib: tracked method body non-ABI change")
    @GradleTest
    override fun testNonAbiChangeInLib_changeMethodBody_tracked(gradleVersion: GradleVersion) {
        defaultProject(gradleVersion) {
            build("assemble")

            trackedJavaClassInLib.modify(changeMethodBody)

            build("assemble") {
                val expectedSources = sourceFilesRelativeToProject(
                    listOf("bar/useTrackedJavaClassSameModule.kt"),
                    subProjectName = "lib"
                )
                assertCompiledKotlinSources(expectedSources, output)
            }
        }
    }
}

@DisplayName("Default incremental compilation with disabled precise java tracking and enabled K1")
class IncrementalK1JavaChangeDisablePreciseIT : IncrementalJavaChangeDisablePreciseIT() {
    override val defaultBuildOptions = super.defaultBuildOptions.copyEnsuringK1()
}

@DisplayName("Default incremental compilation with disabled precise java tracking and enabled K2")
open class IncrementalK2JavaChangeDisablePreciseIT : IncrementalJavaChangeDisablePreciseIT() {
    override val defaultBuildOptions = super.defaultBuildOptions.copyEnsuringK2()
}

@DisplayName("Default incremental compilation with disabled precise java tracking and enabled FIR runner and K2")
class IncrementalK2JavaChangeDisablePreciseFirIT : IncrementalK2JavaChangeDisablePreciseIT() {
    override val defaultBuildOptions: BuildOptions = super.defaultBuildOptions.copy(useFirJvmRunner = true)
}

@JvmGradlePluginTests
abstract class IncrementalCompilationJavaChangesBase(
    val usePreciseJavaTracking: Boolean?
) : IncrementalCompilationBaseIT() {
    override val defaultProjectName: String
        get() = "incrementalMultiproject"

    override val defaultBuildOptions = super.defaultBuildOptions.copy(usePreciseJavaTracking = usePreciseJavaTracking)

    protected val TestProject.javaClassInLib: Path get() = subProject("lib").javaSourcesDir().resolve("bar/JavaClass.java")
    protected val TestProject.trackedJavaClassInLib: Path get() = subProject("lib").javaSourcesDir().resolve("bar/TrackedJavaClass.java")
    protected val changeMethodSignature: (String) -> String = { it.replace("String getString", "Object getString") }
    protected val changeMethodBody: (String) -> String = { it.replace("Hello, World!", "Hello, World!!!!") }

    @DisplayName("Lib: tracked method signature ABI change")
    @GradleTest
    open fun testAbiChangeInLib_changeMethodSignature(gradleVersion: GradleVersion) {
        defaultProject(gradleVersion) {
            build("assemble")

            javaClassInLib.modify(changeMethodSignature)

            build("assemble") {
                // Fewer Kotlin files are recompiled
                val expectedSources = sourceFilesRelativeToProject(
                    listOf("foo/JavaClassChild.kt", "foo/useJavaClass.kt"),
                    subProjectName = "app"
                )
                assertCompiledKotlinSources(expectedSources, output)
            }
        }
    }

    @DisplayName("Lib: method body non-ABI change")
    @GradleTest
    open fun testNonAbiChangeInLib_changeMethodBody(gradleVersion: GradleVersion) {
        defaultProject(gradleVersion) {
            build("assemble")

            javaClassInLib.modify(changeMethodBody)

            build("assemble") {
                assertTasksExecuted(":lib:compileKotlin")
                assertTasksUpToDate(":app:compileKotlin") // App compilation has 'compile avoidance'
                assertCompiledKotlinSources(emptyList(), output)
            }
        }
    }

    abstract fun testAbiChangeInLib_changeMethodSignature_tracked(gradleVersion: GradleVersion)
    abstract fun testNonAbiChangeInLib_changeMethodBody_tracked(gradleVersion: GradleVersion)
}

@JvmGradlePluginTests
class BasicIncrementalJavaInteropIT : KGPBaseTest() {

    @DisplayName("Basic scenario: Kotlin constant tracks a Java constant")
    @GradleTest
    fun testKotlinConstantTrackingJavaConstant(gradleVersion: GradleVersion) {
        project("kt-69042-basic-java-interop", gradleVersion) {
            build("assemble")

            val javaSource = projectPath.resolve("src/main/java/JavaConstants.java")
            javaSource.replaceWithVersion("newValue")

            // KT-75850: we need to explicitly invalidate the CC here, as changing the logLevel doesn't trigger it
            build(
                "assemble",
                "-PinvalidateCC${generateIdentifier()}",
                buildOptions = defaultBuildOptions.copy(logLevel = LogLevel.DEBUG),
            ) {
                assertTasksExecuted(":compileJava", ":compileKotlin")
                assertIncrementalCompilation(listOf(kotlinSourcesDir().resolve("usage.kt")).relativizeTo(projectPath))
            }
        }
    }
}
