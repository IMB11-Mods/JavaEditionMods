import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.language.jvm.tasks.ProcessResources

plugins {
    id("net.fabricmc.fabric-loom")
    id("imb11.mod")
}

val catalog = extensions.getByType<VersionCatalogsExtension>().named("libs")
val minecraftVersion = providers.gradleProperty("minecraftVersion").get()
val metadata = modMetadata

dependencies {
    "minecraft"("com.mojang:minecraft:$minecraftVersion")
    implementation(catalog.findLibrary("fabric-loader").get())
    implementation(catalog.findLibrary("fabric-api").get())
}

loom {
    mods {
        register(metadata.id) {
            sourceSet(sourceSets["main"])
        }
    }
    runs {
        configureEach {
            runDir(rootProject.file("run/${metadata.id}/fabric/$name").absolutePath)
        }
    }
}

tasks.named<ProcessResources>("processResources") {
    exclude("META-INF/neoforge.mods.toml", "META-INF/mods.toml", "META-INF/accesstransformer.cfg", "interfaces.json")
}
