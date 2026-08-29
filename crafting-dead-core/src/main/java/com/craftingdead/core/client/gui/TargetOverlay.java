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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import com.craftingdead.core.client.ClientDist;
import com.craftingdead.core.client.util.RenderUtil;
import com.craftingdead.core.quality.QualityHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Jade 风格的目标信息叠加层：显示准星所指方块/实体的名称、模组来源与实体血量。
 * 1.20.1 版本（使用 GuiGraphics）。
 */
public class TargetOverlay {

  private static final double RANGE = 4.5D;

  // 面板样式（模仿 Jade / WTHIT / HWYLA）
  private static final int PANEL_BACKGROUND = 0x80101010;
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
    // 安装了 WTHIT 且允许时，交给 WTHIT 渲染，避免两套面板重叠。
    if (shouldDeferToWthit()) {
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
    var pos = blockHit.getBlockPos();
    var state = level.getBlockState(pos);
    var name = state.getBlock().getName();
    var modName = Component.literal(this.getModId(
        ForgeRegistries.BLOCKS.getKey(state.getBlock())));

    List<Component> extraLines = new ArrayList<>();
    // 所需工具 + 是否合适
    extraLines.add(this.getHarvestTool(state));

    // 熔炉 / 高炉 / 烟熏炉：烧制进度与剩余时间
    if (level.getBlockEntity(pos) instanceof AbstractFurnaceBlockEntity furnace) {
      this.addFurnaceInfo(extraLines, furnace);
    } else if (!state.isAir() && state.getDestroySpeed(level, pos) >= 0.0F) {
      // 砍伐时间（原木 / 菌柄）或挖掘时间
      if (state.is(BlockTags.LOGS)) {
        this.addChopTime(extraLines, state, pos);
      } else {
        this.addMiningTime(extraLines, state, pos);
      }
    }

    this.renderPanel(guiGraphics, name, modName, extraLines, null);
  }

  /** 挖掘时间：以当前手持工具 / 徒手计算破坏该方块所需秒数。 */
  private void addMiningTime(List<Component> lines, BlockState state, BlockPos pos) {
    float progress = this.getDestroyProgress(state, pos);
    if (progress <= 0.0F) {
      return;
    }
    float seconds = Mth.ceil(1.0F / progress) / 20.0F;
    lines.add(Component.translatable("target.craftingdead.mining_time",
        String.format("%.1f", seconds)));
  }

  /** 砍伐时间：对原木（树木）使用斧类工具时的破坏耗时。 */
  private void addChopTime(List<Component> lines, BlockState state, BlockPos pos) {
    float progress = this.getDestroyProgress(state, pos);
    if (progress <= 0.0F) {
      return;
    }
    float seconds = Mth.ceil(1.0F / progress) / 20.0F;
    lines.add(Component.translatable("target.craftingdead.chop_time",
        String.format("%.1f", seconds)));
  }

  private float getDestroyProgress(BlockState state, BlockPos pos) {
    var player = this.minecraft.player;
    var level = this.minecraft.level;
    if (player == null || level == null) {
      return 0.0F;
    }
    return state.getDestroyProgress(player, level, pos);
  }

  /** 熔炉烧制进度与剩余时间。 */
  private void addFurnaceInfo(List<Component> lines, AbstractFurnaceBlockEntity furnace) {
    int progress = getFurnaceField(furnace, "cookingProgress");
    int total = getFurnaceField(furnace, "cookingTotalTime");
    if (total <= 0) {
      lines.add(Component.translatable("target.craftingdead.furnace_idle"));
      return;
    }
    lines.add(Component.translatable("target.craftingdead.furnace_progress", progress, total));
    if (progress < total) {
      float remaining = (total - progress) / 20.0F;
      lines.add(Component.translatable("target.craftingdead.furnace_remaining",
          String.format("%.1f", remaining)));
    } else {
      lines.add(Component.translatable("target.craftingdead.furnace_done"));
    }
  }

  /** 反射读取熔炉私有字段（cookingProgress / cookingTotalTime），跨 1.19.2 / 1.20.1 / 1.21.1 通用。 */
  private static int getFurnaceField(AbstractFurnaceBlockEntity furnace, String fieldName) {
    try {
      Field field = AbstractFurnaceBlockEntity.class.getDeclaredField(fieldName);
      field.setAccessible(true);
      return field.getInt(furnace);
    } catch (Exception ignored) {
      return 0;
    }
  }

