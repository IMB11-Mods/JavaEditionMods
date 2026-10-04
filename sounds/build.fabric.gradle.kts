plugins {
    id("imb11.fabric-mod")
}

val mruProject = project(":mru:${project.name}")
evaluationDependsOn(mruProject.path)

dependencies {
    implementation(project(mruProject.path))
    implementation(mcLibrary("yacl-fabric"))
    implementation(mcLibrary("modmenu"))
    compileOnly(mcLibrary("trashslot"))
}

loom {
    mods {
        register("mru") {
            sourceSet(mruProject.extensions.getByType<SourceSetContainer>().getByName("main"))
        }
    }
}

fabricApi {
    configureDataGeneration {
        outputDirectory = project.parent!!.file("src/main/generated")
        addToResources = false
        client = true
    }
}

loom.runs.named("datagen") {
    configName = "${modMetadata.required("mod.name")} - ${project.minecraftVersion} - Fabric - Data Generation"
    runDir(rootProject.file("run/${modMetadata.id}/${project.minecraftVersion}/fabric/datagen").relativeTo(project.projectDir).invariantSeparatorsPath)
}

val legacyPlayerMixin = stonecutter.eval(stonecutter.current.version, "<26.3")
tasks.processResources {
    inputs.property("legacyPlayerMixin", legacyPlayerMixin)
    filesMatching("sounds.mixins.json") {
        filter { line ->
            if (!legacyPlayerMixin && line.trim() == "\"ui.PlayerEntityMixin\",") "" else line
        }
    }
}

stonecutter {
    replacements.string {
        direction = true
        replace("ResourceLocation", "Identifier")
    }
    replacements.string {
        direction = true
        replace("net.minecraft.Util", "net.minecraft.util.Util")
    }
    replacements.string {
        direction = true
        replace("ResourceKey::location", "ResourceKey::identifier")
    }
}
