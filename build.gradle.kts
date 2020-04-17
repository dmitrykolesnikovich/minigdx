plugins {
    id("com.android.application")
    kotlin("multiplatform") version "1.3.70"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
    id("com.github.dwursteisen.collada") version "1.0-SNAPSHOT"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
    maven(url = "https://maven.pkg.github.com/dwursteisen/kotlin-math") {
        this.credentials {
            this.username = System.getenv("GITHUB_USERNAME")
            this.password = System.getenv("GITHUB_TOKEN")
        }
    }
    maven(url = "https://maven.pkg.github.com/dwursteisen/collada-parser") {
        this.credentials {
            this.username = System.getenv("GITHUB_USERNAME")
            this.password = System.getenv("GITHUB_TOKEN")
        }
    }
    google()
    mavenCentral()
    mavenLocal()
}

android {
    compileSdkVersion(29)
    buildToolsVersion = "29.0.3"
    defaultConfig {
        minSdkVersion(13)
    }
    sourceSets.getByName("main") {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        assets.srcDirs("src/commonMain/resources")
    }

    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/license.txt")
        exclude("META-INF/NOTICE")
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/notice.txt")
        exclude("META-INF/ASL2.0")
        exclude("META-INF/*.kotlin_module")
    }
}

kotlin {
    /* Targets configuration omitted. 
    *  To find out how to configure the targets, please follow the link:
    *  https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#setting-up-targets */

    js {
        this.useCommonJs()
        this.browser {
            this.webpackTask {
                this.compilation.kotlinOptions {
                    this.sourceMap = true
                    this.sourceMapEmbedSources = "always"
                }
            }
        }
        this.nodejs
    }

    android("android") {
    }

    jvm {
        this.compilations.getByName("main").kotlinOptions.jvmTarget = "1.8"
    }
/*
    macosX64() {
        binaries {
            executable {
                // Change to specify fully qualified name of your application's entry point:
                entryPoint = "main"
                // Specify command-line arguments, if necessary:
                runTask?.args("")
            }
        }
    }
*/
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("com.github.dwursteisen.kotlin-math:kotlin-math:1.0-SNAPSHOT")
                implementation("com.github.dwursteisen.collada:collada-api:1.0-SNAPSHOT")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation("com.github.dwursteisen.kotlin-math:kotlin-math-js:1.0-SNAPSHOT")
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }

        val jvmMain by getting {
            dependencies {
                api(kotlin("stdlib-jdk8"))
                implementation("com.github.dwursteisen.kotlin-math:kotlin-math-jvm:1.0-SNAPSHOT")

                val lwjglVersion = "3.2.3"
                implementation("org.lwjgl:lwjgl:$lwjglVersion")
                implementation("org.lwjgl:lwjgl:$lwjglVersion:natives-windows")
                implementation("org.lwjgl:lwjgl:$lwjglVersion:natives-linux")
                implementation("org.lwjgl:lwjgl:$lwjglVersion:natives-macos")
                implementation("org.lwjgl:lwjgl-glfw:$lwjglVersion")
                implementation("org.lwjgl:lwjgl-glfw:$lwjglVersion:natives-windows")
                implementation("org.lwjgl:lwjgl-glfw:$lwjglVersion:natives-linux")
                implementation("org.lwjgl:lwjgl-glfw:$lwjglVersion:natives-macos")
                implementation("org.lwjgl:lwjgl-opengl:$lwjglVersion")
                implementation("org.lwjgl:lwjgl-opengl:$lwjglVersion:natives-windows")
                implementation("org.lwjgl:lwjgl-opengl:$lwjglVersion:natives-linux")
                implementation("org.lwjgl:lwjgl-opengl:$lwjglVersion:natives-macos")

                implementation("org.l33tlabs.twl:pngdecoder:1.0")
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        val androidTest by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk7"))
                implementation(kotlin("test-junit"))
            }
        }
    }
}

colladaPlugin {
    create("assetsProtobuf") {
        this.daeDirectory.set(project.projectDir.resolve("src/assets"))
        this.gltfDirectory.set(project.projectDir.resolve("src/assets"))
        this.target.set(project.projectDir.resolve("src/commonMain/resources"))
        this.format.set(collada.Format.PROTOBUF as collada.Format)
    }
    create("assetsJson") {
        this.daeDirectory.set(project.projectDir.resolve("src/assets"))
        this.gltfDirectory.set(project.projectDir.resolve("src/assets"))
        this.target.set(project.projectDir.resolve("src/commonMain/resources"))
        this.format.set(collada.Format.JSON as collada.Format)
    }
}

// -- convenient task to create the documentation.
project.tasks.create<Copy>("docs").apply {
    group = "minigdx"
    // package the application
    dependsOn("jsBrowserProductionWebpack")
    from("build/distributions/") {
        include("*.js", "*.protobuf")
    }
    into("docs")
}

// -- convenient tasks to test the game engine.
project.tasks.create("runJs").apply {
    group = "minigdx"
    dependsOn("jsBrowserDevelopmentRun")
}

project.tasks.create("runJvm").apply {
    group = "minigdx"
    dependsOn(":demo:run")
}

project.tasks.create("runAndroid").apply {
    group = "minigdx"
    dependsOn("installDebug")
}
