pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        jcenter()
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.run { startsWith("kotlin") || startsWith("org.jetbrains.kotlin") }) {
                useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.20")
                useVersion("1.3.20")
            }
        }
    }
}

rootProject.name = "kotlin-mpp-mobile"

enableFeaturePreview("GRADLE_METADATA")

include(":common")
include(":android")