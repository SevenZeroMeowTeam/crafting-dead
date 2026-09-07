// Gradle resources configuration for gun/skin texture generation
plugins {
    id("com.diffplug.spotless") version "6.13.0" apply false
}

// Map gun/skin textures to gradle resources during build
subprojects {
    plugins.withType<JavaPlugin> {
        tasks.named<ProcessResources>("processResources") {
            // Copy gun/skin textures from src/main/resources/assets/craftingdead/textures/gun/skin/ to assets/
            val sourceSets = extensions.getByType<SourceSetContainer>()
            val skinTextures = sourceSets.getByName("main").resources.sourceDirectories
                .filter { it.resolve("assets/craftingdead/textures/gun/skin").exists() }
                .firstOrNull()
                ?: layout.projectDirectory.file("src/main/resources/assets/craftingdead/textures/gun/skin")

            // Copy gun/skin textures to assets directory with proper path mapping
            from(skinTextures) {
                into("assets/craftingdead/textures/gun")
                rename { name ->
                    val lowerName = name.lowercase()
                    if (lowerName.contains("_") && lowerName.split("_").size == 2) {
                        // Map gun/<gun>_<skin>.png -> assets/craftingdead/textures/gun/
                        val parts = lowerName.split("_").dropLast(1)
                            .map { it.replaceFirstChar(Char::uppercase) }
                        "${parts.joinToString("_")}.png"
                    } else {
                        name
                    }
                }
            }
        }
    }
}
