plugins {
    id("compose-common-setup")
}



kotlin {
    sourceSets {
        androidMain {
            dependencies {
//                implementation("androidx.core:core:1.12.0")
                api(libs.androidx.core.ktx)
                implementation(libs.accompanist.systemuicontroller)
                implementation(libs.accompanist.permissions)
            }
        }

        commonMain {
            dependencies {
                api(project(":common:utils"))
                implementation(project(":common:ktor"))
                implementation(project(":common:core"))
                implementation(libs.compose.materialKolor)
                implementation(compose.components.resources)

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