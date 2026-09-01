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

package com.craftingdead.core.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * TaCZ（Timeless and Classics Guns）声音兼容补丁。
 *
 * <p>症状：本地（单机）加载游戏后，TaCZ 枪械开枪 / 换弹没有声音。
 *
 * <p>根因：TaCZ 的 {@code SoundPlayManager.hasSoundResource} 会把枪声资源是否存在
 * 缓存到 {@code SOUND_RESOURCE_EXISTS_CACHE}。若第一次开枪发生在枪包（gunpack）
 * 资源尚未完全就绪时，缓存会写入 {@code false}，之后该枪声一直被跳过（日志仅提示
 * "[TACZ Sound] Missing gun sound resource, skipped"）。
 *
 * <p>修复：进入世界（单人 / 服务器）后延迟约 3 秒，反射调用 TaCZ 的
 * {@code SoundPlayManager.clearSoundResourceCache()} 清空该缓存，让枪声资源被重新检查。
 * 未安装 TaCZ 时自动无操作。
 */
@OnlyIn(Dist.CLIENT)
public final class TaczSoundCompat {

  /** TaCZ 声音播放管理器类名（软引用，无需编译依赖）。 */
  private static final String TACZ_SOUND_PLAY_MANAGER =
      "com.tacz.guns.client.sound.SoundPlayManager";

  /** 进入世界后延迟刷新缓存的客户端 tick 数（3 秒）。 */
  private static int ticksUntilRefresh = -1;

  private TaczSoundCompat() {}

  /**
   * 客户端每 tick 调用：到点后清空 TaCZ 声音资源缓存。
   */
  public static void onClientTick() {
    if (ticksUntilRefresh < 0) {
      return;
    }
    if (--ticksUntilRefresh == 0) {
      clearTaczSoundResourceCache();
    }
  }

  /**
   * 进入世界（单人 / 服务器）后调用：安排一次延迟刷新。
   */
  public static void refreshAfterJoin() {
    ticksUntilRefresh = 60;
  }

  /**
   * 反射调用 TaCZ {@code SoundPlayManager.clearSoundResourceCache()}。
   * TaCZ 未安装或版本不同（方法不存在）时安全空操作。
   */
  public static void clearTaczSoundResourceCache() {
    try {
      Class<?> clazz = Class.forName(TACZ_SOUND_PLAY_MANAGER);
      clazz.getMethod("clearSoundResourceCache").invoke(null);
    } catch (Exception ignored) {
      // TaCZ 未安装或 API 变化：忽略
    }
  }
}
