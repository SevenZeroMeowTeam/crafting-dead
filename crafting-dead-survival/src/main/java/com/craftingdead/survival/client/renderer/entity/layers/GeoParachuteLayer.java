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
import com.craftingdead.core.CraftingDead;
import com.craftingdead.core.client.model.geom.ModModelLayers;
import com.craftingdead.core.world.effect.ModMobEffects;
import com.craftingdead.survival.world.entity.monster.ModZombie;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/**
 * GeckoLib 版本 {@link com.craftingdead.core.client.renderer.entity.layers.ParachuteLayer}：
 * 在具有降落伞效果的僵尸身上渲染降落伞。
 */
public class GeoParachuteLayer extends GeoRenderLayer<ModZombie> {

  private static final ResourceLocation TEXTURE =
      ResourceLocation.fromNamespaceAndPath(CraftingDead.ID, "textures/entity/parachute.png");

  private final ModelPart model;

  public GeoParachuteLayer(GeoRenderer<ModZombie> renderer, EntityModelSet entityModels) {
    super(renderer);
    this.model = entityModels.bakeLayer(ModModelLayers.PARACHUTE);
  }

  @Override
  public void render(PoseStack poseStack, ModZombie animatable, BakedGeoModel bakedModel,
      @Nullable RenderType renderType, MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
    if (animatable.hasEffect(ModMobEffects.PARACHUTE.get())) {
      poseStack.pushPose();
      {
        poseStack.translate(0.0D, 0.0D, 0.125D);
        var vertexConsumer = ItemRenderer.getArmorFoilBuffer(bufferSource,
            RenderType.armorCutoutNoCull(TEXTURE), false, false);
        this.model.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
      }
      poseStack.popPose();
    }
  }
}
