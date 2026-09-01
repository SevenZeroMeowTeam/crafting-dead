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

package com.craftingdead.worldguard;

import com.craftingdead.core.world.effect.ModMobEffects;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Set;
import org.bukkit.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.craftingdead.core.event.GrenadeThrowEvent;
import com.craftingdead.core.event.GunEvent;
import com.craftingdead.core.world.entity.extension.PlayerExtension;
import com.craftingdead.immerse.event.WaterDecayEvent;
import com.craftingdead.survival.world.effect.SurvivalMobEffects;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.BukkitPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

/**
 * Crafting Dead 与 WorldGuard 的集成逻辑。仅在混合端（Arclight / Mohist 等同时提供
 * NeoForge 与 Bukkit WorldGuard 的服务器）上被 {@link CraftingDeadWorldGuard}
 * 通过反射加载；纯 NeoForge 环境不会加载本类。
 */
final class WorldGuardIntegration {

  private static final Logger logger = LoggerFactory.getLogger(WorldGuardIntegration.class);

  // 混合端注入到原版类上的 CraftBukkit 桥接方法。解析失败表示非混合端，集成禁用。
  private static final MethodHandle getWorld;
  private static final MethodHandle getBukkitEntity;
  private static final MethodHandle getHandle;
  private static final boolean CRAFT_BUKKIT_AVAILABLE;

  static {
    MethodHandle worldHandle = null;
    MethodHandle bukkitEntityHandle = null;
    MethodHandle handle = null;
    boolean available = false;
    try {
      // 1.21.1 CraftBukkit 包名（v1_21_R1）。这些桥接方法只在混合端运行时被注入到
      // 原版类中，因此必须反射解析。
      var craftWorld = Class.forName("org.bukkit.craftbukkit.v1_21_R1.CraftWorld");
      var lookup = MethodHandles.publicLookup();
      worldHandle = lookup.findVirtual(
          Level.class,
          "getWorld",
          MethodType.methodType(craftWorld));

      var craftEntity = Class.forName("org.bukkit.craftbukkit.v1_21_R1.entity.CraftEntity");
      bukkitEntityHandle = lookup.findVirtual(
          Entity.class,
          "getBukkitEntity",
          MethodType.methodType(craftEntity));
      handle = lookup.findVirtual(
          craftEntity,
          "getHandle",
          MethodType.methodType(Entity.class));
      available = true;
    } catch (Throwable t) {
      logger.warn("CraftBukkit bridge not available - WorldGuard integration disabled", t);
    }
    getWorld = worldHandle;
    getBukkitEntity = bukkitEntityHandle;
    getHandle = handle;
    CRAFT_BUKKIT_AVAILABLE = available;
  }

  // WorldGuard 自定义旗标（仅当 WorldGuard 存在时在服务端启动阶段创建）
  private StateFlag infection;
  private StateFlag brokenLegs;
  private StateFlag bleeding;
  private StateFlag thirst;
  private StateFlag shooting;
  private StateFlag grenadeThrowing;
  private BooleanFlag clearEquipmentOnExit;

  private WorldGuardIntegration() {
  }

  /** 由 {@link CraftingDeadWorldGuard} 在检测到 WorldGuard 后通过反射调用。 */
  static void init(IEventBus modEventBus) {
    if (!CRAFT_BUKKIT_AVAILABLE) {
      logger.info("CraftBukkit bridge unavailable - WorldGuard integration disabled.");
      return;
    }
    var integration = new WorldGuardIntegration();
    var forgeBus = NeoForge.EVENT_BUS;
    forgeBus.addListener(integration::handleServerStarting);
    forgeBus.addListener(integration::handlePotionApplicable);
    forgeBus.addListener(integration::handleWaterDecay);
    forgeBus.addListener(integration::handleGunEntityHit);
    forgeBus.addListener(integration::handleGunBlockHit);
    forgeBus.addListener(integration::handleGrenadeThrow);
  }

  private void handleServerStarting(ServerStartingEvent event) {
    // 服务端启动时 WorldGuard 已就绪：注册旗标 + 会话处理器
    this.infection = new StateFlag("infection", true);
    this.brokenLegs = new StateFlag("broken-legs", true);
    this.bleeding = new StateFlag("bleeding", true);
    this.thirst = new StateFlag("thirst", true);
    this.shooting = new StateFlag("shooting", true);
    this.grenadeThrowing = new StateFlag("grenade-throwing", true);
    this.clearEquipmentOnExit = new BooleanFlag("clear-equipment-on-exit");
    registerFlag(this.infection);
    registerFlag(this.brokenLegs);
    registerFlag(this.bleeding);
    registerFlag(this.thirst);
    registerFlag(this.shooting);
    registerFlag(this.grenadeThrowing);
    registerFlag(this.clearEquipmentOnExit);

    var sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();
    sessionManager.registerHandler(
        HandlerAdapter.createFactory(
            this::handleEnter,
            this::handleExit),
        null);

    logger.info("Crafting Dead WorldGuard loaded.");
  }

