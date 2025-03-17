import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

val libs = the<LibrariesForLibs>()
plugins {
    kotlin("multiplatform")
    id("com.android.library")
//    id("com.google.devtools.ksp")
}




// source sets
kotlin {
    jvm()
    androidTarget()

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
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
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs() {
        this.binaries.library()
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
        }
    }


    jvmToolchain(Config.Java.intVersion)
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        @Suppress("DEPRECATION")
        kotlinOptions.jvmTarget = Config.Java.stringVersion
    }
}


android {
    namespace = Config.Android.namespace + ".convention"
    compileSdk = Config.Android.compileSdk
    defaultConfig {
        minSdk = Config.Android.minSdk
    }


    sourceSets {
        named("main") {
            res.srcDirs("src/main/res")
        }
    }
}