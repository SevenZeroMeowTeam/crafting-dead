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

package com.craftingdead.survival.world.moon;

import javax.annotation.Nullable;

import com.craftingdead.survival.CraftingDeadSurvival;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

/**
 * 末日生存天数 / 月亮事件 / 僵尸进化的统一管理工具类（服务端逻辑）。
 */
public final class ApocalypseManager {

  private ApocalypseManager() {}

  /**
   * 获取当前世界天数（从世界创建时开始计算）。
   */
  public static int getDay(Level level) {
    return (int) (level.getDayTime() / 24000L);
  }

  /**
   * 获取僵尸进化等级。每 {@code evolutionIntervalDays} 天进化一次，随天数无限提升。
   */
  public static int getEvolutionTier(Level level) {
    return getDay(level) / CraftingDeadSurvival.serverConfig.evolutionIntervalDays.get();
  }

  // ================================================================================
  // 手动覆盖（/moon 命令）：可强制切换月亮事件 / 月相，null 表示按天数推算
  // ================================================================================

  /** 手动覆盖的月亮事件（由 {@code /moon set} 设置），{@code null} 表示未覆盖，按天数推算。 */
  @Nullable
  private static MoonEventType manualEvent;

  /** 手动覆盖的月相（0-7），{@code -1} 表示未覆盖，使用世界真实月相。 */
  private static int manualPhase = -1;

  public static boolean isManualEventSet() {
    return manualEvent != null;
  }

  @Nullable
  public static MoonEventType getManualEvent() {
    return manualEvent;
  }

  public static void setManualEvent(MoonEventType event) {
    manualEvent = event;
  }

  public static void clearManualEvent() {
    manualEvent = null;
  }

  public static boolean isManualPhaseSet() {
    return manualPhase >= 0;
  }

  public static int getManualPhase() {
    return manualPhase;
  }

  public static void setManualPhase(int phase) {
    manualPhase = Math.floorMod(phase, 8);
  }

  public static void clearManualPhase() {
    manualPhase = -1;
  }

  /**
   * 获取今天对应的月亮事件（无论白天黑夜）。若通过 {@code /moon set} 设置了手动覆盖，则返回覆盖值。
   */
  public static MoonEventType getMoonEvent(Level level) {
    if (manualEvent != null) {
      return manualEvent;
    }
    return MoonEventType.forDay(getDay(level));
  }

  /**
   * 月亮事件当前是否处于激活状态（仅在夜晚生效）。
   */
  public static boolean isMoonEventActive(Level level) {
    return level.isNight() && getMoonEvent(level) != MoonEventType.NONE;
  }

  /**
   * 是否处于血月（包含超级血月）的夜晚。
   */
  public static boolean isBloodMoon(Level level) {
    MoonEventType event = getMoonEvent(level);
    return level.isNight()
        && (event == MoonEventType.BLOOD_MOON || event == MoonEventType.SUPER_BLOOD_MOON);
  }

  /**
   * 是否处于超级血月的夜晚。
   */
  public static boolean isSuperBloodMoon(Level level) {
    return level.isNight() && getMoonEvent(level) == MoonEventType.SUPER_BLOOD_MOON;
  }

  /**
   * 是否是蓝月日（幸运效果全天生效，含超级蓝月）。
   */
  public static boolean isBlueMoon(Level level) {
    MoonEventType event = getMoonEvent(level);
    return event == MoonEventType.BLUE_MOON || event == MoonEventType.SUPER_BLUE_MOON;
  }

  /**
   * 是否是超级蓝月日。
   */
  public static boolean isSuperBlueMoon(Level level) {
    return getMoonEvent(level) == MoonEventType.SUPER_BLUE_MOON;
  }

  /**
   * 是否是黄月日（农作物生长全天加速，含超级黄月）。
   */
  public static boolean isYellowMoon(Level level) {
    MoonEventType event = getMoonEvent(level);
    return event == MoonEventType.YELLOW_MOON || event == MoonEventType.SUPER_YELLOW_MOON;
  }

