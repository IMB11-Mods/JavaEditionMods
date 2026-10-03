plugins {
    id("imb11.fabric-mod")
}

loom {
    accessWidenerPath = project.parent!!.file("src/main/resources/mru.classtweaker")
}

dependencies {
    implementation(libs.kaleido)
    implementation(libs.jspecify)

    compileOnly(libs.cloth.fabric)
    compileOnly(libs.yacl.fabric)
    compileOnly(libs.modmenu)
    compileOnly(libs.map.atlases)
    compileOnly(libs.accessories.fabric) {
        isTransitive = false
    }
    compileOnly(variantOf(libs.curios.neoforge) { classifier("api") })
    compileOnly(variantOf(libs.neoforge) { classifier("universal") })
    compileOnly(libs.travelers.backpack.fabric)
    compileOnly(libs.sophisticated.core)
    compileOnly(libs.sophisticated.backpacks)
    compileOnly(libs.porting.lib.transfer)
    compileOnly(libs.jade.fabric)
    compileOnly(libs.cca.entity)
    compileOnly(libs.cca.base)
    compileOnly(libs.trinkets)
    compileOnly(libs.ohmega.fabric)
    compileOnly(libs.forge.config.api.port.fabric)

    runtimeOnly(libs.cloth.fabric)
    runtimeOnly(libs.yacl.fabric)
    runtimeOnly(libs.modmenu)
    runtimeOnly(libs.mcqoy)
    runtimeOnly(libs.surveyor)
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
