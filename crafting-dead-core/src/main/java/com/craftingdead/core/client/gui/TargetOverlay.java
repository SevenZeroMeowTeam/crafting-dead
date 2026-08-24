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

import org.jetbrains.annotations.Nullable;
import com.craftingdead.core.client.ClientDist;
import com.craftingdead.core.client.util.RenderUtil;
import com.craftingdead.core.quality.QualityHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Jade 风格的目标信息叠加层：显示准星所指方块/实体的名称、模组来源与实体血量。
 * 1.19.2 版本（使用 PoseStack + RenderUtil）。
 */
public class TargetOverlay {

  private static final double RANGE = 4.5D;

  // 面板样式（模仿 Jade/HWYLA）
  private static final int PANEL_BACKGROUND = 0xC8101010;
  private static final int PANEL_BORDER = 0xFF555555;
  private static final int NAME_COLOR = 0xFFFFFFFF;
  private static final int MOD_COLOR = 0xFF8A8A8A;
  private static final int BAR_BACKGROUND = 0xFF333333;
  private static final int BAR_BORDER = 0xFF555555;
  private static final int BAR_HEIGHT = 4;

  private static final int PADDING = 4;
  private static final int PANEL_TOP = 8;
  private static final int MOD_NAME_SPACING = 8;

  private final Minecraft minecraft;

  public TargetOverlay(Minecraft minecraft) {
    this.minecraft = minecraft;
  }

  public void render(PoseStack poseStack, float partialTick) {
    var player = this.minecraft.player;
    if (player == null || player.isSpectator() || this.minecraft.options.hideGui) {
      return;
    }
    if (!ClientDist.clientConfig.displayTargetInfo.get()) {
      return;
    }

    // 实体优先于方块（与 Jade 行为一致）
    var entity = this.getEntityHit(player, partialTick);
    if (entity != null) {
      this.renderEntity(poseStack, entity);
      return;
    }

    var blockHit = player.pick(RANGE, partialTick, false);
    if (blockHit.getType() == HitResult.Type.BLOCK) {
      this.renderBlock(poseStack, (BlockHitResult) blockHit);
    }
  }

  @Nullable
  private Entity getEntityHit(Player player, float partialTick) {
    var eyePos = player.getEyePosition(partialTick);
    var look = player.getLookAngle();
    var endPos = eyePos.add(look.scale(RANGE));
    var aabb = player.getBoundingBox().expandTowards(look.scale(RANGE)).inflate(1.0D);
    var hit = ProjectileUtil.getEntityHitResult(player, eyePos, endPos, aabb,
        e -> !e.isSpectator() && e.isPickable(), RANGE * RANGE);
    return hit == null ? null : hit.getEntity();
  }

  private void renderBlock(PoseStack poseStack, BlockHitResult blockHit) {
    var level = this.minecraft.level;
    if (level == null) {
      return;
    }
    var state = level.getBlockState(blockHit.getBlockPos());
    var name = state.getBlock().getName();
    var modName = Component.literal(this.getModId(
        ForgeRegistries.BLOCKS.getKey(state.getBlock())));
    var toolInfo = this.getHarvestTool(state);
    this.renderPanel(poseStack, name, modName, toolInfo, null);
  }

  private void renderEntity(PoseStack poseStack, Entity entity) {
    var name = entity.getDisplayName();
    var modName = Component.literal(this.getModId(
        ForgeRegistries.ENTITY_TYPES.getKey(entity.getType())));

    if (entity instanceof LivingEntity living) {
      var healthPct = Mth.clamp(living.getHealth() / Math.max(living.getMaxHealth(), 1.0F),
          0.0F, 1.0F);
      var healthText = Component.literal(
          String.format("%.0f / %.0f", living.getHealth(), living.getMaxHealth()));
      this.renderPanel(poseStack, name, modName, null, new HealthBar(healthPct, healthText));
    } else {
      this.renderPanel(poseStack, name, modName, null, null);
    }
  }

  private String getModId(net.minecraft.resources.ResourceLocation key) {
    return QualityHelper.getModDisplayName(key == null ? "?" : key.getNamespace());
  }

