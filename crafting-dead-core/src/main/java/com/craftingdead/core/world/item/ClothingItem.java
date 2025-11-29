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

package com.craftingdead.core.world.item;

import com.craftingdead.core.ServerConfig;
import com.craftingdead.core.world.action.item.ItemActionType;
import com.craftingdead.core.world.entity.extension.LivingExtension;
import com.craftingdead.core.world.entity.extension.PlayerExtension;
import com.craftingdead.core.world.item.equipment.ClothingProtection;
import com.craftingdead.core.world.item.equipment.Equipment.Slot;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.craftingdead.core.capability.CapabilityUtil;
import com.craftingdead.core.world.item.equipment.Equipment;
import com.craftingdead.core.world.item.equipment.SimpleClothing;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class ClothingItem extends EquipmentItem {

  public enum ClothingType {
    CASUAL,
    UTILITY,
    MILITARY,
    HEAVY
  }

  public static final UUID ARMOR_MODIFIER_ID =
      UUID.fromString("4117e432-16f5-4eea-a4fe-127b54d39af1");

  private final Multimap<Attribute, AttributeModifier> attributeModifiers;
  private final boolean fireImmunity;
  private final boolean enhancesSwimming;
  private final Supplier<? extends ItemActionType<?>> itemActionType;
  private final ClothingType clothingType;

  public ClothingItem(Properties properties, Supplier<? extends ItemActionType<?>> itemActionType,
      ClothingType clothingType) {
    super(properties);
    this.attributeModifiers = properties.attributeModifiers.build();
    this.fireImmunity = properties.fireImmunity;
    this.enhancesSwimming = properties.enhancesSwimming;
    this.itemActionType = itemActionType;
    this.clothingType = clothingType;
  }

  public ItemActionType<?> getActionType() {
    return this.itemActionType.get();
  }

  public ClothingType getClothingType() {
    return this.clothingType;
  }

  @Override
  public void appendHoverText(ItemStack stack, Level world, List<Component> lines, TooltipFlag tooltipFlag) {
    super.appendHoverText(stack, world, lines, tooltipFlag);
    
    // Clothing tier display
    switch (this.clothingType) {
      case CASUAL -> lines.add(new TranslatableComponent("clothing.protection.level")
          .withStyle(ChatFormatting.GRAY)
          .append(" ")
          .append(new TranslatableComponent("clothing.casual")
          .withStyle(ChatFormatting.WHITE)));
      case UTILITY -> lines.add(new TranslatableComponent("clothing.protection.level")
          .withStyle(ChatFormatting.GRAY)
          .append(" ")
          .append(new TranslatableComponent("clothing.utility")
          .withStyle(ChatFormatting.GREEN)));
      case MILITARY -> lines.add(new TranslatableComponent("clothing.protection.level")
          .withStyle(ChatFormatting.GRAY)
          .append(" ")
          .append(new TranslatableComponent("clothing.military")
          .withStyle(ChatFormatting.AQUA)));
      case HEAVY -> lines.add(new TranslatableComponent("clothing.protection.level")
          .withStyle(ChatFormatting.GRAY)
          .append(" ")
          .append(new TranslatableComponent("clothing.heavy")
          .withStyle(ChatFormatting.GOLD)));
    }
    
    // Fire immunity indicator
    if (this.fireImmunity) {
      lines.add(new TextComponent("※ ")
          .withStyle(ChatFormatting.GOLD)
          .append(new TranslatableComponent("clothing.immune_to_fire")
              .withStyle(ChatFormatting.GOLD)));
    }

    // Add blank line for separation
    lines.add(TextComponent.EMPTY);
    
    // Protection Stats Header
    lines.add(new TranslatableComponent("clothing.protection.stats")
        .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD));

    // Get protection values from NBT if available, otherwise show config defaults
    float biteProtection = ClothingProtection.getBiteProtection(stack);
    float stabResistance = ClothingProtection.getStabResistance(stack);
    float bluntResistance = ClothingProtection.getBluntResistance(stack);
    float weightModifier = ClothingProtection.getWeightModifier(stack);
    
    // Get legacy values
    float damageReductionPct = switch (this.clothingType) {
      case CASUAL -> ServerConfig.instance.casualClothingDamageReduction.get().floatValue() * 100.0F;
      case UTILITY -> ServerConfig.instance.utilityClothingDamageReduction.get().floatValue() * 100.0F;
      case MILITARY -> ServerConfig.instance.militaryClothingDamageReduction.get().floatValue() * 100.0F;
      case HEAVY -> ServerConfig.instance.militaryClothingDamageReduction.get().floatValue() * 100.0F;
    };
    
    float bleedReductionPct = switch (this.clothingType) {
      case CASUAL -> ServerConfig.instance.casualClothingBleedAndInfectionReduction.get().floatValue() * 100.0F;
      case UTILITY -> ServerConfig.instance.utilityClothingBleedAndInfectionReduction.get().floatValue() * 100.0F;
      case MILITARY -> ServerConfig.instance.militaryClothingBleedAndInfectionReduction.get().floatValue() * 100.0F;
      case HEAVY -> ServerConfig.instance.militaryClothingBleedAndInfectionReduction.get().floatValue() * 100.0F;
    };
    
    // If no NBT data yet, show what values will be applied
    if (!ClothingProtection.hasProtectionAttributes(stack)) {
      biteProtection = switch (this.clothingType) {
        case CASUAL -> ServerConfig.instance.casualClothingBiteProtection.get().floatValue();
        case UTILITY -> ServerConfig.instance.utilityClothingBiteProtection.get().floatValue();
        case MILITARY -> ServerConfig.instance.militaryClothingBiteProtection.get().floatValue();
        case HEAVY -> ServerConfig.instance.heavyClothingBiteProtection.get().floatValue();
      };
      stabResistance = switch (this.clothingType) {
        case CASUAL -> ServerConfig.instance.casualClothingStabResistance.get().floatValue();
        case UTILITY -> ServerConfig.instance.utilityClothingStabResistance.get().floatValue();
        case MILITARY -> ServerConfig.instance.militaryClothingStabResistance.get().floatValue();
        case HEAVY -> ServerConfig.instance.heavyClothingStabResistance.get().floatValue();
      };
      bluntResistance = switch (this.clothingType) {
        case CASUAL -> ServerConfig.instance.casualClothingBluntResistance.get().floatValue();
        case UTILITY -> ServerConfig.instance.utilityClothingBluntResistance.get().floatValue();
        case MILITARY -> ServerConfig.instance.militaryClothingBluntResistance.get().floatValue();
        case HEAVY -> ServerConfig.instance.heavyClothingBluntResistance.get().floatValue();
      };
      weightModifier = switch (this.clothingType) {
        case CASUAL -> ServerConfig.instance.casualClothingWeightModifier.get().floatValue();
        case UTILITY -> ServerConfig.instance.utilityClothingWeightModifier.get().floatValue();
        case MILITARY -> ServerConfig.instance.militaryClothingWeightModifier.get().floatValue();
        case HEAVY -> ServerConfig.instance.heavyClothingWeightModifier.get().floatValue();
      };
    }
    
    // General Damage Reduction (if any)
    if (damageReductionPct > 0.0F) {
      var damageText = new TextComponent(String.format(Locale.ROOT, "%.0f%%", damageReductionPct))
          .withStyle(ChatFormatting.GREEN);
      lines.add(new TextComponent("■ ")
          .withStyle(ChatFormatting.DARK_GRAY)
          .append(new TranslatableComponent("clothing.protection.general_damage")
              .withStyle(ChatFormatting.GRAY))
          .append(": ")
          .append(damageText));
    }
    
    // Bite Protection (zombies)
    if (biteProtection > 0.0F) {
      var biteText = new TextComponent(String.format(Locale.ROOT, "%.0f%%", biteProtection * 100.0F))
          .withStyle(ChatFormatting.GREEN);
      lines.add(new TextComponent("☠ ")
          .withStyle(ChatFormatting.DARK_GRAY)
          .append(new TranslatableComponent("clothing.protection.bite")
              .withStyle(ChatFormatting.GRAY))
          .append(": ")
          .append(biteText));
    }
    
    // Stab Resistance (knives, blades)
    if (stabResistance > 0.0F) {
      var stabText = new TextComponent(String.format(Locale.ROOT, "%.0f%%", stabResistance * 100.0F))
          .withStyle(ChatFormatting.GREEN);
      lines.add(new TextComponent("† ")
          .withStyle(ChatFormatting.DARK_GRAY)
          .append(new TranslatableComponent("clothing.protection.stab")
              .withStyle(ChatFormatting.GRAY))
          .append(": ")
          .append(stabText));
    }
    
    // Blunt Resistance (clubs, fists)
    if (bluntResistance > 0.0F) {
      var bluntText = new TextComponent(String.format(Locale.ROOT, "%.0f%%", bluntResistance * 100.0F))
          .withStyle(ChatFormatting.GREEN);
      lines.add(new TextComponent("◆ ")
          .withStyle(ChatFormatting.DARK_GRAY)
          .append(new TranslatableComponent("clothing.protection.blunt")
              .withStyle(ChatFormatting.GRAY))
          .append(": ")
          .append(bluntText));
    }
    
    // Bullet Resistance (minimal for clothing, use vests for real protection)
    float bulletProtection = Math.max(biteProtection, stabResistance) * 0.1F;
    if (bulletProtection > 0.001F) {
      var bulletText = new TextComponent(String.format(Locale.ROOT, "%.0f%%", bulletProtection * 100.0F))
          .withStyle(ChatFormatting.DARK_GREEN);
      lines.add(new TextComponent("✖ ")
          .withStyle(ChatFormatting.DARK_GRAY)
          .append(new TranslatableComponent("clothing.protection.bullet")
              .withStyle(ChatFormatting.GRAY))
          .append(": ")
          .append(bulletText));
    }
    
    // Infection Resistance
    if (bleedReductionPct > 0.0F) {
      var infectionText = new TextComponent(String.format(Locale.ROOT, "%.0f%%", bleedReductionPct))
          .withStyle(ChatFormatting.GREEN);
      lines.add(new TextComponent("✦ ")
          .withStyle(ChatFormatting.DARK_GRAY)
          .append(new TranslatableComponent("clothing.protection.infection")
              .withStyle(ChatFormatting.GRAY))
          .append(": ")
          .append(infectionText));
    }
    
    // Weight Modifier (affects movement speed)
    if (Math.abs(weightModifier) > 0.001F) {
      var weightColor = weightModifier < 0 ? ChatFormatting.RED : ChatFormatting.GREEN;
      var weightText = new TextComponent(String.format(Locale.ROOT, "%+.0f%%", weightModifier * 100.0F))
          .withStyle(weightColor);
      lines.add(new TextComponent("» ")
          .withStyle(ChatFormatting.DARK_GRAY)
          .append(new TranslatableComponent("clothing.protection.weight")
              .withStyle(ChatFormatting.GRAY))
          .append(": ")
          .append(weightText));
    }
  }

  @Override
  public ICapabilityProvider initCapabilities(ItemStack itemStack, @Nullable CompoundTag nbt) {
    return CapabilityUtil.provider(
        () -> new SimpleClothing(this.attributeModifiers, this.fireImmunity, this.enhancesSwimming,
            new ResourceLocation(Objects.requireNonNull(this.getRegistryName()).getNamespace(),
                "textures/clothing/"
                    + this.getRegistryName().getPath() + "_" + "default" + ".png")),
        Equipment.CAPABILITY);
  }

  @Override
  public @NotNull InteractionResult useOn(UseOnContext context) {
    if (!context.getLevel().isClientSide()) {
      var performer = PlayerExtension.getOrThrow(context.getPlayer());
      if (this.getActionType().createBlockAction(performer, context)
          .map(action -> performer.performAction(action, true))
          .orElse(false)) {
        return InteractionResult.CONSUME;
      }
    }
    return InteractionResult.PASS;
  }

  @Override
  public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack itemStack,
      Player player, @NotNull LivingEntity targetEntity, @NotNull InteractionHand hand) {
    if (!player.getLevel().isClientSide()) {
      var performer = PlayerExtension.getOrThrow(player);
      var target = LivingExtension.getOrThrow(targetEntity);
      if (this.getActionType().createEntityAction(performer, target, hand)
          .map(action -> performer.performAction(action, true))
          .orElse(false)) {
        return InteractionResult.CONSUME;
      }
    }
    return InteractionResult.PASS;
  }

  @Override
  public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player,
      @NotNull InteractionHand hand) {
    if (!player.getLevel().isClientSide()) {
      var performer = PlayerExtension.getOrThrow(player);
      var hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
      if (hitResult.getType() == HitResult.Type.BLOCK
          && this.getActionType()
          .createBlockAction(performer, new UseOnContext(player, hand, hitResult))
          .map(action -> performer.performAction(action, true))
          .orElse(false)) {
        return InteractionResultHolder.consume(player.getItemInHand(hand));
      }

      if (this.getActionType().createAction(performer, hand)
          .map(action -> performer.performAction(action, true))
          .orElse(false)) {
        return InteractionResultHolder.consume(player.getItemInHand(hand));
      }
    }

    return InteractionResultHolder.pass(player.getItemInHand(hand));
  }

  @Override
  public int getUseDuration(@NotNull ItemStack itemStack) {
    return this.getActionType().getDurationTicks();
  }

  public static class Properties extends Item.Properties {

    private final ImmutableMultimap.Builder<Attribute, AttributeModifier> attributeModifiers =
        ImmutableMultimap.builder();
    private boolean fireImmunity;
    private boolean enhancesSwimming;

    public Properties attributeModifier(Attribute attribute, AttributeModifier modifier) {
      this.attributeModifiers.put(attribute, modifier);
      return this;
    }

    public Properties fireImmunity() {
      this.fireImmunity = true;
      return this;
    }

    public Properties enhancesSwimming() {
      this.enhancesSwimming = true;
      return this;
    }
  }

  public static ClothingItem getClothingItem(Player player) {
    var playerExtension = PlayerExtension.getOrThrow(player);
    if (!playerExtension.getItemInSlot(Slot.CLOTHING).isEmpty()) {
      return (ClothingItem) playerExtension.getItemInSlot(Slot.CLOTHING).getItem();
    }
    return null;
  }

  public float calculateDamage(float damage) {
    float reductionFactor = switch (this.clothingType) {
      case CASUAL -> 1.00F - ServerConfig.instance.
          casualClothingDamageReduction.get().floatValue();
      case UTILITY -> 1.00F - ServerConfig.instance.
          utilityClothingDamageReduction.get().floatValue();
      case MILITARY -> 1.00F - ServerConfig.instance.
          militaryClothingDamageReduction.get().floatValue();
      case HEAVY -> 1.00F - ServerConfig.instance.
          militaryClothingDamageReduction.get().floatValue();
    };
    return damage * reductionFactor;
  }

  public float calculateBleedAndInfectionChance(float baseChance) {
    float reductionFactor = switch (this.clothingType) {
      case CASUAL -> 1.00F - ServerConfig.instance.
          casualClothingBleedAndInfectionReduction.get().floatValue();
      case UTILITY -> 1.00F - ServerConfig.instance.
          utilityClothingBleedAndInfectionReduction.get().floatValue();
      case MILITARY -> 1.00F - ServerConfig.instance.
          militaryClothingBleedAndInfectionReduction.get().floatValue();
      case HEAVY -> 1.00F - ServerConfig.instance.
          militaryClothingBleedAndInfectionReduction.get().floatValue();
    };
    return baseChance * reductionFactor;
  }
}
