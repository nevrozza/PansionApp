@file:Suppress("OPT_IN_USAGE")

import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.js.binaryen.BinaryenRootExtension
import org.jetbrains.kotlin.gradle.targets.js.toHex
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import java.security.MessageDigest

plugins {
//    id("compose-setup")
    id(libs.plugins.android.get().pluginId)
    id(libs.plugins.kotlin.get().pluginId)
    id(libs.plugins.compose.plugin.get().pluginId)
    id(libs.plugins.cocoapods.get().pluginId)
    id(libs.plugins.serialization.get().pluginId)
    id(libs.plugins.compose.compiler.get().pluginId)
//    id("org.jetbrains.kotlin.plugin.compose")
}

version = "1.3.0"
val versionAndroidCode = 29

val jsAppName = project.name+"-js"
val wasmAppName = project.name+"-wasm"

kotlin {
    jvm("jvm")

    applyDefaultHierarchyTemplate() {
        common {
            group("web") {
                withJs()
                withWasmJs()
            }
        }
    }

    androidTarget()
    listOf(
        iosArm64(),
//        iosX64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "ComposeApp"
            isStatic = false
            linkerOpts.add("-lsqlite3")
        }
    }

    cocoapods {
        summary = "PansionApp iOS SDK"
        homepage = "https://google.com"
        ios.deploymentTarget = "16.0"

        framework {
            transitiveExport = false
            baseName = "SharedSDK"

//            export(libs.decompose.core)
//            export("com.arkivanov.essenty:lifecycle:<essenty_version>")
//            export(project(":common:core"))
//
//            export(project(":common:utils-compose"))
//            export(project(":common:utils"))
//
//            export(project(":common:umbrella-core"))
//
//            export(project(":common:settings:api"))
//            export(project(":common:auth:api"))
//            export(project(":common:auth:compose"))
//            export(project(":common:main:compose"))
//            export(project(":common:admin:compose"))
//            export(project(":common:journal:compose"))
        }
    }

    targets.withType<KotlinNativeTarget> {
        binaries {
            all {
                linkerOpts("-lsqlite3")
            }
        }
    }
    listOf(
        js(IR) to jsAppName,
        wasmJs() to wasmAppName
    ).forEach { (target, appName) ->

        target.compilations.all {
            kotlinOptions {
                freeCompilerArgs += listOf("-Xir-minimized-member-names")
            }
        }
        target.browser {
            commonWebpackConfig {
                configDirectory = project.projectDir.resolve("webpack.config.d")
                mode = KotlinWebpackConfig.Mode.PRODUCTION
                outputFileName = "$appName.js"
                
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(project.projectDir.path)
                    }
                }
            }
        }
        target.binaries.executable()
    }

    jvmToolchain(17)

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.alexzhirkevich:compottie:2.0.0-rc04")
            implementation("io.github.alexzhirkevich:qrose:1.0.1")


            runtimeOnly(compose.runtime)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material3)
//            implementation(libs.jetbrains.compose.splitpane)

//            implementation(libs.moko.resources.compose)
            implementation(libs.decompose.core)
            implementation(libs.decompose.compose)

            implementation(libs.mvikotlin.core)
            implementation(libs.mvikotlin.main)
            implementation(libs.mvikotlin.coroutines)

            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(compose.components.resources)


            implementation(project(":common:core"))
            implementation(project(":common:ktor"))

            implementation(project(":common:utils-compose"))
            implementation(project(":common:utils"))

            implementation(project(":common:umbrella-core"))

            implementation(project(":common:settings:api"))
            implementation(project(":common:auth:api"))
            implementation(project(":common:auth:compose"))
            implementation(project(":common:main:compose"))
            implementation(project(":common:admin:compose"))
            implementation(project(":common:journal:compose"))
            implementation(project(":common:settings:compose"))
            implementation(project(":common:settings:presentation"))

            implementation(libs.compose.haze.core)
            implementation(libs.compose.haze.materials)

        }

        androidMain.dependencies {
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.activity)
            runtimeOnly(libs.androidx.compose.runtime)
            implementation("androidx.fragment:fragment-ktx:1.8.6")
        }

        jvmMain.dependencies {
//            implementation(project(":server"))
            implementation(compose.desktop.currentOs)  {
                exclude(group = "org.jetbrains.compose.material", module = "material")
            }
            implementation("org.jetbrains.jewel:jewel-int-ui-decorated-window-243:0.27.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")
        }

        jsMain.dependencies {
            implementation("com.benasher44:uuid:0.8.4")
            implementation(project.dependencies.enforcedPlatform("org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:1.0.0-pre.648"))
            implementation("org.jetbrains.kotlin-wrappers:kotlin-browser")
           }

        wasmJsMain.dependencies {
            implementation("com.benasher44:uuid:0.8.4")
        }

