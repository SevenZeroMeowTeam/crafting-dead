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
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 防御 TaCZ（Timeless and Classics Guns）动画音效空数组崩溃。
 *
 * <p>TaCZ 的 {@code ObjectAnimationSoundChannel.getEndTimeS()} 直接访问
 * {@code keyframeTimeS[length - 1]}。当某个枪包的动画 {@code sound_effects} 为空时，
 * 数组长度为 0，会抛出 {@code ArrayIndexOutOfBoundsException: Index -1 out of bounds
 * for length 0}（症状：启动时 Rendering overlay 崩溃，堆栈指向
 * {@code Animations.createAnimationFromBedrock} → {@code ObjectAnimationSoundChannel}）。
 *
 * <p>本补丁在 {@code getEndTimeS} 入口对空数组返回安全值 0，防止崩溃。使用
 * {@code @Pseudo} + 字符串 targets + 反射访问字段，编译期不依赖 TaCZ；TaCZ 未安装
 * 时该 mixin 会被静默跳过，不影响其他模组。
 */
@Pseudo
@Mixin(targets = "com.tacz.guns.api.client.animation.ObjectAnimationSoundChannel")
public abstract class TaczAnimationSoundChannelMixin {

  /**
   * 空 {@code keyframeTimeS} 数组时返回 0，避免 TaCZ 索引 -1 崩溃。
   */
  @Inject(method = "getEndTimeS", at = @At("HEAD"), cancellable = true)
  private void craftingdead$preventCrashOnEmptySoundChannel(CallbackInfoReturnable<Double> cir) {
    try {
      Class<?> channelClass =
          Class.forName("com.tacz.guns.api.client.animation.ObjectAnimationSoundChannel");
      Object content = channelClass.getField("content").get(this);
      if (content == null) {
        cir.setReturnValue(0.0D);
        return;
      }
      double[] keyframes = (double[]) content.getClass().getField("keyframeTimeS").get(content);
      if (keyframes == null || keyframes.length == 0) {
        cir.setReturnValue(0.0D);
      }
    } catch (Exception ignored) {
      // TaCZ 未安装或 API 变化：忽略，走原逻辑
    }
  }
}
