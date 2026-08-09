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

package com.craftingdead.core.trauma;

import com.craftingdead.core.trauma.ProtectionConfig.HeadTraumaSettings;
import com.craftingdead.core.trauma.ProtectionConfig.ProtectionProfile;
import com.craftingdead.core.trauma.ProtectionConfig.TraumaEffect;
import com.craftingdead.core.world.entity.extension.LivingExtension;
import com.craftingdead.core.world.item.ClothingItem;
import com.craftingdead.core.world.item.equipment.Equipment;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public final class ProtectionLookup {

  public static final ProtectionLookup INSTANCE = new ProtectionLookup();

  private static final ResourceLocation JUGGERNAUT_CLOTHING_ID =
      new ResourceLocation("craftingdead", "juggernaut_clothing");

  private ProtectionLookup() {
  }

  public HeadshotResult resolveHeadshot(LivingExtension<?, ?> target, float incomingEnergy) {
    ProtectionConfig config = ProtectionConfig.get();
    HeadTraumaSettings settings = config.headTrauma();
    if (!settings.enabled() || incomingEnergy <= 0.0F) {
      return HeadshotResult.noEffect();
    }

    LivingEntity entity = target.entity();
    float energy = Math.max(0.0F, incomingEnergy);
    float helmetAbsorbed = 0.0F;
    float vestAbsorbed = 0.0F;
    int helmetDurabilityLoss = 0;
    int vestDurabilityLoss = 0;
    boolean helmetEquipped = false;
    boolean forcedSevere = false;

    ItemStack helmetStack = target.getItemInSlot(Equipment.Slot.HAT);
    ResourceLocation helmetId = idForItem(helmetStack.getItem());
    ProtectionProfile helmetProfile = resolveProfile(helmetStack,
        config.helmetProfile(helmetId), helmetId != null && config.isHelmetCosmetic(helmetId));
    if (!helmetProfile.isEmpty()) {
      helmetEquipped = true;
      helmetAbsorbed = Math.min(energy * helmetProfile.absorption(), helmetProfile.stoppingPower());
      energy = Math.max(0.0F, energy - helmetAbsorbed);
      if (helmetProfile.stunThreshold() > 0.0F && incomingEnergy > helmetProfile.stunThreshold()) {
        forcedSevere = true;
      }
      if (!entity.level.isClientSide()) {
        helmetDurabilityLoss = applyDurabilityLoss(helmetStack, helmetProfile, helmetAbsorbed,
            entity, EquipmentSlot.HEAD);
      }
    } else {
      energy *= settings.noHelmetMultiplier();
    }

    ItemStack vestStack = target.getItemInSlot(Equipment.Slot.VEST);
    ProtectionProfile vestProfile = resolveProfile(vestStack,
        config.vestProfile(idForItem(vestStack.getItem())),
        config.isVestCosmetic(idForItem(vestStack.getItem())));
    if (!vestProfile.isEmpty() && energy > 0.0F) {
      float share = Math.min(energy * 0.15F, vestProfile.stoppingPower() * 0.5F);
      vestAbsorbed = Math.min(energy, share);
      energy = Math.max(0.0F, energy - vestAbsorbed);
      if (!entity.level.isClientSide()) {
        vestDurabilityLoss = applyDurabilityLoss(vestStack, vestProfile, vestAbsorbed, entity,
            EquipmentSlot.CHEST);
      }
    }

    float energyBeforeClothing = energy;
    energy = applyClothingMitigation(target, energy);
    float clothingMitigated = energyBeforeClothing - energy;

    TraumaSeverity severity = config.determineSeverity(energy, forcedSevere);
    TraumaEffect effect = config.effectForSeverity(severity);
    return new HeadshotResult(severity, effect, energy, helmetAbsorbed, vestAbsorbed,
        helmetEquipped, Math.max(0.0F, clothingMitigated), helmetDurabilityLoss,
        vestDurabilityLoss);
  }

  private ProtectionProfile resolveProfile(ItemStack stack, ProtectionProfile profile,
      boolean cosmetic) {
    if (stack.isEmpty() || cosmetic) {
      return ProtectionProfile.EMPTY;
    }
    return profile;
  }

  private int applyDurabilityLoss(ItemStack stack, ProtectionProfile profile, float absorbedEnergy,
      LivingEntity entity, EquipmentSlot equipmentSlot) {
    if (stack.isEmpty() || !stack.isDamageableItem() || absorbedEnergy <= 0.0F) {
      return 0;
    }
    int durabilityLoss = Mth.floor(absorbedEnergy * profile.durabilityPerEnergy());
    if (durabilityLoss <= 0) {
      durabilityLoss = 1;
    }
    int finalLoss = durabilityLoss;
    if (stack.hurt(finalLoss, Objects.requireNonNull(entity.getRandom()),
        entity instanceof ServerPlayer serverPlayer ? serverPlayer : null)) {
      entity.broadcastBreakEvent(Objects.requireNonNull(equipmentSlot));
    }
    return finalLoss;
  }

  private float applyClothingMitigation(LivingExtension<?, ?> target, float energy) {
    ItemStack clothingStack = target.getItemInSlot(Equipment.Slot.CLOTHING);
    if (clothingStack.isEmpty()) {
      return energy;
    }
    if (clothingStack.getItem() instanceof ClothingItem clothingItem) {
      energy = Math.max(0.0F, clothingItem.calculateDamage(energy));
      if (isJuggernautClothing(clothingStack)) {
        energy *= 0.85F;
      }
    }
    return energy;
  }

  private boolean isJuggernautClothing(ItemStack stack) {
    ResourceLocation id = idForItem(stack.getItem());
    return JUGGERNAUT_CLOTHING_ID.equals(id);
  }

  private static ResourceLocation idForItem(Item item) {
    return ForgeRegistries.ITEMS.getKey(item);
  }

  public record HeadshotResult(TraumaSeverity severity,
      TraumaEffect effect,
      float remainingEnergy,
      float helmetAbsorbed,
      float vestAbsorbed,
      boolean helmetEquipped,
      float clothingMitigated,
      int helmetDurabilityLoss,
      int vestDurabilityLoss) {

    public static HeadshotResult noEffect() {
      return new HeadshotResult(TraumaSeverity.NONE, TraumaEffect.EMPTY, 0.0F, 0.0F, 0.0F,
          false, 0.0F, 0, 0);
    }
  }
}
