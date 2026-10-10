plugins {
    id("imb11.fabric-mod")
}

loom {
    accessWidenerPath = project.parent!!.file("src/main/resources/glass.accesswidener")
}

dependencies {
    compileOnly(mcLibrary("sodium-fabric"))
    if (providers.gradleProperty("with_sodium").isPresent) {
        "localRuntime"(mcLibrary("sodium-fabric"))
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
