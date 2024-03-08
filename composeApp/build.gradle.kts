plugins {
//    id("compose-setup")
    id(libs.plugins.android.get().pluginId)
    id(libs.plugins.kotlin.get().pluginId)
    id(libs.plugins.compose.get().pluginId)
}

kotlin {
    jvm("jvm")

    androidTarget()

    ios()

//    iosX64()
//    iosArm64()
//    iosSimulatorArm64()

    js(IR) {
        moduleName = "composeApp"
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
//        browser()
        binaries.executable()
    }

//    wasmJs {
//        moduleName = "composeApp"
//
//        useCommonJs()
//        browser()
//        binaries.executable()
//    }

    jvmToolchain(17)

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
//            implementation(libs.jetbrains.compose.splitpane)

//            implementation(libs.moko.resources.compose)
            implementation(libs.decompose.core)
            implementation(libs.decompose.compose)

                        implementation(libs.mvikotlin.core)
            implementation(libs.mvikotlin.main)
            implementation(libs.mvikotlin.coroutines)

            implementation(libs.kotlinx.datetime)

            implementation(project(":common:umbrella-core"))

            implementation(project(":common:core"))

            implementation(project(":common:utils-compose"))
            implementation(project(":common:utils"))

            implementation(project(":common:umbrella-core"))

            implementation(project(":common:settings:api"))
            implementation(project(":common:auth:api"))
            implementation(project(":common:auth:compose"))
            implementation(project(":common:main:compose"))
            implementation(project(":common:admin:compose"))
            implementation(project(":common:journal:compose"))

        }

        androidMain.dependencies {
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.activity)
            implementation(libs.androidx.compose.runtime)
        }

        jvmMain.dependencies {
            implementation(project(":server"))
//            implementation(compose.desktop.common)
            implementation(compose.desktop.currentOs)
            implementation("org.jetbrains.jewel:jewel-int-ui-decorated-window:0.12.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")
        }
        jsMain.dependencies {
            implementation("org.jetbrains.kotlin-wrappers:kotlin-react")
            implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom")
            implementation("org.jetbrains.kotlin-wrappers:kotlin-styled")
            implementation("org.jetbrains.kotlin-wrappers:kotlin-css")
            implementation("org.jetbrains.kotlin-wrappers:kotlin-mui-system") //mui-system
            implementation(project.dependencies.enforcedPlatform("org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:1.0.0-pre.648"))
        }
    }
}

android {
    namespace = "com.nevrozq.pansion.android"
    compileSdk = 34
//    namespace = "com.nevrozq.pansion.android"
//    compileSdk = 34
    defaultConfig {
        applicationId = "com.nevrozq.pansion.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidx.compiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
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
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
            )

            packageName = "PansionApp"
            packageVersion = "1.0.0"
            windows {
                menuGroup = "PansionApp"
                upgradeUuid = "134213"
            }
        }
    }
}

compose.experimental {
    web.application {}
}