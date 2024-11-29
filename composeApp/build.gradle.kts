@file:Suppress("OPT_IN_USAGE")

import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
//    id("compose-setup")
    id(libs.plugins.android.get().pluginId)
    id(libs.plugins.kotlin.get().pluginId)
    id(libs.plugins.compose.plugin.get().pluginId)
    id(libs.plugins.cocoapods.get().pluginId)
    id(libs.plugins.serialization.get().pluginId)
    id(libs.plugins.compose.compiler.get().pluginId)
//    id("org.jetbrains.kotlin.plugin.compose")
}

version = "1.2.1"

kotlin {
    jvm("jvm")

    applyDefaultHierarchyTemplate()

    androidTarget()
    listOf(
        iosArm64(),
//        iosX64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "ComposeApp"
            isStatic = false
            linkerOpts.add("-lsqlite3")
        }
    }

    cocoapods {
        summary = "PansionApp iOS SDK"
        homepage = "https://google.com"
        ios.deploymentTarget = "16.0"

        framework {
            transitiveExport = false
            baseName = "SharedSDK"

//            export(libs.decompose.core)
//            export("com.arkivanov.essenty:lifecycle:<essenty_version>")
//            export(project(":common:core"))
//
//            export(project(":common:utils-compose"))
//            export(project(":common:utils"))
//
//            export(project(":common:umbrella-core"))
//
//            export(project(":common:settings:api"))
//            export(project(":common:auth:api"))
//            export(project(":common:auth:compose"))
//            export(project(":common:main:compose"))
//            export(project(":common:admin:compose"))
//            export(project(":common:journal:compose"))
        }
    }

    targets.withType<KotlinNativeTarget> {
        binaries {
            all {
                linkerOpts("-lsqlite3")
            }
        }
    }


    js(IR) {
        moduleName = "composeApp"
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
            distribution {
                outputDirectory = file("$projectDir/build/jsDistribution/")
            }
        }

        useCommonJs()
        binaries.executable()
    }
    wasmJs {
        moduleName = "composeApp"
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
            distribution {
                outputDirectory = file("$projectDir/build/wasmJsDistribution/")
            }
        }
        useCommonJs()
        binaries.executable()
    }

    jvmToolchain(17)

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.alexzhirkevich:qrose:1.0.1")


            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material3)
//            implementation(libs.jetbrains.compose.splitpane)

//            implementation(libs.moko.resources.compose)
            implementation(libs.decompose.core)
            implementation(libs.decompose.compose)

            implementation(libs.mvikotlin.core)
            implementation(libs.mvikotlin.main)
            implementation(libs.mvikotlin.coroutines)

            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(compose.components.resources)


            implementation(project(":common:core"))
            implementation(project(":common:ktor"))

            implementation(project(":common:utils-compose"))
            implementation(project(":common:utils"))

            implementation(project(":common:umbrella-core"))

            implementation(project(":common:settings:api"))
            implementation(project(":common:auth:api"))
            implementation(project(":common:auth:compose"))
            implementation(project(":common:main:compose"))
            implementation(project(":common:admin:compose"))
            implementation(project(":common:journal:compose"))
            implementation(project(":common:settings:compose"))
            implementation(project(":common:settings:presentation"))

            implementation(libs.compose.haze.core)
            implementation(libs.compose.haze.materials)

        }

        androidMain.dependencies {
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.activity)
            implementation(libs.androidx.compose.runtime)
            implementation("androidx.fragment:fragment:1.7.0-alpha10")
            implementation("androidx.fragment:fragment-ktx:1.7.0-alpha10")
            implementation("androidx.lifecycle:lifecycle-livedata-core-ktx:2.8.0-alpha02")
        }

        jvmMain.dependencies {
            implementation(project(":server"))
//            implementation(compose.desktop.common)
            implementation(compose.desktop.common)
            implementation(compose.desktop.currentOs)
            implementation("org.jetbrains.jewel:jewel-int-ui-decorated-window:0.12.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")
        }

        jsMain.dependencies {
            implementation("com.benasher44:uuid:0.8.4")
            implementation(project.dependencies.enforcedPlatform("org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:1.0.0-pre.648"))
            implementation("org.jetbrains.kotlin-wrappers:kotlin-browser")
           }

        wasmJsMain.dependencies {
            implementation("com.benasher44:uuid:0.8.4")
        }

//        val iosX64Main by getting
//        val iosArm64Main by getting
//        val iosSimulatorArm64Main by getting
//        val commonMain by getting

        iosMain {
//            dependsOn(commonMain)
//            iosX64Main.dependsOn(this)
//            iosArm64Main.dependsOn(this)
//            iosSimulatorArm64Main.dependsOn(this)
            dependencies {

//                api(libs.decompose.core)
//                api(libs.decompose.compose)
//                api(project(":common:core"))
//
//                api(project(":common:utils-compose"))
//                api(project(":common:utils"))
//
//                api(project(":common:umbrella-core"))
//
//                api(project(":common:settings:api"))
//                api(project(":common:auth:api"))
//                api(project(":common:auth:compose"))
//                api(project(":common:main:compose"))
//                api(project(":common:admin:compose"))
//                api(project(":common:journal:compose"))
            }
        }
    }
}

android {

    sourceSets {
        getByName("main") {
            manifest.srcFile ("src/androidMain/AndroidManifest.xml")
        }
    }

    namespace = "com.nevrozq.pansion.android"
    compileSdk = 34
//    namespace = "com.nevrozq.pansion.android"
//    compileSdk = 34
    defaultConfig {
        applicationId = "com.nevrozq.pansion.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 5
        versionName = version.toString()
    }
    buildFeatures {
        compose = true
    }
//    compose {
//        kotlinCompilerPlugin = "org.jetbrains.kotlin:kotlin-compose-compiler-plugin-embeddable:2.0.21"
//    }
//    composeOptions {
//        kotlinCompilerExtensionVersion = "org.jetbrains.kotlin:kotlin-compose-compiler-plugin-embeddable:2.0.0-RC2"
//    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        release {
            // blah blah
            this.matchingFallbacks.add("release")
        }
        debug {
            // blah blah
            this.matchingFallbacks.add("debug")

        }
    }

//    buildTypes {
//        getByName("release") {
//            isMinifyEnabled = false
//        }
//    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

compose.desktop {
    application {
        mainClass = "Main_desktopKt"
        nativeDistributions {
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Exe
            )

            packageName = "PansionApp"
            packageVersion = version.toString()
            windows {
                menuGroup = "PansionApp"
                upgradeUuid = "f11ae455-b203-4ff9-9a63-e28e6d7a4bdf"
                this.iconFile.set(File("src/jvmMain/resources/favicon.ico"))
            }

            buildTypes.release.proguard {
                obfuscate.set(true)
                isEnabled.set(true)
                configurationFiles.from("src/jvmMain/compose-desktop.pro")
                configurationFiles.from("src/commonMain/wtf.pro")
            }

//            buildTypes.release.proguard {
////                version.set("7.3.2")
//                configurationFiles.from(project.file("compose-desktop.pro"))
//                isEnabled.set(false)
//                obfuscate.set(false)
//            }

        }
    }
}