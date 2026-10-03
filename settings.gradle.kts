pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.kikugie.dev/releases/")
        maven("https://maven.kikugie.dev/snapshots/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.kikugie.stonecutter") version "0.9.8"
}

rootProject.name = "JavaEditionMods"

val minecraftVersion = providers.gradleProperty("minecraftVersion").get()
val sourceVersion = minecraftVersion.split('.').take(2).joinToString(".")
val modProjects = providers.gradleProperty("modProjects").get().split(',').map(String::trim)

stonecutter {
    create(rootProject) {
        modProjects.forEach { mod ->
            branch(mod) {
                listOf("fabric", "neoforge").forEach { loader ->
                    version("$minecraftVersion-$loader", sourceVersion)
                        .buildscript("build.$loader.gradle.kts")
                }
            }
        }
        vcsVersion.set("$minecraftVersion-fabric")
    }
}
