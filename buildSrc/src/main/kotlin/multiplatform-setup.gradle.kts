@file:Suppress("OPT_IN_USAGE")

import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyTemplate
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    id("com.android.library")
    kotlin("multiplatform")
}


android {
    namespace = "com.nevrozq.pansion.android"
    compileSdk = 34
}

kotlin {

    jvm("desktop")

    androidTarget()


    ios()
//    iosX64()
//    iosArm64()
//    iosSimulatorArm64()

    js(IR) {
        browser()
        binaries.executable()
    }

//    wasmJs {
//        browser()
//        useCommonJs()
//        binaries.executable()
//    }

    jvmToolchain(17)

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}

