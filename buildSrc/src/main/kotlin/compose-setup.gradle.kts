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

            implementation("dev.chrisbanes.haze:haze:1.2.2")
            implementation("dev.chrisbanes.haze:haze-materials:1.2.2")
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs) {
                exclude(group = "org.jetbrains.compose.material", module = "material")
            }
        }
    }
}