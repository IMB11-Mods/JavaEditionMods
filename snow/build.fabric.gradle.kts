plugins {
    id("imb11.fabric-mod")
}

val mruProject = project(":mru:${project.name}")
evaluationDependsOn(mruProject.path)

dependencies {
    implementation(project(mruProject.path))
    implementation(mcLibrary("yacl-fabric"))
    implementation(mcLibrary("modmenu"))
}

loom {
    mods {
        register("mru") {
            sourceSet(mruProject.extensions.getByType<SourceSetContainer>().getByName("main"))
        }
    }
}
