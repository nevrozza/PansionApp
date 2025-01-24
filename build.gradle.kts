plugins {
    id(libs.plugins.kotlin.get().pluginId).apply(false)
//    id(libs.plugins.android.get().pluginId).apply(false)
//    id(libs.plugins.compose.get().pluginId).apply(false)
    id(libs.plugins.cocoapods.get().pluginId).apply(false)
    id(libs.plugins.serialization.get().pluginId).apply(false)
    id(libs.plugins.compose.compiler.get().pluginId) apply false
    id(libs.plugins.compose.plugin.get().pluginId) apply false
//    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0-RC2" apply false
//    id(libs.plugins.compose.compiler.get().pluginId).apply(false)
//    id(libs.plugins.moko.get().pluginId).apply(false)
}
allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
        maven("https://packages.jetbrains.team/maven/p/kpm/public/")
        maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        gradlePluginPortal()
        mavenLocal()
        maven("https://oss.sonatype.org/content/repositories/snapshots")

//
//        flatDir {
//            dirs("libs")
//        }
    }
}




