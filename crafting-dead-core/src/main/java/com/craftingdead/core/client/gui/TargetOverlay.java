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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Jade 风格的目标信息叠加层：显示准星所指方块/实体的名称、模组来源与实体血量。
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

  public void render(GuiGraphics guiGraphics, float partialTick) {
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
      this.renderEntity(guiGraphics, entity);
      return;
    }

    var blockHit = player.pick(RANGE, partialTick, false);
    if (blockHit.getType() == HitResult.Type.BLOCK) {
      this.renderBlock(guiGraphics, (BlockHitResult) blockHit);
    }
  }

  @Nullable
  private Entity getEntityHit(Player player, float partialTick) {
    var level = player.level();
    var eyePos = player.getEyePosition(partialTick);
    var look = player.getLookAngle();
    var endPos = eyePos.add(look.scale(RANGE));
    var aabb = player.getBoundingBox().expandTowards(look.scale(RANGE)).inflate(1.0D);
    var hit = ProjectileUtil.getEntityHitResult(player, eyePos, endPos, aabb,
        e -> !e.isSpectator() && e.isPickable(), RANGE * RANGE);
    return hit == null ? null : hit.getEntity();
  }

  private void renderBlock(GuiGraphics guiGraphics, BlockHitResult blockHit) {
    var level = this.minecraft.level;
    if (level == null) {
      return;
    }
    var state = level.getBlockState(blockHit.getBlockPos());
    var name = state.getBlock().getName();
    var modName = this.getModName(state);
    this.renderPanel(guiGraphics, name, modName, null);
  }

  private void renderEntity(GuiGraphics guiGraphics, Entity entity) {
    var name = entity.getDisplayName();
    var modName = Component.literal(
        String.valueOf(ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).getNamespace()));

    if (entity instanceof LivingEntity living) {
      var healthPct = Mth.clamp(living.getHealth() / Math.max(living.getMaxHealth(), 1.0F),
          0.0F, 1.0F);
      MutableComponent healthText = Component.literal(
          String.format("%.0f / %.0f", living.getHealth(), living.getMaxHealth()));
      this.renderPanel(guiGraphics, name, modName, new HealthBar(healthPct, healthText));
    } else {
      this.renderPanel(guiGraphics, name, modName, null);
    }
  }

  private Component getModName(BlockState state) {
    var key = ForgeRegistries.BLOCKS.getKey(state.getBlock());
    return Component.literal(key == null ? "?" : key.getNamespace());
  }

  private void renderPanel(GuiGraphics guiGraphics, Component name, Component modName,
      @Nullable HealthBar healthBar) {
    var font = this.minecraft.font;

    var nameWidth = font.width(name);
    var modWidth = font.width(modName);
    var contentWidth = Math.max(nameWidth + MOD_NAME_SPACING + modWidth,
        healthBar == null ? 0 : 60);

    var panelWidth = contentWidth + PADDING * 2;
    var lineHeight = font.lineHeight;
    var barArea = healthBar == null ? 0 : BAR_HEIGHT + 3;
    var panelHeight = PADDING * 2 + lineHeight + barArea;

    var screenWidth = guiGraphics.guiWidth();
    var x = (screenWidth - panelWidth) / 2;
    var y = PANEL_TOP;

    // 背景
    guiGraphics.fill(x, y, x + panelWidth, y + panelHeight, PANEL_BACKGROUND);

    // 边框（Jade 风格的四边细线）
    guiGraphics.fill(x, y, x + panelWidth, y + 1, PANEL_BORDER);
    guiGraphics.fill(x, y + panelHeight - 1, x + panelWidth, y + panelHeight, PANEL_BORDER);
    guiGraphics.fill(x, y, x + 1, y + panelHeight, PANEL_BORDER);
    guiGraphics.fill(x + panelWidth - 1, y, x + panelWidth, y + panelHeight, PANEL_BORDER);

    // 名称（左）与模组来源（右）
    guiGraphics.drawString(font, name, x + PADDING, y + PADDING, NAME_COLOR, false);
    guiGraphics.drawString(font, modName, x + panelWidth - PADDING - modWidth,
        y + PADDING, MOD_COLOR, false);

    if (healthBar != null) {
      var barY = y + PADDING + lineHeight + 3;
      var barX = x + PADDING;
      var barWidth = 60;
      // 血量背景 + 边框
      guiGraphics.fill(barX - 1, barY - 1, barX + barWidth + 1, barY + BAR_HEIGHT + 1,
          BAR_BORDER);
      guiGraphics.fill(barX, barY, barX + barWidth, barY + BAR_HEIGHT, BAR_BACKGROUND);
      // 血量填充（随剩余血量由绿转红）
      var fillWidth = Math.round(barWidth * healthBar.healthPct);
      if (fillWidth > 0) {
        guiGraphics.fill(barX, barY, barX + fillWidth, barY + BAR_HEIGHT,
            this.getHealthColor(healthBar.healthPct));
      }
      // 血量文本
      guiGraphics.drawString(font, healthBar.healthText, barX + barWidth + 5, barY - 2,
          NAME_COLOR, false);
    }
  }

  private int getHealthColor(float healthPct) {
    var red = Mth.clamp((int) ((1.0F - healthPct) * 255.0F), 0, 255);
    var green = Mth.clamp((int) (healthPct * 255.0F), 0, 255);
    return 0xFF000000 | (red << 16) | (green << 8);
  }

  private record HealthBar(float healthPct, Component healthText) {}
}
