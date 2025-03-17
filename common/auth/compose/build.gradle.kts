plugins {
    id("compose-setup")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {

                api(project(":common:auth:presentation"))
                implementation(project(":common:settings:compose"))
                implementation(libs.qrose)
            }
        }
    }
}