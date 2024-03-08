plugins {
    id("multiplatform-setup")
//    id("android-setup")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":common:settings:api"))
                implementation(project(":common:core"))
                implementation(project(":common:utils"))
                implementation(libs.decompose.core)
            }
        }
    }
}