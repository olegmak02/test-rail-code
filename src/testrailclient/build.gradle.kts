plugins {
    java
    id("jacoco")
}

group = "systems.ajax.codetests"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
    implementation("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.1")
    implementation("com.google.guava:guava:33.2.0-jre")
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("org.slf4j:slf4j-simple:2.0.16")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
