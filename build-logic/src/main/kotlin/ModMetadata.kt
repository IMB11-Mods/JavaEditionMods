import org.gradle.api.Project
import org.gradle.api.file.Directory
import java.util.Properties

data class ModMetadata(val values: Map<String, String>) {
    fun required(key: String): String = requireNotNull(values[key]?.takeIf(String::isNotBlank)) {
        "Missing module property: $key"
    }

    fun optional(key: String): String = values[key].orEmpty()

    fun list(key: String): List<String> = optional(key).split(',').map(String::trim).filter(String::isNotEmpty)

    val id: String get() = required("mod.id")
    val version: String get() = required("mod.version")
}

fun Project.readModMetadata(moduleDirectory: Directory = requireNotNull(parent).layout.projectDirectory): ModMetadata {
    val contents = providers.fileContents(moduleDirectory.file("mod.properties")).asText.get()
    val properties = Properties().apply { contents.reader().use(::load) }
    return ModMetadata(properties.stringPropertyNames().associateWith(properties::getProperty))
}

val Project.modMetadata: ModMetadata
    get() = extensions.getByType(ModMetadata::class.java)
