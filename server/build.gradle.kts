plugins {
    kotlin("multiplatform")
    id("io.ktor.plugin") version "2.3.5"
    id(libs.plugins.serialization.get().pluginId)
}

group = "com.nevrozq.pansion"
version = "0.0.1"

application {
    mainClass.set("com.nevrozq.pansion.ApplicationKt") //com.nevrozq

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {

    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    mavenLocal()
    gradlePluginPortal()
    maven("https://packages.jetbrains.team/maven/p/kpm/public/")
}
kotlin {
    jvm {
        withJava()

    }
    sourceSets {
        commonMain {
            dependencies {

                implementation(project(":common:ktor"))


                implementation(project(":common:utils"))

                implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.5")
                implementation("io.ktor:ktor-server-core-jvm:2.3.5")
                implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.5")
                implementation("io.ktor:ktor-server-netty-jvm:2.3.5")
                implementation("ch.qos.logback:logback-classic:1.4.11")


                implementation("org.postgresql:postgresql:42.5.1")

                api("org.jetbrains.exposed:exposed-core:0.44.1")
                implementation("org.jetbrains.exposed:exposed-dao:0.44.1")
                implementation("org.jetbrains.exposed:exposed-jdbc:0.44.1")

                implementation(libs.kotlinx.datetime)
            }
        }
//        jvmTest {
//            this.dependsOn(jvmMain.get())
//            this.impl
//            implementation("io.ktor:ktor-server-tests-jvm")
//            implementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.10")
//        }
    }

}
