plugins {
    id("multiplatform-setup")
//    id("android-setup")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":common:settings:api"))
                implementation(project(":common:core"))
                implementation(project(":common:utils"))
                implementation(project(":common:ktor"))

                implementation(libs.kodein.di)
                implementation(libs.settings.core)
            }
        }
    }
}