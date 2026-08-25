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

import javax.annotation.Nullable;
import com.craftingdead.core.world.item.equipment.Equipment;
import com.craftingdead.survival.client.model.ZombieGeoModel;
import com.craftingdead.survival.client.renderer.entity.layers.GeoClothingLayer;
import com.craftingdead.survival.client.renderer.entity.layers.GeoEquipmentLayer;
import com.craftingdead.survival.world.entity.monster.ModZombie;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * GeckoLib 渲染器 - 所有僵尸使用同一人形 Geo 模型与动画，
 * 附带服装与装备(近战/背包/背心/帽子/枪械)渲染层。
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

    this.addRenderLayer(new GeoClothingLayer(this));
    this.addRenderLayer(GeoEquipmentLayer.builder(this)
        .slot(Equipment.Slot.MELEE)
        .useCrouchOrientation(true)
        .build());
    this.addRenderLayer(GeoEquipmentLayer.builder(this)
        .slot(Equipment.Slot.BACKPACK)
        .useCrouchOrientation(true)
        .build());
    this.addRenderLayer(GeoEquipmentLayer.builder(this)
        .slot(Equipment.Slot.VEST)
        .useCrouchOrientation(true)
        .build());
    this.addRenderLayer(GeoEquipmentLayer.builder(this)
        .slot(Equipment.Slot.HAT)
        .useHeadOrientation(true)
        .transformation(poseStack -> poseStack.scale(-1F, -1F, 1F))
        .build());
    this.addRenderLayer(GeoEquipmentLayer.builder(this)
        .slot(Equipment.Slot.GUN)
        .useCrouchOrientation(true)
        .build());
  }

  @Override
  public void preRender(PoseStack poseStack, ModZombie animatable, BakedGeoModel model,
      @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
      boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
    float entityScale = this.scale * (animatable.isBaby() ? 0.5F : 1.0F);
    this.scaleWidth = entityScale;
    this.scaleHeight = entityScale;
    super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender,
        partialTick, packedLight, packedOverlay, colour);
  }
}
