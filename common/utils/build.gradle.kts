plugins {
    id("multiplatform-setup")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.decompose.core)
            implementation(libs.mvikotlin.core)
            implementation(libs.mvikotlin.coroutines)

            implementation(libs.kotlinx.coroutines)
        }
    }
}