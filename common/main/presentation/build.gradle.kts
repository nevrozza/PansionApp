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
                api(project(":common:main:api"))
//                api(project(":common:settings:api"))

                implementation(project(":common:utils"))
                implementation(project(":common:journal:presentation"))
                implementation(libs.decompose.core)
                implementation(libs.mvikotlin.core)
                implementation(libs.mvikotlin.coroutines)


            }
        }
    }
}