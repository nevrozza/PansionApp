plugins {
    id("multiplatform-setup")
//    id("android-setup")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":common:ktor"))
                implementation(libs.decompose.core)
            }
        }
    }
}