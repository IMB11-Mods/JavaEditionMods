plugins {
    id("imb11.fabric-mod")
}

val mruProject = project(":mru:${project.name}")
evaluationDependsOn(mruProject.path)

dependencies {
    implementation(project(mruProject.path))
    implementation(libs.yacl.fabric)
    implementation(libs.modmenu)
    compileOnly(libs.trashslot)
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
