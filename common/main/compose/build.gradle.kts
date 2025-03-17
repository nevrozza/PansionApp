plugins {
    id("compose-setup")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":common:main:presentation"))
                api(project(":common:journal:presentation"))
                api(project(":common:journal:compose"))
            }
        }
    }
}