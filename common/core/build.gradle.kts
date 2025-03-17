plugins {
    id("multiplatform-setup")
    id(libs.plugins.serialization.get().pluginId)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":common:auth:api"))
                implementation(project(":common:utils"))

                api(libs.kodein.di)

                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.coroutines)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.json)
                implementation(libs.ktor.client.serialization)
                implementation(libs.ktor.client.negotiation)
                implementation(libs.ktor.client.logging)

                implementation(libs.settings.core)
                implementation(libs.settings.no.arg)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.ktor.client.android)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.ktor.client.ios)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.ktor.client.okhttp)
            }
        }
    }
}