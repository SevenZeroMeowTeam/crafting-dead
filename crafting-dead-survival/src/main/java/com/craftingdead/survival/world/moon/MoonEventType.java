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

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * 月亮事件类型。以 28 天为一个完整周期：
 * <ul>
 *   <li>第 6 天  → 蓝月（玩家获得幸运效果）</li>
 *   <li>第 7 天  → 超级蓝月（幸运效果加强）</li>
 *   <li>第 13 天 → 血月（怪物增多、禁止睡觉、僵尸有概率额外进化、禁止苦力怕/蜘蛛/洞穴蜘蛛/女巫），每 14 天一次</li>
 *   <li>第 20 天 → 黄月（农作物生长加速）</li>
 *   <li>第 21 天 → 超级黄月（农作物生长加速加强）</li>
 *   <li>第 27 天 → 超级血月（血月加强版，每 28 天一次）</li>
 * </ul>
 */
public enum MoonEventType {
  NONE("正常", 0xFFFFFF),
  BLOOD_MOON("血月", 0xFF5555),
  BLUE_MOON("蓝月", 0x55AAFF),
  YELLOW_MOON("黄月", 0xFFE055),
  SUPER_BLOOD_MOON("超级血月", 0xCC44FF),
  SUPER_BLUE_MOON("超级蓝月", 0x88CCFF),
  SUPER_YELLOW_MOON("超级黄月", 0xFFCC55);

  private final String displayName;
  private final int color;

  MoonEventType(String displayName, int color) {
    this.displayName = displayName;
    this.color = color;
  }

  public String getDisplayName() {
    return this.displayName;
  }

  public int getColor() {
    return this.color;
  }

  public MutableComponent getDisplayComponent() {
    return Component.literal(this.displayName).withColor(this.color);
  }

  /**
   * 根据天数返回对应的月亮事件。
   *
   * @param day 世界天数（getDayTime() / 24000）
   */
  public static MoonEventType forDay(long day) {
    long m = Math.floorMod(day, 28L);
    if (m == 27) {
      return SUPER_BLOOD_MOON;
    }
    if (m == 21) {
      return SUPER_YELLOW_MOON;
    }
    if (m == 20) {
      return YELLOW_MOON;
    }
    if (m == 13) {
      return BLOOD_MOON;
    }
    if (m == 7) {
      return SUPER_BLUE_MOON;
    }
    if (m == 6) {
      return BLUE_MOON;
    }
    return NONE;
  }
}
