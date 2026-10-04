import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.language.jvm.tasks.ProcessResources
import java.util.Properties

plugins {
    `java-library`
    `maven-publish`
    id("dev.kikugie.postprocess.jsonlang")
}

val metadata = readModMetadata()
extensions.add("modMetadata", metadata)
metadata.values.forEach { (key, value) -> extensions.extraProperties[key] = value }

val catalog = minecraftCatalog
val minecraftVersion = project.minecraftVersion
val loader = project.name.substringAfterLast('-')
val moduleDirectory = requireNotNull(project.parent).layout.projectDirectory
val javaVersion = providers.gradleProperty("javaVersion").get().toInt()
val releaseVersion = "${metadata.version}+$minecraftVersion"

group = metadata.required("mod.group")
version = "$releaseVersion-$loader"
base.archivesName.set(metadata.id)

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion))
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = metadata.optional("publish.mavenGroup").ifBlank { metadata.required("mod.group") }
            artifactId = "${metadata.id}-$loader"
            version = releaseVersion
            from(components["java"])
            pom {
                name.set(metadata.required("mod.name"))
                description.set(metadata.required("mod.description"))
                licenses {
                    license {
                        name.set(metadata.required("mod.license"))
                    }
                }
            }
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(javaVersion)
    dependsOn("stonecutterGenerate")
}

tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

tasks.withType<Jar>().configureEach {
    from(moduleDirectory.file("LICENSE")) {
        rename { "LICENSE_${metadata.id}" }
    }
}

val requiredMod = metadata.list("mod.required").firstOrNull()
val requiredModVersion = requiredMod?.let { mod ->
    val contents = providers.fileContents(rootProject.layout.projectDirectory.file("$mod/mod.properties")).asText.get()
    Properties().apply { contents.reader().use(::load) }.getProperty("mod.version")
}.orEmpty()
val targetMinecraft = if (loader == "fabric") "=$minecraftVersion" else "[$minecraftVersion]"
val yaclVersion = mcLibrary("yacl-$loader").get().versionConstraint.requiredVersion.removeSuffix("-$loader")
val resourceProperties = mapOf(
    "version" to releaseVersion,
    "mod_version" to metadata.version,
    "minecraft" to targetMinecraft,
    "target_minecraft" to targetMinecraft,
    "mod_id" to metadata.id,
    "mod_name" to metadata.required("mod.name"),
    "mod_description" to metadata.required("mod.description"),
    "mod_license" to metadata.required("mod.license"),
    "loader" to loader,
    "fml_loader" to "[4,)",
    "target_loader" to if (loader == "fabric") catalog.findVersion("fabric-loader").get().requiredVersion
        else "[${catalog.findVersion("neoforge").get().requiredVersion},)",
    "target_fabricloader" to catalog.findVersion("fabric-loader").get().requiredVersion,
    "target_yacl" to if (loader == "fabric") ">=$yaclVersion" else yaclVersion,
    "target_mru" to if (loader == "fabric") ">=$requiredModVersion" else requiredModVersion
)

tasks.named<ProcessResources>("processResources") {
    dependsOn("stonecutterGenerate")
    inputs.properties(resourceProperties)
    filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml")) {
        expand(resourceProperties)
    }
}

tasks.named("sourcesJar") {
    dependsOn("stonecutterGenerate")
}

val processedSources = layout.buildDirectory.dir("generated/stonecutter/main")
sourceSets.named("main") {
    java.setSrcDirs(listOf(file("src/main/java"), files(processedSources.map { it.dir("java") }).builtBy("stonecutterGenerate")))
    resources.setSrcDirs(listOf(
        file("src/main/resources"),
        file("src/main/generated"),
        files(processedSources.map { it.dir("resources") }, processedSources.map { it.dir("generated") })
            .builtBy("stonecutterGenerate")
    ))
}

jsonlang {
    languageDirectories = listOf("assets/${metadata.id}/lang")
    prettyPrint = true
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.shedaniel.me/")
    maven("https://maven.wispforest.io/releases/")
    maven("https://api.modrinth.com/maven") {
        content { includeGroup("maven.modrinth") }
    }
    maven("https://repo.sleeping.town/")
    maven("https://maven.isxander.dev/releases")
    maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
    maven("https://maven.ladysnake.org/releases")
    maven("https://mvn.devos.one/snapshots")
    maven("https://mvn.devos.one/releases")
    maven("https://cursemaven.com") {
        content { includeGroup("curse.maven") }
    }
    maven("https://maven.nucleoid.xyz")
    maven("https://maven.theillusivec4.top/")
    maven("https://maven.su5ed.dev/releases")
    maven("https://maven.terraformersmc.com/releases/")
    maven("https://maven.cassian.cc/")
    maven("https://maven.gegy.dev")
}
