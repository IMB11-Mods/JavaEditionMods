plugins {
    id("imb11.fabric-mod")
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
    implementation(mcLibrary("yacl-fabric"))
    implementation(mcLibrary("modmenu"))
    compileOnly(mcLibrary("iris-fabric"))
    if (minecraftVersion == "26.1.2") {
        compileOnly(mcLibrary("polytone-fabric"))
        add(polytoneLibraries.name, mcLibrary("polytone-fabric"))
        compileOnly(files(provider {
            polytoneLibraries.files.flatMap { zipTree(it).matching { include("META-INF/jars/*.jar", "META-INF/jarjar/*.jar") }.files }
        }))
    }
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
    configName = "Fog - ${project.minecraftVersion} - Fabric - Data Generation"
    runDir(rootProject.file("run/fog/${project.minecraftVersion}/fabric/datagen").relativeTo(project.projectDir).invariantSeparatorsPath)
}
