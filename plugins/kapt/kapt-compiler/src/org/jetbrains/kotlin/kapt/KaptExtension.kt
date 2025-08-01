/*
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.kapt

import com.intellij.openapi.project.Project
import com.sun.tools.javac.tree.JCTree
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.backend.jvm.JvmIrCodegenFactory
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.OUTPUT
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.messages.OutputMessageUtil
import org.jetbrains.kotlin.cli.common.output.writeAll
import org.jetbrains.kotlin.codegen.ClassBuilderMode
import org.jetbrains.kotlin.codegen.OriginCollectingClassBuilderFactory
import org.jetbrains.kotlin.codegen.state.GenerationState
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.config.fileMappingTracker
import org.jetbrains.kotlin.container.ComponentProvider
import org.jetbrains.kotlin.context.ProjectContext
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.kapt.base.*
import org.jetbrains.kotlin.kapt.base.AptMode.APT_ONLY
import org.jetbrains.kotlin.kapt.base.AptMode.WITH_COMPILATION
import org.jetbrains.kotlin.kapt.base.util.KaptBaseError
import org.jetbrains.kotlin.kapt.base.util.getPackageNameJava9Aware
import org.jetbrains.kotlin.kapt.base.util.info
import org.jetbrains.kotlin.kapt.stubs.KaptStubConverter
import org.jetbrains.kotlin.kapt.stubs.KaptStubConverter.KaptStub
import org.jetbrains.kotlin.kapt.util.MessageCollectorBackedKaptLogger
import org.jetbrains.kotlin.kapt.util.prettyPrint
import org.jetbrains.kotlin.kapt3.diagnostic.KaptError
import org.jetbrains.kotlin.modules.TargetId
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.utils.kapt.MemoryLeakDetector
import java.io.File

class ClasspathBasedKaptExtension(
    options: KaptOptions,
    logger: MessageCollectorBackedKaptLogger,
    compilerConfiguration: CompilerConfiguration
) : AbstractKaptExtension(options, logger, compilerConfiguration) {
    override val analyzePartially: Boolean
        get() = options[KaptFlag.USE_LIGHT_ANALYSIS] && super.analyzePartially

    private var processorLoader: ProcessorLoader? = null

    override fun loadProcessors(): LoadedProcessors {
        this.processorLoader = EfficientProcessorLoader(options, logger)
        return processorLoader!!.loadProcessors()
    }

    override fun analysisCompleted(
        project: Project,
        module: ModuleDescriptor,
        bindingTrace: BindingTrace,
        files: Collection<KtFile>
    ): AnalysisResult? {
        try {
            return super.analysisCompleted(project, module, bindingTrace, files)
        } finally {
            processorLoader?.close()
            clearJavacZipCaches()
        }
    }

    private fun clearJavacZipCaches() {
        try {
            val zipFileIndexCacheClass = Class.forName("com.sun.tools.javac.file.ZipFileIndexCache")
            val zipFileIndexCacheInstance = zipFileIndexCacheClass.getMethod("getSharedInstance").invoke(null)
            zipFileIndexCacheClass.getMethod("clearCache").invoke(zipFileIndexCacheInstance)
        } catch (e: Throwable) {
        }
    }
}

abstract class AbstractKaptExtension(
    val options: KaptOptions,
    val logger: MessageCollectorBackedKaptLogger,
    val compilerConfiguration: CompilerConfiguration
) : PartialAnalysisHandlerExtension() {
    private val pluginInitializedTime: Long = System.currentTimeMillis()

    private var annotationProcessingComplete = false

    private fun setAnnotationProcessingComplete(): Boolean {
        if (annotationProcessingComplete) return true

        annotationProcessingComplete = true
        return false
    }

    override val analyzePartially: Boolean
        get() = !annotationProcessingComplete

    override val analyzeDefaultParameterValues: Boolean
        get() = options[KaptFlag.DUMP_DEFAULT_PARAMETER_VALUES]

    override fun doAnalysis(
        project: Project,
        module: ModuleDescriptor,
        projectContext: ProjectContext,
        files: Collection<KtFile>,
        bindingTrace: BindingTrace,
        componentProvider: ComponentProvider
    ): AnalysisResult? {
        if (options.mode == APT_ONLY) {
            return AnalysisResult.EMPTY
        }

        return super.doAnalysis(project, module, projectContext, files, bindingTrace, componentProvider)
    }

    override fun analysisCompleted(
        project: Project,
        module: ModuleDescriptor,
        bindingTrace: BindingTrace,
        files: Collection<KtFile>
    ): AnalysisResult? {
        if (setAnnotationProcessingComplete()) return null

        fun doNotGenerateCode() = AnalysisResult.success(BindingContext.EMPTY, module, shouldGenerateCode = false)

        logger.info { "Initial analysis took ${System.currentTimeMillis() - pluginInitializedTime} ms" }

        val bindingContext = bindingTrace.bindingContext
        if (options.mode.generateStubs) {
            logger.info { "Kotlin files to compile: " + files.map { it.virtualFile?.name ?: "<in memory ${it.hashCode()}>" } }

            contextForStubGeneration(project, module, bindingContext, files.toList()).use { context ->
                generateKotlinSourceStubs(context)
            }
        }

        if (!options.mode.runAnnotationProcessing) return doNotGenerateCode()

        val processors = loadProcessors()
        if (processors.processors.isEmpty()) return if (options.mode != WITH_COMPILATION) doNotGenerateCode() else null

        val kaptContext = KaptContext(options, false, logger)

        fun handleKaptError(error: KaptError): AnalysisResult {
            val cause = error.cause

            if (cause != null) {
                kaptContext.logger.exception(cause)
            }

            return AnalysisResult.compilationError(bindingTrace.bindingContext)
        }

        try {
            runAnnotationProcessing(kaptContext, processors)
        } catch (error: KaptBaseError) {
            val kind = when (error.kind) {
                KaptBaseError.Kind.EXCEPTION -> KaptError.Kind.EXCEPTION
                KaptBaseError.Kind.ERROR_RAISED -> KaptError.Kind.ERROR_RAISED
            }

            val cause = error.cause
            return handleKaptError(if (cause != null) KaptError(kind, cause) else KaptError(kind))
        } catch (error: KaptError) {
            return handleKaptError(error)
        } catch (thr: Throwable) {
            return AnalysisResult.internalError(bindingTrace.bindingContext, thr)
        } finally {
            kaptContext.close()
        }

        return if (options.mode != WITH_COMPILATION) {
            doNotGenerateCode()
        } else {
            AnalysisResult.RetryWithAdditionalRoots(
                bindingTrace.bindingContext,
                module,
                listOf(options.sourcesOutputDir),
                listOfNotNull(options.sourcesOutputDir, options.getKotlinGeneratedSourcesDirectory()),
                addToEnvironment = true
            )
        }
    }

    private fun runAnnotationProcessing(kaptContext: KaptContext, processors: LoadedProcessors) {
        if (!options.mode.runAnnotationProcessing) return

        val javaSourceFiles = options.collectJavaSourceFiles(kaptContext.sourcesToReprocess)
        logger.info { "Java source files: " + javaSourceFiles.joinToString { it.normalize().absolutePath } }

        val (annotationProcessingTime) = measureTimeMillis {
            kaptContext.doAnnotationProcessing(javaSourceFiles, processors.processors)
        }

        logger.info { "Annotation processing took $annotationProcessingTime ms" }

        if (options.detectMemoryLeaks != DetectMemoryLeaksMode.NONE) {
            MemoryLeakDetector.add(processors.classLoader)

            val isParanoid = options.detectMemoryLeaks == DetectMemoryLeaksMode.PARANOID
            val (leakDetectionTime, leaks) = measureTimeMillis { MemoryLeakDetector.process(isParanoid) }
            logger.info { "Leak detection took $leakDetectionTime ms" }

            for (leak in leaks) {
                logger.warn(buildString {
                    appendLine("Memory leak detected!")
                    appendLine("Location: '${leak.className}', static field '${leak.fieldName}'")
                    append(leak.description)
                })
            }
        }
    }

    private fun contextForStubGeneration(
        project: Project,
        module: ModuleDescriptor,
        bindingContext: BindingContext,
        files: List<KtFile>
    ): KaptContextForStubGeneration {
        val builderFactory = OriginCollectingClassBuilderFactory(ClassBuilderMode.KAPT3)

        val configuration = compilerConfiguration.copy().apply {
            put(JVMConfigurationKeys.DO_NOT_CLEAR_BINDING_CONTEXT, true)
        }

        val targetId = TargetId(
            name = configuration[CommonConfigurationKeys.MODULE_NAME] ?: module.name.asString(),
            type = "java-production"
        )

        val generationState = GenerationState(project, module, configuration, builderFactory, targetId = targetId)

        val (classFilesCompilationTime) = measureTimeMillis {
            JvmIrCodegenFactory(configuration).convertAndGenerate(files, generationState, bindingContext)
        }

        val compiledClasses = builderFactory.compiledClasses
        val origins = builderFactory.origins

        logger.info { "Stubs compilation took $classFilesCompilationTime ms" }
        logger.info { "Compiled classes: " + compiledClasses.joinToString { it.name } }

        return KaptContextForStubGeneration(options, false, logger, compiledClasses, origins, generationState, bindingContext, emptyList())
    }

    private fun generateKotlinSourceStubs(kaptContext: KaptContextForStubGeneration) {
        val converter = KaptStubConverter(kaptContext, generateNonExistentClass = true)

        val (stubGenerationTime, kaptStubs) = measureTimeMillis {
            converter.convert()
        }

        logger.info { "Java stub generation took $stubGenerationTime ms" }
        logger.info { "Stubs for Kotlin classes: " + kaptStubs.joinToString { it.file.sourcefile.name } }

        saveStubs(kaptContext, kaptStubs, logger.messageCollector)
        saveIncrementalData(kaptContext, logger.messageCollector, converter)
    }

    protected open fun saveStubs(
        kaptContext: KaptContextForStubGeneration,
        stubs: List<KaptStub>,
        messageCollector: MessageCollector,
    ) {
        val reportOutputFiles = kaptContext.generationState.configuration.getBoolean(CommonConfigurationKeys.REPORT_OUTPUT_FILES)
        val outputFiles = if (reportOutputFiles) kaptContext.generationState.factory.asList().associateBy {
            it.relativePath.substringBeforeLast(".class", missingDelimiterValue = "")
        } else null

        for (kaptStub in stubs) {
            val stub = kaptStub.file
            val className = (stub.defs.first { it is JCTree.JCClassDecl } as JCTree.JCClassDecl).simpleName.toString()

            val packageName = stub.getPackageNameJava9Aware()?.toString() ?: ""
            val packageDir =
                if (packageName.isEmpty()) options.stubsOutputDir else File(options.stubsOutputDir, packageName.replace('.', '/'))
            packageDir.mkdirs()

            val sourceFile = File(packageDir, "$className.java")
            val classFilePathWithoutExtension = if (packageName.isEmpty()) {
                className
            } else {
                "${packageName.replace('.', '/')}/$className"
            }

            fun reportStubsOutputForIC(generatedFile: File) {
                if (!reportOutputFiles) return
                if (classFilePathWithoutExtension == "error/NonExistentClass") return
                val sourceFiles = (outputFiles?.get(classFilePathWithoutExtension)
                    ?: error("The `outputFiles` map is not properly initialized (key = $classFilePathWithoutExtension)")).sourceFiles
                kaptContext.generationState.configuration.fileMappingTracker?.recordSourceFilesToOutputFileMapping(
                    sourceFiles,
                    generatedFile
                )
                messageCollector.report(OUTPUT, OutputMessageUtil.formatOutputMessage(sourceFiles, generatedFile))
            }

            reportStubsOutputForIC(sourceFile)
            sourceFile.writeText(stub.prettyPrint(kaptContext.context))

            kaptStub.writeMetadataIfNeeded(forSource = sourceFile, ::reportStubsOutputForIC)
        }
    }

    protected open fun saveIncrementalData(
        kaptContext: KaptContextForStubGeneration,
        messageCollector: MessageCollector,
        converter: KaptStubConverter
    ) {
        val incrementalDataOutputDir = options.incrementalDataOutputDir ?: return

        val reportOutputFiles = kaptContext.generationState.configuration.getBoolean(CommonConfigurationKeys.REPORT_OUTPUT_FILES)
        kaptContext.generationState.factory.writeAll(incrementalDataOutputDir) { sources, output ->
            kaptContext.generationState.configuration.fileMappingTracker?.recordSourceFilesToOutputFileMapping(
                sources,
                output
            )
            if (reportOutputFiles) {
                messageCollector.report(OUTPUT, OutputMessageUtil.formatOutputMessage(sources, output))
            }
        }
    }

    protected abstract fun loadProcessors(): LoadedProcessors
}


inline fun <T> measureTimeMillis(block: () -> T): Pair<Long, T> {
    val start = System.currentTimeMillis()
    val result = block()
    return Pair(System.currentTimeMillis() - start, result)
}
