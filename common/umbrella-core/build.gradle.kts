plugins {
    id("multiplatform-setup")
    id(libs.plugins.serialization.get().pluginId)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":common:core"))

                implementation(project(":common:auth:data"))
                implementation(project(":common:admin:data"))
                implementation(project(":common:main:data"))
                implementation(project(":common:journal:data"))
                implementation(project(":common:settings:data"))

                implementation(project(":common:auth:presentation"))
                implementation(project(":common:main:presentation"))
                implementation(project(":common:admin:presentation"))
                implementation(project(":common:journal:presentation"))
                implementation(project(":common:settings:presentation"))

                implementation(project(":common:utils"))
                implementation(project(":common:ktor"))

                implementation(libs.decompose.core)
                implementation(libs.mvikotlin.core)
                implementation(libs.mvikotlin.coroutines)

                implementation(libs.kotlinx.coroutines)
                implementation(libs.kotlinx.serialization.core)
            }
        }

    }
}