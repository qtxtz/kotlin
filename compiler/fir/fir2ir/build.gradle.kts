plugins {
    kotlin("jvm")
    id("jps-compatible")
    id("java-test-fixtures")
}

dependencies {
    compileOnly(project(":core:descriptors"))
    compileOnly(project(":core:descriptors.jvm"))
    compileOnly(project(":compiler:fir:cones"))
    compileOnly(project(":compiler:fir:resolve"))
    compileOnly(project(":compiler:fir:providers"))
    compileOnly(project(":compiler:fir:semantics"))
    compileOnly(project(":compiler:fir:tree"))
    compileOnly(project(":compiler:ir.tree"))
    compileOnly(project(":compiler:ir.backend.common"))
    compileOnly(project(":compiler:ir.serialization.common"))
    compileOnly(project(":compiler:fir:fir-serialization"))
    compileOnly(project(":compiler:fir:fir-deserialization"))

    compileOnly(intellijCore())

    testCompileOnly(kotlinTest("junit"))
    testFixturesApi(testFixtures(project(":compiler:test-infrastructure")))
    testFixturesApi(testFixtures(project(":compiler:test-infrastructure-utils")))
    testFixturesApi(testFixtures(project(":compiler:tests-compiler-utils")))
    testFixturesApi(testFixtures(project(":compiler:tests-common-new")))
    testFixturesApi(testFixtures(project(":compiler:fir:analysis-tests")))

    testApi(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)

    testRuntimeOnly(project(":core:deserialization"))
    testRuntimeOnly(project(":core:descriptors.runtime"))
    testRuntimeOnly(project(":core:descriptors.jvm"))
    testRuntimeOnly(project(":compiler:fir:fir2ir:jvm-backend"))
    testRuntimeOnly(project(":kotlin-util-klib-abi"))
    testRuntimeOnly(project(":generators"))

    testCompileOnly(intellijCore())
    testRuntimeOnly(intellijCore())

    testRuntimeOnly(toolsJar())
    testRuntimeOnly(commonDependency("org.jetbrains.intellij.deps.jna:jna"))
    testRuntimeOnly(libs.intellij.fastutil)
    testRuntimeOnly(commonDependency("one.util:streamex"))

    testRuntimeOnly(jpsModel())
    testRuntimeOnly(jpsModelImpl())
}

optInToObsoleteDescriptorBasedAPI()

sourceSets {
    "main" { projectDefault() }
    "test" { generatedTestDir() }
    "testFixtures" { projectDefault() }
}

fun Test.configure(configureJUnit: JUnitPlatformOptions.() -> Unit = {}) {
    dependsOn(":dist")
    workingDir = rootDir
    useJUnitPlatform {
        configureJUnit()
    }
}

projectTest(
    jUnitMode = JUnitMode.JUnit5,
    defineJDKEnvVariables = listOf(JdkMajorVersion.JDK_1_8, JdkMajorVersion.JDK_11_0, JdkMajorVersion.JDK_17_0, JdkMajorVersion.JDK_21_0)
) {
    configure()
}

if (kotlinBuildProperties.isTeamcityBuild) {
    projectTest("aggregateTests", jUnitMode = JUnitMode.JUnit5) {
        configure {
            excludeTags("FirPsiCodegenTest")
        }
    }

    projectTest("nightlyTests", jUnitMode = JUnitMode.JUnit5) {
        configure {
            includeTags("FirPsiCodegenTest")
        }
    }
} else {
    /*
     * There is no much sense in those configurations in the local development
     * They actually reduce the UX of running tests, as IDEA suggests choosing one of three
     *   test tasks when you run any test
     * So to fix this inconvenience in local environment, those
     *   tasks just do nothing (and not inherit TestTask), so the IDEA won't see them
    */
    tasks.register("aggregateTests")
    tasks.register("nightlyTests")
}

testsJarToBeUsedAlongWithFixtures()
