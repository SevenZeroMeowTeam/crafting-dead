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

package com.craftingdead.survival.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 侧边栏计分板整体下移，避免被右上角旅行地图（JourneyMap）小地图遮挡。
 *
 * <p>1.21.1 中侧边栏在 {@link Gui#renderScoreboardSidebar} 里渲染，位置垂直居中偏上，
 * 与右上角的小地图重叠时部分内容（标题/前几行）会被盖住。
 * 这里在渲染前把整个绘制矩阵向下平移，渲染结束后恢复，不影响其它 HUD 元素。
 */
@Mixin(Gui.class)
public class ScoreboardSidebarMixin {

  /**
   * 下移像素数（GUI 缩放后坐标）。
   * 可按屏幕分辨率 / 小地图大小自行调整：数值越大计分板越靠下。
   */
  private static final int SIDEBAR_Y_OFFSET = 45;

  @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"))
  private void craftingdead$shiftSidebarDown(GuiGraphics guiGraphics, DeltaTracker deltaTracker,
      CallbackInfo ci) {
    guiGraphics.pose().pushPose();
    guiGraphics.pose().translate(0.0F, SIDEBAR_Y_OFFSET, 0.0F);
  }

  @Inject(method = "renderScoreboardSidebar", at = @At("RETURN"))
  private void craftingdead$restorePose(GuiGraphics guiGraphics, DeltaTracker deltaTracker,
      CallbackInfo ci) {
    guiGraphics.pose().popPose();
  }
}
