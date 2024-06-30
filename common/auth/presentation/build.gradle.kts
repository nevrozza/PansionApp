plugins {
    id("multiplatform-setup")
//    id("android-setup")
    id("kotlin-parcelize")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":common:core"))
                api(project(":common:auth:api"))
                api(project(":common:settings:api"))
                api(project(":common:admin:api"))

                implementation(project(":common:utils"))
                implementation(libs.decompose.core)
                implementation(libs.mvikotlin.core)
                implementation(libs.mvikotlin.coroutines)


            }
        }
    }
}