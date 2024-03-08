plugins {
//    id("android-setup")
    id("multiplatform-setup")
    id(libs.plugins.serialization.get().pluginId)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":common:core"))
//                implementation(project(":common:launch:api"))
                implementation(project(":common:auth:data"))
                implementation(project(":common:admin:data"))
                implementation(project(":common:main:data"))
                implementation(project(":common:auth:presentation"))
                implementation(project(":common:main:presentation"))
                implementation(project(":common:admin:presentation"))
                implementation(project(":common:journal:presentation"))
                implementation(project(":common:utils"))
//                implementation(project(":common:utils-screens:presentation"))
//                implementation(project(":common:launch:presentation"))
                implementation(project(":common:settings:data"))
                implementation(libs.decompose.core)
                implementation(libs.kodein.di)
                implementation(libs.mvikotlin.core)
                implementation(libs.mvikotlin.coroutines)
            }
        }

    }
}