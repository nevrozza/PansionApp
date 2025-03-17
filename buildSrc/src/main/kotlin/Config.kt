
@file:Suppress(
//    "MayBeConstant",
    "SpellCheckingInspection",
    "MemberVisibilityCanBePrivate",
    "ConstPropertyName"
)

import org.gradle.api.JavaVersion

object Config {
    object Application {
        const val globalVersion = "1.3.0"
        const val version = "1.3.0-alpha08"
        const val versionCode = 29
    }


    object Android {
        const val namespace = "com.nevrozq.pansion.android"

        const val compileSdk = 35
        const val minSdk = 26
        const val targetSdk = 35
    }
    object Java {
        val version = JavaVersion.VERSION_17
        val stringVersion = version.majorVersion
        val intVersion = stringVersion.toInt()
    }
}