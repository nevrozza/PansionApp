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
    //Targets
    jvm()
    androidTarget()
    ios()

//        js(IR) {
//        browser()
//        binaries.executable()
//    }
//RIP
//    wasmJs {
//        browser()
////        useCommonJs()
//        binaries.executable()
//    }
    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
        }
    }
    js(IR) {
        browser()
//        useCommonJs()
        binaries.executable()
    }

    //JVM
    jvmToolchain(17)
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}

