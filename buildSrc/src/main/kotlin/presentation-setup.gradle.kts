import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()
plugins {
    id("logic-internal-setup")
}


kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.decompose.core)
            implementation(libs.mvikotlin.core)
            implementation(libs.mvikotlin.coroutines)
            implementation(libs.kotlinx.coroutines)
        }
    }
}