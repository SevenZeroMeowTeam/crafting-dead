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
import com.craftingdead.core.world.item.equipment.Equipment;
import com.google.common.base.Predicates;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;

public abstract class EquipmentItem extends Item {

  public EquipmentItem(Properties properties) {
    super(properties);
  }

  @Override
  public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext world, List<Component> lines,
      TooltipFlag tooltipFlag) {
    super.appendHoverText(stack, world, lines, tooltipFlag);

    var equipment = stack.getCapability(Equipment.CAPABILITY);
    if (equipment != null && !equipment.attributeModifiers().isEmpty()) {
      var attributeModifiers = equipment.attributeModifiers();

      lines.add(Component.empty());
      lines.add(Component.translatable("item.modifiers.clothing")
          .withStyle(ChatFormatting.GRAY));

      for (var entry : attributeModifiers.entries()) {
            var modifier = entry.getValue();
            var amount = modifier.amount();

            double multipliedAmount;
            if (modifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                && modifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
              if (entry.getKey().equals(Attributes.KNOCKBACK_RESISTANCE)) {
                multipliedAmount = amount * 10.0D;
              } else {
                multipliedAmount = amount;
              }
            } else {
              multipliedAmount = amount * 100.0D;
            }

            if (modifier.id() == Item.BASE_ATTACK_DAMAGE_ID
                || modifier.id() == Item.BASE_ATTACK_SPEED_ID) {
              lines.add(Component.literal(" ")
                  .append(Component.translatable(
                      "attribute.modifier.equals." + modifier.operation().getSerializedName(),
                      ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(multipliedAmount),
                      Component.translatable(entry.getKey().value().getDescriptionId())))
                  .withStyle(ChatFormatting.DARK_GREEN));
            } else if (amount > 0.0D) {
              lines.add(Component.translatable(
                  "attribute.modifier.plus." + modifier.operation().getSerializedName(),
                  ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(multipliedAmount),
                  Component.translatable(entry.getKey().value().getDescriptionId()))
                      .withStyle(ChatFormatting.BLUE));
            } else if (amount < 0.0D) {
              multipliedAmount = multipliedAmount * -1.0D;
              lines.add(Component.translatable(
                  "attribute.modifier.take." + modifier.operation().getSerializedName(),
                  ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(multipliedAmount),
                  Component.translatable(entry.getKey().value().getDescriptionId()))
                      .withStyle(ChatFormatting.RED));
            }
          }
    }
  }
}
