/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.script.experimental.jvmhost.test

import kotlinx.coroutines.runBlocking
import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.common.arguments.CommonCompilerArguments
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.arguments.cliArgument
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.scripting.compiler.plugin.impl.SCRIPT_BASE_COMPILER_ARGUMENTS_PROPERTY
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.Resources
import java.io.*
import java.net.URLClassLoader
import java.security.MessageDigest
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.host.with
import kotlin.script.experimental.jvm.*
import kotlin.script.experimental.jvm.impl.KJvmCompiledScript
import kotlin.script.experimental.jvm.util.KotlinJars
import kotlin.script.experimental.jvm.util.classpathFromClass
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.CompiledScriptJarsCache
import kotlin.script.experimental.jvmhost.JvmScriptCompiler
import kotlin.script.experimental.jvmhost.loadScriptFromJar
import kotlin.test.*

@ResourceLock(Resources.SYSTEM_OUT)
class CachingTest {

    val simpleScript = "val x = 1\nprintln(\"x = \$x\")".toScriptSource()
    val simpleScriptExpectedOutput = listOf("x = 1")

    val scriptWithImport = "println(\"Hello from imported \$helloScriptName script!\")".toScriptSource()
    val scriptWithImportExpectedOutput = listOf("Hello from helloWithVal script!", "Hello from imported helloWithVal script!")

    @Test
    fun testMemoryCache() {
        val cache = SimpleMemoryScriptsCache()
        checkWithCache(cache, simpleScript, simpleScriptExpectedOutput)
    }

    @Test
    fun testSimpleImportWithMemoryCache() {
        val cache = SimpleMemoryScriptsCache()
        checkWithCache(
            cache, scriptWithImport, scriptWithImportExpectedOutput,
            compilationConfiguration = { makeSimpleConfigurationWithTestImport() }
        )
    }


    @Test
    fun testFileCache() {
        withTempDir("scriptingTestCache") { cacheDir ->
            val cache = FileBasedScriptCache(cacheDir)
            assertEquals(true, cache.baseDir.listFiles()?.isEmpty())

            checkWithCache(cache, simpleScript, simpleScriptExpectedOutput)
        }
    }

    @Test
    fun testSimpleImportWithFileCache() {
        withTempDir("scriptingTestCache") { cacheDir ->
            val cache = FileBasedScriptCache(cacheDir)
            assertEquals(true, cache.baseDir.listFiles()?.isEmpty())

            checkWithCache(
                cache, scriptWithImport, scriptWithImportExpectedOutput,
                compilationConfiguration = { makeSimpleConfigurationWithTestImport() }
            )
        }
    }

    @Test
    fun testJarCache() {
        withTempDir("scriptingTestJarCache") { cacheDir ->
            val cache = TestCompiledScriptJarsCache(cacheDir)
            assertTrue(cache.baseDir.listFiles()!!.isEmpty())

            checkWithCache(cache, simpleScript, simpleScriptExpectedOutput)

            val scriptOut = runScriptFromJar(cache.baseDir.listFiles()!!.first { it.extension == "jar" })

            assertEquals(simpleScriptExpectedOutput, scriptOut)
        }
    }

    @Test
    fun testSimpleImportWithJarCache() {
        withTempDir("scriptingTestJarCache") { cacheDir ->
            val cache = TestCompiledScriptJarsCache(cacheDir)
            assertTrue(cache.baseDir.listFiles()!!.isEmpty())

            checkWithCache(
                cache, scriptWithImport, scriptWithImportExpectedOutput,
                compilationConfiguration = { makeSimpleConfigurationWithTestImport() }
            )

            // cannot make it work in this form - it requires a dependency on the current test classes, but classes directory seems
            // not work when specified in the manifest
            // TODO: find a way to make it work
//        val scriptOut = runScriptFromJar(cache.baseDir.listFiles()!!.first { it.extension == "jar" })
//
//        assertEquals(scriptWithImportExpectedOutput, scriptOut)
        }
    }

    @Test
    fun testImplicitReceiversWithJarCache() {
        withTempDir("scriptingTestJarCache") { cacheDir ->
            val cache = TestCompiledScriptJarsCache(cacheDir)
            assertTrue(cache.baseDir.listFiles()!!.isEmpty())

            checkWithCache(
                cache, simpleScript, simpleScriptExpectedOutput, checkDirectEval = false,
                compilationConfiguration = {
                    updateClasspath(classpathFromClass<ScriptingHostTest>()) // the class defined here should be in the classpath
                    implicitReceivers(Implicit::class)
                }
            ) {
                implicitReceivers(Implicit)
            }
        }
    }

