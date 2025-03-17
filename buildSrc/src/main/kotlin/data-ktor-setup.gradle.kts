import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()
plugins {
    /*
        Градл умный: мы можем в одном модуле юзать
        data-ktor и data-settings, несмотря на то, что
        они оба юзают `logic-internal-setup`
     */
    id("logic-internal-setup")
    kotlin("plugin.serialization")
}


kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(libs.kotlinx.serialization.core)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}