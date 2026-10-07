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

package com.craftingdead.survival.client;

import com.craftingdead.survival.world.moon.ApocalypseManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 客户端月相染色工具：根据当前月相给僵尸模型染上对应颜色（满月暖金、新月石板灰等）。
 *
 * <p>返回 0xRRGGBB 乘算色（{@link #NO_TINT} = 不染色），由渲染器通过
 * {@code RenderSystem.setShaderColor} 应用。与渲染方式解耦，不依赖任何动画库。</p>
 */
@OnlyIn(Dist.CLIENT)
public final class MoonPhaseTint {

  /** 不染色（乘算白色）。 */
  public static final int NO_TINT = 0xFFFFFF;

  /** 向白色混合的比例：0 = 纯白（不染色），1 = 直接用月相颜色。 */
  private static final float BLEND = 0.6F;

  private MoonPhaseTint() {}

  /**
   * 计算当前月相下僵尸的渲染染色颜色。
   *
   * @return 0xRRGGBB 染色值；未收到月相同步数据或配置关闭时返回 {@link #NO_TINT}（不染色）
   */
  public static int getZombieTint() {
    if (!MoonDataHolder.isSynced()
        || !ClientDist.clientConfig.moonPhaseZombieTintEnabled.get()) {
      return NO_TINT;
    }
    int color = ApocalypseManager.getMoonPhaseColor(MoonDataHolder.getMoonPhase());
    int r = (color >> 16) & 0xFF;
    int g = (color >> 8) & 0xFF;
    int b = color & 0xFF;
    int tr = (int) (255 + (r - 255) * BLEND);
    int tg = (int) (255 + (g - 255) * BLEND);
    int tb = (int) (255 + (b - 255) * BLEND);
    return (tr << 16) | (tg << 8) | tb;
  }
}
