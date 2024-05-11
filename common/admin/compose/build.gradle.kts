plugins {
//    id("android-setup")
    id("compose-setup")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {

                api(project(":common:admin:presentation"))
                ////implementation(":common:settings:presentation")
                implementation(project(":common:core"))

                //implementation(project(":common:utils-compose"))

                //implementation(Deps.Moko.Resources.compose)
                implementation(project(":common:utils"))
                implementation(project(":common:utils-compose"))
                implementation("com.mohamedrejeb.dnd:compose-dnd:0.2.0")
                implementation(libs.kotlinx.datetime)

                implementation(libs.decompose.core)
                implementation(libs.decompose.compose)
//                implementation(Deps.Moko.Resources.compose)
//                implementation(Deps.Moko.Resources.graphics)
//                implementation(Deps.Moko.Resources.res)
            }
        }
    }
}