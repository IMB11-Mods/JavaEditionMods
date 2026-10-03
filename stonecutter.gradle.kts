plugins {
    id("dev.kikugie.stonecutter")
    id("imb11.root")
}

stonecutter.flags["extra_source_check"] = false

stonecutter active "26.1.2-fabric"

stonecutter parameters {
    constants.match(node.metadata.project.substringAfterLast('-'), "fabric", "neoforge")
    filters.include("**/*.fsh", "**/*.vsh")
}
