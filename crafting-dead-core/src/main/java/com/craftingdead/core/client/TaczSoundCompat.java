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

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.WeighedSoundEvents;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

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
 *
 * <p>诊断：{@link #registerSoundDiag()} 注册一个 {@link SoundEventListener}，
 * 每次 SoundEngine 播放 TaCZ 声音实例时输出该声音的类名 / 位置 / 音量 / 音高 / 实际文件路径，
 * 用于定位"能开火但无声"的问题。
 */
@OnlyIn(Dist.CLIENT)
public final class TaczSoundCompat {

  /** 诊断日志名。 */
  private static final Logger LOGGER = LogUtils.getLogger();

  /** TaCZ 声音播放管理器类名（软引用，无需编译依赖）。 */
  private static final String TACZ_SOUND_PLAY_MANAGER =
      "com.tacz.guns.client.sound.SoundPlayManager";

  /** 进入世界后延迟刷新缓存的客户端 tick 数（3 秒）。 */
  private static int ticksUntilRefresh = -1;

  /** 声音诊断监听器是否已注册（避免重复注册）。 */
  private static boolean soundDiagRegistered = false;

  /**
   * 声音诊断开关：默认关闭（诊断已确认枪声正常，避免每次开枪刷屏 / 影响性能）。
   * 需要时可 {@code -Dcraftingdead.taczSoundDiag=true} 重新开启。
   */
  private static final boolean SOUND_DIAG_ENABLED =
      Boolean.parseBoolean(System.getProperty("craftingdead.taczSoundDiag", "false"));

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
   * 进入世界（单人 / 服务器）后调用：安排一次延迟刷新，并注册声音诊断监听器。
   */
  public static void refreshAfterJoin() {
    ticksUntilRefresh = 60;
    registerSoundDiag();
  }

  /**
   * 注册一个 {@link SoundEventListener}，打印所有进入 SoundEngine 的 TaCZ 声音实例。
   *
   * <p>诊断用途：开一枪后，若日志出现 {@code [SoundDiag] TACZ play ...}，说明枪声已进入
   * SoundEngine（问题在 OpenAL 输出端）；若完全没有，说明枪声在 TaCZ 调用链上被跳过。
   */
  public static void registerSoundDiag() {
    if (!SOUND_DIAG_ENABLED || soundDiagRegistered) {
      return;
    }
    Minecraft mc = Minecraft.getInstance();
    if (mc == null || mc.getSoundManager() == null) {
      return;
    }
    soundDiagRegistered = true;
    mc.getSoundManager().addListener(new SoundEventListener() {
      @Override
      public void onPlaySound(SoundInstance sound, WeighedSoundEvents events, float range) {
        if (!sound.getClass().getName().startsWith("com.tacz.guns.client.sound.")) {
          return;
        }
        String path = "null";
        String type = "null";
        float soundVol = -1.0F;
        float soundPitch = -1.0F;
        int weight = -1;
        boolean streaming = false;
        boolean preload = false;
        try {
          Sound s = sound.getSound();
          if (s != null) {
            path = s.getPath().toString();
            type = String.valueOf(s.getType());
            soundVol = s.getVolume().sample(net.minecraft.util.RandomSource.create());
            soundPitch = s.getPitch().sample(net.minecraft.util.RandomSource.create());
            weight = s.getWeight();
            streaming = s.shouldStream();
            preload = s.shouldPreload();
          }
        } catch (Throwable ignored) {
          // 忽略解析异常
        }
        LOGGER.info("[SoundDiag] TACZ play {} cls={} source={} pos=({},{},{}) rel={} atten={} "
            + "instVol={} instPitch={} canPlay={} type={} sndVol={} sndPitch={} weight={} "
            + "streaming={} preload={} effVol={} path={}",
            sound.getLocation(), sound.getClass().getSimpleName(), sound.getSource(),
            sound.getX(), sound.getY(), sound.getZ(),
            sound.isRelative(), sound.getAttenuation(), 
            sound.getVolume(), sound.getPitch(), sound.canPlaySound(),
            type, soundVol, soundPitch, weight, streaming, preload,
            sound.getVolume() * soundVol, path);
      }
    });
    LOGGER.info("[SoundDiag] listener registered");
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
