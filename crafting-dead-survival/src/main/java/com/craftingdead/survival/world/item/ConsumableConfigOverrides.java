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

package com.craftingdead.survival.world.item;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;

/**
 * Manages consumable (food/drink) value overrides from config/craftingdead/items/consumables.toml.
 * These overrides allow server admins to fine-tune food nutrition and drink hydration values
 * without modifying item definitions or datapacks.
 * 
 * Priority order:
 * 1. config/craftingdead/items/consumables.toml (highest priority)
 * 2. Global multipliers from ServerConfig
 * 3. Item default values (lowest priority)
 */
public class ConsumableConfigOverrides {

  private static final Logger logger = LogUtils.getLogger();
  private static final Path CONFIG_FILE = FMLPaths.CONFIGDIR.get()
      .resolve("craftingdead").resolve("items").resolve("consumables.toml");

  private static final Map<ResourceLocation, FoodOverride> foodOverrides = new HashMap<>();
  private static final Map<ResourceLocation, DrinkOverride> drinkOverrides = new HashMap<>();
  private static boolean loaded = false;

  public record FoodOverride(Integer nutrition, Float saturation) {}
  public record DrinkOverride(Integer hydration) {}

  /**
   * Loads consumable configuration overrides from config file.
   */
  public static void loadOverrides() {
    foodOverrides.clear();
    drinkOverrides.clear();
    loaded = false;

    try {
      Path configDir = CONFIG_FILE.getParent();
      if (!Files.exists(configDir)) {
        Files.createDirectories(configDir);
      }

      if (!Files.exists(CONFIG_FILE)) {
        createDefaultConfig();
      }

      CommentedFileConfig config = CommentedFileConfig.of(CONFIG_FILE);
      config.load();

      // Load food value overrides
      if (config.contains("food_values")) {
        var foodSection = config.get("food_values");
        if (foodSection instanceof com.electronwill.nightconfig.core.UnmodifiableConfig foodConfig) {
          foodConfig.valueMap().forEach((key, value) -> {
            try {
              ResourceLocation id = new ResourceLocation(key);
              if (value instanceof com.electronwill.nightconfig.core.UnmodifiableConfig itemConfig) {
                Integer nutrition = itemConfig.contains("nutrition") 
                    ? itemConfig.<Number>get("nutrition").intValue() : null;
                Float saturation = itemConfig.contains("saturation") 
                    ? itemConfig.<Number>get("saturation").floatValue() : null;
                foodOverrides.put(id, new FoodOverride(nutrition, saturation));
                logger.debug("Loaded food override: {} -> nutrition={}, saturation={}", 
                    id, nutrition, saturation);
              }
            } catch (Exception e) {
              logger.error("Failed to parse food override for: {}", key, e);
            }
          });
        }
      }

      // Load drink value overrides
      if (config.contains("drink_values")) {
        var drinkSection = config.get("drink_values");
        if (drinkSection instanceof com.electronwill.nightconfig.core.UnmodifiableConfig drinkConfig) {
          drinkConfig.valueMap().forEach((key, value) -> {
            try {
              ResourceLocation id = new ResourceLocation(key);
              if (value instanceof Number number) {
                drinkOverrides.put(id, new DrinkOverride(number.intValue()));
                logger.debug("Loaded drink override: {} -> hydration={}", id, number.intValue());
              }
            } catch (Exception e) {
              logger.error("Failed to parse drink override for: {}", key, e);
            }
          });
        }
      }

      config.close();
      loaded = true;
      logger.info("Loaded {} food override(s) and {} drink override(s)", 
          foodOverrides.size(), drinkOverrides.size());

    } catch (IOException e) {
      logger.error("Failed to load consumable config", e);
    }
  }

  /**
   * Gets a food value override if one exists.
   */
  public static FoodOverride getFoodOverride(ResourceLocation itemId) {
    return foodOverrides.get(itemId);
  }

  /**
   * Gets a drink value override if one exists.
   */
  public static DrinkOverride getDrinkOverride(ResourceLocation itemId) {
    return drinkOverrides.get(itemId);
  }

  /**
   * Checks if overrides have been loaded.
   */
  public static boolean isLoaded() {
    return loaded;
  }

  /**
   * Creates a default configuration file with examples.
   */
  private static void createDefaultConfig() {
    try {
      String defaultConfig = """
          # Crafting Dead - Consumables Configuration
          # Override food nutrition/saturation and drink hydration values per-item.
          # These overrides take precedence over global multipliers.
          # Format: "mod:item_id" = value (for drinks) or { nutrition = X, saturation = Y } (for food)
          
          [food_values]
          # Examples (disabled by default):
          # "minecraft:bread" = { nutrition = 5, saturation = 6.0 }
          # "craftingdead:energy_bar" = { nutrition = 8, saturation = 8.0 }
          # "minecraft:golden_apple" = { nutrition = 10, saturation = 12.0 }
          
          [drink_values]
          # Examples (disabled by default):
          # "craftingdead:water_bottle" = 10
          # "craftingdead:dirty_water_bottle" = 6
          # "craftingdead:radiated_water_bottle" = 4
          # "craftingdead:coffee" = 12
          """;
      
      Files.writeString(CONFIG_FILE, defaultConfig);
      logger.info("Created default consumables config at: {}", CONFIG_FILE);
    } catch (IOException e) {
      logger.error("Failed to create default consumables config", e);
    }
  }
}
