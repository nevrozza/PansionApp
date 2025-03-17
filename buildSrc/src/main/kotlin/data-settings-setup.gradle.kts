import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()
plugins {
    /*
        Градл умный: мы можем в одном модуле юзать
        data-ktor и data-settings, несмотря на то, что
        они оба юзают `logic-internal-setup`
     */
    id("logic-internal-setup")
}


kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.settings.core)
        }
    }
}