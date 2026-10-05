plugins {
    id("imb11.neoforge-mod")
}

val generatedInterfaces = stonecutter.tasks.generatedSourcesDir.file("main/resources/interfaces.json")
val interfaces = tasks.named("stonecutterGenerate").map { generatedInterfaces.get() }

neoForge {
    interfaceInjectionData {
        from(interfaces)
        publish(interfaces)
    }
}

dependencies {
    implementation(libs.jspecify)
    compileOnly(mcLibrary("cloth-neoforge"))
    compileOnly(mcLibrary("yacl-neoforge"))
    compileOnly(mcLibrary("accessories-neoforge"))
    compileOnly(variantOf(mcLibrary("curios-neoforge")) { classifier("api") })
    compileOnly(mcLibrary("ohmega-neoforge"))
    compileOnly(mcLibrary("trinkets"))
    compileOnly(mcLibrary("travelers-backpack-neoforge"))
    compileOnly(mcLibrary("sophisticated-core-neoforge"))
    compileOnly(mcLibrary("sophisticated-backpacks-neoforge"))
    compileOnly(mcLibrary("satchels"))
    compileOnly(mcLibrary("forgified-fabric-api"))

    runtimeOnly(mcLibrary("yacl-neoforge"))
}

val useIconFile = stonecutter.eval(stonecutter.current.version, ">=26.3")
tasks.processResources {
    inputs.property("useIconFile", useIconFile)
    filesMatching("META-INF/neoforge.mods.toml") {
        filter { line -> if (useIconFile) line.replace("logoFile=", "iconFile=") else line }
    }
}

stonecutter {
    replacements.string {
        direction = true
        replace("ResourceLocation", "Identifier")
    }
    replacements.string {
        direction = true
        replace("GuiGraphics", "GuiGraphicsExtractor")
    }
    replacements.string {
        direction = true
        replace("guiGraphics.drawString", "guiGraphics.text")
    }
}
