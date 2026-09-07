// Gradle resources configuration for gun/skin texture generation
plugins {
    id('com.diffplug.spotless') version '6.13.0' apply false
}

// Map gun/skin textures to gradle resources during build
subprojects {
    processResources {
        // Copy gun/skin textures from src/main/resources/assets/craftingdead/textures/gun/skin/ to assets/
        val skinTextures = sourceSets.main.resources.srcDirs
            .filter { it.resolve("assets/craftingdead/textures/gun/skin").exists() }
            .firstOrNull()
                ?: layout.projectDirectory.file("src\main\resources\assets\craftingdead\textures\gun\skin")
        
        // Copy gun/skin textures to assets directory with proper path mapping
        from(skinTextures)
            .into(layout.buildDirectory.resolve("resources\main\assets\craftingdead\textures\gun"))
            .rename { 
                val name = it.name.lowercase()
                if (name.contains("_") && name.split("_").size == 2) {
                    // Map gun/<gun>_<skin>.png -> assets/craftingdead/textures/gun/
                    val parts = name.split("_").dropLast(1).map { it.capitalize() }
                    layout.buildDirectory.resolve("resources\main\assets\craftingdead\textures\gun/${parts.joinToString("_")}.png")
                } else {
                    it
                }
            }
    }
}
