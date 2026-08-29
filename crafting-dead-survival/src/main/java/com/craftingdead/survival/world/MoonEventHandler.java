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

package com.craftingdead.survival.world;

import com.craftingdead.survival.CraftingDeadSurvival;
import com.craftingdead.survival.network.SurvivalNetworkChannel;
import com.craftingdead.core.quality.QualityHelper;
import com.craftingdead.survival.network.message.SurvivalKillFeedMessage;
import com.craftingdead.survival.network.message.SyncMoonDataMessage;
import com.craftingdead.survival.world.entity.SurvivalEntityTypes;
import com.craftingdead.survival.world.moon.ApocalypseManager;
import com.craftingdead.survival.world.moon.MoonEventType;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 末日生存事件处理器：
 * <ul>
 *   <li>计分板显示天数 / 时间 / 月相 / 事件 / 进化等级</li>
 *   <li>血月：怪物增多（额外生成僵尸）、禁止睡觉、僵尸有概率额外进化、禁止苦力怕/蜘蛛/洞穴蜘蛛/女巫</li>
 *   <li>超级血月：血月加强版（更多怪物、更高进化概率）</li>
 *   <li>蓝月：玩家获得幸运效果</li>
 *   <li>黄月：农作物生长加速</li>
 *   <li>击杀：概率掉落原版物品与其他模组物品，并广播击杀信息</li>
 * </ul>
 * <p>Minecraft 1.19.2 / Forge 43.5.2 版本。</p>
 */
public class MoonEventHandler {

  private static final String SCOREBOARD_OBJECTIVE = "apocalypse_info";

  private static List<Item> lootPool;

  private final List<String> lastScoreboardRows = new ArrayList<>();

  // ================================================================================
  // 尸潮状态（服务端）
  // ================================================================================

  /** 尸潮是否正在进行。 */
  private boolean hordeActive;
  /** 当前尸潮波数（0 = 未开始，1..N = 第几波）。 */
  private int hordeCurrentWave;
  /** 下一波生成的服务器 tick。 */
  private long hordeNextWaveTick;
  /** 本次尸潮绑定的天数（避免同一天重复触发）。 */
  private long hordeBoundDay = -1L;

  // ================================================================================
  // Server Tick：计分板 / 数据同步 / 蓝月幸运 / 血月生成
  // ================================================================================

