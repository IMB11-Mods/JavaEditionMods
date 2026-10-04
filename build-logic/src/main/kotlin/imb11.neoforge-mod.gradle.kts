import org.gradle.language.jvm.tasks.ProcessResources

plugins {
    id("net.neoforged.moddev")
    id("imb11.mod")
}

val catalog = minecraftCatalog
val minecraftVersion = project.minecraftVersion
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
        configureEach {
            ideName.set("${metadata.required("mod.name")} - $minecraftVersion - NeoForge - ${name.replaceFirstChar { it.titlecase() }}")
            ideFolderName.set(metadata.required("mod.name"))
        }
        register("client") {
            client()
            gameDirectory.set(rootProject.layout.projectDirectory.dir("run/${metadata.id}/$minecraftVersion/neoforge/client"))
        }
        if (metadata.optional("mod.environment") != "client") {
            register("server") {
                server()
                gameDirectory.set(rootProject.layout.projectDirectory.dir("run/${metadata.id}/$minecraftVersion/neoforge/server"))
            }
        }
    }
}

tasks.named("createMinecraftArtifacts") {
    dependsOn("stonecutterGenerate")
}

tasks.named<ProcessResources>("processResources") {
    exclude("fabric.mod.json", "**/*.accesswidener", "**/*.classtweaker", "META-INF/mods.toml")
}
