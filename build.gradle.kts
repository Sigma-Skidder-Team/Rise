plugins {
    kotlin("jvm") version "2.2.0"
}

group = "test"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("org.glassfish.tyrus:tyrus-client:2.2.1")
    implementation("org.glassfish.tyrus:tyrus-container-grizzly-client:2.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}

kotlin {
    jvmToolchain(17)
}
