plugins {
    id("imb11.neoforge-mod")
}

val mruProject = project(":mru:${project.name}")
evaluationDependsOn(mruProject.path)

val sodiumDistribution = configurations.create("sodiumDistribution") {
    isCanBeConsumed = false
    isTransitive = false
}

dependencies {
    implementation(project(mruProject.path))
    implementation(mcLibrary("yacl-neoforge"))
    add(sodiumDistribution.name, mcLibrary("sodium-neoforge"))
    compileOnly(mcLibrary("iris-neoforge"))
    compileOnly(files(provider {
        sodiumDistribution.files.flatMap { zipTree(it).matching { include("META-INF/jarjar/net.caffeinemc.sodium-*-mod.jar") }.files }
    }))
    val withIris = providers.gradleProperty("with_iris").isPresent
    if (withIris || providers.gradleProperty("with_sodium").isPresent) {
        runtimeOnly(mcLibrary("sodium-neoforge"))
    }
    if (withIris) {
        runtimeOnly(mcLibrary("iris-neoforge"))
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
