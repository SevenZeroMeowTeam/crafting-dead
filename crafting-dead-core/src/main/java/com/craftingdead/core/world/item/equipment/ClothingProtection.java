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

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Manages passive protection attributes for clothing items without making them direct armor.
 * These modifiers affect infection chance, bleeding, damage reduction, and movement.
 * 
 * <p>Protection attributes are stored in NBT data and can be configured per-item or per-type.
 * This system integrates with the existing LivingHandler damage pipeline.</p>
 * 
 * @author Crafting Dead Team
 */
public class ClothingProtection {

  // NBT tag keys for storing protection attributes
  private static final String BITE_PROTECTION_TAG = "BiteProtection";
  private static final String STAB_RESISTANCE_TAG = "StabResistance";
  private static final String BLUNT_RESISTANCE_TAG = "BluntResistance";
  private static final String WEIGHT_MODIFIER_TAG = "WeightModifier";
  private static final String CLOTHING_TIER_TAG = "ClothingTier";

  /**
   * Gets the bite protection value from a clothing item.
   * Bite protection reduces the chance of infection from zombie attacks.
   * 
   * @param itemStack the clothing item to check
   * @return protection value (0.0 = no protection, 1.0 = full protection)
   */
  public static float getBiteProtection(@NotNull ItemStack itemStack) {
    if (itemStack.isEmpty() || !itemStack.has(DataComponents.CUSTOM_DATA)) {
      return 0.0F;
    }
    CompoundTag tag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).getUnsafe();
    return tag != null ? tag.getFloat(BITE_PROTECTION_TAG) : 0.0F;
  }

  /**
   * Gets the stab resistance value from a clothing item.
   * Stab resistance reduces bleeding chance from melee/stabbing attacks.
   * 
   * @param itemStack the clothing item to check
   * @return resistance value (0.0 = no resistance, 1.0 = full resistance)
   */
  public static float getStabResistance(@NotNull ItemStack itemStack) {
    if (itemStack.isEmpty() || !itemStack.has(DataComponents.CUSTOM_DATA)) {
      return 0.0F;
    }
    CompoundTag tag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).getUnsafe();
    return tag != null ? tag.getFloat(STAB_RESISTANCE_TAG) : 0.0F;
  }

  /**
   * Gets the blunt resistance value from a clothing item.
   * Blunt resistance reduces direct damage from blunt weapons.
   * 
   * @param itemStack the clothing item to check
   * @return resistance value (0.0 = no resistance, 1.0 = full resistance)
   */
  public static float getBluntResistance(@NotNull ItemStack itemStack) {
    if (itemStack.isEmpty() || !itemStack.has(DataComponents.CUSTOM_DATA)) {
      return 0.0F;
    }
    CompoundTag tag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).getUnsafe();
    return tag != null ? tag.getFloat(BLUNT_RESISTANCE_TAG) : 0.0F;
  }

  /**
   * Gets the weight modifier from a clothing item.
   * Weight affects movement speed (negative = slower, positive = faster).
   * 
   * @param itemStack the clothing item to check
   * @return weight modifier (-1.0 to 1.0, typically -0.1 to 0.0)
   */
  public static float getWeightModifier(@NotNull ItemStack itemStack) {
    if (itemStack.isEmpty() || !itemStack.has(DataComponents.CUSTOM_DATA)) {
      return 0.0F;
    }
    CompoundTag tag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).getUnsafe();
    return tag != null ? tag.getFloat(WEIGHT_MODIFIER_TAG) : 0.0F;
  }

  /**
   * Gets the clothing tier/classification.
   * Tiers help categorize clothing by protection level (casual, utility, military, heavy).
   * 
   * @param itemStack the clothing item to check
   * @return tier string (default: "casual")
   */
  public static String getClothingTier(@NotNull ItemStack itemStack) {
    if (itemStack.isEmpty() || !itemStack.has(DataComponents.CUSTOM_DATA)) {
      return "casual";
    }
    CompoundTag tag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).getUnsafe();
    return tag != null ? tag.getString(CLOTHING_TIER_TAG) : "casual";
  }

  /**
   * Sets bite protection on a clothing item.
   * 
   * @param itemStack the clothing item to modify
   * @param protection protection value (0.0 - 1.0)
   */
  public static void setBiteProtection(@NotNull ItemStack itemStack, float protection) {
    CustomData.update(DataComponents.CUSTOM_DATA, itemStack,
        tag -> tag.putFloat(BITE_PROTECTION_TAG, clamp(protection, 0.0F, 1.0F)));
  }

  /**
   * Sets stab resistance on a clothing item.
   * 
   * @param itemStack the clothing item to modify
   * @param resistance resistance value (0.0 - 1.0)
   */
  public static void setStabResistance(@NotNull ItemStack itemStack, float resistance) {
    CustomData.update(DataComponents.CUSTOM_DATA, itemStack,
        tag -> tag.putFloat(STAB_RESISTANCE_TAG, clamp(resistance, 0.0F, 1.0F)));
  }

  /**
   * Sets blunt resistance on a clothing item.
   * 
   * @param itemStack the clothing item to modify
   * @param resistance resistance value (0.0 - 1.0)
   */
  public static void setBluntResistance(@NotNull ItemStack itemStack, float resistance) {
    CustomData.update(DataComponents.CUSTOM_DATA, itemStack,
        tag -> tag.putFloat(BLUNT_RESISTANCE_TAG, clamp(resistance, 0.0F, 1.0F)));
  }

  /**
   * Sets weight modifier on a clothing item.
   * 
   * @param itemStack the clothing item to modify
   * @param weight weight modifier (-1.0 to 1.0)
   */
  public static void setWeightModifier(@NotNull ItemStack itemStack, float weight) {
    CustomData.update(DataComponents.CUSTOM_DATA, itemStack,
        tag -> tag.putFloat(WEIGHT_MODIFIER_TAG, clamp(weight, -1.0F, 1.0F)));
  }

  /**
   * Sets the clothing tier.
   * 
   * @param itemStack the clothing item to modify
   * @param tier tier name (e.g., "casual", "utility", "military", "heavy")
   */
  public static void setClothingTier(@NotNull ItemStack itemStack, String tier) {
    CustomData.update(DataComponents.CUSTOM_DATA, itemStack,
        tag -> tag.putString(CLOTHING_TIER_TAG, tier));
  }

  /**
   * Applies all protection attributes to a clothing item at once.
   * 
   * @param itemStack the clothing item to modify
   * @param biteProtection bite protection (0.0 - 1.0)
   * @param stabResistance stab resistance (0.0 - 1.0)
   * @param bluntResistance blunt resistance (0.0 - 1.0)
   * @param weightModifier weight modifier (-1.0 to 1.0)
   * @param tier clothing tier
   */
  public static void applyProtectionAttributes(@NotNull ItemStack itemStack,
      float biteProtection, float stabResistance, float bluntResistance,
      float weightModifier, String tier) {
    setBiteProtection(itemStack, biteProtection);
    setStabResistance(itemStack, stabResistance);
    setBluntResistance(itemStack, bluntResistance);
    setWeightModifier(itemStack, weightModifier);
    setClothingTier(itemStack, tier);
  }

  /**
   * Calculates the final infection chance after applying bite protection.
   * 
   * @param baseInfectionChance base chance before protection
   * @param biteProtection protection value from clothing (0.0 - 1.0)
   * @return final infection chance
   */
  public static float calculateInfectionChance(float baseInfectionChance, float biteProtection) {
    return baseInfectionChance * (1.0F - biteProtection);
  }

  /**
   * Calculates the final bleeding chance after applying stab resistance.
   * 
   * @param baseBleedChance base chance before resistance
   * @param stabResistance resistance value from clothing (0.0 - 1.0)
   * @return final bleed chance
   */
  public static float calculateBleedChance(float baseBleedChance, float stabResistance) {
    return baseBleedChance * (1.0F - stabResistance);
  }

  /**
   * Calculates the final damage after applying blunt resistance.
   * 
   * @param baseDamage base damage before resistance
   * @param bluntResistance resistance value from clothing (0.0 - 1.0)
   * @return final damage amount
   */
  public static float calculateFinalDamage(float baseDamage, float bluntResistance) {
    return baseDamage * (1.0F - bluntResistance);
  }

  /**
   * Checks if a clothing item has any protection attributes set.
   * 
   * @param itemStack the clothing item to check
   * @return true if any protection attribute is non-zero
   */
  public static boolean hasProtectionAttributes(@NotNull ItemStack itemStack) {
    return getBiteProtection(itemStack) > 0.0F
        || getStabResistance(itemStack) > 0.0F
        || getBluntResistance(itemStack) > 0.0F
        || Math.abs(getWeightModifier(itemStack)) > 0.001F;
  }

  /**
   * Utility method to clamp a value between min and max.
   */
  private static float clamp(float value, float min, float max) {
    return Math.max(min, Math.min(max, value));
  }
}