//        val iosX64Main by getting
//        val iosArm64Main by getting
//        val iosSimulatorArm64Main by getting
//        val commonMain by getting

        iosMain {
//            dependsOn(commonMain)
//            iosX64Main.dependsOn(this)
//            iosArm64Main.dependsOn(this)
//            iosSimulatorArm64Main.dependsOn(this)
            dependencies {

//                api(libs.decompose.core)
//                api(libs.decompose.compose)
//                api(project(":common:core"))
//
//                api(project(":common:utils-compose"))
//                api(project(":common:utils"))
//
//                api(project(":common:umbrella-core"))
//
//                api(project(":common:settings:api"))
//                api(project(":common:auth:api"))
//                api(project(":common:auth:compose"))
//                api(project(":common:main:compose"))
//                api(project(":common:admin:compose"))
//                api(project(":common:journal:compose"))
            }
        }
    }
}

android {

    sourceSets {
        getByName("main") {
            manifest.srcFile ("src/androidMain/AndroidManifest.xml")
        }
    }

    namespace = "com.nevrozq.pansion.android"
    compileSdk = 35
//    namespace = "com.nevrozq.pansion.android"
//    compileSdk = 34
    defaultConfig {
        applicationId = "com.nevrozq.pansion.android"
        minSdk = 26
        targetSdk = 34
        versionCode = versionAndroidCode
        versionName = version.toString()
    }
    buildFeatures {
        compose = true
    }
//    compose {
//        kotlinCompilerPlugin = "org.jetbrains.kotlin:kotlin-compose-compiler-plugin-embeddable:2.0.21"
//    }
//    composeOptions {
//        kotlinCompilerExtensionVersion = "org.jetbrains.kotlin:kotlin-compose-compiler-plugin-embeddable:2.0.0-RC2"
//    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        release {
            this.matchingFallbacks.add("release")

            this.isMinifyEnabled = true
            proguardFile("proguard-rules.pro")
        }
        debug {
            this.matchingFallbacks.add("debug")
        }
    }

//    buildTypes {
//        getByName("release") {
//            isMinifyEnabled = false
//        }
//    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
compose.desktop {
    application {
        mainClass = "Main_desktopKt"
        nativeDistributions {

            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg,
//                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Pkg,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Exe
            )

            packageName = "PansionApp"
            packageVersion = version.toString()
            windows {
                menuGroup = "PansionApp"
                upgradeUuid = "f11ae455-b203-4ff9-9a63-e28e6d7a4bdf"
                this.iconFile.set(File("src/jvmMain/resources/favicon.ico"))
            }

            macOS {
                this.dockName = "PansionApp"
            }

            buildTypes.release.proguard {
                version.set("7.6.1")
                obfuscate.set(true)
                optimize.set(true)
                isEnabled.set(true)

                configurationFiles.from(
                    "proguard-rules.pro",
                    "src/jvmMain/proguard-excelkt.pro",
                    "src/jvmMain/proguard-jvm.pro"
//                    "src/jvmMain/compose-desktop.pro"
                )
            }

//            buildTypes.release.proguard {
////                version.set("7.3.2")
//                configurationFiles.from(project.file("compose-desktop.pro"))
//                isEnabled.set(false)
//                obfuscate.set(false)
//            }

        }
    }
}


rootProject.configure<BinaryenRootExtension> {
    this.version = "122"
}





/**
./gradlew clean compatBrowserProductionDistribution
./gradlew compatBrowserDevelopmentDistribution
 */

enum class Mode(
    val jsDistTask: String,
    val wasmJsDistTask : String,
) {
    Production(
        jsDistTask = "jsBrowserDistribution",
        wasmJsDistTask = "wasmJsBrowserDistribution",
    ),
    Development(
        jsDistTask = "jsBrowserDevelopmentExecutableDistribution",
        wasmJsDistTask = "wasmJsBrowserDevelopmentExecutableDistribution",
    )
}

Mode.values().forEach {

    val hashTask = registerHashTask(it)

    registerCompatDistTask(it).configure {
        finalizedBy(hashTask)
    }
}

// append scripts for ***run tasks that doesn't perform webpack.
// for webpack tasks these scripts will be removed later
listOf(
    "js" to jsAppName,
    "wasmJs" to wasmAppName
).forEach { (sourceSet, appName) ->
    tasks.named("${sourceSet}ProcessResources").configure {
        try {
            doLast {
                buildDir
                    .resolve("processedResources")
                    .resolve(sourceSet)
                    .resolve("main")
                    .resolve("index.html")
                    .apply {
                        addScriptForTag("$appName.js", false, "body")
                        if (sourceSet == "js") {
                            addScriptForTag("skiko.js", false, "head")
                        }
                    }
            }
        } catch (e: Throwable) {
            println("CHECK: ${e}" )
        }
    }
}

