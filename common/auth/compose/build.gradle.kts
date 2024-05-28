plugins {
//    id("android-setup")
    id("compose-setup")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {

                api(project(":common:auth:presentation"))
                implementation(project(":common:settings:compose"))
                implementation(project(":common:core"))

                //implementation(project(":common:utils-compose"))

                //implementation(Deps.Moko.Resources.compose)
                implementation(project(":common:utils"))
                implementation(project(":common:utils-compose"))

                implementation(libs.decompose.core)
                implementation(libs.decompose.compose)
//                implementation(libs.moko.resources.compose)
//                implementation(libs.moko.graphics)
//                implementation(libs.moko.resources.core)
            }
        }
    }
}