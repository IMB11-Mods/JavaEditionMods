import me.modmuss50.mpp.PublishModTask
import me.modmuss50.mpp.platforms.modrinth.ModrinthEnvironment

plugins {
    id("me.modmuss50.mod-publish-plugin")
}

val modData = readModMetadata(layout.projectDirectory)
val minecraftVersion = providers.gradleProperty("minecraftVersion").get()
val releaseVersion = "${modData.version}+$minecraftVersion"
val releaseTag = "${modData.id}/v${modData.version}"
val publishDryRun = providers.gradleProperty("publishDryRun").map(String::toBoolean).orElse(false)
val loaderProjects = listOf("fabric", "neoforge").associateWith { loader ->
    val node = project("$path:$minecraftVersion-$loader")
    evaluationDependsOn(node.path)
    node
}

val assemble = tasks.register("assemble") {
    group = "build"
    dependsOn(loaderProjects.values.map { it.tasks.named("assemble") })
}

val validateRelease = tasks.register("validateRelease") {
    doLast {
        if (providers.environmentVariable("GITHUB_REF_TYPE").orNull == "tag") {
            require(providers.environmentVariable("GITHUB_REF_NAME").get() == releaseTag) {
                "Release tag must exactly match $releaseTag"
            }
        }
        if (!publishDryRun.get()) {
            listOf("MODRINTH_TOKEN", "CURSEFORGE_TOKEN", "GITHUB_TOKEN", "GITHUB_REPOSITORY", "GITHUB_SHA").forEach {
                require(!providers.environmentVariable(it).orNull.isNullOrBlank()) { "Missing release environment variable: $it" }
            }
        }
    }
}

publishMods {
    version = releaseVersion
    displayName = "${modData.required("mod.name")} ${modData.version}"
    changelog = providers.fileContents(layout.projectDirectory.file("CHANGELOG-LATEST.md")).asText
        .orElse("${modData.required("mod.name")} ${modData.version} for Minecraft $minecraftVersion.")
    type = when {
        modData.version.contains("alpha", ignoreCase = true) -> ALPHA
        Regex("beta|edge|rc|snapshot", RegexOption.IGNORE_CASE).containsMatchIn(modData.version) -> BETA
        else -> STABLE
    }
    dryRun = publishDryRun

    loaderProjects.forEach { (loader, node) ->
        val jar = node.tasks.named<Jar>("jar").flatMap { it.archiveFile }
        val sources = node.tasks.named<Jar>("sourcesJar").flatMap { it.archiveFile }
        val requiredMods = modData.list("publish.dependencies.$loader")
        val loaderName = if (loader == "fabric") "Fabric" else "NeoForge"
        val options = publishOptions {
            file = jar
            additionalFiles.from(sources)
            version = "$releaseVersion-$loader"
            displayName = "${modData.required("mod.name")} ${modData.version} for $minecraftVersion $loaderName"
            modLoaders.add(loader)
        }

        modrinth("modrinth$loaderName") {
            from(options.get())
            projectId = modData.required("publish.modrinth")
            accessToken = providers.environmentVariable("MODRINTH_TOKEN")
            minecraftVersions.add(minecraftVersion)
            environment = if (modData.id == "sounds") ModrinthEnvironment.CLIENT_ONLY else ModrinthEnvironment.CLIENT_AND_SERVER
            requiredMods.forEach { requires(it) }
        }

        curseforge("curseforge$loaderName") {
            from(options.get())
            projectId = modData.required("publish.curseforge")
            accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
            minecraftVersions.add(minecraftVersion)
            client = true
            server = modData.id != "sounds"
            requiredMods.forEach { requires(it) }
        }
    }

    github {
        accessToken = providers.environmentVariable("GITHUB_TOKEN")
        repository = providers.environmentVariable("GITHUB_REPOSITORY")
        commitish = providers.environmentVariable("GITHUB_SHA")
        tagName = releaseTag
        file = loaderProjects.getValue("fabric").tasks.named<Jar>("jar").flatMap { it.archiveFile }
        additionalFiles.from(loaderProjects.getValue("neoforge").tasks.named("jar"))
        additionalFiles.from(loaderProjects.values.map { it.tasks.named("sourcesJar") })
        modLoaders.addAll(loaderProjects.keys)
    }
}

tasks.withType<PublishModTask>().configureEach {
    dependsOn(assemble, validateRelease)
    mustRunAfter(rootProject.tasks.named("buildAll"))
}
