import io.ktor.plugin.features.DockerPortMapping
import io.ktor.plugin.features.DockerPortMappingProtocol

val ktorV = "3.0.1"
plugins {
    kotlin("multiplatform")
    id("io.ktor.plugin") version "3.0.1"
    id(libs.plugins.serialization.get().pluginId)
}

group = "com.nevrozq.pansion"
version = Config.Application.version
application {
    mainClass.set("com.nevrozq.pansion.ApplicationKt") //com.nevrozq

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

ktor {
    docker {
        jreVersion.set(JavaVersion.VERSION_17)
        localImageName.set("pansionApp-docker-image")
        imageTag.set(Config.Application.version)

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

    //noinspection UseTomlInstead
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
    }

}
