plugins {
    id("presentation-setup")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":common:settings:api"))
                api(project(":common:auth:api"))
            }
        }
    }
}