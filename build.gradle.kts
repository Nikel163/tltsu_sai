/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin application project to get you started.
 */

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.3.72"

    // Apply the application plugin to add support for building a CLI application.
    application
}

repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
    maven(url = "https://jetbrains.bintray.com/lets-plot-maven")
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    api("org.jetbrains.lets-plot-kotlin:lets-plot-kotlin-api:1.0.0")
    // https://mvnrepository.com/artifact/org.jetbrains.lets-plot/lets-plot-jfx
    compile ("org.jetbrains.lets-plot", "lets-plot-jfx", "1.5.2")

}

application {
    // Define the main class for the application.
    mainClassName = "tltsu_sai.AppKt"
}