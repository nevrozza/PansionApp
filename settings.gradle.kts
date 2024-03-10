pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        gradlePluginPortal()
        mavenLocal()
        maven("https://packages.jetbrains.team/maven/p/kpm/public/")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        mavenLocal()
        maven("https://packages.jetbrains.team/maven/p/kpm/public/")
    }
}

rootProject.name = "Pansion"
//include(":androidApp")
//include(":desktopApp")
//include(":jsApp")
include(":composeApp")
include(":server")

include(":common:auth:api")
include(":common:auth:compose")
//include(":common:auth:js")
include(":common:auth:data")
include(":common:auth:presentation")

include(":common:settings:api")
include(":common:settings:compose")
//include(":common:settings:js")
include(":common:settings:data")
include(":common:settings:presentation")

include(":common:main:api")
include(":common:main:compose")
//include(":common:main:js")
include(":common:main:data")
include(":common:main:presentation")

include(":common:admin:api")
include(":common:admin:compose")
//include(":common:admin:js")
include(":common:admin:data")
include(":common:admin:presentation")

include(":common:journal:api")
include(":common:journal:compose")
//include(":common:journal:js")
include(":common:journal:data")
include(":common:journal:presentation")


include(":common:core")
include(":common:ktor")

include(":common:umbrella-core")
//include(":common:umbrella-compose")
//include(":common:umbrella-ios")

include(":common:utils")
include(":common:utils-compose")
//include(":common:utils-js")