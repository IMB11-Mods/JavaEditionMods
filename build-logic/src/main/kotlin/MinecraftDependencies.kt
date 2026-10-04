import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider

val Project.minecraftVersion: String
    get() = name.substringBeforeLast('-')

val Project.minecraftCatalog: VersionCatalog
    get() = extensions.getByType(VersionCatalogsExtension::class.java)
        .named("mc${minecraftVersion.replace('.', 'x')}")

fun Project.mcLibrary(alias: String): Provider<MinimalExternalModuleDependency> =
    minecraftCatalog.findLibrary(alias).orElseThrow {
        IllegalArgumentException("Missing dependency '$alias' for Minecraft $minecraftVersion")
    }
