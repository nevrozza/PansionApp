plugins {
    id(libs.plugins.kotlin.get().pluginId).apply(false)
    id(libs.plugins.android.get().pluginId).apply(false)
    id(libs.plugins.compose.get().pluginId).apply(false)
    id(libs.plugins.cocoapods.get().pluginId).apply(false)
//    id(libs.plugins.moko.get().pluginId).apply(false)
}
allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://packages.jetbrains.team/maven/p/kpm/public/")
        gradlePluginPortal()
        mavenLocal()
    }
}
