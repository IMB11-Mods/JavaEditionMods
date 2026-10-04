import org.gradle.language.jvm.tasks.ProcessResources

plugins {
    id("net.fabricmc.fabric-loom")
    id("imb11.mod")
}

val minecraftVersion = project.minecraftVersion
val metadata = modMetadata

dependencies {
    "minecraft"("com.mojang:minecraft:$minecraftVersion")
    implementation(mcLibrary("fabric-loader"))
    implementation(mcLibrary("fabric-api"))
}

loom {
    mods {
        register(metadata.id) {
            sourceSet(sourceSets["main"])
        }
    }
    runs {
        configureEach {
            runDir(rootProject.file("run/${metadata.id}/$minecraftVersion/fabric/$name").absolutePath)
        }
    }
}

tasks.named<ProcessResources>("processResources") {
    exclude("META-INF/neoforge.mods.toml", "META-INF/mods.toml", "META-INF/accesstransformer.cfg", "interfaces.json")
}
