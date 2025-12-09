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

package com.craftingdead.core.world.entity.extension;

import com.craftingdead.core.CraftingDead;
import com.craftingdead.core.world.item.equipment.ClothingProtection;
import com.craftingdead.core.world.item.equipment.Equipment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles passive protection mechanics for clothing items.
 * Integrates with the existing LivingHandler damage pipeline to apply protection modifiers
 * based on damage type, body region, and clothing attributes.
 * 
 * <p>This handler processes damage AFTER armor but BEFORE final damage is applied,
 * allowing clothing to provide passive benefits without becoming direct armor.</p>
 * 
 * <p>Implements PlayerHandler to ensure compatibility with PlayerExtensions while
 * maintaining LivingHandler functionality for all living entities.</p>
 * 
 * @author Crafting Dead Team
 */
public class ClothingProtectionHandler implements PlayerHandler {

  public static final LivingHandlerType<ClothingProtectionHandler> TYPE =
      new LivingHandlerType<>(new ResourceLocation(CraftingDead.ID, "clothing_protection"));

  private static final Logger logger = LoggerFactory.getLogger(ClothingProtectionHandler.class);

  private final LivingExtension<?, ?> extension;

  // Attack type detection strings
  private static final String ZOMBIE_DAMAGE = "mob";
  private static final String MELEE_DAMAGE = "player";
  private static final String BLUNT_DAMAGE = "blunt";
  private static final String STAB_DAMAGE = "stab";

  public ClothingProtectionHandler(LivingExtension<?, ?> extension) {
    this.extension = extension;
  }

  /**
   * Intercepts damage events to apply clothing protection modifiers.
   * Called after armor damage reduction but before final damage is applied.
   * 
   * @param source the damage source
   * @param amount the current damage amount
   * @return modified damage amount after clothing protection
   */
  @Override
  public float handleDamaged(DamageSource source, float amount) {
    // Get the clothing item from the clothing slot
    ItemStack clothingStack = this.extension.getItemInSlot(Equipment.Slot.CLOTHING);
    
    if (clothingStack.isEmpty()) {
      // No clothing equipped, return original damage
      return amount;
    }

    // Check if clothing has protection attributes
    if (!ClothingProtection.hasProtectionAttributes(clothingStack)) {
      return amount;
    }

    float finalDamage = amount;
    DamageType damageType = detectDamageType(source);

    // Apply appropriate protection based on damage type
    switch (damageType) {
      case ZOMBIE_BITE:
        // Bite protection reduces both damage and infection chance
        float biteProtection = ClothingProtection.getBiteProtection(clothingStack);
        finalDamage = ClothingProtection.calculateFinalDamage(amount, biteProtection * 0.3F);
        
        // Log infection reduction (actual infection logic should be elsewhere)
        if (biteProtection > 0.0F) {
          float baseInfectionChance = 0.15F; // Example base chance
          float finalInfectionChance = ClothingProtection.calculateInfectionChance(
              baseInfectionChance, biteProtection);
          logger.debug("Bite attack: Damage {} -> {}, Infection chance {} -> {}",
              amount, finalDamage, baseInfectionChance, finalInfectionChance);
        }
        break;

      case MELEE_STAB:
        // Stab resistance reduces bleeding and some damage
        float stabResistance = ClothingProtection.getStabResistance(clothingStack);
        finalDamage = ClothingProtection.calculateFinalDamage(amount, stabResistance * 0.2F);
        
        if (stabResistance > 0.0F) {
          float baseBleedChance = 0.25F; // Example base chance
          float finalBleedChance = ClothingProtection.calculateBleedChance(
              baseBleedChance, stabResistance);
          logger.debug("Stab attack: Damage {} -> {}, Bleed chance {} -> {}",
              amount, finalDamage, baseBleedChance, finalBleedChance);
        }
        break;

      case BLUNT_IMPACT:
        // Blunt resistance reduces direct impact damage
        float bluntResistance = ClothingProtection.getBluntResistance(clothingStack);
        finalDamage = ClothingProtection.calculateFinalDamage(amount, bluntResistance);
        
        if (bluntResistance > 0.0F) {
          logger.debug("Blunt attack: Damage {} -> {} ({}% reduction)",
              amount, finalDamage, bluntResistance * 100.0F);
        }
        break;

      case PROJECTILE:
        // Projectiles bypass most clothing protection (use vests/armor for this)
        // Apply minimal protection
        float minimalProtection = Math.max(
            ClothingProtection.getBiteProtection(clothingStack),
            ClothingProtection.getStabResistance(clothingStack)) * 0.1F;
        finalDamage = ClothingProtection.calculateFinalDamage(amount, minimalProtection);
        break;

      case ENVIRONMENTAL:
      case UNKNOWN:
      default:
        // No clothing protection for environmental damage (fire, fall, etc.)
        break;
    }

    // Log the final result for testing/debugging
    if (finalDamage != amount) {
      logger.info("Clothing protection applied: {} damage reduced to {} (Type: {}, Tier: {})",
          amount, finalDamage, damageType, 
          ClothingProtection.getClothingTier(clothingStack));
    }

    return finalDamage;
  }

