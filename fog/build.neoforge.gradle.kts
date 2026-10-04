plugins {
    id("imb11.neoforge-mod")
}

stonecutter {
    replacements.string {
        direction = minecraftVersion == "26.3"
        replace("gameRenderer.getMainCamera()", "gameRenderer.mainCamera()")
        replace(".gui.getChat()", ".gui.hud.getChat()")
        replace(".gui.getBossOverlay()", ".gui.hud.getBossOverlay()")
    }
}

val mruProject = project(":mru:${project.name}")
evaluationDependsOn(mruProject.path)

val polytoneLibraries = configurations.create("polytoneLibraries") {
    isCanBeConsumed = false
    isTransitive = false
}

dependencies {
    implementation(project(mruProject.path))
    implementation(mcLibrary("yacl-neoforge"))
    compileOnly(mcLibrary("iris-neoforge"))
    if (minecraftVersion == "26.1.2") {
        compileOnly(mcLibrary("polytone-neoforge"))
        add(polytoneLibraries.name, mcLibrary("polytone-neoforge"))
        compileOnly(files(provider {
            polytoneLibraries.files.flatMap { zipTree(it).matching { include("META-INF/jars/*.jar", "META-INF/jarjar/*.jar") }.files }
        }))
    }
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
