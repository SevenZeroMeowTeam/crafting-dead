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

package com.craftingdead.core.telemetry;

import com.mojang.authlib.GameProfile;
import io.sentry.Sentry;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.server.ServerLifecycleHooks;

public final class TelemetryRuntimeSampler {

  private static final AtomicBoolean STARTED = new AtomicBoolean();

  private TelemetryRuntimeSampler() {
  }

  public static void ensureStarted() {
    if (STARTED.compareAndSet(false, true)) {
      MinecraftForge.EVENT_BUS.register(new ServerSampler());
      DistExecutor.safeRunWhenOn(net.minecraftforge.api.distmarker.Dist.CLIENT,
          () -> ClientSampler::register);
    }
  }

  private static final class ServerSampler {

    private int tickCounter;

    @SubscribeEvent
    public void handleServerTick(TickEvent.ServerTickEvent event) {
      if (event.phase != TickEvent.Phase.END) {
        return;
      }
      if (++this.tickCounter % 20 != 0) {
        return;
      }
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      if (server == null) {
        return;
      }
      captureServerSnapshot(server);
    }

    private void captureServerSnapshot(MinecraftServer server) {
      var playerList = server.getPlayerList();
      List<Map<String, Object>> players = new ArrayList<>();
      for (ServerPlayer player : playerList.getPlayers()) {
        Map<String, Object> info = new LinkedHashMap<>();
        GameProfile profile = player.getGameProfile();
        info.put("name", profile.getName());
        info.put("uuid", profile.getId().toString());
  info.put("dimension", player.level.dimension().location().toString());
        info.put("x", player.getX());
        info.put("y", player.getY());
        info.put("z", player.getZ());
        info.put("health", player.getHealth());
        info.put("food", player.getFoodData().getFoodLevel());
        info.put("experienceLevel", player.experienceLevel);
        players.add(info);
      }

      List<Map<String, Object>> worlds = new ArrayList<>();
      for (ServerLevel level : server.getAllLevels()) {
        Map<String, Object> worldInfo = new LinkedHashMap<>();
        DimensionType dimensionType = level.dimensionType();
        worldInfo.put("dimension", level.dimension().location().toString());
        worldInfo.put("difficulty", level.getDifficulty().getKey());
        worldInfo.put("isDay", level.isDay());
        worldInfo.put("isRaining", level.isRaining());
        worldInfo.put("isThundering", level.isThundering());
        worldInfo.put("dayTime", level.getDayTime());
  worldInfo.put("players", level.players().size());
        worldInfo.put("logicalHeight", dimensionType.logicalHeight());
        worlds.add(worldInfo);
      }

      Sentry.configureScope(scope -> {
        scope.setExtra("server.motd", server.getMotd());
        scope.setExtra("server.playerCount", Integer.toString(playerList.getPlayerCount()));
        scope.setExtra("server.maxPlayers", Integer.toString(playerList.getMaxPlayers()));
        scope.setExtra("server.viewDistance", Integer.toString(playerList.getViewDistance()));
        scope.setExtra("server.simulationDistance",
            Integer.toString(playerList.getSimulationDistance()));
        scope.setExtra("server.players", TelemetryManager.encodeToJson(players));
        scope.setExtra("server.worlds", TelemetryManager.encodeToJson(worlds));
        scope.setTag("server.dedicated", String.valueOf(server.isDedicatedServer()));
      });
    }
  }

  private static final class ClientSampler {

    private int tickCounter;

    private static void register() {
      MinecraftForge.EVENT_BUS.register(new ClientSampler());
    }

    @SubscribeEvent
    public void handleClientTick(TickEvent.ClientTickEvent event) {
      if (event.phase != TickEvent.Phase.END || event.side != LogicalSide.CLIENT) {
        return;
      }
      if (++this.tickCounter % 20 != 0) {
        return;
      }
      if (!FMLEnvironment.dist.isClient()) {
        return;
      }
      net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
      if (minecraft == null) {
        return;
      }
      int width = minecraft.getWindow().getWidth();
      int height = minecraft.getWindow().getHeight();
      String language = minecraft.getLanguageManager().getSelected().toString();
      boolean isDemo = minecraft.isDemo();
      Sentry.configureScope(scope -> {
        scope.setExtra("client.user", minecraft.getUser().getName());
        scope.setExtra("client.type", minecraft.getUser().getType().getName());
        scope.setExtra("client.window",
            TelemetryManager.encodeToJson(mapOf("width", width, "height", height)));
        scope.setExtra("client.language", language);
        scope.setExtra("client.demo", Boolean.toString(isDemo));
      });
    }
  }

  private static Map<String, Object> mapOf(Object... entries) {
    Map<String, Object> map = new LinkedHashMap<>();
    for (int i = 0; i + 1 < entries.length; i += 2) {
      map.put(String.valueOf(entries[i]), entries[i + 1]);
    }
    return map;
  }
}
