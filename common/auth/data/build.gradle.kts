plugins {
//    id("android-setup")
    id("multiplatform-setup")
    id(libs.plugins.serialization.get().pluginId)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":common:auth:api"))
                implementation(project(":common:core"))

                implementation(libs.kodein.di)
                implementation(libs.settings.core)
            }
        }
    }
}