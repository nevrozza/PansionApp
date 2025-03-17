plugins {
    id("compose-common-setup")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":common:utils-compose"))
        }
    }
}