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

package com.craftingdead.survival.world.moon;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Locale;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * 「/moon」命令：手动切换月亮事件 / 月相。
 * <ul>
 *   <li>{@code /moon info}      显示当前状态</li>
 *   <li>{@code /moon list}      列出可用事件与用法</li>
 *   <li>{@code /moon set <事件>} 强制切换月亮事件（覆盖天数推算，夜晚生效）</li>
 *   <li>{@code /moon clear}     清除手动覆盖，恢复按天数推算</li>
 *   <li>{@code /moon phase <0-7>} 手动切换月相</li>
 *   <li>{@code /moon night}     把主世界时间切换到夜晚（让事件立即生效）</li>
 *   <li>{@code /moon day <n>}   设置主世界天数（进化等级会同步变化）</li>
 * </ul>
 * 需要权限等级 2（管理员）。
 */
public final class MoonCommand {

  private static final int REQUIRED_PERMISSION = 2;
  private static final long DAY_TICKS = 24000L;
  private static final long NIGHT_TIME = 18000L;

  private static final SuggestionProvider<CommandSourceStack> EVENT_SUGGESTIONS = (context, builder) -> {
    builder.suggest("none");
    builder.suggest("blood_moon");
    builder.suggest("super_blood_moon");
    builder.suggest("blue_moon");
    builder.suggest("super_blue_moon");
    builder.suggest("yellow_moon");
    builder.suggest("super_yellow_moon");
    return builder.buildFuture();
  };

  private MoonCommand() {}

  @SubscribeEvent
  public static void onRegisterCommands(RegisterCommandsEvent event) {
    CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
    dispatcher.register(Commands.literal("moon")
        .executes(MoonCommand::info)
        .then(Commands.literal("info")
            .executes(MoonCommand::info))
        .then(Commands.literal("list")
            .executes(MoonCommand::list))
        .then(Commands.literal("set")
            .requires(MoonCommand::isOp)
            .then(Commands.argument("event", StringArgumentType.word())
                .suggests(EVENT_SUGGESTIONS)
                .executes(MoonCommand::setEvent)))
        .then(Commands.literal("clear")
            .requires(MoonCommand::isOp)
            .executes(MoonCommand::clear))
        .then(Commands.literal("phase")
            .requires(MoonCommand::isOp)
            .then(Commands.argument("phase", IntegerArgumentType.integer(0, 7))
                .executes(MoonCommand::setPhase)))
        .then(Commands.literal("night")
            .requires(MoonCommand::isOp)
            .executes(MoonCommand::night))
        .then(Commands.literal("day")
            .requires(MoonCommand::isOp)
            .then(Commands.argument("day", IntegerArgumentType.integer(0))
                .executes(MoonCommand::setDay))));
  }

  private static boolean isOp(CommandSourceStack source) {
    return source.hasPermission(REQUIRED_PERMISSION);
  }

  private static int info(CommandContext<CommandSourceStack> ctx) {
    CommandSourceStack source = ctx.getSource();
    ServerLevel level = overworld(source);
    if (level == null) {
      source.sendFailure(Component.literal("主世界不存在。"));
      return 0;
    }
    MoonEventType event = ApocalypseManager.getMoonEvent(level);
    boolean active = ApocalypseManager.isMoonEventActive(level);
    String manualEvent = ApocalypseManager.isManualEventSet() ? "§r | §d[已手动覆盖事件]" : "";
    String manualPhase = ApocalypseManager.isManualPhaseSet() ? "§r | §d[已手动覆盖月相]" : "";
    int phase = ApocalypseManager.getMoonPhase(level);
    source.sendSuccess(Component.literal(
        "§6[月相]§r 天数: §b" + ApocalypseManager.getDay(level)
        + "§r | 时间: §b" + formatTime((int) (level.getDayTime() % DAY_TICKS))
        + "§r | 月相: " + ApocalypseManager.getMoonPhaseColorCode(phase)
        + ApocalypseManager.getMoonPhaseName(phase)
        + "(" + ApocalypseManager.getMoonPhaseStrengthName(phase) + ")"
        + "§r | 事件: " + eventColor(event) + event.getDisplayName()
        + "§r | 状态: " + (active ? "§c● 进行中" : "§7○ 未发生")
        + "§r | 进化: §eLV." + ApocalypseManager.getEvolutionTier(level)
        + manualEvent + manualPhase), false);
    return 1;
  }

  private static int list(CommandContext<CommandSourceStack> ctx) {
    CommandSourceStack source = ctx.getSource();
    source.sendSuccess(Component.literal(
        "§6[月相]§r 可用事件: §7none§r, §cblood_moon§r, §9blue_moon§r, §eyellow_moon§r, "
        + "§dsuper_blood_moon§r, §bsuper_blue_moon§r, §6super_yellow_moon§r"), false);
    source.sendSuccess(Component.literal(
        "§6[月相]§r 用法: §7/moon set <事件>§r | §7/moon clear§r | "
        + "§7/moon phase <0-7>§r | §7/moon night§r | §7/moon day <n>§r | §7/moon info§r"), false);
    return 1;
  }

