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

package com.craftingdead.survival.client.gui;

import com.craftingdead.survival.client.MoonDataHolder;
import com.craftingdead.survival.world.moon.MoonEventType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 左上角 HUD：显示玩家当前手持的武器 / 工具（兼容其他模组物品）、
 * 击杀信息（用什么武器击杀了什么），以及月亮事件横幅。
 */
@OnlyIn(Dist.CLIENT)
public class MoonHudOverlay {

  private static final int TEXT_COLOR = 0xFFFFFF;
  private static final int BG_COLOR = 0x66000000;
  private static final long KILL_FEED_FADE_MS = 2000L;

  private final Minecraft minecraft;

  public MoonHudOverlay(Minecraft minecraft) {
    this.minecraft = minecraft;
  }

  public void render(GuiGraphics guiGraphics, float partialTick) {
    var player = this.minecraft.player;
    if (player == null || this.minecraft.options.hideGui) {
      return;
    }

    renderMoonBanner(guiGraphics);

    int x = 4;
    int y = 4;

    // 手持物品显示（主手 / 副手）
    ItemStack mainHand = player.getMainHandItem();
    ItemStack offHand = player.getOffhandItem();
    if (!mainHand.isEmpty()) {
      renderItemLine(guiGraphics, x, y, mainHand);
      y += 18;
    }
    if (!offHand.isEmpty()) {
      renderItemLine(guiGraphics, x, y, offHand);
      y += 18;
    }

    // 击杀信息
    renderKillFeed(guiGraphics, x, y);
  }

  private void renderMoonBanner(GuiGraphics guiGraphics) {
    if (!MoonDataHolder.isActive()) {
      return;
    }
    MoonEventType event = MoonDataHolder.getEventType();
    String banner = "☠ " + event.getDisplayName() + " 降临！";
    String description = eventDescription(event);
    int bannerX = (guiGraphics.guiWidth() - this.minecraft.font.width(banner)) / 2;
    guiGraphics.drawString(this.minecraft.font, banner, bannerX, 4, event.getColor());
    int descX = (guiGraphics.guiWidth() - this.minecraft.font.width(description)) / 2;
    guiGraphics.drawString(this.minecraft.font, description, descX, 15, TEXT_COLOR);
  }

  private void renderItemLine(GuiGraphics guiGraphics, int x, int y, ItemStack stack) {
    // 半透明背景
    int width = Math.max(this.minecraft.font.width(stack.getHoverName()), 40) + 26;
    guiGraphics.fill(x - 2, y - 2, x + width, y + 18, BG_COLOR);
    guiGraphics.renderItem(stack, x, y);
    guiGraphics.drawString(this.minecraft.font, stack.getHoverName(),
        x + 18, y + 4, TEXT_COLOR);
  }

  private void renderKillFeed(GuiGraphics guiGraphics, int x, int y) {
    int lineIndex = 0;
    var iterator = MoonDataHolder.getKillFeed();
    while (iterator.hasNext() && lineIndex < 5) {
      var line = iterator.next();
      int alpha = killFeedAlpha(line.timeMs());
      int color = (alpha << 24) | 0xFFFFFF;

      ItemStack weapon = line.weapon();
      if (!weapon.isEmpty()) {
        guiGraphics.renderItem(weapon, x, y);
      }
      String weaponName = line.weaponName() != null
          ? line.weaponName().getString()
          : (weapon.isEmpty() ? null : weapon.getHoverName().getString());
      String text;
      if (weaponName != null) {
        text = line.killer().getString() + " 用 " + weaponName
            + " 击杀了 " + line.victim().getString();
      } else {
        text = line.killer().getString() + " 击杀了 " + line.victim().getString();
      }
      guiGraphics.drawString(this.minecraft.font, text, x + (weapon.isEmpty() ? 0 : 18),
          y + 4, color);
      y += 18;
      lineIndex++;
    }
  }

  private static int killFeedAlpha(long timeMs) {
    long elapsed = System.currentTimeMillis() - timeMs;
    long remaining = MoonDataHolder.KILL_FEED_LIFETIME_MS - elapsed;
    if (remaining <= 0L) {
      return 0;
    }
    float fade = Mth.clamp(remaining / (float) KILL_FEED_FADE_MS, 0.0F, 1.0F);
    return (int) (255.0F * fade);
  }

  private static String eventDescription(MoonEventType event) {
    return switch (event) {
      case BLOOD_MOON -> "怪物增多 · 无法入睡 · 僵尸进化";
      case SUPER_BLOOD_MOON -> "怪物暴增 · 无法入睡 · 僵尸大量进化";
      case BLUE_MOON -> "幸运降临";
      case YELLOW_MOON -> "农作物加速生长";
      default -> "";
    };
  }
}
