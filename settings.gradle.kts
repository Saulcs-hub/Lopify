pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
val localProperties = java.util.Properties()
val localPropertiesFile = rootProject.projectDir.resolve("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        // --- AQUÍ ES DONDE AGREGAS EL "PASSWORD" ---
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            credentials {
                username = "saulcs2"

                password = localProperties.getProperty("MAPBOX_DOWNLOADS_TOKEN")
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}

rootProject.name = "Lopify"
include(":app")
 