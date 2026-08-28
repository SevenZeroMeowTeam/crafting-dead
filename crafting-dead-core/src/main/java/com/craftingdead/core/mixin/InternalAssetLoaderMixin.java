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

import java.util.Collections;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修复 TaCZ（Timeless and Classics Guns）加载顺序竞态导致的 NPE 崩溃。
 *
 * <p><b>症状（来自崩溃报告）：</b>
 * {@code java.lang.NullPointerException: Cannot invoke "java.util.List.iterator()" because the
 * return value of "com.tacz.guns.client.resource.InternalAssetLoader.getDefaultRifleAnimations()"
 * is null}，堆栈指向 {@code GunDisplayInstance.checkAnimation} →
 * {@code ClientIndexManager.reload}。描述为 {@code Rendering overlay}。
 *
 * <p><b>根因：</b>{@code ClientIndexManager.reload}（创建 {@code GunDisplayInstance}）比
 * {@code InternalAssetLoader.onResourceReload()}（填充 {@code defaultRifleAnimations} /
 * {@code defaultPistolAnimations} 静态字段）先运行。当某个枪包尚未被
 * {@code onResourceReload} 处理时，这两个 getter 返回 {@code null}，后续
 * {@code list.iterator()} 直接 NPE，导致进世界 / 渲染枪械时崩溃。
 *
 * <p><b>修复：</b>在两个 getter 返回 {@code null} 时懒调用 {@code onResourceReload()} 填充字段；
 * 若资源暂不可用（例如加载早期）则兜底返回空列表，避免崩溃。使用 {@code @Pseudo} + 字符串
 * targets + 反射，编译期不依赖 TaCZ；TaCZ 未安装时该 mixin 被静默跳过。
 */
@Pseudo
@Mixin(targets = "com.tacz.guns.client.resource.InternalAssetLoader")
public abstract class InternalAssetLoaderMixin {

  /** TaCZ 内部：默认步枪动画静态字段（mojmap 名，不需 remap）。 */
  @Shadow(remap = false)
  private static List defaultRifleAnimations;

  /** TaCZ 内部：默认手枪动画静态字段。 */
  @Shadow(remap = false)
  private static List defaultPistolAnimations;

  /** 防止 {@link #craftingdead$loadDefaults()} 重入。 */
  private static boolean craftingdead$loadingDefaults = false;

  @Inject(method = "getDefaultRifleAnimations", at = @At("HEAD"), cancellable = true)
  private static void craftingdead$ensureRifleAnimations(CallbackInfoReturnable<List> cir) {
    if (defaultRifleAnimations == null) {
      craftingdead$loadDefaults();
      if (defaultRifleAnimations == null) {
        cir.setReturnValue(Collections.emptyList());
      }
    }
  }

  @Inject(method = "getDefaultPistolAnimations", at = @At("HEAD"), cancellable = true)
  private static void craftingdead$ensurePistolAnimations(CallbackInfoReturnable<List> cir) {
    if (defaultPistolAnimations == null) {
      craftingdead$loadDefaults();
      if (defaultPistolAnimations == null) {
        cir.setReturnValue(Collections.emptyList());
      }
    }
  }

  /**
   * 通过反射调用 {@code InternalAssetLoader.onResourceReload()}，填充静态动画字段。
   * 资源暂不可用时静默失败（getter 会兜底返回空列表）。
   */
  private static void craftingdead$loadDefaults() {
    if (craftingdead$loadingDefaults) {
      return;
    }
    craftingdead$loadingDefaults = true;
    try {
      Class<?> cls = Class.forName("com.tacz.guns.client.resource.InternalAssetLoader");
      cls.getMethod("onResourceReload").invoke(null);
    } catch (Throwable ignored) {
      // 资源尚未就绪或 TaCZ 未安装：忽略，getter 兜底返回空列表
    } finally {
      craftingdead$loadingDefaults = false;
    }
  }
}