    @Test
    @Ignore // does not work reliably probably due to file cashing TODO: rewrite to more reliable variant
    fun ignoredTestLocalDependencyWithJarCacheInvalidation() {
        withTempDir("scriptingTestDepDir") { depDir ->
            val standardJars = KotlinJars.kotlinScriptStandardJars
            val outJar = makeDependenciesJar(depDir, standardJars)

            withTempDir("scriptingTestJarChacheWithDep") { cacheDir ->
                val cache = TestCompiledScriptJarsCache(cacheDir)
                assertTrue(cache.baseDir.listFiles()!!.isEmpty())

                val hostConfiguration = defaultJvmScriptingHostConfiguration.with {
                    jvm {
                        baseClassLoader.replaceOnlyDefault(null)
                        compilationCache(cache)
                    }
                }
                val host = BasicJvmScriptingHost(compiler = JvmScriptCompiler(hostConfiguration), evaluator = BasicJvmScriptEvaluator())

                val scriptCompilationConfiguration = ScriptCompilationConfiguration {
                    updateClasspath(standardJars +outJar)
                    this.hostConfiguration.update { hostConfiguration }
                }

                val script = "Dependency(42).v".toScriptSource()

                val res0 = host.eval(script, scriptCompilationConfiguration, null).valueOrThrow().returnValue
                assertEquals(42, (res0 as? ResultValue.Value)?.value)
                assertEquals(1, cache.storedScripts)
                assertEquals(0, cache.retrievedScripts)

                val res1 = host.eval(script, scriptCompilationConfiguration, null).valueOrThrow().returnValue
                assertEquals(42, (res1 as? ResultValue.Value)?.value)
                assertEquals(1, cache.storedScripts)
                assertEquals(1, cache.retrievedScripts)

                val outJar2 = File(depDir, "dependency2.jar")
                outJar.renameTo(outJar2)

                val cachedScriptJar = cache.baseDir.listFiles().single()
                val loadedScript = cachedScriptJar.loadScriptFromJar(checkMissingDependencies = false)
                val res2 = runBlocking {
                    BasicJvmScriptEvaluator().invoke(loadedScript!!)
                }.valueOrThrow().returnValue
                assertEquals("Dependency", (res2 as? ResultValue.Error)?.error?.message)

                assertNull(cachedScriptJar.loadScriptFromJar(checkMissingDependencies = true))
                assertNull(cache.get(script, scriptCompilationConfiguration))
                assertEquals(0, cacheDir.listFiles().size)
            }
        }
    }

    @Test
    fun testLocalDependencyWithExternalLoadAndCache() {
        withTempDir("scriptingTestDepDir") { depDir ->
            val standardJars = KotlinJars.kotlinScriptStandardJars
            val outJar = makeDependenciesJar(depDir, standardJars)

            withTempDir("scriptingTestJarChacheWithExtLoadedDep") { cacheDir ->
                val cache = TestCompiledScriptJarsCache(cacheDir)
                assertTrue(cache.baseDir.listFiles()!!.isEmpty())

                val hostConfiguration = defaultJvmScriptingHostConfiguration.with {
                    jvm {
                        baseClassLoader(URLClassLoader((standardJars + outJar).map { it.toURI().toURL() }.toTypedArray(), null))
                        compilationCache(cache)
                    }
                }
                val host = BasicJvmScriptingHost(compiler = JvmScriptCompiler(hostConfiguration), evaluator = BasicJvmScriptEvaluator())

                val scriptCompilationConfiguration = ScriptCompilationConfiguration {
                    updateClasspath(standardJars + outJar)
                    this.hostConfiguration.update { hostConfiguration }
                }
                val scriptEvaluationConfiguration = ScriptEvaluationConfiguration {
                    jvm {
                        loadDependencies(false)
                    }
                    this.hostConfiguration.update { hostConfiguration }
                }

                val script = "Dependency(42).v".toScriptSource()

                // Without the patch that fixes loadDependencies usage in kotlin.script.experimental.jvmhost.KJvmCompiledScriptLazilyLoadedFromClasspath.getClass
                // AND with hostConfiguration removed from scriptEvaluationConfiguration (essentially creating a misconfigured evaluator)
                // the first evaluation fails because it cannot find the class for dependency, but the second mistakingly succeed, because dependency is taken from the cache
                // (see #KT-50902 for details)
                val res0 = host.eval(script, scriptCompilationConfiguration, scriptEvaluationConfiguration).valueOrThrow().returnValue
                assertEquals(42, (res0 as? ResultValue.Value)?.value)

                val res1 = host.eval(script, scriptCompilationConfiguration, scriptEvaluationConfiguration).valueOrThrow().returnValue
                assertEquals(42, (res1 as? ResultValue.Value)?.value)
            }
        }
    }

