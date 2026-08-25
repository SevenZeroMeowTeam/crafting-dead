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
import com.craftingdead.core.client.animation.PlayerAnimationHelper.PoseState;
import com.craftingdead.core.world.entity.extension.PlayerExtension;
import com.craftingdead.core.world.item.ModItems;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * 第三人称玩家动画改进（设计思路参考 NotEnoughAnimations，代码为本项目原创）：
 *
 * <ul>
 *   <li>平滑手臂过渡：手铐/迷你枪/双手持枪等姿势用 lerp 平滑切换，不再瞬间硬切。</li>
 *   <li>双手持枪姿势：持枪/弓/弩时主手抬起、副手托枪。</li>
 *   <li>瞄准时身体随头部转动。</li>
 *   <li>第三人称吃喝动画：使用中的手臂抬到嘴边。</li>
 * </ul>
 */
@Mixin(HumanoidModel.class)
public class HumanoidModelMixin<T extends LivingEntity> {

  /** 手铐姿势：双手举到胸前。 */
  private static final float HANDCUFF_ARM_X_ROT = 0.5F;
  private static final float HANDCUFF_ARM_Z_ROT = 0.25F;

  /**
   * 迷你枪姿势：原代码直接赋 31F/30.5F 弧度（等价于多圈旋转后的 -0.4159F/-0.9159F），
   * 这里取其等价角度，保证最终姿势不变的同时能平滑过渡。
   */
  private static final float MINIGUN_RIGHT_X_ROT = -0.4159F;
  private static final float MINIGUN_LEFT_X_ROT = -0.9159F;

  /** 双手持枪姿势（类似原版弓弩姿势，两臂前伸）。 */
  private static final float GUN_MAIN_ARM_X_ROT = -(float) Math.PI / 2.0F;
  private static final float GUN_SUPPORT_ARM_X_ROT = -0.9424779F;

  /** 吃喝姿势：使用中的手臂抬到嘴边。 */
  private static final float EAT_DRINK_ARM_X_ROT = -1.1F;

  @Inject(at = @At("TAIL"), method = "setupAnim")
  private void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
      float netHeadYaw, float headPitch, CallbackInfo callbackInfo) {
    if (!(entity instanceof Player player)) {
      return;
    }

    final var playerExt = PlayerExtension.get(player);
    final var model = (HumanoidModel<?>) (Object) this;
    final PoseState pose = PlayerAnimationHelper.getPoseState(player);

    final boolean handcuffed = playerExt != null && playerExt.isHandcuffed();
    final boolean minigun =
        playerExt != null && playerExt.mainHandItem().is(ModItems.MINIGUN.get());
    final boolean eating = PlayerAnimationHelper.isEatingOrDrinking(player);
    final boolean holdingGun = PlayerAnimationHelper.isTwoHandedWeapon(player.getMainHandItem());
    final boolean aiming = PlayerAnimationHelper.isAiming(player, playerExt);
    // 第一人称本地玩家之手渲染时不套用持枪/瞄准姿势（避免影响第一人称手部）
    final boolean firstPerson = PlayerAnimationHelper.isFirstPersonView(player);

    // 平滑插值：状态激活时向 1 靠近，否则向 0 回落
    pose.updatePose(handcuffed || minigun);
    pose.updateEat(eating && !handcuffed && !minigun);
    pose.updateGun(holdingGun && !handcuffed && !minigun && !eating && !firstPerson);
    pose.updateAim(aiming && !handcuffed && !minigun && !firstPerson);

    final float poseBlend = pose.getPoseBlend();
    final float eatBlend = pose.getEatBlend();
    final float gunBlend = pose.getGunBlend();
    final float aimBlend = pose.getAimBlend();

    if (handcuffed) {
      // 手铐：双手举到胸前（平滑过渡）
      model.rightArmPose = ArmPose.EMPTY;
      model.leftArmPose = ArmPose.EMPTY;
      model.rightArm.xRot = Mth.lerp(poseBlend, model.rightArm.xRot, HANDCUFF_ARM_X_ROT);
      model.rightArm.zRot = Mth.lerp(poseBlend, model.rightArm.zRot, -HANDCUFF_ARM_Z_ROT);
      model.leftArm.xRot = Mth.lerp(poseBlend, model.leftArm.xRot, HANDCUFF_ARM_X_ROT);
      model.leftArm.zRot = Mth.lerp(poseBlend, model.leftArm.zRot, HANDCUFF_ARM_Z_ROT);
    } else if (minigun) {
      // 迷你枪：两臂前举（保持原有姿势语义，仅平滑过渡）
      model.rightArmPose = ArmPose.EMPTY;
      model.leftArmPose = ArmPose.EMPTY;
      model.rightArm.xRot = Mth.lerp(poseBlend, model.rightArm.xRot, MINIGUN_RIGHT_X_ROT);
      model.leftArm.xRot = Mth.lerp(poseBlend, model.leftArm.xRot, MINIGUN_LEFT_X_ROT);
    } else if (eating) {
      // 吃喝：把使用中的手臂抬到嘴边（第一人称动画带入第三人称）
      final boolean rightHanded = player.getMainArm() == HumanoidArm.RIGHT;
      final boolean usingMainHand = player.getUsedItemHand() == InteractionHand.MAIN_HAND;
      final var usedArm = usingMainHand == rightHanded ? model.rightArm : model.leftArm;
      usedArm.xRot = Mth.lerp(eatBlend, usedArm.xRot, EAT_DRINK_ARM_X_ROT);
      usedArm.yRot = Mth.lerp(eatBlend, usedArm.yRot, 0.0F);
      usedArm.zRot = Mth.lerp(eatBlend, usedArm.zRot, rightHanded ? 0.1F : -0.1F);
      model.body.yRot = Mth.lerp(eatBlend * 0.5F, model.body.yRot, model.head.yRot);
    } else if (holdingGun && !firstPerson) {
      // 双手持枪：主手抬起、副手托枪（平滑过渡，类似原版弓弩姿势）
      final boolean rightHanded = player.getMainArm() == HumanoidArm.RIGHT;
      final var mainArm = rightHanded ? model.rightArm : model.leftArm;
      final var offArm = rightHanded ? model.leftArm : model.rightArm;
      model.rightArmPose = ArmPose.EMPTY;
      model.leftArmPose = ArmPose.EMPTY;
      mainArm.xRot = Mth.lerp(gunBlend, mainArm.xRot, GUN_MAIN_ARM_X_ROT + model.head.xRot);
      mainArm.yRot = Mth.lerp(gunBlend, mainArm.yRot, model.head.yRot - 0.1F);
      offArm.xRot = Mth.lerp(gunBlend, offArm.xRot, GUN_SUPPORT_ARM_X_ROT);
      offArm.yRot = Mth.lerp(gunBlend, offArm.yRot, model.head.yRot);
    } else {
      // 默认：恢复 EMPTY 姿势，避免残留
      model.rightArmPose = ArmPose.EMPTY;
      model.leftArmPose = ArmPose.EMPTY;
    }

    // 瞄准时身体跟随头部转动（平滑，参考 NEA 的盾牌/身体旋转思路）
    if (aimBlend > 0.0F) {
      model.body.yRot = Mth.lerp(aimBlend, model.body.yRot, model.head.yRot);
    }
  }
}
