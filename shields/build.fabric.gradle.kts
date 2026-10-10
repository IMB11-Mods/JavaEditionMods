plugins {
    id("imb11.fabric-mod")
}

val mruProject = project(":mru:${project.name}")
evaluationDependsOn(mruProject.path)

dependencies {
    implementation(project(mruProject.path))
    implementation(mcLibrary("modmenu"))
    implementation(mcLibrary("rrv-fabric"))
    compileOnly(mcLibrary("jei-fabric"))
}

loom {
    mods {
        register("mru") {
            sourceSet(mruProject.extensions.getByType<SourceSetContainer>().getByName("main"))
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
