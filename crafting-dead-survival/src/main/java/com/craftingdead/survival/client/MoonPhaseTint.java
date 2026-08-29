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
import software.bernie.geckolib.core.object.Color;

/**
 * 客户端月相染色工具：根据当前月相给僵尸模型染上对应颜色（满月暖金、新月石板灰等）。
 *
 * <p>GeckoLib 4.x（1.20.1）的 {@code getRenderColor} 返回的是乘算色（{@link Color#WHITE} = 不染色），
 * 因此这里把月相颜色向白色柔和混合，得到一个可见但不刺眼的叠加色。</p>
 */
@OnlyIn(Dist.CLIENT)
public final class MoonPhaseTint {

  /** 向白色混合的比例：0 = 纯白（不染色），1 = 直接用月相颜色。 */
  private static final float BLEND = 0.6F;

  private MoonPhaseTint() {}

  /**
   * 计算当前月相下僵尸的渲染染色颜色。
   *
   * @return 染色 {@link Color}；未收到月相同步数据或配置关闭时返回 {@link Color#WHITE}（不染色）
   */
  public static Color getZombieTint() {
    if (!MoonDataHolder.isSynced()
        || !ClientDist.clientConfig.moonPhaseZombieTintEnabled.get()) {
      return Color.WHITE;
    }
    int color = ApocalypseManager.getMoonPhaseColor(MoonDataHolder.getMoonPhase());
    int r = (color >> 16) & 0xFF;
    int g = (color >> 8) & 0xFF;
    int b = color & 0xFF;
    int tr = (int) (255 + (r - 255) * BLEND);
    int tg = (int) (255 + (g - 255) * BLEND);
    int tb = (int) (255 + (b - 255) * BLEND);
    return Color.ofRGBA(tr, tg, tb, 255);
  }
}
