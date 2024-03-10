plugins {
    id("multiplatform-setup")
    id("org.jetbrains.compose")

}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)

        }
        jvmMain.dependencies {

            implementation(compose.desktop.common)

        }
        iosMain.dependencies {
//            api("androidx.collection:collection:1.4.0")
//            api("androidx.annotation:annotation:1.7.1")
//                api("androidx.collection:collection-iosarm64:1.4.0")
//                api("androidx.annotation:annotation-iosarm64:1.7.1")
        }
    }
}

