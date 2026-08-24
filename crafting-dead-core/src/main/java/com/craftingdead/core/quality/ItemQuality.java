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

package com.craftingdead.core.quality;

import java.util.List;
import java.util.Random;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

/**
 * 物品品质系统。
 *
 * <p>品质从高到低：
 * <pre>
 * 神话（紫粉） &gt; 传说（橙色） &gt; 英雄（红色） &gt; 史诗（金色） &gt; 稀有（紫色）
 *   &gt; 优秀（蓝色） &gt; 普通（绿色） &gt; 劣质（黑色）
 * </pre>
 *
 * <p>品质越高，武器 / 工具造成的伤害越高。
 */
public enum ItemQuality {

  /** 神话 - 紫粉（最高品质，无视游戏规则，由下界合金+钻石合成） */
  MYTHIC("mythic", 0xFF00FF, 5.0F, 1),
  /** 传说 - 橙色 */
  LEGENDARY("legendary", 0xFFB000, 3.0F, 1),
  /** 英雄 - 红色 */
  HERO("hero", 0xFF2A2A, 2.5F, 2),
  /** 史诗 - 金色 */
  EPIC("epic", 0xFFAA00, 2.0F, 4),
  /** 稀有 - 紫色 */
  RARE("rare", 0xAA00AA, 1.6F, 8),
  /** 优秀 - 蓝色 */
  EXCELLENT("excellent", 0x5555FF, 1.3F, 12),
  /** 普通 - 绿色 */
  COMMON("common", 0x00AA00, 1.0F, 24),
  /** 劣质 - 黑色 */
  POOR("poor", 0x404040, 0.6F, 8);

  private static final Random RANDOM = new Random();

  private final String name;
  private final int color;
  private final float damageMultiplier;
  private final int weight;

  ItemQuality(String name, int color, float damageMultiplier, int weight) {
    this.name = name;
    this.color = color;
    this.damageMultiplier = damageMultiplier;
    this.weight = weight;
  }

  public String getName() {
    return this.name;
  }

  public int getColor() {
    return this.color;
  }

  /**
   * 伤害倍率，品质越高倍率越高。
   */
  public float getDamageMultiplier() {
    return this.damageMultiplier;
  }

  public int getWeight() {
    return this.weight;
  }

  public Component getDisplayName() {
    return Component.translatable("quality.craftingdead." + this.name)
        .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(this.color)));
  }

  /**
   * 根据权重随机抽取一个品质。
   */
  public static ItemQuality rollRandom() {
    List<ItemQuality> qualities = List.of(values());
    int totalWeight = qualities.stream().mapToInt(ItemQuality::getWeight).sum();
    int roll = RANDOM.nextInt(totalWeight);
    for (ItemQuality quality : qualities) {
      roll -= quality.getWeight();
      if (roll < 0) {
        return quality;
      }
    }
    return COMMON;
  }

  /**
   * 按名称解析品质，未知名称返回 null。
   */
  public static ItemQuality byName(String name) {
    for (ItemQuality quality : values()) {
      if (quality.name.equalsIgnoreCase(name)) {
        return quality;
      }
    }
    return null;
  }
}
