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
import com.craftingdead.core.world.item.equipment.Equipment.Slot;
import java.util.List;
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
import com.craftingdead.core.world.item.equipment.Equipment;
import com.craftingdead.core.world.item.equipment.SimpleClothing;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;

public class ClothingItem extends EquipmentItem {

  public enum ClothingType {
    CASUAL,
    UTILITY,
    MILITARY,
    HEAVY
  }

  public static final UUID ARMOR_MODIFIER_ID =
      UUID.fromString("4117e432-16f5-4eea-a4fe-127b54d39af1");

  private final Multimap<Holder<Attribute>, AttributeModifier> attributeModifiers;
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

  public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers() {
    return this.attributeModifiers;
  }

  public boolean isFireImmune() {
    return this.fireImmunity;
  }

  public boolean enhancesSwimming() {
    return this.enhancesSwimming;
  }

  @Override
  public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext world, List<Component> lines, TooltipFlag tooltipFlag) {
    super.appendHoverText(stack, world, lines, tooltipFlag);
    switch (this.clothingType) {
      case CASUAL -> lines.add(Component.translatable("clothing.protection.level")
          .withStyle(ChatFormatting.GRAY)
          .append(" ")
          .append(Component.translatable("clothing.casual")
              .withStyle(ChatFormatting.RED)));
      case UTILITY -> lines.add(Component.translatable("clothing.protection.level")
          .withStyle(ChatFormatting.GRAY)
          .append(" ")
          .append(Component.translatable("clothing.utility")
              .withStyle(ChatFormatting.RED)));
      case MILITARY -> lines.add(Component.translatable("clothing.protection.level")
          .withStyle(ChatFormatting.GRAY)
          .append(" ")
          .append(Component.translatable("clothing.military")
              .withStyle(ChatFormatting.RED)));
    }
    if (this.fireImmunity) {
      lines.add(Component.translatable("clothing.immune_to_fire")
          .withStyle(ChatFormatting.GRAY));
    }
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
    if (!player.level().isClientSide()) {
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
    if (!player.level().isClientSide()) {
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
  public int getUseDuration(@NotNull ItemStack itemStack, net.minecraft.world.entity.LivingEntity livingEntity) {
    return this.getActionType().getDurationTicks();
  }

  public static class Properties extends Item.Properties {

    private final ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> attributeModifiers =
        ImmutableMultimap.builder();
    private boolean fireImmunity;
    private boolean enhancesSwimming;

    public Properties attributeModifier(Holder<Attribute> attribute, AttributeModifier modifier) {
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
      case MILITARY, HEAVY -> 1.00F - ServerConfig.instance.
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
      case MILITARY, HEAVY -> 1.00F - ServerConfig.instance.
          militaryClothingBleedAndInfectionReduction.get().floatValue();
    };
    return baseChance * reductionFactor;
  }
}
