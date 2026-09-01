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

package com.craftingdead.core.world.item.gun.skin;

import java.util.OptionalInt;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import com.craftingdead.core.CraftingDead;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.minecraft.core.registries.Registries;

public interface Paint {

  ItemCapability<Paint, Void> CAPABILITY = ItemCapability.createVoid(
      net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(CraftingDead.ID, "paint"), Paint.class);

  Holder<Skin> getSkin();

  default OptionalInt getColor() {
    return OptionalInt.empty();
  }

  static Paint of(ResourceKey<Skin> skin) {
    return () -> Skins.REGISTRY.getHolderOrThrow(skin);
  }

  static Paint of(ResourceKey<Skin> skin, int color) {
    return of(skin, OptionalInt.of(color));
  }

  static Paint of(ResourceKey<Skin> skin, OptionalInt color) {
    return new Paint() {

      @Override
      public OptionalInt getColor() {
        return color;
      }

      @Override
      public Holder<Skin> getSkin() {
        return Skins.REGISTRY.getHolderOrThrow(skin);
      }
    };
  }

  static boolean isValid(ItemStack gunStack, ItemStack itemStack) {
    return isValid(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(gunStack.getItem()), itemStack);
  }

  static boolean isValid(ResourceLocation gunName, ItemStack itemStack) {
    var paint = itemStack.getCapability(Paint.CAPABILITY);
    if (paint == null) {
      return false;
    }
    var skin = paint.getSkin();
    return skin != null && skin.value().getAcceptedGuns().contains(gunName);
  }
}
