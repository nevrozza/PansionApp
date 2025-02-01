plugins {
//    id("android-setup")
    id("compose-setup")
}



kotlin {
    sourceSets {
        androidMain {
            dependencies {
//                implementation("androidx.core:core:1.12.0")
                api("androidx.core:core-ktx:1.13.0-alpha05")
                implementation("com.google.accompanist:accompanist-systemuicontroller:0.27.0")
                implementation("com.google.accompanist:accompanist-permissions:0.34.0")
            }
        }

        commonMain {
            dependencies {
                implementation(project(":common:utils"))
                implementation(project(":common:ktor"))
                implementation(project(":common:core"))
                implementation(libs.compose.materialKolor)
                implementation(compose.components.resources)
//                implementation(libs.moko.resources.compose)
                implementation(libs.decompose.core)
                implementation(libs.decompose.compose)

                implementation(libs.coil.compose)
                implementation(libs.coil.svg)
                implementation(libs.coil.network.ktor3)
            }
        }
    }
}

compose.resources {
    generateResClass = always
    this.publicResClass = true
    packageOfResClass = "pansion"
}