  private void renderEntity(GuiGraphics guiGraphics, Entity entity) {
    var name = entity.getDisplayName();
    var modName = Component.literal(this.getModId(
        ForgeRegistries.ENTITY_TYPES.getKey(entity.getType())));

    if (entity instanceof LivingEntity living) {
      var healthPct = Mth.clamp(living.getHealth() / Math.max(living.getMaxHealth(), 1.0F),
          0.0F, 1.0F);
      var healthText = Component.literal(
          String.format("%.0f / %.0f", living.getHealth(), living.getMaxHealth()));
      this.renderPanel(guiGraphics, name, modName, null, new HealthBar(healthPct, healthText));
    } else {
      this.renderPanel(guiGraphics, name, modName, null, null);
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

  private void renderPanel(GuiGraphics guiGraphics, Component name, Component modName,
      @Nullable List<Component> extraLines, @Nullable HealthBar healthBar) {
    var font = this.minecraft.font;

    var nameWidth = font.width(name);
    var modWidth = font.width(modName);
    var contentWidth = Math.max(nameWidth + MOD_NAME_SPACING + modWidth,
        healthBar == null ? 0 : 60);
    if (extraLines != null) {
      for (Component line : extraLines) {
        contentWidth = Math.max(contentWidth, font.width(line));
      }
    }

    var panelWidth = contentWidth + PADDING * 2;
    var lineHeight = font.lineHeight;
    var extraArea = (extraLines == null || extraLines.isEmpty()) ? 0
        : extraLines.size() * (lineHeight + 2);
    var barArea = healthBar == null ? 0 : BAR_HEIGHT + 3;
    var panelHeight = PADDING * 2 + lineHeight + extraArea + barArea;

    var screenWidth = this.minecraft.getWindow().getGuiScaledWidth();
    var x = (screenWidth - panelWidth) / 2;
    var y = PANEL_TOP;

    // 背景
    RenderUtil.fill(guiGraphics.pose(), x, y, panelWidth, panelHeight, PANEL_BACKGROUND);

    // 边框（Jade 风格的四边细线）
    RenderUtil.fill(guiGraphics.pose(), x, y, panelWidth, 1, PANEL_BORDER);
    RenderUtil.fill(guiGraphics.pose(), x, y + panelHeight - 1, panelWidth, 1, PANEL_BORDER);
    RenderUtil.fill(guiGraphics.pose(), x, y, 1, panelHeight, PANEL_BORDER);
    RenderUtil.fill(guiGraphics.pose(), x + panelWidth - 1, y, 1, panelHeight, PANEL_BORDER);

    // 名称（左）与模组来源（右）
    guiGraphics.drawString(font, name, x + PADDING, y + PADDING, NAME_COLOR);
    guiGraphics.drawString(font, modName, x + panelWidth - PADDING - modWidth,
        y + PADDING, MOD_COLOR);

    // 额外信息（工具 / 挖掘 / 砍伐 / 熔炉时间）
    var lineY = y + PADDING + lineHeight;
    if (extraLines != null) {
      for (Component line : extraLines) {
        guiGraphics.drawString(font, line, x + PADDING, lineY + 2, MOD_COLOR);
        lineY += lineHeight + 2;
      }
    }

    if (healthBar != null) {
      var barY = lineY + 3;
      var barX = x + PADDING;
      var barWidth = 60;
      // 血量背景 + 边框
      RenderUtil.fill(guiGraphics.pose(), barX - 1, barY - 1, barWidth + 2, BAR_HEIGHT + 2, BAR_BORDER);
      RenderUtil.fill(guiGraphics.pose(), barX, barY, barWidth, BAR_HEIGHT, BAR_BACKGROUND);
      // 血量填充（随剩余血量由绿转红）
      var fillWidth = Math.round(barWidth * healthBar.healthPct);
      if (fillWidth > 0) {
        RenderUtil.fill(guiGraphics.pose(), barX, barY, fillWidth, BAR_HEIGHT,
            this.getHealthColor(healthBar.healthPct));
      }
      // 血量文本
      guiGraphics.drawString(font, healthBar.healthText, barX + barWidth + 5,
          barY - 2, NAME_COLOR);
    }
  }

  private int getHealthColor(float healthPct) {
    var red = Mth.clamp((int) ((1.0F - healthPct) * 255.0F), 0, 255);
    var green = Mth.clamp((int) (healthPct * 255.0F), 0, 255);
    return 0xFF000000 | (red << 16) | (green << 8);
  }

  /** 是否把目标信息渲染交给 WTHIT（已安装且配置允许时）。 */
  static boolean shouldDeferToWthit() {
    return ClientDist.clientConfig.deferTargetInfoToWthit.get()
        && ModList.get().isLoaded("wthit");
  }

  private record HealthBar(float healthPct, Component healthText) {}
}
