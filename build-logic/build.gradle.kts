plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(localGroovy())
    implementation(libs.loom.plugin)
    implementation(libs.moddev.plugin)
    implementation(libs.jsonlang.plugin)
    implementation(libs.mod.publish.plugin)
}

kotlin {
    jvmToolchain(25)
}
