plugins {
    id("compose-setup")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":common:admin:presentation"))
            }
        }

        jvmMain.dependencies {
            implementation(libs.mpfilepicker)
        }
    }
}