plugins {
    id("data-ktor-setup")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":common:main:api"))
            }
        }
    }
}