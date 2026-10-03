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
    compileOnly(libs.cloth.neoforge)
    compileOnly(libs.yacl.neoforge)
    compileOnly(libs.accessories.neoforge)
    compileOnly(variantOf(libs.curios.neoforge) { classifier("api") })
    compileOnly(libs.ohmega.neoforge)
    compileOnly(libs.trinkets)
    compileOnly(libs.travelers.backpack.neoforge)
    compileOnly(libs.sophisticated.core)
    compileOnly(libs.sophisticated.backpacks)
    compileOnly(libs.forgified.fabric.api)
    runtimeOnly(libs.yacl.neoforge)
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
