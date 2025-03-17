plugins {
    id("presentation-setup")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":common:auth:api"))

                implementation(libs.ktor.client.core)
            }
        }
    }
}