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

package com.craftingdead.immerse.command;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import com.craftingdead.core.world.entity.extension.PlayerExtension;
import com.craftingdead.immerse.CraftingDeadImmerse;
import com.craftingdead.immerse.Permissions;
import com.craftingdead.immerse.game.survival.SurvivalGame;
import com.craftingdead.immerse.game.survival.SurvivalPlayerHandler;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.fml.LogicalSide;

public class HydrateCommand {

    private static final long TICKS_PER_SECOND = 20L;
    private static final long TICKS_PER_MINUTE = TICKS_PER_SECOND * 60L;
    private static final Map<UUID, Long> COOLDOWNS = new HashMap<>();

  public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    dispatcher.register(Commands.literal("hydrate")
        .requires(source -> ImmerseCommands.hasPermission(source, Permissions.HYDRATE)
            && CraftingDeadImmerse.getInstance()
                .getGame(LogicalSide.SERVER) instanceof SurvivalGame game
            && game.isThirstEnabled())
        .executes(context -> processCommand(context.getSource(),
            List.of(context.getSource().getPlayerOrException())))
        .then(Commands.argument("targets", EntityArgument.entities())
            .executes(context -> processCommand(context.getSource(),
                EntityArgument.getPlayers(context, "targets")))));
  }

  private static int processCommand(CommandSourceStack source, Collection<ServerPlayer> targets) {
        var cooldownMinutes = CraftingDeadImmerse.serverConfig.hydrateCommandCooldownMinutes.get();
        if (cooldownMinutes > 0 && source.getEntity() instanceof ServerPlayer executingPlayer) {
            var currentTick = executingPlayer.getLevel().getGameTime();
            var cooldownTicks = cooldownMinutes * TICKS_PER_MINUTE;
            var lastUse = COOLDOWNS.get(executingPlayer.getUUID());
            if (lastUse != null) {
                var elapsed = currentTick - lastUse;
                if (elapsed < cooldownTicks) {
                    var remainingTicks = cooldownTicks - elapsed;
                    source.sendFailure(new TranslatableComponent("commands.hydrate.cooldown",
                            formatCooldown(remainingTicks)).withStyle(ChatFormatting.RED));
                    return 0;
                }
            }
            COOLDOWNS.put(executingPlayer.getUUID(), currentTick);
        }

    var hydrated = targets.stream()
        .filter(ServerPlayer::isAlive)
        .flatMap(player -> Stream.ofNullable(PlayerExtension.get(player)))
        .peek(HydrateCommand::hydrate)
        .toList();

    source.sendSuccess(
        hydrated.size() == 1
            ? new TranslatableComponent("commands.hydrate.success.single",
                hydrated.get(0).entity().getDisplayName())
                    .withStyle(ChatFormatting.GREEN)
            : new TranslatableComponent("commands.hydrate.success.multiple", hydrated.size())
                .withStyle(ChatFormatting.GREEN),
        false);

    return 0;
  }

  private static void hydrate(PlayerExtension<ServerPlayer> player) {
    player.getHandlerOrThrow(SurvivalPlayerHandler.TYPE).hydrate();
    player.entity().playNotifySound(SoundEvents.GENERIC_DRINK, SoundSource.NEUTRAL, 5.0F, 5.0F);
  }

    private static TextComponent formatCooldown(long remainingTicks) {
        var remainingSeconds = (long) Math.ceil(remainingTicks / (double) TICKS_PER_SECOND);
        var minutes = remainingSeconds / 60;
        var seconds = remainingSeconds % 60;

        var builder = new StringBuilder();
        if (minutes > 0) {
            builder.append(minutes).append("m");
        }
        if (seconds > 0 || minutes == 0) {
            if (minutes > 0) {
                builder.append(' ');
            }
            builder.append(seconds).append('s');
        }

        return new TextComponent(builder.toString());
    }
}
