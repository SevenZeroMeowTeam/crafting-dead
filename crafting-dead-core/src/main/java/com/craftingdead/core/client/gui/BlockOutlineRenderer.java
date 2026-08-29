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

package com.craftingdead.core.client.gui;

import com.craftingdead.core.client.ClientDist;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;

/**
 * 世界中的方块范围框：准星对准方块时，渲染一个「方框」（目标方块线框）并绕其画一圈
 * （水平面 3×3 外圈），标出挖掘 / 砍伐影响的范围。
 * <p>1.20.1 版本（Forge 47.x，事件直接提供 PoseStack）。</p>
 */
@OnlyIn(Dist.CLIENT)
public class BlockOutlineRenderer {

  private static final double RANGE = 4.5D;

  // 目标方块「方框」颜色（白）
  private static final float BOX_R = 1.0F;
  private static final float BOX_G = 1.0F;
  private static final float BOX_B = 1.0F;
  private static final float BOX_A = 0.85F;

  // 绕方框一圈的范围环颜色（金黄）
  private static final float RING_R = 1.0F;
  private static final float RING_G = 0.85F;
  private static final float RING_B = 0.2F;
  private static final float RING_A = 0.55F;

  private final Minecraft minecraft;

  public BlockOutlineRenderer(Minecraft minecraft) {
    this.minecraft = minecraft;
  }

  public void render(RenderLevelStageEvent event) {
    if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) {
      return;
    }
    var player = this.minecraft.player;
    if (player == null || player.isSpectator() || this.minecraft.options.hideGui) {
      return;
    }
    if (!ClientDist.clientConfig.displayTargetInfo.get()) {
      return;
    }
    // 安装了 WTHIT 且允许时，交给 WTHIT 渲染，不再画本方框/范围环。
    if (TargetOverlay.shouldDeferToWthit()) {
      return;
    }
    var level = this.minecraft.level;
    if (level == null) {
      return;
    }
    var hit = player.pick(RANGE, event.getPartialTick(), false);
    if (hit.getType() != HitResult.Type.BLOCK) {
      return;
    }
    var pos = ((BlockHitResult) hit).getBlockPos();
    if (level.getBlockState(pos).isAir()) {
      return;
    }

    // 事件提供的 PoseStack 已含相机平移，直接按世界坐标绘制
    var poseStack = event.getPoseStack();
    poseStack.pushPose();

    var buffers = this.minecraft.renderBuffers().bufferSource();
    VertexConsumer lines = buffers.getBuffer(RenderType.lines());

    // 1. 目标方块「方框」
    LevelRenderer.renderLineBox(poseStack, lines, new AABB(pos), BOX_R, BOX_G, BOX_B, BOX_A);

    // 2. 绕方框一圈：水平面 3×3 外圈（标出挖掘 / 砍伐范围）
    LevelRenderer.renderLineBox(poseStack, lines,
        new AABB(pos.getX() - 1, pos.getY(), pos.getZ() - 1,
            pos.getX() + 2, pos.getY() + 1, pos.getZ() + 2),
        RING_R, RING_G, RING_B, RING_A);

    buffers.endBatch();
    poseStack.popPose();
  }
}
