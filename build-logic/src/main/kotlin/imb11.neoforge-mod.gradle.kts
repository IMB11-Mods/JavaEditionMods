import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.language.jvm.tasks.ProcessResources

plugins {
    id("net.neoforged.moddev")
    id("imb11.mod")
}

val catalog = extensions.getByType<VersionCatalogsExtension>().named("libs")
val metadata = modMetadata

neoForge {
    enable {
        version = catalog.findVersion("neoforge").get().requiredVersion
        isDisableRecompilation = providers.environmentVariable("CI").map { it == "true" }.getOrElse(false)
    }
    validateAccessTransformers = true
    mods {
        register(metadata.id) {
            sourceSet(sourceSets["main"])
        }
    }
    runs {
        register("client") {
            client()
            gameDirectory.set(rootProject.layout.projectDirectory.dir("run/${metadata.id}/neoforge/client"))
        }
        register("server") {
            server()
            gameDirectory.set(rootProject.layout.projectDirectory.dir("run/${metadata.id}/neoforge/server"))
        }
    }
}

tasks.named("createMinecraftArtifacts") {
    dependsOn("stonecutterGenerate")
}

tasks.named<ProcessResources>("processResources") {
    exclude("fabric.mod.json", "**/*.accesswidener", "**/*.classtweaker", "META-INF/mods.toml")
}
