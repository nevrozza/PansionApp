plugins {
    id("compose-setup")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":common:journal:presentation"))
            }
        }
    }
}