    private fun makeDependenciesJar(depDir: File, standardJars: List<File>): File {
        val isK2 = System.getProperty(SCRIPT_BASE_COMPILER_ARGUMENTS_PROPERTY)?.contains("-language-version 1.9") != true
        val outJar = File(depDir, "dependency.jar")
        val inKt = File(depDir, "Dependency.kt").apply { writeText("class Dependency(val v: Int)") }
        val outStream = ByteArrayOutputStream()
        val compileExitCode = K2JVMCompiler().exec(
            PrintStream(outStream),
            K2JVMCompilerArguments::destination.cliArgument,
            outJar.path,
            K2JVMCompilerArguments::noStdlib.cliArgument,
            K2JVMCompilerArguments::classpath.cliArgument,
            standardJars.joinToString(File.pathSeparator),
            CommonCompilerArguments::languageVersion.cliArgument,
            if (isK2) "2.0" else "1.9",
            CommonCompilerArguments::suppressVersionWarnings.cliArgument,
            inKt.path
        )
        assertTrue(
            outStream.size() == 0 && compileExitCode == ExitCode.OK && outJar.exists(),
            "Compilation Failed:\n$outStream"
        )
        return outJar
    }

    private fun checkWithCache(
        cache: ScriptingCacheWithCounters, script: SourceCode, expectedOutput: List<String>, checkDirectEval: Boolean = true,
        compilationConfiguration: ScriptCompilationConfiguration.Builder.() -> Unit = {},
        evaluationConfiguration: ScriptEvaluationConfiguration.Builder.() -> Unit = {}
    ) {
        val myHostConfiguration = defaultJvmScriptingHostConfiguration.with {
            jvm {
                baseClassLoader.replaceOnlyDefault(null)
                compilationCache(cache)
            }
        }
        val compiler = JvmScriptCompiler(myHostConfiguration)
        val evaluator = BasicJvmScriptEvaluator()
        val host = BasicJvmScriptingHost(compiler = compiler, evaluator = evaluator)

        val scriptCompilationConfiguration = ScriptCompilationConfiguration(body = compilationConfiguration).with {
            updateClasspath(KotlinJars.kotlinScriptStandardJarsWithReflect)
            hostConfiguration.update { myHostConfiguration }
        }

        val scriptEvaluationConfiguration = ScriptEvaluationConfiguration(body = evaluationConfiguration)

        assertEquals(0, cache.storedScripts)
        var compiledScript: CompiledScript? = null
        val output = captureOut {
            runBlocking {
                compiler(script, scriptCompilationConfiguration).onSuccess {
                    compiledScript = it
                    evaluator(it, scriptEvaluationConfiguration)
                }.throwOnFailure()
            }
        }.lines()
        assertEquals(expectedOutput, output)
        assertEquals(1, cache.storedScripts)
        assertEquals(0, cache.retrievedScripts)

        if (checkDirectEval) {
            val cachedScript = cache.get(script, scriptCompilationConfiguration)
            assertNotNull(cachedScript)
            assertEquals(1, cache.retrievedScripts)

            val compiledScriptClassRes = runBlocking { compiledScript!!.getClass(null) }
            val cachedScriptClassRes = runBlocking { cachedScript.getClass(null) }

            val compiledScriptClass = compiledScriptClassRes.valueOrThrow()
            val cachedScriptClass = cachedScriptClassRes.valueOrThrow()

            assertEquals(compiledScriptClass.qualifiedName, cachedScriptClass.qualifiedName)
            assertEquals(compiledScriptClass.java.supertypes(), cachedScriptClass.java.supertypes())

            val output2 = captureOut {
                runBlocking {
                    evaluator(cachedScript, scriptEvaluationConfiguration).throwOnFailure()
                }
            }.lines()
            assertEquals(output, output2)
        }

        val output3 = captureOut {
            host.eval(script, scriptCompilationConfiguration, scriptEvaluationConfiguration).throwOnFailure()
        }.lines()
        assertEquals(if (checkDirectEval) 2 else 1, cache.retrievedScripts)
        assertEquals(output, output3)
    }
}