  /**
   * 计算方块需要的破坏工具，并判断玩家当前手持工具是否合适（模仿 Jade）。
   */
  private Component getHarvestTool(BlockState state) {
    String toolKey = null;
    if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
      toolKey = "pickaxe";
    } else if (state.is(BlockTags.MINEABLE_WITH_AXE)) {
      toolKey = "axe";
    } else if (state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
      toolKey = "shovel";
    } else if (state.is(BlockTags.MINEABLE_WITH_HOE)) {
      toolKey = "hoe";
    }
    if (toolKey == null) {
      return Component.translatable("target.craftingdead.tool_any");
    }
    var held = this.minecraft.player == null ? ItemStack.EMPTY
        : this.minecraft.player.getMainHandItem();
    boolean correct = held.isCorrectToolForDrops(state);
    return Component.translatable(
        correct ? "target.craftingdead.tool_correct" : "target.craftingdead.tool_wrong",
        Component.translatable("target.craftingdead.tool_" + toolKey));
  }

  private void renderPanel(PoseStack poseStack, Component name, Component modName,
      @Nullable Component extraLine, @Nullable HealthBar healthBar) {
    var font = this.minecraft.font;

    var nameWidth = font.width(name);
    var modWidth = font.width(modName);
    var contentWidth = Math.max(nameWidth + MOD_NAME_SPACING + modWidth,
        healthBar == null ? 0 : 60);
    if (extraLine != null) {
      contentWidth = Math.max(contentWidth, font.width(extraLine));
    }

    var panelWidth = contentWidth + PADDING * 2;
    var lineHeight = font.lineHeight;
    var extraArea = extraLine == null ? 0 : lineHeight + 2;
    var barArea = healthBar == null ? 0 : BAR_HEIGHT + 3;
    var panelHeight = PADDING * 2 + lineHeight + extraArea + barArea;

    var screenWidth = this.minecraft.getWindow().getGuiScaledWidth();
    var x = (screenWidth - panelWidth) / 2;
    var y = PANEL_TOP;

    // 背景
    RenderUtil.fill(poseStack, x, y, panelWidth, panelHeight, PANEL_BACKGROUND);

    // 边框（Jade 风格的四边细线）
    RenderUtil.fill(poseStack, x, y, panelWidth, 1, PANEL_BORDER);
    RenderUtil.fill(poseStack, x, y + panelHeight - 1, panelWidth, 1, PANEL_BORDER);
    RenderUtil.fill(poseStack, x, y, 1, panelHeight, PANEL_BORDER);
    RenderUtil.fill(poseStack, x + panelWidth - 1, y, 1, panelHeight, PANEL_BORDER);

    // 名称（左）与模组来源（右）
    GuiComponent.drawString(poseStack, font, name, x + PADDING, y + PADDING, NAME_COLOR);
    GuiComponent.drawString(poseStack, font, modName, x + panelWidth - PADDING - modWidth,
        y + PADDING, MOD_COLOR);

    // 破坏工具信息（方块专用）
    var lineY = y + PADDING + lineHeight;
    if (extraLine != null) {
      GuiComponent.drawString(poseStack, font, extraLine, x + PADDING, lineY + 2, MOD_COLOR);
      lineY += lineHeight + 2;
    }

    if (healthBar != null) {
      var barY = lineY + 3;
      var barX = x + PADDING;
      var barWidth = 60;
      // 血量背景 + 边框
      RenderUtil.fill(poseStack, barX - 1, barY - 1, barWidth + 2, BAR_HEIGHT + 2, BAR_BORDER);
      RenderUtil.fill(poseStack, barX, barY, barWidth, BAR_HEIGHT, BAR_BACKGROUND);
      // 血量填充（随剩余血量由绿转红）
      var fillWidth = Math.round(barWidth * healthBar.healthPct);
      if (fillWidth > 0) {
        RenderUtil.fill(poseStack, barX, barY, fillWidth, BAR_HEIGHT,
            this.getHealthColor(healthBar.healthPct));
      }
      // 血量文本
      GuiComponent.drawString(poseStack, font, healthBar.healthText, barX + barWidth + 5,
          barY - 2, NAME_COLOR);
    }
  }

  private int getHealthColor(float healthPct) {
    var red = Mth.clamp((int) ((1.0F - healthPct) * 255.0F), 0, 255);
    var green = Mth.clamp((int) (healthPct * 255.0F), 0, 255);
    return 0xFF000000 | (red << 16) | (green << 8);
  }

  private record HealthBar(float healthPct, Component healthText) {}
}
