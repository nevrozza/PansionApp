plugins {
    id("multiplatform-setup")
    id("kotlin-parcelize")
//    id("android-setup")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":common:settings:api"))
                implementation(project(":common:core"))
                implementation(project(":common:utils"))
                api(project(":common:auth:api"))


                implementation(libs.decompose.core)
                implementation(libs.mvikotlin.core)
                implementation(libs.mvikotlin.coroutines)
            }
        }
    }
}