  private static int setEvent(CommandContext<CommandSourceStack> ctx) {
    CommandSourceStack source = ctx.getSource();
    MoonEventType event = parseEvent(StringArgumentType.getString(ctx, "event"));
    if (event == null) {
      source.sendFailure(Component.literal("§c未知事件，使用 /moon list 查看可用事件。"));
      return 0;
    }
    ApocalypseManager.setManualEvent(event);
    source.sendSuccess(Component.literal(
        "§6[月相]§r 已手动切换事件: " + eventColor(event) + event.getDisplayName()
        + "§r  (仅在夜晚生效，可用 /moon night 切换到夜晚)"), true);
    return 1;
  }

  private static int clear(CommandContext<CommandSourceStack> ctx) {
    CommandSourceStack source = ctx.getSource();
    ApocalypseManager.clearManualEvent();
    ApocalypseManager.clearManualPhase();
    source.sendSuccess(Component.literal(
        "§6[月相]§r 已清除手动覆盖，月相与事件恢复按天数推算。"), true);
    return 1;
  }

  private static int setPhase(CommandContext<CommandSourceStack> ctx) {
    CommandSourceStack source = ctx.getSource();
    int phase = IntegerArgumentType.getInteger(ctx, "phase");
    ApocalypseManager.setManualPhase(phase);
    source.sendSuccess(Component.literal(
        "§6[月相]§r 已手动切换月相: §b" + ApocalypseManager.getMoonPhaseName(phase)
        + " (" + phase + "/7)"), true);
    return 1;
  }

  private static int night(CommandContext<CommandSourceStack> ctx) {
    CommandSourceStack source = ctx.getSource();
    ServerLevel level = overworld(source);
    if (level == null) {
      source.sendFailure(Component.literal("主世界不存在。"));
      return 0;
    }
    level.setDayTime(level.getDayTime() - (level.getDayTime() % DAY_TICKS) + NIGHT_TIME);
    source.sendSuccess(Component.literal("§6[月相]§r 已把主世界时间切换到夜晚。"), true);
    return 1;
  }

  private static int setDay(CommandContext<CommandSourceStack> ctx) {
    CommandSourceStack source = ctx.getSource();
    ServerLevel level = overworld(source);
    if (level == null) {
      source.sendFailure(Component.literal("主世界不存在。"));
      return 0;
    }
    int day = IntegerArgumentType.getInteger(ctx, "day");
    long currentTimeOfDay = level.getDayTime() % DAY_TICKS;
    level.setDayTime(day * DAY_TICKS + currentTimeOfDay);
    source.sendSuccess(Component.literal(
        "§6[月相]§r 已设置天数: §b" + day
        + "§r (若未手动覆盖事件，今日月亮事件将按新天数推算)"), true);
    return 1;
  }

  // ================================================================================
  // Helpers
  // ================================================================================

  @javax.annotation.Nullable
  private static ServerLevel overworld(CommandSourceStack source) {
    MinecraftServer server = source.getServer();
    return server != null ? server.overworld() : null;
  }

  @javax.annotation.Nullable
  private static MoonEventType parseEvent(String input) {
    String normalized = input.trim().toLowerCase(Locale.ROOT)
        .replace(' ', '_').replace('-', '_');
    return switch (normalized) {
      case "none", "normal", "正常" -> MoonEventType.NONE;
      case "blood", "blood_moon", "血月" -> MoonEventType.BLOOD_MOON;
      case "super_blood", "super_blood_moon", "超级血月" -> MoonEventType.SUPER_BLOOD_MOON;
      case "blue", "blue_moon", "蓝月" -> MoonEventType.BLUE_MOON;
      case "super_blue", "super_blue_moon", "超级蓝月" -> MoonEventType.SUPER_BLUE_MOON;
      case "yellow", "yellow_moon", "黄月" -> MoonEventType.YELLOW_MOON;
      case "super_yellow", "super_yellow_moon", "超级黄月" -> MoonEventType.SUPER_YELLOW_MOON;
      default -> null;
    };
  }

  private static String eventColor(MoonEventType event) {
    return switch (event) {
      case BLOOD_MOON -> "§c";
      case BLUE_MOON -> "§9";
      case YELLOW_MOON -> "§e";
      case SUPER_BLOOD_MOON -> "§d";
      case SUPER_BLUE_MOON -> "§b";
      case SUPER_YELLOW_MOON -> "§6";
      default -> "§7";
    };
  }

  private static String formatTime(int timeOfDay) {
    int hour = (int) (((timeOfDay / 1000.0F) + 6.0F) % 24.0F);
    int minute = (int) ((timeOfDay % 1000) / 1000.0F * 60.0F);
    return String.format("%02d:%02d", hour, minute);
  }
}
