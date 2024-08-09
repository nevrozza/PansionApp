plugins {
    id("multiplatform-setup")
//    id("android-setup")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":common:utils"))
                implementation(project(":common:ktor"))
//                implementation(Deps.Decompose.decompose)
            }
        }
    }
}