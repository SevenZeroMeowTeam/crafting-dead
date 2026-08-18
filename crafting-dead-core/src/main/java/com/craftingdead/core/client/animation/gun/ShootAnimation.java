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

package com.craftingdead.core.client.animation.gun;

import com.craftingdead.core.client.animation.TimedAnimation;
import com.craftingdead.core.util.EasingFunction;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.util.Mth;

public class ShootAnimation extends TimedAnimation {

  private final float bounceAmplification;
  private final float kickbackAmplification;

  public ShootAnimation(int lifetimeTicks, float bounceAmplification, float kickbackAmplification) {
    super(lifetimeTicks);
    this.bounceAmplification = bounceAmplification;
    this.kickbackAmplification = kickbackAmplification;
  }

  @Override
  public void apply(float partialTick, PoseStack poseStack) {
    var percent = this.lerpProgress(partialTick);
    var kickback = this.getKickbackTranslation(percent);
    float sideRecoil = 0.01F * Mth.sin(percent * Mth.PI);
    poseStack.translate(sideRecoil, 0, kickback);
    var rotation = this.getRecoilRotation(percent);
    poseStack.mulPose(rotation);
  }


  private Quaternion getRecoilRotation(float percent) {
    float easedBounce = EasingFunction.SINE_IN.andThen(EasingFunction.ELASTIC_OUT).apply(percent);
    float xRot = this.bounceAmplification * Mth.sin(easedBounce * Mth.PI);
    float yRot = this.bounceAmplification * 0.1F * Mth.sin(easedBounce * Mth.PI * 2);
    float zRot = this.bounceAmplification * 0.05F * Mth.sin(easedBounce * Mth.PI * 2);
    var q = Vector3f.XP.rotationDegrees(xRot);
    q.mul(Vector3f.YP.rotationDegrees(yRot));
    q.mul(Vector3f.ZP.rotationDegrees(zRot));
    return q;
  }

  private float getKickbackTranslation(float percent) {
    float easedProgress = EasingFunction.EXPO_OUT.apply(percent);
    return Mth.sin(easedProgress * Mth.PI) * this.kickbackAmplification;
  }

  @Override
  public void applyArm(InteractionHand hand, HumanoidArm arm, float partialTick, PoseStack poseStack) {
    var lerpProgress = this.lerpProgress(partialTick);
    var translation = this.getKickbackTranslation(lerpProgress);
    float armAngle = 2.5F * Mth.sin(lerpProgress * Mth.PI);
    switch (arm) {
      case LEFT:
        poseStack.translate(0, -translation * 0.25F, translation * 0.5F);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(-armAngle));
        break;
      case RIGHT:
        poseStack.translate(0, 0, translation);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(-armAngle));
        break;
    }
  }

  @Override
  public void applyCamera(float partialTick, Vector3f rotations) {
    float percent = this.lerpProgress(partialTick);
    float deltaX = 0.5F * Mth.sin(percent * Mth.PI);
    float deltaY = 0.2F * Mth.sin(percent * Mth.PI * 2);
    rotations.add(deltaX, deltaY, 0.0F);
  }

  public static ShootAnimation rifle() {
    return new ShootAnimation(5, 1.25F, 0.1F);
  }

  public static ShootAnimation boltActionSniper() {
    return new ShootAnimation(16, 3.25F, 0.4F);

  }
  public static ShootAnimation autoSniper() {
    return new ShootAnimation(6, 3.25F, 0.65F);
  }

  public static ShootAnimation submachineGun() {
    return new ShootAnimation(5, 2.0F, 0.15F);
  }

  public static ShootAnimation pistol() {
    return new ShootAnimation(7, 3.5F, 0.5F);
  }

  public static ShootAnimation shotGun() {
    return new ShootAnimation(13, 3.0F, 0.4F);
  }
}
