pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        id("org.jetbrains.kotlin.android") version "2.0.0" apply false
        id("org.jetbrains.kotlin.kapt") version "2.0.0" apply false
        id("com.android.application") version "8.4.0" apply false
        id("com.google.gms.google-services") version "4.4.0"

        // ✅ Compose Compiler Plugin for Kotlin 2.0
//        id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "FoundOnCampus"
include(":app")
