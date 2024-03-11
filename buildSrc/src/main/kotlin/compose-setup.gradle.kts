plugins {
    id("multiplatform-setup")
    id("org.jetbrains.compose")

}

kotlin {
//    configurations.configureEach {
//        resolutionStrategy.eachDependency {
//            if (requested.group == "androidx.collection" && requested.name.startsWith("collection")) {
//                useTarget("androidx.collection:collection:1.4.0")
//            }
//            else if (requested.group == "androidx.annotation" && requested.name.startsWith("annotation")) {
//                useTarget("androidx.annotation:annotation:1.7.1")
//            }
//        }
//    }
//    configurations.configureEach {
//        resolutionStrategy.eachDependency {
//            if (requested.group == "androidx.collection" && requested.name.startsWith("collection")) {
//                useTarget("org.jetbrains.compose.collection-internal:collection:1.6.0-beta02")
//            } else if (requested.group == "androidx.annotation" && requested.name.startsWith("annotation")) {
//                useTarget("org.jetbrains.compose.annotation-internal:annotation:1.8.0-alpha01")
//            }
//        }
//    }
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
//            implementation("org.jetbrains.compose.annotation-internal:annotation:1.6.0-beta02")
        }
        jvmMain.dependencies {

            implementation(compose.desktop.common)
            implementation(compose.desktop.currentOs)

        }
        iosMain.dependencies {
//            api("androidx.collection:collection:1.4.0")
//            api("androidx.annotation:annotation:1.7.1")
//                api("androidx.collection:collection-iosarm64:1.4.0")
//                api("androidx.annotation:annotation-iosarm64:1.7.1")
        }
    }
}

