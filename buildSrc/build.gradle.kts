plugins {
//    id("dev.icerock.mobile.multiplatform-resources").version("0.24.0-alpha-4")
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    mavenLocal()
    gradlePluginPortal()
    maven("https://packages.jetbrains.team/maven/p/kpm/public/")
    maven("https://jitpack.io")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}
dependencies {
    implementation(libs.plugin.android)
    implementation(libs.plugin.kotlin)
    implementation(libs.plugin.compose)
    implementation(libs.plugin.compose.compiler)
    implementation(libs.plugin.serialization)
}

kotlin {
    sourceSets.getByName("main").kotlin.srcDir("buildSrc/src/main/kotlin")
}
