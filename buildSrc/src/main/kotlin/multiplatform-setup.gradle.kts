@file:Suppress("OPT_IN_USAGE")

plugins {
    id("com.android.library")
    kotlin("multiplatform")
}


android {
    namespace = "com.nevrozq.pansion.android"
    compileSdk = 34
    defaultConfig {
        minSdk = 26
    }
}

kotlin {
    //Targets
    jvm()
    androidTarget()

    applyDefaultHierarchyTemplate {
        common {
            group("notJvm") {
                withJs()
                withWasmJs()
                withIos()
                withAndroidTarget()
            }
            group("mobile") {
                withIos()
                withAndroidTarget()
            }
            group("notMobile") {
                withJvm()
                withWasmJs()
                withJs()
            }
            group("web") {
                withJs()
                withWasmJs()
            }
            group("notWeb") {
                withJvm()
                withApple()
                withAndroidTarget()
            }
            group("skiko") {
                withJvm()
                withApple()
                withJs()
                withWasmJs()
            }
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    js(IR) {
        this.binaries.library()
        browser()
    }
    wasmJs() {
        this.binaries.library()
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            //noinspection UseTomlInstead
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
        }
    }

    //JVM
    jvmToolchain(17)
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}

