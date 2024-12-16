plugins {
    idea
    alias(libs.plugins.cucumberRunner)
    alias(libs.plugins.springConventions)
    alias(libs.plugins.deltaCoverageConventions)
    alias(libs.plugins.kotlinConventions)
    alias(libs.plugins.kotlinSerialization)
}

group = "systems.ajax.codetests"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

cucumber {
    main = "io.cucumber.core.cli.Main"
}

dependencies {
    // Cucumber
    implementation(libs.cucumberCore)
    implementation(libs.cucumberJava)

    // Serialization
    implementation(libs.kotlinJsonserialization)

    // Testrail client
    implementation(project(":src:testrailclient"))

    // Tests
    testImplementation(kotlin("test"))
    testImplementation(libs.cucumberJunit)
    testImplementation(libs.junitJupiter)
    testImplementation(libs.mockk)
    testImplementation(libs.wiremock)
}


// Add gradle properties to the spring properties
tasks.processResources {
    if (System.getenv("GITHUB_ACTIONS").isNullOrBlank()) {
        filesMatching("**/application.yaml") { expand(project.properties) }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.register("allCheck") {
    group = "verification"
    description = "Runs check, deltaCoverage, detektMain, and detektTest tasks"

    dependsOn("check", "deltaCoverage", "detektMain", "detektTest")
}

