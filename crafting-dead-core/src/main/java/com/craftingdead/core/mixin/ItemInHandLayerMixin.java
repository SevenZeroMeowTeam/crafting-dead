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

package com.craftingdead.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.craftingdead.core.client.animation.PlayerAnimationHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 双手持枪/弓/弩时隐藏副手物品的渲染（设计思路参考 NotEnoughAnimations 的 two-handed
 * 动画，代码为本项目原创）。
 *
 * <p>当主手持双手武器（枪械/TaCZ 枪/弓/弩/三叉戟）时，取消副手手臂的物品渲染，
 * 避免出现双手武器旁还悬浮着一个副手物品的违和画面。
 *
 * <p>注意：原版 {@link ItemInHandLayer#renderArmWithItem} 对玩家同样生效（
 * {@code PlayerItemInHandLayer} 在非头戴物品时会调用 {@code super}），因此无需单独
 * 针对玩家子类再注入。
 */
@Mixin(ItemInHandLayer.class)
public abstract class ItemInHandLayerMixin {

  @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
  private void hideOffhandWhenTwoHanded(LivingEntity entity, ItemStack stack,
      ItemTransforms.TransformType displayContext, HumanoidArm arm, PoseStack poseStack,
      MultiBufferSource bufferSource, int packedLight, CallbackInfo callbackInfo) {
    if (arm != entity.getMainArm()
        && PlayerAnimationHelper.isTwoHandedWeapon(entity.getMainHandItem())) {
      callbackInfo.cancel();
    }
  }
}
