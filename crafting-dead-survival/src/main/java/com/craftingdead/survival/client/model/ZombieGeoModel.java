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

package com.craftingdead.survival.client.model;

import com.craftingdead.core.world.entity.extension.LivingExtension;
import com.craftingdead.survival.CraftingDeadSurvival;
import com.craftingdead.survival.world.entity.extension.ZombieHandler;
import com.craftingdead.survival.world.entity.monster.ModZombie;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

/**
 * GeckoLib 模型 - 所有僵尸共享同一人形 Geo 模型与动画，
 * 仅贴图按 ZombieHandler 的随机贴图索引返回。
 */
public class ZombieGeoModel extends DefaultedEntityGeoModel<ModZombie> {

  public ZombieGeoModel() {
    super(ResourceLocation.fromNamespaceAndPath(CraftingDeadSurvival.ID, "zombie"), true);
  }

  @Override
  public ResourceLocation getTextureResource(ModZombie animatable) {
    var textureIndex = LivingExtension.getOrThrow(animatable)
        .getHandler(ZombieHandler.TYPE)
        .map(ZombieHandler::getTextureIndex)
        .orElse(0);
    return ResourceLocation.fromNamespaceAndPath(CraftingDeadSurvival.ID,
        "textures/entity/zombie/zombie" + textureIndex + ".png");
  }
}
