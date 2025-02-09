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
        browser()
//        useEsModules()
        useCommonJs()
        binaries.executable()
    }
    wasmJs {
        browser()
        useCommonJs()
        binaries.executable()
    }

//        js(IR) {
//        browser()
//        binaries.executable()
//    }
//RIP

    sourceSets {
        commonMain.dependencies {
            //noinspection UseTomlInstead
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
        }
        jsMain.dependencies {

            implementation(npm("copy-webpack-plugin", "9.1.0"))
//            implementation(devNpm("node-polyfill-webpack-plugin", "^2.0.1"))
//            implementation(devNpm("path-browserify", "^1.0.1"))
//            implementation(npm("os-browserify", "^0.3.0"))
        }
//        wasmJsMain.dependencies {
//            implementation(devNpm("node-polyfill-webpack-plugin", "^2.0.1"))
//            implementation(devNpm("path-browserify", "^1.0.1"))
//            implementation(npm("os-browserify", "^0.3.0"))
//        }
    }

    //JVM
    jvmToolchain(17)
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}

