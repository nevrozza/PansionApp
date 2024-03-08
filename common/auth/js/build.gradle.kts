plugins {
    id("browser-setup")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {

                api(project(":common:auth:presentation"))
//                implementation(project(":common:auth:compose"))
                ////implementation(":common:settings:presentation")
                implementation(project(":common:core"))

                implementation(project(":common:utils-js"))

//                implementation(Deps.Moko.Resources.res)
                implementation(project(":common:utils"))

                implementation(Deps.Decompose.decompose)

            }
        }
    }
}