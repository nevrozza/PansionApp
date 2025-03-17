import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()
plugins {
    id("multiplatform-setup")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            runtimeOnly(compose.runtime)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(libs.compose.haze.core)
            implementation(libs.compose.haze.materials)
            implementation(libs.decompose.compose)
            implementation(libs.decompose.core)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs) {
                exclude(group = "org.jetbrains.compose.material", module = "material")
            }
        }
    }
}