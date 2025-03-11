plugins {
    id("multiplatform-setup")
//    id("android-setup")
    id("kotlin-parcelize")
    id(libs.plugins.serialization.get().pluginId)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":common:core"))
                api(project(":common:admin:api"))
                api(project(":common:main:api"))
                api(project(":common:ktor"))
//                api(project(":common:settings:api"))

                implementation(project(":common:utils"))
                implementation(libs.decompose.core)
                implementation(libs.mvikotlin.core)
                implementation(libs.mvikotlin.coroutines)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization.core)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.excelkt)
                // PROGUARD FIX
                implementation("org.ow2.asm:asm:9.2")
            }
        }
    }
}