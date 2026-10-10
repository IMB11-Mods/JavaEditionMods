plugins {
    id("imb11.neoforge-mod")
}

neoForge {
    accessTransformers.from(project.parent!!.file("src/main/resources/META-INF/accesstransformer.cfg"))
}

// The NeoForge Sodium jar is a jar-in-jar wrapper; compile against the nested mod jar.
val sodiumDistribution = configurations.create("sodiumDistribution") {
    isCanBeConsumed = false
    isTransitive = false
}

dependencies {
    add(sodiumDistribution.name, mcLibrary("sodium-neoforge"))
    compileOnly(files(provider {
        sodiumDistribution.files.flatMap { zipTree(it).matching { include("META-INF/jarjar/net.caffeinemc.sodium-*-mod.jar") }.files }
    }))
    if (providers.gradleProperty("with_sodium").isPresent) {
        runtimeOnly(mcLibrary("sodium-neoforge"))
    }
}
