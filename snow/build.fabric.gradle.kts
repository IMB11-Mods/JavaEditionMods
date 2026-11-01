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
    compileOnly(mcLibrary("serene-seasons-fabric"))
    compileOnly(mcLibrary("glitchcore-fabric"))
    compileOnly(mcLibrary("nightconfig-core"))
    val withIris = providers.gradleProperty("with_iris").isPresent
    if (withIris || providers.gradleProperty("with_sodium").isPresent) {
        "localRuntime"(mcLibrary("sodium-fabric"))
    }
    if (withIris) {
        "localRuntime"(mcLibrary("iris-fabric"))
    }
    if (providers.gradleProperty("with_seasons").isPresent) {
        "localRuntime"(mcLibrary("serene-seasons-fabric"))
        "localRuntime"(mcLibrary("glitchcore-fabric"))
    }
}

loom {
    mods {
        register("mru") {
            sourceSet(mruProject.extensions.getByType<SourceSetContainer>().getByName("main"))
        }
    }
}
