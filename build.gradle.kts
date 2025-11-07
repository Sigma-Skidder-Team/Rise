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
    implementation("io.netty:netty-handler-proxy:4.2.7.Final")
    implementation("io.netty:netty-codec-socks:4.2.7.Final")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("org.java-websocket:Java-WebSocket:1.6.0")
}

kotlin {
    jvmToolchain(21)
}