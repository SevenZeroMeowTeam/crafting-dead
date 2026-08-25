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

import javax.annotation.Nullable;
import com.craftingdead.core.world.entity.extension.LivingExtension;
import com.craftingdead.survival.CraftingDeadSurvival;
import com.craftingdead.survival.world.entity.animation.VanillaZombieGeoAnimatable;
import com.craftingdead.survival.world.entity.extension.ZombieHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.GeoReplacedEntityRenderer;

/**
 * 原版僵尸的 GeckoLib 模型：复用 zombie.geo.json 与 zombie.animation.json，
 * 贴图按被替换实体(ZombieHandler)的随机贴图索引返回。
 */
public class VanillaZombieGeoModel extends DefaultedEntityGeoModel<VanillaZombieGeoAnimatable> {

  public VanillaZombieGeoModel() {
    super(ResourceLocation.fromNamespaceAndPath(CraftingDeadSurvival.ID, "zombie"), true);
  }

  @Override
  public ResourceLocation getTextureResource(VanillaZombieGeoAnimatable animatable,
      @Nullable GeoRenderer<VanillaZombieGeoAnimatable> renderer) {
    if (renderer instanceof GeoReplacedEntityRenderer<?, ?> replacedRenderer
        && replacedRenderer.getCurrentEntity() instanceof Zombie zombie) {
      int textureIndex = LivingExtension.getOrThrow(zombie)
          .getHandlerOrThrow(ZombieHandler.TYPE)
          .getTextureIndex();
      return ResourceLocation.fromNamespaceAndPath(CraftingDeadSurvival.ID,
          "textures/entity/zombie/zombie" + textureIndex + ".png");
    }
    return super.getTextureResource(animatable, renderer);
  }
}