val scriptsToRemove = listOf(
    "skiko.js",
    "$jsAppName.js",
    "$wasmAppName.js"
)

// remove scripts added in processResources phase
fun removeScripts(html : String) : String {
    var text = html

    var idx = 0

    while (idx < html.lastIndex) {
        val start = text.indexOf("<script", idx)
        if (start == -1)
            break
        val end = text.indexOf("</script>", start) + "</script>".length
        val script = text.substring(start, end)

        if (scriptsToRemove.any { it in script }) {
            text = text.substring(0, start) + text.substring(end)
        } else {
            idx = end
        }
    }
    return text
}

// compiles js and wasm bundles, and merges them into compat dir
fun registerCompatDistTask(mode : Mode): TaskProvider<Task> {
    return tasks.register("compatBrowser${mode}Distribution") {
        group = "kotlin browser"

        dependsOn(
            tasks.named(mode.jsDistTask),
            tasks.named(mode.wasmJsDistTask),
        )

        doFirst {
            val distDir = buildDir.resolve("dist")

            val compatDir = distDir
                .resolve("compat")
                .resolve("${mode.name.lowercase()}Executable")

            mkdir(compatDir)

            listOf("js", "wasmJs").forEach {
                copy {
                    from(distDir.resolve(it).resolve("${mode.name.lowercase()}Executable"))
                    into(compatDir.absolutePath)
                    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
                }
            }
        }
    }
}

// appends files hashes and script for wasm gc detection
// https://github.com/GoogleChromeLabs/wasm-feature-detect
fun registerHashTask(mode : Mode) =
    tasks.register("compat${mode}Cache") {
        doFirst {
            val distDir = buildDir
                .resolve("dist")
                .resolve("compat")
                .resolve("${mode.name.lowercase()}Executable")

            require(distDir.exists()) {
                "Project must be assembled before hashing"
            }

            val jsHashedAppFile = distDir.resolve("$jsAppName.js").hashed()
            val wasmHashedAppFile = distDir.resolve("$wasmAppName.js").hashed().apply {
                // Находим все .wasm файлы в папке
                val wasmFiles = distDir.listFiles { file -> file.extension == "wasm" }

                // Выбираем файл с наибольшим размером
                val largestWasmFile = wasmFiles
                    ?.filter { !it.name.contains("skiko", ignoreCase = true) }
                    ?.maxByOrNull { it.totalSpace }

                if (largestWasmFile != null) {
                    // Переименовываем файл в $wasmAppName.wasm
                    val newWasmFile = distDir.resolve("$wasmAppName.wasm")
                    largestWasmFile.renameTo(newWasmFile)

                    // Обновляем ссылку в .js файле
                    writeText(readText().replace(largestWasmFile.name, newWasmFile.name))
                } else {
                    throw IllegalStateException("No .wasm files found in $distDir")
                }
            }
            val skikoWasmHashedFile = distDir.resolve("skiko.wasm").hashed()
            val skikoJsHashedFile = distDir.resolve("skiko.js").apply {
                writeText(readText().replace("skiko.wasm", skikoWasmHashedFile.name))
            }.hashed()

            distDir.resolve("index.html").apply {
                writeText(removeScripts(readText()))
                addScriptForTag(
                    tag = "body",
                    source = true,
                    script = """
                        import { gc } from "https://unpkg.com/wasm-feature-detect?module";
                        const wasmGCSupported = await gc()
                        
                        if (!wasmGCSupported){
                            let skikoScript = document.createElement("script")
                            skikoScript.src = "${skikoJsHashedFile.name}"
                            document.head.appendChild(skikoScript)
                        }
                        
                        let script = document.createElement("script")
                        script.src = wasmGCSupported 
                            ? "${wasmHashedAppFile.name}" 
                            : "${jsHashedAppFile.name}"
                        document.body.appendChild(script)
                     
                    """.trimIndent()
                )
            }
        }
    }


fun File.hashed() : File {
    val hash = MessageDigest.getInstance("md5").let { d ->
        inputStream().use {
            do {
                val bytes = it.readNBytes(4096)
                d.update(bytes)
            } while (bytes.size == 4096)
        }
        d.digest().toHex()
    }
    val hashed = parentFile.resolve("$nameWithoutExtension-$hash.$extension")
    renameTo(hashed)
    return hashed
}

fun File.addScriptForTag(
    script : String,
    source : Boolean = false,
    tag : String = "body",
) {
    val text = readText()

    val s = if (source) {
        "<script type=\"module\">\n$script\n</script>\n"
    } else {
        "<script src=\"$script\"></script>\n"
    }

    writeText(
        StringBuilder(text).insert(text.indexOf("</$tag>"), s).toString()
    )
}