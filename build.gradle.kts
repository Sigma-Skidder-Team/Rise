plugins {
    kotlin("jvm") version "2.2.0"
    id("org.jetbrains.dokka") version "2.1.0"
    `maven-publish`
}

apply(plugin = "maven-publish")

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

tasks.register<Jar>("sourcesJar") {
    from(sourceSets["main"].allSource)
    archiveClassifier.set("sources")
}

tasks.register<Jar>("dokkaJar") {
    from(tasks.dokkaHtml)
    archiveClassifier.set("javadoc")
}


publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

kotlin {
    jvmToolchain(17)
}
