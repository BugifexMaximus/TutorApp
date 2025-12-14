pluginManagement {
    repositories {
        google()
        maven { url = uri("https://plugins.gradle.org/m2/") }
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "TutorApp"
include("core-domain", "core-test", "infra-openai", "app")
