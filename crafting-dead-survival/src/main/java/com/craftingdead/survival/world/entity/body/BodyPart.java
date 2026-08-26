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

package com.craftingdead.survival.world.entity.body;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * 生物身体部位（用于枪械命中部位判定与断肢效果）。
 *
 * <p>判定基于命中点相对实体的高度比例（命中点高度 / 实体碰撞箱高度）：
 * <ul>
 *   <li>≥ 72%：头部（爆头）</li>
 *   <li>≥ 48%：手臂 / 上身</li>
 *   <li>≥ 30%：腰部</li>
 *   <li>其余：腿部</li>
 * </ul>
 */
public enum BodyPart {

  HEAD,
  ARM,
  WAIST,
  LEG;

  /** 头部高度比例阈值。 */
  private static final double HEAD_RATIO = 0.72D;
  /** 手臂/上身高度比例阈值。 */
  private static final double ARM_RATIO = 0.48D;
  /** 腰部高度比例阈值。 */
  private static final double WAIST_RATIO = 0.30D;

  /**
   * 根据命中位置判定命中部位。
   *
   * @param entity 被击中的生物
   * @param hitPos 弹道命中点（世界坐标）
   * @return 命中的部位
   */
  public static BodyPart fromHitPosition(LivingEntity entity, Vec3 hitPos) {
    double ratio = (hitPos.y - entity.getY()) / entity.getBbHeight();
    if (ratio >= HEAD_RATIO) {
      return HEAD;
    }
    if (ratio >= ARM_RATIO) {
      return ARM;
    }
    if (ratio >= WAIST_RATIO) {
      return WAIST;
    }
    return LEG;
  }
}
