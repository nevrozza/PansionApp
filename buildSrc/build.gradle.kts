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
}
dependencies {
    implementation(libs.plugin.android)
    implementation(libs.plugin.kotlin)
    implementation(libs.plugin.compose)
    implementation(libs.plugin.serialization)
//    implementation(libs.plugin.sqldelight)
//    implementation(libs.plugin.moko)

    //implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
//    implementation(Deps.Kotlin.gradlePlugin)
//    implementation(Deps.Compose.gradlePlugin)
//    implementation(Deps.Android.gradlePlugin)
//    implementation(Deps.Kotlin.Serialization.gradlePlugin)
//    implementation(Deps.Moko.Resources.gradlePlugin)
}

kotlin {
    sourceSets.getByName("main").kotlin.srcDir("buildSrc/src/main/kotlin")
}
