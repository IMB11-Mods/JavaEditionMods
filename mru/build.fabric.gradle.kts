plugins {
    id("imb11.fabric-mod")
}

loom {
    accessWidenerPath = project.parent!!.file("src/main/resources/mru.classtweaker")
}

dependencies {
    implementation(libs.kaleido)
    implementation(libs.jspecify)

    compileOnly(mcLibrary("cloth-fabric"))
    compileOnly(mcLibrary("yacl-fabric"))
    compileOnly(mcLibrary("modmenu"))
    compileOnly(mcLibrary("map-atlases"))
    compileOnly(mcLibrary("accessories-fabric")) {
        isTransitive = false
    }
    compileOnly(variantOf(mcLibrary("curios-neoforge")) { classifier("api") })
    compileOnly(variantOf(mcLibrary("neoforge")) { classifier("universal") })
    compileOnly(mcLibrary("travelers-backpack-fabric"))
    compileOnly(mcLibrary("sophisticated-core-fabric"))
    compileOnly(mcLibrary("sophisticated-backpacks-fabric"))
    compileOnly(mcLibrary("porting-lib-transfer"))
    compileOnly(mcLibrary("jade-fabric"))
    compileOnly(mcLibrary("cca-entity"))
    compileOnly(mcLibrary("cca-base"))
    compileOnly(mcLibrary("trinkets"))
    compileOnly(mcLibrary("ohmega-fabric"))
    compileOnly(mcLibrary("forge-config-api-port-fabric"))
    compileOnly(mcLibrary("satchels"))

    runtimeOnly(mcLibrary("cloth-fabric"))
    runtimeOnly(mcLibrary("yacl-fabric"))
    runtimeOnly(mcLibrary("modmenu"))
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
