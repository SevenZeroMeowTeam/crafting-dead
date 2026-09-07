import org.gradle.api.file.RelativePath
import org.gradle.language.jvm.tasks.ProcessResources

subprojects {
    tasks.withType<ProcessResources>().configureEach {
        val gunTextures = layout.projectDirectory.dir(
            "src/main/resources/assets/craftingdead/textures/gun"
        )

        from(gunTextures) {
            include("*_*.png")
            into("assets/craftingdead/textures/gun")
            eachFile {
                val parts = nameWithoutExtension.split("_", limit = 2)
                if (parts.size == 2) {
                    relativePath = RelativePath(true, parts[0], parts[1], name)
                }
            }
            includeEmptyDirs = false
        }
    }
}
