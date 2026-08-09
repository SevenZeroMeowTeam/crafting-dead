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

package com.craftingdead.core.world.item.equipment;

import com.craftingdead.core.ServerConfig;
import com.craftingdead.core.world.item.ClothingItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for initializing clothing items with protection attributes.
 * All values are read from ServerConfig for full server-side configurability.
 * 
 * @author Crafting Dead Team
 */
public class ClothingProtectionPresets {

  /**
   * Applies protection based on ClothingType using server config values.
   * This is the recommended method for applying protection to clothing items.
   * 
   * @param itemStack the clothing item to initialize
   * @param type the clothing type (CASUAL, UTILITY, MILITARY)
   */
  public static void applyByType(@NotNull ItemStack itemStack, @NotNull ClothingItem.ClothingType type) {
    switch (type) {
      case CASUAL -> applyCasualClothing(itemStack);
      case UTILITY -> applyUtilityClothing(itemStack);
      case MILITARY -> applyMilitaryClothing(itemStack);
      case HEAVY -> applyHeavyClothing(itemStack);
    }
  }

  /**
   * Initializes casual clothing using server config values.
   * 
   * @param itemStack the clothing item to initialize
   */
  public static void applyCasualClothing(@NotNull ItemStack itemStack) {
    ClothingProtection.applyProtectionAttributes(
        itemStack,
        ServerConfig.instance.casualClothingBiteProtection.get().floatValue(),
        ServerConfig.instance.casualClothingStabResistance.get().floatValue(),
        ServerConfig.instance.casualClothingBluntResistance.get().floatValue(),
        ServerConfig.instance.casualClothingWeightModifier.get().floatValue(),
        "casual"
    );
  }

  /**
   * Initializes utility clothing using server config values.
   * 
   * @param itemStack the clothing item to initialize
   */
  public static void applyUtilityClothing(@NotNull ItemStack itemStack) {
    ClothingProtection.applyProtectionAttributes(
        itemStack,
        ServerConfig.instance.utilityClothingBiteProtection.get().floatValue(),
        ServerConfig.instance.utilityClothingStabResistance.get().floatValue(),
        ServerConfig.instance.utilityClothingBluntResistance.get().floatValue(),
        ServerConfig.instance.utilityClothingWeightModifier.get().floatValue(),
        "utility"
    );
  }

  /**
   * Initializes military clothing using server config values.
   * 
   * @param itemStack the clothing item to initialize
   */
  public static void applyMilitaryClothing(@NotNull ItemStack itemStack) {
    ClothingProtection.applyProtectionAttributes(
        itemStack,
        ServerConfig.instance.militaryClothingBiteProtection.get().floatValue(),
        ServerConfig.instance.militaryClothingStabResistance.get().floatValue(),
        ServerConfig.instance.militaryClothingBluntResistance.get().floatValue(),
        ServerConfig.instance.militaryClothingWeightModifier.get().floatValue(),
        "military"
    );
  }

  /**
   * Initializes heavy clothing (Juggernaut) using server config values.
   * 
   * @param itemStack the clothing item to initialize
   */
  public static void applyHeavyClothing(@NotNull ItemStack itemStack) {
    ClothingProtection.applyProtectionAttributes(
        itemStack,
        ServerConfig.instance.heavyClothingBiteProtection.get().floatValue(),
        ServerConfig.instance.heavyClothingStabResistance.get().floatValue(),
        ServerConfig.instance.heavyClothingBluntResistance.get().floatValue(),
        ServerConfig.instance.heavyClothingWeightModifier.get().floatValue(),
        "heavy"
    );
  }
}
