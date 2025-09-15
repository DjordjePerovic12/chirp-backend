plugins {
    id("java-library")
    id("org.springframework.boot")
    id("chirp.kotlin-common")
}

group = "llc.bokadev"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}