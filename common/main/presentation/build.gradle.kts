plugins {
    id("presentation-setup")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":common:main:api"))
                implementation(project(":common:journal:presentation"))
            }
        }
    }
}