import me.modmuss50.mpp.PublishModTask

val modProjects = rootProject.childProjects.values
val targetProjects = modProjects.flatMap { it.childProjects.values }
val loaders = listOf("fabric", "neoforge")

val loaderBuilds = loaders.map { loader ->
    val title = if (loader == "fabric") "Fabric" else "NeoForge"
    tasks.register("build$title") {
        group = "build"
        dependsOn(targetProjects.filter { it.name.endsWith("-$loader") }.map { "${it.path}:assemble" })
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
    dependsOn(targetProjects.map { "${it.path}:clean" })
}

val collectArtifacts = tasks.register<Sync>("collectArtifacts") {
    group = "distribution"
    dependsOn(buildAll)
    into(layout.buildDirectory.dir("artifacts"))
}

targetProjects.forEach { node ->
    node.pluginManager.withPlugin("imb11.mod") {
        collectArtifacts.configure {
            from(listOf(node.tasks.named("jar"), node.tasks.named("sourcesJar"))) {
                into(requireNotNull(node.parent).name)
            }
        }
    }
}

tasks.register("publishAllMods") {
    group = "publishing"
    dependsOn(buildAll)
    dependsOn(modProjects.map { "${it.path}:publishMods" })
}

listOf(":sounds", ":fog").forEach { mod ->
    project(mod).pluginManager.withPlugin("imb11.publish-mod") {
        project(mod).tasks.withType<PublishModTask>().configureEach {
            mustRunAfter(":mru:publishMods")
        }
    }
}

tasks.register("prepareSourcesFabric") {
    group = "ide"
    dependsOn(targetProjects.filter { it.name.endsWith("-fabric") }.distinctBy { it.name }.map { "${it.path}:genSources" })
}