  /**
   * 是否是超级黄月日。
   */
  public static boolean isSuperYellowMoon(Level level) {
    return getMoonEvent(level) == MoonEventType.SUPER_YELLOW_MOON;
  }

  /**
   * 返回当前月相（0-7）。若通过 {@code /moon phase} 设置了手动覆盖，则返回覆盖值。
   */
  public static int getMoonPhase(Level level) {
    return manualPhase >= 0 ? manualPhase : level.getMoonPhase();
  }

  /**
   * 返回当前月相的中文名称（0-7）。
   */
  public static String getMoonPhaseName(int phase) {
    return switch (phase) {
      case 0 -> "满月";
      case 1 -> "亏凸月";
      case 2 -> "下弦月";
      case 3 -> "残月";
      case 4 -> "新月";
      case 5 -> "娥眉月";
      case 6 -> "上弦月";
      case 7 -> "盈凸月";
      default -> "未知";
    };
  }

  /**
   * 血月 / 超级血月夜晚被禁止自然生成的原版怪物（苦力怕、蜘蛛、洞穴蜘蛛、女巫）。
   */
  public static boolean isForbiddenMob(EntityType<?> type) {
    return type == EntityType.CREEPER
        || type == EntityType.SPIDER
        || type == EntityType.CAVE_SPIDER
        || type == EntityType.WITCH;
  }

  /**
   * 根据当前进化等级调整僵尸属性（血量 / 攻击 / 速度），并回满血量。
   */
  public static void applyZombieEvolution(Zombie zombie, Level level) {
    int tier = getEvolutionTier(level);
    if (tier <= 0) {
      return;
    }
    var config = CraftingDeadSurvival.serverConfig;
    double healthMultiplier = 1.0D + tier * config.evolutionHealthPerTier.get();
    double damageMultiplier = 1.0D + tier * config.evolutionDamagePerTier.get();
    double speedMultiplier = 1.0D + tier * config.evolutionSpeedPerTier.get();
    scaleAttribute(zombie, Attributes.MAX_HEALTH, healthMultiplier);
    scaleAttribute(zombie, Attributes.ATTACK_DAMAGE, damageMultiplier);
    scaleAttribute(zombie, Attributes.MOVEMENT_SPEED, speedMultiplier);
    zombie.setHealth(zombie.getMaxHealth());
  }

  /**
   * 血月夜晚僵尸有概率额外进化一级（仅提升一次，不叠加到进化等级上）。
   */
  public static void applyBloodMoonEvolution(Zombie zombie, Level level) {
    var config = CraftingDeadSurvival.serverConfig;
    double chance = isSuperBloodMoon(level)
        ? config.superBloodMoonExtraEvolutionChance.get()
        : config.bloodMoonExtraEvolutionChance.get();
    if (chance > 0.0D && level.getRandom().nextFloat() < chance) {
      double healthMultiplier = 1.0D + config.evolutionHealthPerTier.get();
      double damageMultiplier = 1.0D + config.evolutionDamagePerTier.get();
      double speedMultiplier = 1.0D + config.evolutionSpeedPerTier.get();
      scaleAttribute(zombie, Attributes.MAX_HEALTH, healthMultiplier);
      scaleAttribute(zombie, Attributes.ATTACK_DAMAGE, damageMultiplier);
      scaleAttribute(zombie, Attributes.MOVEMENT_SPEED, speedMultiplier);
      zombie.setHealth(zombie.getMaxHealth());
    }
  }

  private static void scaleAttribute(Zombie zombie, Holder<Attribute> attribute,
      double multiplier) {
    AttributeInstance instance = zombie.getAttribute(attribute);
    if (instance != null) {
      instance.setBaseValue(instance.getBaseValue() * multiplier);
    }
  }
}
