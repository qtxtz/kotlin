plugins {
    kotlin("jvm")
    id("project-tests-convention")
    id("test-inputs-check")
}

dependencies {
    api(kotlinStdlib())

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
}

sourceSets {
    "main" { projectDefault() }
    "test" { projectDefault() }
}

projectTests {
    testTask(jUnitMode = JUnitMode.JUnit5)
}
