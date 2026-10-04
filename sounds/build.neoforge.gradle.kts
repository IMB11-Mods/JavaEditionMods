plugins {
    id("imb11.neoforge-mod")
}

val mruProject = project(":mru:${project.name}")
evaluationDependsOn(mruProject.path)

dependencies {
    implementation(project(mruProject.path))
    implementation(mcLibrary("yacl-neoforge"))
    compileOnly(mcLibrary("trashslot"))
}

neoForge {
    interfaceInjectionData.from(mruProject.tasks.named("stonecutterGenerate").map {
        mruProject.layout.buildDirectory.file("generated/stonecutter/main/resources/interfaces.json").get()
    })
    mods {
        register("mru") {
            sourceSet(mruProject.extensions.getByType<SourceSetContainer>().getByName("main"))
        }
    }
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
