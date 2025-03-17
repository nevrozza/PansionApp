plugins {
    id("data-ktor-setup")
    id("data-settings-setup")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":common:auth:api"))
            }
        }
    }
}