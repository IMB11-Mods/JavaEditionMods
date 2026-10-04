import org.gradle.language.jvm.tasks.ProcessResources

plugins {
    id("net.fabricmc.fabric-loom")
    id("imb11.mod")
}

val targetMinecraftVersion = project.minecraftVersion
val metadata = modMetadata

dependencies {
    "minecraft"("com.mojang:minecraft:$targetMinecraftVersion")
    implementation(mcLibrary("fabric-loader"))
    implementation(mcLibrary("fabric-api"))
}

loom {
    mods {
        register(metadata.id) {
            sourceSet(sourceSets["main"])
        }
    }
    runs {
        if (metadata.optional("mod.environment") == "client") {
            remove(getByName("server"))
        }
        configureEach {
            isIdeConfigGenerated = true
            configName = "${metadata.required("mod.name")} - $targetMinecraftVersion - Fabric - ${name.replaceFirstChar { it.titlecase() }}"
            appendProjectPathToConfigName.set(false)
            ideConfigFolder.set(metadata.required("mod.name"))
            runDir(rootProject.file("run/${metadata.id}/$targetMinecraftVersion/fabric/$name").relativeTo(project.projectDir).invariantSeparatorsPath)
        }
    }
}

tasks.named<ProcessResources>("processResources") {
    exclude("META-INF/neoforge.mods.toml", "META-INF/mods.toml", "META-INF/accesstransformer.cfg", "interfaces.json")
}
