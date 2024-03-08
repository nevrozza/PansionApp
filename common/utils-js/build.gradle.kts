plugins {
    id("browser-setup")
//    id("dev.icerock.mobile.multiplatform-resources")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":common:utils"))
                implementation(Deps.Decompose.decompose)
                implementation(project(":common:core"))
            }
        }
    }
}