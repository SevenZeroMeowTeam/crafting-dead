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

package com.craftingdead.survival.client.renderer.entity;

import com.craftingdead.survival.client.model.ZombieGeoModel;
import com.craftingdead.survival.client.renderer.entity.layers.GeoParachuteLayer;
import com.craftingdead.survival.world.entity.monster.ModZombie;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

/**
 * GeckoLib 3 渲染器 - 所有僵尸使用同一人形 Geo 模型与动画，附带降落伞渲染层。
 */
public class ZombieGeoRenderer extends GeoEntityRenderer<ModZombie> {

  private final float scale;

  public ZombieGeoRenderer(EntityRendererProvider.Context context) {
    this(context, 1.0F);
  }

  public ZombieGeoRenderer(EntityRendererProvider.Context context, float scale) {
    super(context, new ZombieGeoModel());
    this.scale = scale;
    this.shadowRadius = 0.5F * scale;
    this.addLayer(new GeoParachuteLayer(this, context.getModelSet()));
  }

  @Override
  public void renderEarly(ModZombie entity, PoseStack poseStack, float partialTicks,
      MultiBufferSource bufferSource, VertexConsumer buffer, int packedLight, int packedOverlay,
      float red, float green, float blue, float alpha) {
    float entityScale = this.scale * (entity.isBaby() ? 0.5F : 1.0F);
    this.widthScale = entityScale;
    this.heightScale = entityScale;
    super.renderEarly(entity, poseStack, partialTicks, bufferSource, buffer, packedLight,
        packedOverlay, red, green, blue, alpha);
  }
}
