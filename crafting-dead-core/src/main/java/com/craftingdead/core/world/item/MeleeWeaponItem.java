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

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.craftingdead.core.world.item.combatslot.CombatSlot;
import com.craftingdead.core.world.item.combatslot.CombatSlotProvider;
import com.craftingdead.core.world.item.equipment.Equipment;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class MeleeWeaponItem extends ToolItem {

  private final int attackDamage;

  private final ItemAttributeModifiers attributeModifiers;

  public MeleeWeaponItem(int attackDamage, double attackSpeed, Item.Properties properties) {
    super(properties);
    this.attackDamage = attackDamage;
    this.attributeModifiers = ItemAttributeModifiers.builder()
        .add(Attributes.ATTACK_DAMAGE,
            new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, this.attackDamage,
                AttributeModifier.Operation.ADD_VALUE),
            EquipmentSlotGroup.MAINHAND)
        .add(Attributes.ATTACK_SPEED,
            new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, attackSpeed,
                AttributeModifier.Operation.ADD_VALUE),
            EquipmentSlotGroup.MAINHAND)
        .build();
  }

  @Override
  public ItemAttributeModifiers getAttributeModifiers(
      EquipmentSlot equipmentSlot, ItemStack itemStack) {
    return equipmentSlot == EquipmentSlot.MAINHAND
        ? this.attributeModifiers
        : super.getAttributeModifiers(equipmentSlot, itemStack);
  }

  @Override
  public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext world, List<Component> tooltip,
      @NotNull TooltipFlag flag) {
    tooltip.add(Component.translatable("item.craftingdead.damage").append(" ").append(
            Component.translatable(String.valueOf(this.attackDamage))
                .withStyle(style -> style.withColor(ChatFormatting.RED)))
        .withStyle(style -> style.withColor(ChatFormatting.GRAY)));

    tooltip.add(Component.translatable("item.craftingdead.durability").append(" ").append(
            Component.translatable(String.valueOf(stack.getMaxDamage() - stack.getDamageValue()))
                .withStyle(style -> style.withColor(ChatFormatting.RED)))
        .withStyle(style -> style.withColor(ChatFormatting.GRAY)));
  }

  @Override
  public net.minecraftforge.common.capabilities.ICapabilityProvider getCapabilityProvider(ItemStack itemStack) {
    var combatSlotProvider = LazyOptional.of(() -> CombatSlot.MELEE);
    var equipment = LazyOptional.of(() -> Equipment.forSlot(Equipment.Slot.MELEE));
    return new ICapabilityProvider() {

      @Override
      public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap,
          Direction side) {
        if (cap == CombatSlotProvider.CAPABILITY) {
          return combatSlotProvider.cast();
        }

        if (cap == Equipment.CAPABILITY) {
          return equipment.cast();
        }

        return LazyOptional.empty();
      }
    };
  }
}
