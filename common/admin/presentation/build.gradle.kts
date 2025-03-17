plugins {
    id("presentation-setup")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":common:admin:api"))
                api(project(":common:main:api"))
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.excelkt)
                // PROGUARD FIX
                implementation(libs.asm)
            }
        }
    }
}