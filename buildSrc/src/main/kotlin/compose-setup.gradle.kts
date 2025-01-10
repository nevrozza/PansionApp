plugins {

    id("multiplatform-setup")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material3)

            implementation("dev.chrisbanes.haze:haze:1.1.1")
            implementation("dev.chrisbanes.haze:haze-materials:1.1.1")
        }
        jvmMain.dependencies {
            implementation(compose.desktop.common)
            implementation(compose.desktop.currentOs)
        }
    }
}

