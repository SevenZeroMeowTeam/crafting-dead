/*
 * Crafting Dead
 * Copyright (C) 2022  NexusNode LTD
 *
 * This Non-Commercial Software License Agreement (the "Agreement") is made between
 * you (the "Licensee") and NEXUSNODE (BRAD HUNTER). (the "Licensor").
 * By installing or otherwise using Crafting Dead (the "Software"), you agree to be
 * bound by the terms and conditions of this Agreement as may be revised from time
 * to time at Licensor's sole discretion.
 *
 * If you do not agree to the terms and conditions of this Agreement do not download,
 * copy, reproduce or otherwise use any of the source code available online at any time.
 *
 * https://github.com/nexusnode/crafting-dead/blob/1.18.x/LICENSE.txt
 *
 * https://craftingdead.net/terms.php
 */

package com.craftingdead.core.world.item.gun;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;

/**
 * Manages gun configuration overrides from config/craftingdead/guns/ directory.
 * These overrides take precedence over datapack definitions, allowing server admins
 * to tune gun balance without creating custom datapacks.
 * 
 * Priority order:
 * 1. config/craftingdead/guns/ *.json (highest priority)
 * 2. Datapacks data/ (star) /gun_configurations/ *.json
 * 3. Built-in defaults (lowest priority)
 */
public class GunConfigOverrides {

  private static final Logger logger = LogUtils.getLogger();
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private static final Path CONFIG_DIR = FMLPaths.CONFIGDIR.get().resolve("craftingdead").resolve("guns");
  
  private static final Map<ResourceLocation, GunConfiguration> overrides = new HashMap<>();
  private static boolean loaded = false;

  /**
   * Loads gun configuration overrides from config/craftingdead/guns/ directory.
   * Called during server start to apply custom gun configurations.
   */
  public static void loadOverrides() {
    overrides.clear();
    loaded = false;

    try {
      if (!Files.exists(CONFIG_DIR)) {
        Files.createDirectories(CONFIG_DIR);
        createExampleConfig();
        logger.info("Created gun config override directory at: {}", CONFIG_DIR);
      }

      if (!Files.isDirectory(CONFIG_DIR)) {
        logger.error("Gun config path exists but is not a directory: {}", CONFIG_DIR);
        return;
      }

      File[] files = CONFIG_DIR.toFile().listFiles((dir, name) -> name.endsWith(".json"));
      if (files == null || files.length == 0) {
        logger.info("No gun config overrides found");
        loaded = true;
        return;
      }

      int loadedCount = 0;
      for (File file : files) {
        try {
          String fileName = file.getName();
          String gunId = fileName.substring(0, fileName.length() - 5); // Remove .json
          ResourceLocation id = ResourceLocation.fromNamespaceAndPath("craftingdead", gunId);
          
          JsonElement json = JsonParser.parseReader(new FileReader(file));
          var result = GunConfiguration.DIRECT_CODEC.parse(JsonOps.INSTANCE, json);
          
          if (result.result().isPresent()) {
            overrides.put(id, result.result().get());
            loadedCount++;
            logger.info("Loaded gun config override: {}", id);
          }
          
          result.error().ifPresent(error -> {
            logger.error("Failed to parse gun config {}: {}", file.getName(), error.message());
          });
          
        } catch (Exception e) {
          logger.error("Failed to load gun config from {}", file.getName(), e);
        }
      }

      logger.info("Loaded {} gun configuration override(s)", loadedCount);
      loaded = true;

    } catch (IOException e) {
      logger.error("Failed to create gun config directory", e);
    }
  }

  /**
   * Gets a gun configuration override if one exists.
   * 
   * @param id The gun's resource location
   * @return The override configuration, or null if no override exists
   */
  public static GunConfiguration getOverride(ResourceLocation id) {
    return overrides.get(id);
  }

  /**
   * Checks if overrides have been loaded.
   */
  public static boolean isLoaded() {
    return loaded;
  }

  /**
   * Gets the number of loaded overrides.
   */
  public static int getOverrideCount() {
    return overrides.size();
  }

  /**
   * Creates an example gun configuration file to show server admins the format.
   */
  private static void createExampleConfig() {
    try {
      Path exampleFile = CONFIG_DIR.resolve("_example_ak47.json.disabled");
      String exampleJson = """
          {
            "_comment": "This is an example gun configuration override.",
            "_instructions": "To use this, rename to 'ak47.json' (remove .disabled extension).",
            "_note": "This file overrides the AK47 datapack config. Adjust values as needed.",
            "fire_modes": ["auto", "semi"],
            "range": 175.0,
            "accuracy_percent": 0.83,
            "recoil": 3.75,
            "fire_delay_ms": 114.2,
            "damage": 5.5,
            "reload_duration_ticks": 55,
            "crosshair_enabled": true,
            "secondary_action_trigger": "toggle",
            "sounds": {
              "reload_sound": "craftingdead:ak47_reload",
              "shoot_sound": "craftingdead:ak47_shoot",
              "distant_shoot_sound": "craftingdead:ak47_distant_shoot",
              "silenced_shoot_sound": "craftingdead:silenced_ak47_shoot"
            }
          }
          """;
      
      Files.writeString(exampleFile, exampleJson);
      logger.info("Created example gun config at: {}", exampleFile);
    } catch (IOException e) {
      logger.warn("Failed to create example gun config", e);
    }
  }

  /**
   * Applies an override to a base configuration.
   * 
   * @param base The base configuration from datapacks
   * @param id The gun's resource location
   * @return The base configuration with overrides applied, or just the base if no override exists
   */
  public static GunConfiguration applyOverride(GunConfiguration base, ResourceLocation id) {
    GunConfiguration override = getOverride(id);
    return override != null ? override : base;
  }

  /**
   * Checks if a specific gun has an override configured.
   */
  public static boolean hasOverride(ResourceLocation id) {
    return overrides.containsKey(id);
  }
}
