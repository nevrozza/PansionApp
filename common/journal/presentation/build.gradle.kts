plugins {
    id("presentation-setup")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":common:auth:api"))
                api(project(":common:journal:api"))
                api(project(":common:settings:api"))
            }
        }
    }
}