// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.android.application) apply false
}

buildscript {
    val kotlinVersion by extra("1.9.20") // Sintaxe Kotlin
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.20")
        classpath(kotlin("gradle-plugin", kotlinVersion))
    }
    repositories {
        mavenCentral();
    }
}


