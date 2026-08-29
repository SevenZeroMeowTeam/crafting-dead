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

import com.craftingdead.survival.network.message.SyncMoonDataMessage;
import com.craftingdead.survival.world.moon.MoonEventType;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 客户端持有的月亮 / 天数 / 击杀信息数据（由网络消息更新）。
 */
@OnlyIn(Dist.CLIENT)
public final class MoonDataHolder {

  public static final long KILL_FEED_LIFETIME_MS = 5000L;
  private static final int MAX_KILL_FEED = 5;

  private static int day;
  private static int timeOfDay;
  private static int moonPhase;
  private static int evolutionTier;
  private static MoonEventType eventType = MoonEventType.NONE;
  private static boolean active;
  private static boolean synced;

  private static final Deque<KillFeedLine> killFeed = new ArrayDeque<>();

  private MoonDataHolder() {}

  public static void update(SyncMoonDataMessage msg) {
    day = msg.day();
    timeOfDay = msg.timeOfDay();
    moonPhase = msg.moonPhase();
    evolutionTier = msg.evolutionTier();
    eventType = msg.eventType();
    active = msg.active();
    synced = true;
  }

  public static boolean isSynced() {
    return synced;
  }

  public static void addKillFeed(Component killer, Component victim, ItemStack weapon,
      @Nullable Component weaponName) {
    killFeed.addFirst(new KillFeedLine(killer, victim, weapon, weaponName, Util.getMillis()));
    while (killFeed.size() > MAX_KILL_FEED) {
      killFeed.removeLast();
    }
  }

  public static Iterator<KillFeedLine> getKillFeed() {
    long now = Util.getMillis();
    killFeed.removeIf(line -> now - line.timeMs() > KILL_FEED_LIFETIME_MS);
    return killFeed.iterator();
  }

  public static int getDay() {
    return day;
  }

  public static int getTimeOfDay() {
    return timeOfDay;
  }

  public static int getMoonPhase() {
    return moonPhase;
  }

  public static int getEvolutionTier() {
    return evolutionTier;
  }

  public static MoonEventType getEventType() {
    return eventType;
  }

  public static boolean isActive() {
    return active;
  }

  /**
   * 一条击杀记录。{@code weaponName} 为 TaCZ 枪械按 GunId 解析出的真实枪名（可为空）。
   */
  public record KillFeedLine(Component killer, Component victim, ItemStack weapon,
      @Nullable Component weaponName, long timeMs) {}
}
