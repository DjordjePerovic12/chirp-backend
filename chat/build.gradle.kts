plugins {
    id("java-library")
    id("chirp.spring-boot-service")
    kotlin("plugin.jpa")
}

group = "llc.bokadev"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.common)
    testImplementation(kotlin("test"))

    implementation(libs.spring.boot.starter.data.jpa)
    runtimeOnly(libs.postgresql)

    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.amqp)
    implementation(libs.spring.boot.starter.websocket)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}