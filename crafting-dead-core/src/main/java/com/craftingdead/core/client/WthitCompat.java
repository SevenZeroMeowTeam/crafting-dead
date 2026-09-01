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
import net.neoforged.fml.ModList;

/**
 * WTHIT（What The Hell Is That）兼容检测。
 *
 * <p>WTHIT 是社区常用的「准星所指方块/实体信息显示」模组（Jade / HWYLA 系）。
 * crafting-dead 内置的 {@code TargetOverlay}（Jade 风格目标信息叠加层）与
 * {@code BlockOutlineRenderer}（方块范围框）与之功能重复。
 *
 * <p>当玩家已安装 WTHIT 时，应隐藏 crafting-dead 内置的同类功能，避免重复显示。
 */
@OnlyIn(Dist.CLIENT)
public final class WthitCompat {

  /** WTHIT 的 mod id。 */
  private static final String WTHIT_MOD_ID = "wthit";

  private WthitCompat() {}

  /**
   * 是否已安装并加载 WTHIT 模组。
   */
  public static boolean isWthitLoaded() {
    return ModList.get().isLoaded(WTHIT_MOD_ID);
  }
}