object Implicit

private interface ScriptingCacheWithCounters : CompiledJvmScriptsCache {

    val storedScripts: Int
    val retrievedScripts: Int
}

private class SimpleMemoryScriptsCache : ScriptingCacheWithCounters {

    internal val data = hashMapOf<Pair<SourceCode, Map<*, *>>, CompiledScript>()

    private var _storedScripts = 0
    private var _retrievedScripts = 0

    override val storedScripts: Int
        get() = _storedScripts

    override val retrievedScripts: Int
        get() = _retrievedScripts

    override fun get(script: SourceCode, scriptCompilationConfiguration: ScriptCompilationConfiguration): CompiledScript? =
        data[script to scriptCompilationConfiguration.notTransientData]?.also { _retrievedScripts++ }

    override fun store(
        compiledScript: CompiledScript,
        script: SourceCode,
        scriptCompilationConfiguration: ScriptCompilationConfiguration
    ) {
        data[script to scriptCompilationConfiguration.notTransientData] = compiledScript
        _storedScripts++
    }
}

private class FileBasedScriptCache(val baseDir: File) : ScriptingCacheWithCounters {

    override fun get(script: SourceCode, scriptCompilationConfiguration: ScriptCompilationConfiguration): CompiledScript? {
        val file = File(baseDir, uniqueScriptHash(script, scriptCompilationConfiguration))
        return if (!file.exists()) null else file.readCompiledScript().also { retrievedScripts++ }
    }

    override fun store(
        compiledScript: CompiledScript,
        script: SourceCode,
        scriptCompilationConfiguration: ScriptCompilationConfiguration
    ) {
        val file = File(baseDir, uniqueScriptHash(script, scriptCompilationConfiguration))
        file.outputStream().use { fs ->
            ObjectOutputStream(fs).use { os ->
                os.writeObject(compiledScript)
            }
        }
        storedScripts++
    }

    override var storedScripts: Int = 0
        private set

    override var retrievedScripts: Int = 0
        private set
}

class TestCompiledScriptJarsCache(val baseDir: File) :
    CompiledScriptJarsCache(
        { script, scriptCompilationConfiguration ->
            File(baseDir, uniqueScriptHash(script, scriptCompilationConfiguration) + ".jar")
        }
    ), ScriptingCacheWithCounters {

    override fun get(script: SourceCode, scriptCompilationConfiguration: ScriptCompilationConfiguration): CompiledScript? =
        super.get(script, scriptCompilationConfiguration)?.also { retrievedScripts++ }

    override fun store(
        compiledScript: CompiledScript,
        script: SourceCode,
        scriptCompilationConfiguration: ScriptCompilationConfiguration
    ) {
        super.store(compiledScript, script, scriptCompilationConfiguration).also { storedScripts++ }
    }

    override var storedScripts: Int = 0
        private set

    override var retrievedScripts: Int = 0
        private set
}

internal fun uniqueScriptHash(script: SourceCode, scriptCompilationConfiguration: ScriptCompilationConfiguration): String {
    val digestWrapper = MessageDigest.getInstance("MD5")
    digestWrapper.update(script.text.toByteArray())
    scriptCompilationConfiguration.notTransientData.entries
        .sortedBy { it.key.name }
        .forEach {
            digestWrapper.update(it.key.name.toByteArray())
            digestWrapper.update(it.value.toString().toByteArray())
        }
    return digestWrapper.digest().toHexString()
}

private fun File.readCompiledScript(): CompiledScript {
    return inputStream().use { fs ->
        ObjectInputStream(fs).use {
            it.readObject() as KJvmCompiledScript
        }
    }
}

private fun ByteArray.toHexString(): String = joinToString("", transform = { "%02x".format(it) })

private fun Class<*>.supertypes(): MutableList<Class<*>> = when {
    superclass == null -> interfaces.toMutableList()
    interfaces.isEmpty() -> mutableListOf(superclass)
    else -> ArrayList<Class<*>>(interfaces.size + 1).apply {
        interfaces.toCollection(this@apply)
        add(superclass)
    }
}

