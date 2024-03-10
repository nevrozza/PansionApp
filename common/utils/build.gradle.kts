plugins {
//    id("browser-setup")
//    id("android-setup")
    id("multiplatform-setup")
//    id(libs.plugins.moko.get().pluginId)
}

kotlin {

    sourceSets {
        commonMain.dependencies {
//            api(libs.moko.resources.core)
            implementation(libs.decompose.core)
//            implementation(libs.decompose.compose)
            implementation(libs.kotlinx.coroutines)
            implementation(libs.mvikotlin.core)
            implementation(libs.mvikotlin.coroutines)

            implementation("com.arkivanov.mvikotlin:rx:3.3.0")

        }

    }
//        val androidMain by getting {
//            dependsOn(commonMain)
//        }
//        val jvmMain by getting {
//            dependsOn(commonMain)
//        }
//        val jsMain by getting {
//            dependsOn(commonMain)
//        }
//        val iosMain by getting {
//            dependsOn(commonMain)
//        }

}


//multiplatformResources {
////    multiplatformResourcesPackage = "com.nevrozq.pansion.common.utils"
//    resourcesPackage.set("com.nevrozq.pansion.common.utils")
//}