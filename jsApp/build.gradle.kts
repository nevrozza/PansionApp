import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

//
//plugins {
//    kotlin("js")
//}
//
//dependencies {
////    implementation(project(":shared"))
//}
//
//kotlin {
//    js(IR) {
//        browser()
//        binaries.library()
//        binaries.executable()
//    }
//
//}
//import com.android.build.gradle.internal.setupTaskName
//
plugins {
//    id("org.jetbrains.kotlin.js")
    kotlin("multiplatform")
//    id("dev.icerock.mobile.multiplatform-resources")
//    id("browser-setup")
}


kotlin {

    js() {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
        binaries.library()
        binaries.executable()
    }
    sourceSets {
//        val commonMain by getting
        val jsMain by getting{
//            dependsOn(commonMain)
            dependencies {
//                api(Deps.Moko.Resources.res)

                implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.0.0"))
                implementation(npm("sql.js", "1.8.0"))
                implementation(npm("enhanced-resolve", "5.15.0"))
                implementation(project(":common:core"))
                //implementation(project(":common:utils-compose"))
                implementation(project(":common:utils"))
                implementation(project(":common:utils-js"))
                //implementation(project(":common:auth:compose"))
                //implementation(project(":common:settings:compose"))
//                implementation(project(":common:utils-screens:js"))
                implementation(project(":common:auth:js"))
                implementation(project(":common:umbrella-core"))
                implementation(Deps.MVIKotlin.mvikotlinMain)
                implementation(Deps.MVIKotlin.mvikotlin)
                implementation(Deps.MVIKotlin.mvikotlinExtensionsCoroutines)
                // implementation(project(":common:umbrella-compose"))

                //implementation(Dependencies.Decompose.compose)
                implementation(Deps.Decompose.decompose)
                //implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")

                implementation("org.jetbrains.kotlin-wrappers:kotlin-react")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-styled")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-css")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-mui-system") //mui-system
                implementation(project.dependencies.enforcedPlatform("org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:${Deps.webVer}"))
            }
        }
    }
}

//
//
//
////kotlin {
////    sourceSets {
////        jsMain {
////
////        }
////
////    }
////}
//
////kotlin {
////    sourceSets {
////        commonMain {
////            dependencies {
////
////                implementation(project(":common:main:presentation"))
//////                implementation(project(":common:chat:web"))
//////                implementation(project(":common:chat:presentation"))
////                implementation(project(":common:core"))
////
////                //implementation(project(":common:utils-compose"))
////
////                //implementation(Dependencies.Moko.Resources.compose)
////                implementation(project(":common:utils"))
////                //implementation(project(":common:utils-compose"))
////
////                implementation(Dependencies.Decompose.decompose)
////            }
////        }
////    }
////}