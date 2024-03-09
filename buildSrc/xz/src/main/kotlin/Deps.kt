//object Deps {
//    const val webVer = "1.0.0-pre.648"
//    object Kodein {
//        const val core = "org.kodein.di:kodein-di:7.20.2"
//    }
//
//    object Settings {
//        const val core = "com.russhwolf:multiplatform-settings:1.0.0"
//        const val noargs = "com.russhwolf:multiplatform-settings-no-arg:1.0.0"
//    }
//
//    object Kotlin {
//        private const val version = "1.9.22" //or 1.9.20
//        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
//
//        object Serialization {
//            const val gradlePlugin = "org.jetbrains.kotlin:kotlin-serialization:$version"
//            const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.0"
//        }
//
//        object Coroutines {
//            private const val version = "1.7.3"
//            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
//        }
//
//        object DateTime {
//            private const val version = "0.4.1"
//            const val dateTime = "org.jetbrains.kotlinx:kotlinx-datetime:$version"
//        }
//    }
//
//    object Compose {
//        private const val version = "1.6.0-rc02"
//        const val gradlePlugin = "org.jetbrains.compose:compose-gradle-plugin:$version"
//        const val splitPane = "org.jetbrains.compose.components:components-splitpane:$version"
//    }
//
//    object Ktor {
//        private const val version = "2.3.4"
//        const val core = "io.ktor:ktor-client-core:$version"
//        const val json = "io.ktor:ktor-client-json:$version"
//        const val serialization = "io.ktor:ktor-client-serialization:$version"
//        const val logging = "io.ktor:ktor-client-logging:$version"
//        const val negotiation = "io.ktor:ktor-client-content-negotiation:$version"
//        const val kotlinx_json = "io.ktor:ktor-serialization-kotlinx-json:$version"
//        const val android = "io.ktor:ktor-client-android:$version"
//        const val ios = "io.ktor:ktor-client-ios:$version"
//        const val okhttp = "io.ktor:ktor-client-okhttp:$version"
//        const val js = "io.ktor:ktor-client-js:$version"
//    }
//
//    object Moko {
//        object Resources {
//            private const val version = "0.24.0-alpha-4"
//
//            const val gradlePlugin = "dev.icerock.moko:resources-generator:$version"
//            const val res = "dev.icerock.moko:resources:$version"
//            const val compose = "dev.icerock.moko:resources-compose:$version"
//            const val graphics = "dev.icerock.moko:graphics:0.9.0"
//        }
//    }
//
//    object SqlDelight {
//        private const val version = "2.0.0"
//        const val android = "app.cash.sqldelight:android-driver:$version"
//        const val ios = "app.cash.sqldelight:native-driver:$version"
//        const val desktop = "app.cash.sqldelight:sqlite-driver:$version"
//        const val web = "app.cash.sqldelight:web-worker-driver:$version"
//    }
//
//    object Android {
//        const val compilerVer = "1.5.8"
//        private const val version = "8.1.2"
//        const val gradlePlugin = "com.android.tools.build:gradle:$version"
//        const val composeActivity = "androidx.activity:activity-compose:1.7.0"
//        const val appCompat = "androidx.appcompat:appcompat:1.6.1"
//        const val runtime = "androidx.compose.runtime:runtime:1.6.1"
//        private const val composeVer = "1.6.1"
//        object Compose {
//            const val ui = "androidx.compose.ui:ui:$composeVer"
//
//            const val material3 = "androidx.compose.material3:material3:1.2.0"
//            const val icons  = "androidx.compose.material:material-icons-core:$composeVer" //*TODO*
//            const val tooling  = "androidx.compose.ui:ui-tooling:$composeVer"
//
//        }
//    }
//    object Decompose {
//        private const val version = "2.1.2"
//        const val decompose = "com.arkivanov.decompose:decompose:$version"
//        const val androidCompose = "com.arkivanov.decompose:extensions-compose-jetpack:$version"
//        const val compose = "com.arkivanov.decompose:extensions-compose-jetbrains:$version"
//    }
//    object MVIKotlin {
//        private const val version = "3.3.0"
//        const val mvikotlin = "com.arkivanov.mvikotlin:mvikotlin:$version"
//        const val mvikotlinMain = "com.arkivanov.mvikotlin:mvikotlin-main:$version"
//        const val mvikotlinExtensionsCoroutines = "com.arkivanov.mvikotlin:mvikotlin-extensions-coroutines:$version"
//    }
//
//}