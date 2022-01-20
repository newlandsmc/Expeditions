plugins {
    kotlin("jvm") version "1.6.0"
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "me.cookie"
version = ""

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
    compileOnly(files("D:\\coding\\Test Servers\\TimeRewards\\plugins\\CookieCore.jar"))
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    compileOnly("com.vexsoftware:nuvotifier-universal:2.7.2")
    compileOnly("io.papermc.paper:paper-api:1.18.1-R0.1-SNAPSHOT")
}

java {
    withSourcesJar()
    withJavadocJar()

    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks{
    shadowJar{
        archiveClassifier.set("")
        project.configurations.implementation.get().isCanBeResolved = true
        configurations = listOf(project.configurations.implementation.get())
        destinationDirectory.set(file("D:\\coding\\Test Servers\\TimeRewards\\plugins"))
    }
}