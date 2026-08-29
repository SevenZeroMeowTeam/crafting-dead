package com.qlm.zombie.moon;

import com.craftingdead.survival.world.moon.ApocalypseManager;
import com.craftingdead.survival.world.moon.MoonEventType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * QLM（qlmzombie）月亮 / 作物 API 兼容实现。
 *
 * <p>为 KubeJS 服务端脚本（{@code server_scripts/moon_scheduler.js}、
 * {@code airdrop_scheduler.js}、{@code harvest_moon_growth.js}、
 * {@code lucky_moon_buff.js}）提供 {@code com.qlm.zombie.moon.MoonHelper} 类，
 * 底层桥接到本模组（craftingdeadsurvival）的 {@link ApocalypseManager} 月亮事件系统。</p>
 *
 * <p>映射关系：幸运月 → 蓝月（{@link MoonEventType#BLUE_MOON}），丰收月 → 黄月
 * （{@link MoonEventType#YELLOW_MOON}），血月 → 血月 / 超级血月。</p>
 *
 * <p>通过 {@link #onLevelTick} 在跨天时自动清除由本桥接强制设置的月亮事件，
 * 避免月亮事件长期滞留（用户用 {@code /moon set} 设置的手动事件不受影响）。</p>
 */
public final class MoonHelper {

  private MoonHelper() {}

  /** 本桥接强制事件所在的天数；{@code -1} 表示没有待自动清理的强制事件。 */
  private static int forcedDay = -1;

  /** 当前世界昼夜时间（0-24000 循环的 tick 数）。 */
  public static long getDayTime(Level level) {
    return level.getDayTime();
  }

  /** 当前世界天数。 */
  public static int getDay(Level level) {
    return ApocalypseManager.getDay(level);
  }

  /** 当前月亮事件 id；无事件时返回 {@code "none"}。 */
  public static String getCurrentMoonId(Level level) {
    return toMoonId(ApocalypseManager.getMoonEvent(level));
  }

  /** 强制血月。 */
  public static boolean forceBloodMoon(ServerLevel level) {
    return forceEvent(level, MoonEventType.BLOOD_MOON);
  }

  /** 强制蓝月（幸运之月）。 */
  public static boolean forceLuckyMoon(ServerLevel level) {
    return forceEvent(level, MoonEventType.BLUE_MOON);
  }

  /** 强制黄月（丰收之月）。 */
  public static boolean forceHarvestMoon(ServerLevel level) {
    return forceEvent(level, MoonEventType.YELLOW_MOON);
  }

  /** 当前是否处于丰收（黄）月。 */
  public static boolean isHarvestMoon(Level level) {
    return ApocalypseManager.isYellowMoon(level);
  }

  /** 当前是否处于幸运（蓝）月。 */
  public static boolean isLuckyMoon(Level level) {
    return ApocalypseManager.isBlueMoon(level);
  }

  /** 让指定位置的作物流一阶段（最多到成熟）。 */
  public static void forceGrowCrop(Level level, BlockPos pos) {
    BlockState state = level.getBlockState(pos);
    if (state.getBlock() instanceof CropBlock crop) {
      if (!crop.isMaxAge(state)) {
        level.setBlockAndUpdate(pos, crop.getStateForAge(crop.getAge(state) + 1));
      }
    }
  }

  private static boolean forceEvent(ServerLevel level, MoonEventType event) {
    ApocalypseManager.setManualEvent(event);
    forcedDay = ApocalypseManager.getDay(level);
    return true;
  }

  /**
   * 跨天时清除由本桥接强制设置的月亮事件，避免长期滞留。
   *
   * <p>仅在 {@link #forcedDay} 已设置（即由 {@code force*} 方法触发）时生效，
   * 不会影响用户通过 {@code /moon set} 设置的手动事件。</p>
   */
  @SubscribeEvent
  public static void onLevelTick(TickEvent.LevelTickEvent event) {
    if (event.phase != TickEvent.Phase.END) {
      return;
    }
    if (!(event.level instanceof ServerLevel level)) {
      return;
    }
    if (forcedDay < 0) {
      return;
    }
    if (ApocalypseManager.getDay(level) != forcedDay) {
      ApocalypseManager.clearManualEvent();
      forcedDay = -1;
    }
  }

  private static String toMoonId(MoonEventType event) {
    return switch (event) {
      case NONE -> "none";
      case BLOOD_MOON -> "craftingdead:blood_moon";
      case BLUE_MOON -> "craftingdead:lucky_moon";
      case YELLOW_MOON -> "craftingdead:harvest_moon";
      case SUPER_BLOOD_MOON -> "craftingdead:super_blood_moon";
      case SUPER_BLUE_MOON -> "craftingdead:super_lucky_moon";
      case SUPER_YELLOW_MOON -> "craftingdead:super_harvest_moon";
    };
  }
}
