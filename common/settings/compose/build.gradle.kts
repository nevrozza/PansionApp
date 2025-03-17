plugins {
    id("compose-setup")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":common:settings:presentation"))
                implementation(project(":common:core"))
            }
        }
        mobileMain.dependencies {
            implementation(libs.qr.kit)
        }
    }
}