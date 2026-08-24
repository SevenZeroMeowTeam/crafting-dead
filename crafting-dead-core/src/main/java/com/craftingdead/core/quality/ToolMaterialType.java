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
 * 工具（镐子 / 铲子 / 斧子 / 锄头）的随机材质。
 *
 * <p>合成的工具会随机获得一种材质，材质决定其挖掘速度与攻击力加成。
 */
public enum ToolMaterialType {

  WOOD("wood", 1, 1.0F, 2.0F),
  STONE("stone", 2, 2.0F, 4.0F),
  IRON("iron", 3, 3.0F, 6.0F),
  GOLD("gold", 4, 4.0F, 2.0F),
  DIAMOND("diamond", 5, 5.0F, 8.0F),
  NETHERITE("netherite", 6, 6.0F, 10.0F);

  private static final Random RANDOM = new Random();

  private final String name;
  /** 材质等级，用于判定可挖掘方块（与 Tier 对应）。 */
  private final int level;
  /** 挖掘速度（方块破坏速度倍率）。 */
  private final float digSpeed;
  /** 攻击力加成（在物品基础伤害之上额外增加的伤害）。 */
  private final float attackBonus;

  ToolMaterialType(String name, int level, float digSpeed, float attackBonus) {
    this.name = name;
    this.level = level;
    this.digSpeed = digSpeed;
    this.attackBonus = attackBonus;
  }

  public String getName() {
    return this.name;
  }

  public int getLevel() {
    return this.level;
  }

  public float getDigSpeed() {
    return this.digSpeed;
  }

  public float getAttackBonus() {
    return this.attackBonus;
  }

  public Component getDisplayName() {
    return Component.translatable("tool_material.craftingdead." + this.name)
        .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAAAA)));
  }

  /**
   * 随机抽取一个工具材质（普通到高级概率递减）。
   */
  public static ToolMaterialType rollRandom() {
    int roll = RANDOM.nextInt(100);
    if (roll < 40) {
      return WOOD;
    } else if (roll < 65) {
      return STONE;
    } else if (roll < 82) {
      return IRON;
    } else if (roll < 88) {
      return GOLD;
    } else if (roll < 96) {
      return DIAMOND;
    } else {
      return NETHERITE;
    }
  }

  /**
   * 按名称解析材质，未知名称返回 null。
   */
  public static ToolMaterialType byName(String name) {
    for (ToolMaterialType material : values()) {
      if (material.name.equalsIgnoreCase(name)) {
        return material;
      }
    }
    return null;
  }

  public static List<ToolMaterialType> all() {
    return List.of(values());
  }
}
