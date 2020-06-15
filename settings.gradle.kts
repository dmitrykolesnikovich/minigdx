rootProject.name = "3d-skeleton"

pluginManagement {
    repositories {
        maven(
            url = uri("https://dl.bintray.com/dwursteisen/minigdx")
        )
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
        gradlePluginPortal()
        google()
        mavenLocal()
    }
    // Replace the plugin com.android.application with a dependency with another name.
    // See https://medium.com/@StefMa/its-time-to-ditch-the-buildscript-block-a1ab12e0d9ce
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("com.android")) {
                useModule("com.android.tools.build:gradle:3.6.1")
            }
            if (requested.id.id.startsWith("org.jetbrains.kotlin")) {
                useVersion("1.3.70")
            }
        }
    }
}

include("demo")
