plugins {
//    id("android-setup")
    id("compose-setup")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":common:settings:presentation"))
                implementation(project(":common:utils-compose"))
                implementation(project(":common:core"))

                //implementation(Deps.Moko.Resources.compose)
                implementation(project(":common:utils"))

                implementation(libs.decompose.core)
                implementation(libs.decompose.compose)
            }
        }

        iosMain.dependencies {
            implementation("network.chaintech:qr-kit:2.0.0")
        }

        androidMain.dependencies {
            implementation("network.chaintech:qr-kit:2.0.0")
        }
    }
}