  private void handleEnter(PlayerExtension<?> extension, Set<ProtectedRegion> regions) {
    var stopBleeding = false;
    var stopBrokenLegs = false;

    for (var region : regions) {
      if (region.getFlag(this.bleeding) == StateFlag.State.DENY) {
        stopBleeding = true;
      }

      if (region.getFlag(this.brokenLegs) == StateFlag.State.DENY) {
        stopBrokenLegs = true;
      }
    }

    if (stopBleeding && hasEffect(extension.entity(), ModMobEffects.BLEEDING)) {
      removeEffect(extension.entity(), ModMobEffects.BLEEDING);
    }

    if (stopBrokenLegs && hasEffect(extension.entity(), SurvivalMobEffects.BROKEN_LEG)) {
      removeEffect(extension.entity(), SurvivalMobEffects.BROKEN_LEG);
    }
  }

  private void handleExit(PlayerExtension<?> extension, Set<ProtectedRegion> regions) {
    var clearEquipment = false;

    for (var region : regions) {
      if (region.getFlag(this.clearEquipmentOnExit) == Boolean.TRUE) {
        clearEquipment = true;
      }
    }

    if (clearEquipment) {
      extension.clearEquipment();
    }
  }

  private void handlePotionApplicable(MobEffectEvent.Applicable event) {
    if (event.getEntity() instanceof Player player) {
      var localPlayer = toWorldGuardPlayer(player);
      var regions = getApplicableRegions(player);
      var effect = getEffect(event.getEffectInstance());

      StateFlag flag = null;
      if (effect == SurvivalMobEffects.INFECTION) {
        flag = this.infection;
      } else if (effect == SurvivalMobEffects.BROKEN_LEG) {
        flag = this.brokenLegs;
      } else if (effect == ModMobEffects.BLEEDING) {
        flag = this.bleeding;
      }

      if (flag != null && !regions.testState(localPlayer, flag)) {
        event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
      }
    }
  }

  private void handleWaterDecay(WaterDecayEvent event) {
    var localPlayer = toWorldGuardPlayer(event.getPlayer());
    var regions = getApplicableRegions(event.getPlayer());
    event.setCanceled(!regions.testState(localPlayer, this.thirst));
  }

  private void handleGunEntityHit(GunEvent.EntityHit event) {
    if (event.living() instanceof PlayerExtension<?> player) {
      var localPlayer = toWorldGuardPlayer(player.entity());
      var regions = getApplicableRegions(player.entity());
      event.setCanceled(!regions.testState(localPlayer, this.shooting));
    }
  }

  private void handleGunBlockHit(GunEvent.BlockHit event) {
    if (event.living() instanceof PlayerExtension<?> player) {
      var localPlayer = toWorldGuardPlayer(player.entity());
      var regions = getApplicableRegions(player.entity());
      event.setCanceled(!regions.testState(localPlayer, this.shooting));
    }
  }

  private void handleGrenadeThrow(GrenadeThrowEvent event) {
    var localPlayer = toWorldGuardPlayer(event.getPlayer());
    var regions = getApplicableRegions(event.getPlayer());
    event.setCanceled(!regions.testState(localPlayer, this.grenadeThrowing));
  }

  private static ApplicableRegionSet getApplicableRegions(Player player) {
    var container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    var regions = container.get(BukkitAdapter.adapt(adapt(player.level())));
    var position = BlockVector3.at(player.getX(), player.getY(), player.getZ());
    return regions.getApplicableRegions(position);
  }

  private static LocalPlayer toWorldGuardPlayer(Player player) {
    return WorldGuardPlugin.inst().wrapPlayer((org.bukkit.entity.Player) adapt(player));
  }

  private static World adapt(Level level) {
    try {
      return (World) getWorld.invoke(level);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  private static org.bukkit.entity.Entity adapt(Entity entity) {
    try {
      return (org.bukkit.entity.Entity) getBukkitEntity.invoke(entity);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  private static void registerFlag(Flag<?> flag) {
    var registry = WorldGuard.getInstance().getFlagRegistry();
    try {
      registry.register(flag);
    } catch (FlagConflictException e) {
      throw new IllegalStateException(e);
    }
  }

  private static Entity toEntity(org.bukkit.entity.Entity entity) {
    try {
      return (Entity) getHandle.invoke(entity);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  private static Holder<MobEffect> getEffect(MobEffectInstance effectInstance) {
    return effectInstance.getEffect();
  }

  private static boolean hasEffect(LivingEntity entity, Holder<MobEffect> effect) {
    return entity.hasEffect(effect);
  }

  private static boolean removeEffect(LivingEntity entity, Holder<MobEffect> effect) {
    return entity.removeEffect(effect);
  }

  static Player toEntity(LocalPlayer player) {
    return (Player) toEntity(((BukkitPlayer) player).getPlayer());
  }
}
