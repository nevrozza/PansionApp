plugins {
//    id("android-setup")
//    id("multiplatform-compose-setup")
    id("multiplatform-setup")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":common:core"))

            implementation(project(":common:utils-compose"))
            implementation(project(":common:utils"))

            implementation(project(":common:umbrella-core"))

            implementation(project(":common:settings:api"))
            implementation(project(":common:auth:api"))
            implementation(project(":common:auth:compose"))
            implementation(project(":common:main:compose"))
            implementation(project(":common:admin:compose"))
            implementation(project(":common:journal:compose"))
//                implementation(project(":common:launch:api"))
//                implementation(project(":common:launch:compose"))

//                implementation(project(":common:utils-screens:api"))
//                implementation(project(":common:utils-screens:compose"))

            implementation(Deps.Decompose.decompose)
            implementation(Deps.Kotlin.DateTime.dateTime)

        }
    }

    androidMain.dependencies {
        implementation(Deps.Android.composeActivity)

        implementation("com.google.accompanist:accompanist-systemuicontroller:0.27.0")
    }
}

