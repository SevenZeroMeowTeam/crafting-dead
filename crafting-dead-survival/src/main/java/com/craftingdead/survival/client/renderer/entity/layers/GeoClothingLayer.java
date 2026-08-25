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

package com.craftingdead.survival.client.renderer.entity.layers;

import javax.annotation.Nullable;
import com.craftingdead.core.world.entity.extension.LivingExtension;
import com.craftingdead.core.world.item.equipment.Clothing;
import com.craftingdead.core.world.item.equipment.Equipment;
import com.craftingdead.survival.world.entity.monster.ModZombie;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/**
 * GeckoLib 版本 {@link com.craftingdead.core.client.renderer.entity.layers.ClothingLayer}：
 * 将服装纹理作为叠加层重新渲染到 Geo 人形模型上。
 */
public class GeoClothingLayer extends GeoRenderLayer<ModZombie> {

  public GeoClothingLayer(GeoRenderer<ModZombie> renderer) {
    super(renderer);
  }

  @Nullable
  private ResourceLocation getClothingTexture(ModZombie animatable) {
    final var living = LivingExtension.get(animatable);
    if (living == null) {
      return null;
    }
    return living.getEquipmentInSlot(Equipment.Slot.CLOTHING, Clothing.class)
        .map(clothing -> clothing.getTexture("default"))
        .orElse(null);
  }

  @Override
  public void render(PoseStack poseStack, ModZombie animatable, BakedGeoModel bakedModel,
      @Nullable RenderType renderType, MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
    Minecraft minecraft = Minecraft.getInstance();
    boolean invisible = animatable.isInvisible();
    boolean partiallyVisible =
        animatable.isInvisible() && !animatable.isInvisibleTo(minecraft.player);
    if (partiallyVisible || !invisible) {
      ResourceLocation texture = this.getClothingTexture(animatable);
      if (texture != null) {
        RenderType clothingRenderType = RenderType.entityTranslucent(texture);
        getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable,
            clothingRenderType, buffer, partialTick, packedLight, OverlayTexture.NO_OVERLAY,
            partiallyVisible ? 0x26FFFFFF : 0xFFFFFFFF);
      }
    }
  }
}
