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

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 让 TaCZ 枪声以「相对声音」播放（{@code isRelative() == true}），并固定到监听器位置
 * （{@code getX()/getY()/getZ() == 0}）。
 *
 * <p><b>症状：</b>只有 TaCZ 枪械声音在游戏内听不到，其他声音正常。声音实例能到达
 * {@code SoundEngine}（{@code SoundDiag} 可见 {@code canPlay=true}、{@code effVol≈0.64}），
 * 但玩家听不到——很可能是因为枪声被定位到远离玩家的坐标，距离衰减把音量压没。
 *
 * <p><b>修复：</b>对 TaCZ 的枪声类（包前缀 {@code com.tacz.guns.client.sound.}）强制
 * {@code isRelative()=true} 且坐标归零，使其始终在监听器位置播放、无距离衰减，用户即可听到。
 * TaCZ 未安装时对任意声音都不生效（仅当类名匹配时才改写），不影响其他模组。
 */
@Mixin(AbstractSoundInstance.class)
public abstract class TaczGunSoundRelativeMixin {

  private boolean isTaczGunSound() {
    return this.getClass().getName().startsWith("com.tacz.guns.client.sound.");
  }

  @Inject(method = "isRelative", at = @At("RETURN"), cancellable = true)
  private void craftingdead$forceRelative(CallbackInfoReturnable<Boolean> cir) {
    if (this.isTaczGunSound()) {
      cir.setReturnValue(true);
    }
  }

  @Inject(method = "getX", at = @At("RETURN"), cancellable = true)
  private void craftingdead$forceOriginX(CallbackInfoReturnable<Double> cir) {
    if (this.isTaczGunSound()) {
      cir.setReturnValue(0.0D);
    }
  }

  @Inject(method = "getY", at = @At("RETURN"), cancellable = true)
  private void craftingdead$forceOriginY(CallbackInfoReturnable<Double> cir) {
    if (this.isTaczGunSound()) {
      cir.setReturnValue(0.0D);
    }
  }

  @Inject(method = "getZ", at = @At("RETURN"), cancellable = true)
  private void craftingdead$forceOriginZ(CallbackInfoReturnable<Double> cir) {
    if (this.isTaczGunSound()) {
      cir.setReturnValue(0.0D);
    }
  }
}
