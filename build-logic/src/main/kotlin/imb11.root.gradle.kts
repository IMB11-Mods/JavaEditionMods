import me.modmuss50.mpp.PublishModTask

val targetMinecraft = providers.gradleProperty("minecraftVersion").get()
val modProjects = providers.gradleProperty("modProjects").get().split(',').map(String::trim)
val loaders = listOf("fabric", "neoforge")

val loaderBuilds = loaders.map { loader ->
    val title = if (loader == "fabric") "Fabric" else "NeoForge"
    tasks.register("build$title") {
        group = "build"
        dependsOn(modProjects.map { ":$it:$targetMinecraft-$loader:assemble" })
    }
}

val buildAll = tasks.register("buildAll") {
    group = "build"
    dependsOn(loaderBuilds)
}

tasks.register("assemble") {
    group = "build"
    dependsOn(buildAll)
}

tasks.register("build") {
    group = "build"
    dependsOn(buildAll)
}

tasks.register<Delete>("clean") {
    group = "build"
    delete(layout.buildDirectory)
    dependsOn(modProjects.flatMap { mod -> loaders.map { ":$mod:$targetMinecraft-$it:clean" } })
}

val collectArtifacts = tasks.register<Sync>("collectArtifacts") {
    group = "distribution"
    dependsOn(buildAll)
    into(layout.buildDirectory.dir("artifacts"))
}

modProjects.forEach { mod ->
    loaders.forEach { loader ->
        val node = project(":$mod:$targetMinecraft-$loader")
        node.pluginManager.withPlugin("imb11.mod") {
            collectArtifacts.configure {
                from(listOf(node.tasks.named("jar"), node.tasks.named("sourcesJar"))) {
                    into(mod)
                }
            }
        }
    }
}

tasks.register("publishAllMods") {
    group = "publishing"
    dependsOn(buildAll, ":mru:publishMods", ":sounds:publishMods")
}

project(":sounds").pluginManager.withPlugin("imb11.publish-mod") {
    project(":sounds").tasks.withType<PublishModTask>().configureEach {
        mustRunAfter(":mru:publishMods")
    }
}

tasks.register("prepareSourcesFabric") {
    group = "ide"
    dependsOn(":${modProjects.first()}:$targetMinecraft-fabric:genSources")
}