  @SubscribeEvent
  public void handleServerTick(TickEvent.ServerTickEvent event) {
    if (event.phase != TickEvent.Phase.END) {
      return;
    }
    MinecraftServer server = event.getServer();
    if (server == null || server.overworld() == null) {
      return;
    }
    ServerLevel level = server.overworld();
    // 每秒更新一次
    if (server.getTickCount() % 20 != 0) {
      return;
    }
    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
      this.updateScoreboard(player, level);
      this.sendMoonData(player, level);
    }
    if (CraftingDeadSurvival.serverConfig.moonEventsEnabled.get()) {
      if (ApocalypseManager.isBlueMoon(level)) {
        this.handleBlueMoon(server, level);
      }
      if (ApocalypseManager.isBloodMoon(level)) {
        this.bloodMoonTick(server, level);
      }
      this.hordeTick(server, level);
    }
  }

  private void sendMoonData(ServerPlayer player, ServerLevel level) {
    SurvivalNetworkChannel.PLAY.getSimpleChannel().send(
        PacketDistributor.PLAYER.with(() -> player),
        new SyncMoonDataMessage(
            ApocalypseManager.getDay(level),
            (int) (level.getDayTime() % 24000L),
            ApocalypseManager.getMoonPhase(level),
            ApocalypseManager.getEvolutionTier(level),
            ApocalypseManager.getMoonEvent(level),
            ApocalypseManager.isMoonEventActive(level),
            this.hordeCurrentWave));
  }

  // ================================================================================
  // 计分板
  // ================================================================================

  private void updateScoreboard(ServerPlayer player, ServerLevel level) {
    if (!CraftingDeadSurvival.serverConfig.scoreboardEnabled.get()) {
      return;
    }
    Scoreboard scoreboard = player.getScoreboard();
    Objective objective = scoreboard.getObjective(SCOREBOARD_OBJECTIVE);
    if (objective == null) {
      objective = scoreboard.addObjective(SCOREBOARD_OBJECTIVE, ObjectiveCriteria.DUMMY,
          Component.literal("§6┃ 末日生存 ┃"), ObjectiveCriteria.RenderType.INTEGER);
    }
    scoreboard.setDisplayObjective(Scoreboard.DISPLAY_SLOT_SIDEBAR, objective);

    // 清理上一轮的计分行
    for (String row : this.lastScoreboardRows) {
      scoreboard.resetPlayerScore(row, objective);
    }
    this.lastScoreboardRows.clear();

    int day = ApocalypseManager.getDay(level);
    int timeOfDay = (int) (level.getDayTime() % 24000L);
    MoonEventType event = ApocalypseManager.getMoonEvent(level);
    boolean active = ApocalypseManager.isMoonEventActive(level);
    int tier = ApocalypseManager.getEvolutionTier(level);

    addRow(scoreboard, objective, "§f 天数: §b" + day, 7);
    addRow(scoreboard, objective, "§f 时间: §b" + formatTime(timeOfDay), 6);
    int moonPhase = ApocalypseManager.getMoonPhase(level);
    addRow(scoreboard, objective,
        "§f 月相: " + ApocalypseManager.getMoonPhaseColorCode(moonPhase)
        + ApocalypseManager.getMoonPhaseName(moonPhase), 5);
    addRow(scoreboard, objective,
        "§f 强度: §b" + ApocalypseManager.getMoonPhaseStrengthName(moonPhase), 4);
    addRow(scoreboard, objective,
        "§f 今日: " + eventColor(event) + event.getDisplayName(), 3);
    addRow(scoreboard, objective,
        "§f 状态: " + (active ? "§c● 进行中" : "§7○ 未发生"), 2);
    if (this.hordeActive && this.hordeCurrentWave > 0) {
      addRow(scoreboard, objective,
          "§f 尸潮: §c第 " + this.hordeCurrentWave + " 波", 1);
    }
    addRow(scoreboard, objective, "§f 进化: §eLV." + tier, 0);
  }

  private void addRow(Scoreboard scoreboard, Objective objective, String text, int score) {
    scoreboard.getOrCreatePlayerScore(text, objective).setScore(score);
    this.lastScoreboardRows.add(text);
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

  // ================================================================================
  // 蓝月：幸运效果
  // ================================================================================

  private void handleBlueMoon(MinecraftServer server, ServerLevel level) {
    // 天亮则清除幸运，结束「直到天亮」的效果
    if (!level.isNight()) {
      this.clearBlueMoonLuck(server, level);
      return;
    }
    var config = CraftingDeadSurvival.serverConfig;
    // 超级蓝月：幸运等级 +1（更强）
    int amplifier = config.blueMoonLuckAmplifier.get()
        + (ApocalypseManager.isSuperBlueMoon(level) ? 1 : 0);
    // 持续时长覆盖到天亮（刷新期间避免断档）
    int duration = Math.max(2400, ApocalypseManager.getTicksUntilDawn(level));
    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
      if (player.level().dimension() != Level.OVERWORLD) {
        continue;
      }
      var instance = player.getEffect(MobEffects.LUCK);
      if (instance == null || instance.getDuration() < 1200) {
        player.addEffect(new MobEffectInstance(MobEffects.LUCK, duration, amplifier, false, false));
      }
    }
  }

  private void clearBlueMoonLuck(MinecraftServer server, ServerLevel level) {
    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
      if (player.level().dimension() == Level.OVERWORLD) {
        player.removeEffect(MobEffects.LUCK);
      }
    }
  }

  // ================================================================================
  // 血月 / 超级血月：额外生成僵尸
  // ================================================================================

  private void bloodMoonTick(MinecraftServer server, ServerLevel level) {
    var config = CraftingDeadSurvival.serverConfig;
    int interval = config.bloodMoonSpawnIntervalTicks.get();
    if (interval <= 0 || server.getTickCount() % interval != 0) {
      return;
    }
    int count = config.bloodMoonSpawnCount.get();
    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
      if (player.level().dimension() != Level.OVERWORLD) {
        continue;
      }
      int near = level.getEntitiesOfClass(Zombie.class,
          player.getBoundingBox().inflate(48.0D)).size();
      if (near >= config.bloodMoonMaxZombiesNear.get()) {
        continue;
      }
      for (int i = 0; i < count; i++) {
        this.trySpawnBloodMoonZombie(level, player);
      }
    }
  }

  private void trySpawnBloodMoonZombie(ServerLevel level, Player player) {
    var random = level.random;
    double angle = random.nextDouble() * Math.PI * 2.0D;
    double dist = 24.0D + random.nextDouble() * 20.0D;
    int x = player.getBlockX() + (int) (Math.cos(angle) * dist);
    int z = player.getBlockZ() + (int) (Math.sin(angle) * dist);
    BlockPos ground = findGroundPos(level, new BlockPos(x, player.getBlockY(), z));
    if (ground == null) {
      return;
    }
    EntityType<? extends Zombie> type = pickZombieType(random);
    var zombie = type.create(level);
    if (zombie != null) {
      zombie.moveTo(ground.getX() + 0.5D, ground.getY(), ground.getZ() + 0.5D,
          random.nextFloat() * 360.0F, 0.0F);
      Mob mob = (Mob) zombie;
      mob.finalizeSpawn(level, level.getCurrentDifficultyAt(ground),
          MobSpawnType.EVENT, null, null);
      mob.setPersistenceRequired();
      level.addFreshEntity(zombie);
    }
  }

  private static EntityType<? extends Zombie> pickZombieType(RandomSource random) {
    float r = random.nextFloat();
    if (r < 0.60F) {
      return EntityType.ZOMBIE;
    }
    if (r < 0.80F) {
      return SurvivalEntityTypes.FAST_ZOMBIE.get();
    }
    if (r < 0.93F) {
      return SurvivalEntityTypes.WEAK_ZOMBIE.get();
    }
    return SurvivalEntityTypes.TANK_ZOMBIE.get();
  }

  @Nullable
  private static BlockPos findGroundPos(ServerLevel level, BlockPos pos) {
    int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
    BlockPos ground = new BlockPos(pos.getX(), y, pos.getZ());
    if (!level.getBlockState(ground).isAir() || !level.getBlockState(ground.above()).isAir()) {
      return null;
    }
    return ground;
  }

  // ================================================================================
  // 尸潮：每隔 HORDE_INTERVAL_DAYS 天（默认 14）在夜间触发，共 N 波，每波敌人不同
  // ================================================================================

  private void hordeTick(MinecraftServer server, ServerLevel level) {
    var config = CraftingDeadSurvival.serverConfig;
    if (!config.hordeEnabled.get()) {
      return;
    }
    long day = ApocalypseManager.getDay(level);
    if (!this.hordeActive) {
      // 尸潮触发日夜间开始
      if (ApocalypseManager.isHordeNight(level) && this.hordeBoundDay != day) {
        this.startHorde(server, level, day);
      }
      return;
    }
    // 天亮结束尸潮
    if (!level.isNight()) {
      this.endHorde(server, level);
      return;
    }
    // 到达下一波生成时间
    if (server.getTickCount() >= this.hordeNextWaveTick) {
      if (this.hordeCurrentWave >= config.hordeWaveCount.get()) {
        // 所有波次已放完，等待天亮结束
        return;
      }
      this.hordeCurrentWave++;
      int count = config.hordeSpawnPerWave.get();
      boolean spawned = false;
      for (ServerPlayer player : server.getPlayerList().getPlayers()) {
        if (player.level().dimension() != Level.OVERWORLD) {
          continue;
        }
        int near = level.getEntitiesOfClass(Zombie.class,
            player.getBoundingBox().inflate(48.0D)).size();
        if (near >= config.hordeMaxZombiesNear.get()) {
          continue;
        }
        int amount = Math.min(count, config.hordeMaxZombiesNear.get() - near);
        for (int i = 0; i < amount; i++) {
          if (this.trySpawnHordeZombie(level, player, this.hordeCurrentWave)) {
            spawned = true;
          }
        }
      }
      if (spawned || this.hordeCurrentWave == 1) {
        this.broadcastHordeWave(server, this.hordeCurrentWave);
      }
      this.hordeNextWaveTick = server.getTickCount() + config.hordeWaveIntervalTicks.get();
    }
  }

  private void startHorde(MinecraftServer server, ServerLevel level, long day) {
    var config = CraftingDeadSurvival.serverConfig;
    this.hordeActive = true;
    this.hordeCurrentWave = 0;
    this.hordeBoundDay = day;
    this.hordeNextWaveTick = server.getTickCount() + config.hordeInitialWaveDelayTicks.get();
    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
      player.displayClientMessage(Component.literal("§4☠ 尸潮来袭！夜幕降临，共 "
          + config.hordeWaveCount.get() + " 波敌人…"), true);
    }
  }

  private void endHorde(MinecraftServer server, ServerLevel level) {
    if (!this.hordeActive) {
      return;
    }
    this.hordeActive = false;
    this.hordeCurrentWave = 0;
    this.hordeNextWaveTick = 0L;
    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
      player.displayClientMessage(Component.literal("§f尸潮退去，黎明到来。"), true);
    }
  }

  private void broadcastHordeWave(MinecraftServer server, int wave) {
    var config = CraftingDeadSurvival.serverConfig;
    int total = config.hordeWaveCount.get();
    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
      player.displayClientMessage(
          Component.literal("§6☠ 尸潮 §c第 " + wave + " / " + total + " 波§6 来袭！"),
          true);
    }
  }

  private boolean trySpawnHordeZombie(ServerLevel level, Player player, int wave) {
    var random = level.random;
    double angle = random.nextDouble() * Math.PI * 2.0D;
    double dist = 28.0D + random.nextDouble() * 22.0D;
    int x = player.getBlockX() + (int) (Math.cos(angle) * dist);
    int z = player.getBlockZ() + (int) (Math.sin(angle) * dist);
    BlockPos ground = findGroundPos(level, new BlockPos(x, player.getBlockY(), z));
    if (ground == null) {
      return false;
    }
    EntityType<? extends Zombie> type = pickHordeZombieType(level.random, wave);
    var zombie = type.create(level);
    if (zombie != null) {
      zombie.moveTo(ground.getX() + 0.5D, ground.getY(), ground.getZ() + 0.5D,
          random.nextFloat() * 360.0F, 0.0F);
      Mob mob = (Mob) zombie;
      mob.finalizeSpawn(level, level.getCurrentDifficultyAt(ground),
          MobSpawnType.EVENT, null, null);
      mob.setPersistenceRequired();
      level.addFreshEntity(zombie);
      return true;
    }
    return false;
  }

  /**
   * 根据波数返回该波会出现的僵尸类型（每波敌人不同）：
   * <ul>
   *   <li>第 1 波：普通 + 弱 + 快僵尸</li>
   *   <li>第 2 波：警察 + 士兵 + 侦察</li>
   *   <li>第 3 波：坦克 + 医生 + 矿工</li>
   *   <li>第 4 波：狙击 + 飞行员 + 忍者 + SWAT</li>
   *   <li>第 5 波（最终）：巨人 + 重装 + 阿尔法 + 赏金猎人 + 沙漠掠夺者</li>
   * </ul>
   */
  private static EntityType<? extends Zombie> pickHordeZombieType(
      net.minecraft.util.RandomSource random, int wave) {
    return switch (Math.max(1, wave)) {
      case 1 -> pick(random, new EntityType<?>[]{
          SurvivalEntityTypes.FAST_ZOMBIE.get(),
          SurvivalEntityTypes.WEAK_ZOMBIE.get(),
          EntityType.ZOMBIE});
      case 2 -> pick(random, new EntityType<?>[]{
          SurvivalEntityTypes.POLICE_ZOMBIE.get(),
          SurvivalEntityTypes.SOLDIER_ZOMBIE.get(),
          SurvivalEntityTypes.SCOUT_ZOMBIE.get()});
      case 3 -> pick(random, new EntityType<?>[]{
          SurvivalEntityTypes.TANK_ZOMBIE.get(),
          SurvivalEntityTypes.DOCTOR_ZOMBIE.get(),
          SurvivalEntityTypes.MINER_ZOMBIE.get()});
      case 4 -> pick(random, new EntityType<?>[]{
          SurvivalEntityTypes.SNIPER_ZOMBIE.get(),
          SurvivalEntityTypes.PILOT_ZOMBIE.get(),
          SurvivalEntityTypes.NINJA_ZOMBIE.get(),
          SurvivalEntityTypes.SWAT_ZOMBIE.get()});
      default -> pick(random, new EntityType<?>[]{
          SurvivalEntityTypes.GIANT_ZOMBIE.get(),
          SurvivalEntityTypes.JUGGERNAUT_ZOMBIE.get(),
          SurvivalEntityTypes.ALFA_ZOMBIE.get(),
          SurvivalEntityTypes.BOUNTY_HUNTER_ZOMBIE.get(),
          SurvivalEntityTypes.DESERT_RAIDER_ZOMBIE.get()});
    };
  }

  @SuppressWarnings("unchecked")
  private static EntityType<? extends Zombie> pick(net.minecraft.util.RandomSource random,
      EntityType<?>[] types) {
    return (EntityType<? extends Zombie>) types[random.nextInt(types.length)];
  }

  // ================================================================================
  // 血月：禁止苦力怕 / 蜘蛛 / 洞穴蜘蛛 / 女巫生成
  // ================================================================================

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void handleCheckSpawn(MobSpawnEvent.PositionCheck event) {
    if (!CraftingDeadSurvival.serverConfig.moonEventsEnabled.get()) {
      return;
    }
    if (event.getLevel().isClientSide()) {
      return;
    }
    if (!ApocalypseManager.isBloodMoon(event.getEntity().level())) {
      return;
    }
    if (ApocalypseManager.isForbiddenMob(event.getEntity().getType())) {
      event.setResult(Event.Result.DENY);
    }
  }

  // ================================================================================
  // 血月：禁止睡觉
  // ================================================================================

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void handlePlayerSleep(PlayerSleepInBedEvent event) {
    if (!CraftingDeadSurvival.serverConfig.moonEventsEnabled.get()) {
      return;
    }
    Player player = event.getEntity();
    if (ApocalypseManager.isBloodMoon(player.level())) {
      event.setResult(Player.BedSleepingProblem.NOT_POSSIBLE_NOW);
      player.displayClientMessage(Component.literal("§c血月降临，无法入睡！"), true);
    }
  }

  // ================================================================================
  // 黄月：农作物生长加速
  // ================================================================================

  @SubscribeEvent(priority = EventPriority.LOW)
  public void handleCropGrow(BlockEvent.CropGrowEvent.Post event) {
    if (!CraftingDeadSurvival.serverConfig.moonEventsEnabled.get()) {
      return;
    }
    if (!(event.getLevel() instanceof ServerLevel serverLevel)
        || !ApocalypseManager.isYellowMoon(serverLevel)
        // 黄月只在夜间（直到天亮）加速作物生长
        || !serverLevel.isNight()) {
      return;
    }
    double chance = CraftingDeadSurvival.serverConfig.yellowMoonGrowthBoostChance.get();
    // 超级黄月：生长加速概率翻倍
    if (ApocalypseManager.isSuperYellowMoon(serverLevel)) {
      chance = Math.min(1.0D, chance * 2.0D);
    }
    if (chance > 0.0D && serverLevel.random.nextFloat() < chance) {
      BlockState state = event.getState();
      state.getBlock().randomTick(state, serverLevel, event.getPos(), serverLevel.random);
    }
  }

  // ================================================================================
  // 击杀：击杀信息广播 + 概率掉落原版 / 其他模组物品
  // ================================================================================

  @SubscribeEvent(priority = EventPriority.LOW)
  public void handleLivingDeath(LivingDeathEvent event) {
    LivingEntity victim = event.getEntity();
    var level = victim.level();
    if (level.isClientSide()) {
      return;
    }
    if (!(event.getSource().getEntity() instanceof ServerPlayer killerPlayer)) {
      return;
    }

    // 击杀信息（玩家用什么武器击杀了什么）
    if (CraftingDeadSurvival.serverConfig.killFeedEnabled.get()) {
      ItemStack weapon = killerPlayer.getMainHandItem();
      ResourceLocation weaponId = weapon.isEmpty()
          ? null
          : ForgeRegistries.ITEMS.getKey(weapon.getItem());
      // TaCZ 枪械：统一物品 tacz:modern_kinetic_gun，按 GunId 解析真实枪名翻译组件
      Component weaponName = QualityHelper.getTaCZGunDisplayName(weapon);
      String gunId = QualityHelper.getTaCZGunId(weapon);
      SurvivalNetworkChannel.PLAY.getSimpleChannel().send(
          PacketDistributor.ALL.noArg(),
          new SurvivalKillFeedMessage(killerPlayer.getDisplayName(), victim.getDisplayName(),
              weaponId, weapon.getCount(), weaponName, gunId));
    }

    // 击杀掉落
    if (CraftingDeadSurvival.serverConfig.killDropsEnabled.get()
        && level.random.nextFloat() < CraftingDeadSurvival.serverConfig.killDropChance.get()
            .floatValue()) {
      this.spawnKillLoot(victim, (ServerLevel) level);
    }
  }

  private void spawnKillLoot(LivingEntity victim, ServerLevel level) {
    List<Item> pool = getLootPool();
    if (pool.isEmpty()) {
      return;
    }
    var config = CraftingDeadSurvival.serverConfig;
    Item item = pool.get(level.random.nextInt(pool.size()));
    int count = level.random.nextInt(config.killDropMax.get() - config.killDropMin.get() + 1)
        + config.killDropMin.get();
    ItemEntity itemEntity = new ItemEntity(level,
        victim.getX(), victim.getY() + 0.5D, victim.getZ(), new ItemStack(item, count));
    itemEntity.setDeltaMovement(
        (level.random.nextDouble() - 0.5D) * 0.4D,
        level.random.nextDouble() * 0.2D + 0.1D,
        (level.random.nextDouble() - 0.5D) * 0.4D);
    itemEntity.setDefaultPickUpDelay();
    level.addFreshEntity(itemEntity);
  }

  private static List<Item> getLootPool() {
    if (lootPool == null) {
      lootPool = buildLootPool();
    }
    return lootPool;
  }

  private static List<Item> buildLootPool() {
    List<Item> pool = new ArrayList<>();
    // 原版常用物品
    pool.addAll(List.of(
        Items.IRON_INGOT, Items.GOLD_INGOT, Items.DIAMOND, Items.EMERALD,
        Items.IRON_SWORD, Items.IRON_AXE, Items.IRON_PICKAXE, Items.IRON_SHOVEL,
        Items.BOW, Items.ARROW, Items.GOLDEN_APPLE, Items.APPLE, Items.BREAD,
        Items.COOKED_BEEF, Items.COOKED_PORKCHOP, Items.ROTTEN_FLESH,
        Items.BONE, Items.STRING, Items.GUNPOWDER, Items.LEATHER,
        Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS,
        Items.EXPERIENCE_BOTTLE, Items.DIAMOND_SWORD, Items.SHIELD,
        Items.FLINT, Items.FLINT_AND_STEEL, Items.SNOWBALL, Items.TORCH,
        Items.GOLDEN_CARROT, Items.POTION));
    // 其他模组物品（crafting dead 与其他模组注册的物品）
    for (Item item : ForgeRegistries.ITEMS) {
      if (item == Items.AIR) {
        continue;
      }
      ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
      if (key == null || key.getNamespace().equals("minecraft")) {
        continue;
      }
      if (item instanceof SpawnEggItem || item == Items.BARRIER) {
        continue;
      }
      pool.add(item);
    }
    return pool;
  }

  // ================================================================================
  // 玩家进入：初始化计分板与同步
  // ================================================================================

  @SubscribeEvent
  public void handlePlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
    if (event.getEntity() instanceof ServerPlayer player) {
      ServerLevel level = player.serverLevel();
      this.updateScoreboard(player, level);
      this.sendMoonData(player, level);
    }
  }
}
