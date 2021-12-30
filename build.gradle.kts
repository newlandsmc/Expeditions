plugins {
    kotlin("jvm") version "1.5.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    java
}

group = "me.cookie"
version = "1.0"

repositories {
    mavenCentral()
    maven {
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
    maven {
        url = uri("https://nexus.bencodez.com/repository/maven-public/") // some guy's repo that has NuVotifier
        // Because the GitHub one doesnt work
    }
}

dependencies {
    compileOnly(kotlin("stdlib"))
    //compileOnly(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    compileOnly(files("/home/cookie/TestServers/sudoyou/plugins/CookieCore-1.0-all.jar"))
    compileOnly("com.vexsoftware:nuvotifier-universal:2.7.2")
    compileOnly("io.papermc.paper:paper-api:1.18.1-R0.1-SNAPSHOT")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    destinationDirectory.set(file("/home/cookie/TestServers/sudoyou/plugins/"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}