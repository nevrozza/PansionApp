import io.ktor.plugin.features.DockerPortMapping
import io.ktor.plugin.features.DockerPortMappingProtocol

val ktorV = "3.0.0-beta-2-eap-912"
plugins {
    kotlin("multiplatform")
    id("io.ktor.plugin") version "3.0.0-beta-1"
    id(libs.plugins.serialization.get().pluginId)
}

group = "com.nevrozq.pansion"
version = "1.1.1"
application {
    mainClass.set("com.nevrozq.pansion.ApplicationKt") //com.nevrozq

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

ktor {
    docker {
        jreVersion.set(JavaVersion.VERSION_21)
        localImageName.set("pansionApp-docker-image")
        imageTag.set("1.1.11")

        portMappings.set(
            listOf(
                DockerPortMapping(outsideDocker = 80, insideDocker = 8080, DockerPortMappingProtocol.TCP)
            )
        )
    }


    fatJar {
        archiveFileName.set("fat.jar")
    }
}

repositories {

    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    mavenLocal()
    gradlePluginPortal()
    maven("https://packages.jetbrains.team/maven/p/kpm/public/")
    maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
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
                implementation("io.ktor:ktor-server-cors-jvm:$ktorV")
                implementation("io.ktor:ktor-network-tls-certificates:$ktorV")
                implementation("io.ktor:ktor-server-http-redirect:$ktorV")
                implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorV")
                implementation("io.ktor:ktor-server-core-jvm:$ktorV")
                implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorV")
//                implementation("io.ktor:ktor-serialization-kotlinx-gson-jvm:2.3.5")
                implementation("io.ktor:ktor-server-netty-jvm:$ktorV")
                implementation("ch.qos.logback:logback-classic:1.4.11")


                implementation("org.postgresql:postgresql:42.5.1")

                api("org.jetbrains.exposed:exposed-core:0.44.1")
                implementation("org.jetbrains.exposed:exposed-dao:0.44.1")
                implementation("org.jetbrains.exposed:exposed-jdbc:0.44.1")

                implementation(libs.kotlinx.datetime)

                implementation("org.mindrot:jbcrypt:0.4")
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