  /**
   * Detects the type of damage from the DamageSource.
   * This categorization helps apply appropriate protection modifiers.
   * 
   * @param source the damage source
   * @return the detected damage type
   */
  private DamageType detectDamageType(DamageSource source) {
    String sourceMsg = source.getMsgId().toLowerCase();
    
    // Check for zombie/monster attacks
    if (source.getEntity() instanceof LivingEntity attacker) {
      String attackerType = attacker.getType().toString().toLowerCase();
      if (attackerType.contains("zombie") || sourceMsg.contains("mob")) {
        return DamageType.ZOMBIE_BITE;
      }
    }

    // Check for melee attacks
    if (sourceMsg.contains("player") || sourceMsg.contains("mob")) {
      // Further categorize melee into stab vs blunt
      // This could be enhanced by checking weapon type from source
      if (sourceMsg.contains("stab") || sourceMsg.contains("blade") 
          || sourceMsg.contains("knife")) {
        return DamageType.MELEE_STAB;
      }
      return DamageType.BLUNT_IMPACT; // Default melee to blunt
    }

    // Check for projectiles
    if (source.isProjectile() || sourceMsg.contains("arrow") 
        || sourceMsg.contains("bullet")) {
      return DamageType.PROJECTILE;
    }

    // Environmental damage (fire, fall, drown, etc.)
    if (source.isFire() || source.isFall() || sourceMsg.contains("fall")
        || sourceMsg.contains("drown") || sourceMsg.contains("lava")) {
      return DamageType.ENVIRONMENTAL;
    }

    return DamageType.UNKNOWN;
  }

  /**
   * Gets the body region that was hit (for future expansion).
   * Currently returns TORSO as default since the clothing slot covers torso.
   * 
   * <p>Future enhancement: Integrate with hit detection system to determine
   * if damage was to head (covered by hat), torso (covered by clothing),
   * or limbs (partially covered).</p>
   * 
   * @param source the damage source
   * @return the body region that was hit
   */
  @SuppressWarnings("unused")
  private BodyRegion detectBodyRegion(DamageSource source) {
    // For now, clothing slot always protects torso
    // In future, integrate with hit detection system to get actual hit location
    return BodyRegion.TORSO;
  }

  /**
   * Categories of damage types for applying appropriate protection.
   */
  private enum DamageType {
    ZOMBIE_BITE,    // Zombie attacks - high infection risk
    MELEE_STAB,     // Sharp melee weapons - high bleed risk
    BLUNT_IMPACT,   // Blunt weapons/fists - direct damage
    PROJECTILE,     // Arrows, bullets - bypass most clothing
    ENVIRONMENTAL,  // Fire, fall, drown - no clothing protection
    UNKNOWN         // Unclassified damage
  }

  /**
   * Body regions for targeted protection (future expansion).
   */
  private enum BodyRegion {
    HEAD,      // Protected by hat slot
    TORSO,     // Protected by clothing slot
    LIMBS      // Partially protected by clothing
  }

  @Override
  public void encode(net.minecraft.network.FriendlyByteBuf out, boolean writeAll) {
    // No data to sync - protection is stored in item NBT
  }

  @Override
  public void decode(net.minecraft.network.FriendlyByteBuf in) {
    // No data to sync - protection is stored in item NBT
  }

  @Override
  public boolean requiresSync() {
    // No syncing required - protection is stored in item NBT
    return false;
  }
}
