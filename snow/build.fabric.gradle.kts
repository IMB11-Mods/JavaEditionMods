plugins {
    id("imb11.fabric-mod")
}

val mruProject = project(":mru:${project.name}")
evaluationDependsOn(mruProject.path)

dependencies {
    implementation(project(mruProject.path))
    implementation(mcLibrary("yacl-fabric"))
    implementation(mcLibrary("modmenu"))
    compileOnly(mcLibrary("sodium-fabric"))
    compileOnly(mcLibrary("iris-fabric"))
    if (providers.gradleProperty("with_sodium").isPresent) {
        "localRuntime"(mcLibrary("sodium-fabric"))
    }
}

loom {
    mods {
        register("mru") {
            sourceSet(mruProject.extensions.getByType<SourceSetContainer>().getByName("main"))
        }
    }
}
