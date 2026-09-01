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

import com.craftingdead.core.world.item.ClothingItem;
import com.google.common.collect.Multimap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public record SimpleClothing(
    Multimap<Holder<Attribute>, AttributeModifier> attributeModifiers,
    boolean fireImmunity,
    boolean enhancesSwimming,
    ResourceLocation texture) implements Clothing {

  public static SimpleClothing of(ClothingItem item) {
    var key = BuiltInRegistries.ITEM.getKey(item);
    var texture = ResourceLocation.fromNamespaceAndPath(key.getNamespace(),
        "textures/clothing/" + key.getPath() + "_default.png");
    return new SimpleClothing(item.getAttributeModifiers(), item.isFireImmune(),
        item.enhancesSwimming(), texture);
  }

  @Override
  public ResourceLocation getTexture(String skinType) {
    return this.texture;
  }
}
