plugins {
//    id("android-setup")
    id("compose-setup")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":common:settings:presentation"))
                implementation(project(":common:utils-compose"))
                implementation(project(":common:core"))

                //implementation(Deps.Moko.Resources.compose)
                implementation(project(":common:utils"))

                implementation(libs.decompose.core)
                implementation(libs.decompose.compose)
            }
        }
    }
}