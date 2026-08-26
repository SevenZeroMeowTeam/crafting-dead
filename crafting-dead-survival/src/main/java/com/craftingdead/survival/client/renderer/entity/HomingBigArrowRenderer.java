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

import com.craftingdead.survival.world.entity.projectile.HomingBigArrow;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * 跟踪大号箭矢的渲染器：把原版箭模型放大 1.8 倍以体现「大号」效果。
 */
public class HomingBigArrowRenderer extends ArrowRenderer<HomingBigArrow> {

  private static final ResourceLocation TEXTURE =
      new ResourceLocation("textures/entity/projectile/arrow.png");

  public HomingBigArrowRenderer(EntityRendererProvider.Context context) {
    super(context);
  }

  @Override
  public ResourceLocation getTextureLocation(HomingBigArrow entity) {
    return TEXTURE;
  }

  @Override
  public void render(HomingBigArrow entity, float yaw, float partialTicks, PoseStack poseStack,
      MultiBufferSource buffer, int packedLight) {
    poseStack.pushPose();
    poseStack.scale(1.8F, 1.8F, 1.8F);
    super.render(entity, yaw, partialTicks, poseStack, buffer, packedLight);
    poseStack.popPose();
  }
}
