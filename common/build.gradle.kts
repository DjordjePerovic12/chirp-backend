plugins {
    id("java-library")
    id("chirp.kotlin-common")
}

group = "llc.bokadev"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    api(libs.kotlin.reflect)
    api(libs.jackson.module.kotlin)
    api(libs.jackson.datatype)

    implementation(libs.spring.boot.starter.amqp)
    implementation(libs.spring.boot.starter.security)

    implementation(libs.jwt.api)
    runtimeOnly(libs.jwt.impl)
    runtimeOnly(libs.jwt.jackson)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}