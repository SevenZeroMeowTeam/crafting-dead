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

import com.craftingdead.core.quality.QualityHelper;
import com.craftingdead.survival.CraftingDeadSurvival;
import com.craftingdead.survival.network.SurvivalNetworkChannel;
import com.craftingdead.survival.network.message.SurvivalKillFeedMessage;
import com.craftingdead.survival.network.message.SyncMoonDataMessage;
import com.craftingdead.survival.world.entity.SurvivalEntityTypes;
import com.craftingdead.survival.world.moon.ApocalypseManager;
import com.craftingdead.survival.world.moon.MoonEventType;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.level.BlockEvent;
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
 */
public class MoonEventHandler {

  private static final String SCOREBOARD_OBJECTIVE = "apocalypse_info";

  private static List<Item> lootPool;

  private final List<String> lastScoreboardRows = new ArrayList<>();

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
        this.applyBlueMoonLuck(server, level);
      }
      if (ApocalypseManager.isBloodMoon(level)) {
        this.bloodMoonTick(server, level);
      }
    }
  }

  private void sendMoonData(ServerPlayer player, ServerLevel level) {
    SurvivalNetworkChannel.PLAY.getSimpleChannel().send(
        new SyncMoonDataMessage(
            ApocalypseManager.getDay(level),
            (int) (level.getDayTime() % 24000L),
            ApocalypseManager.getMoonPhase(level),
            ApocalypseManager.getEvolutionTier(level),
            ApocalypseManager.getMoonEvent(level),
            ApocalypseManager.isMoonEventActive(level)),
        PacketDistributor.PLAYER.with(player));
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
          Component.literal("§6┃ 末日生存 ┃"), ObjectiveCriteria.RenderType.INTEGER, true, null);
    }
    scoreboard.setDisplayObjective(DisplaySlot.SIDEBAR, objective);

    // 清理上一轮的计分行
    for (String row : this.lastScoreboardRows) {
      scoreboard.resetSinglePlayerScore(ScoreHolder.forNameOnly(row), objective);
    }
    this.lastScoreboardRows.clear();

    int day = ApocalypseManager.getDay(level);
    int timeOfDay = (int) (level.getDayTime() % 24000L);
    MoonEventType event = ApocalypseManager.getMoonEvent(level);
    boolean active = ApocalypseManager.isMoonEventActive(level);
    int tier = ApocalypseManager.getEvolutionTier(level);

    addRow(scoreboard, objective, "§f 天数: §b" + day, 6);
    addRow(scoreboard, objective, "§f 时间: §b" + formatTime(timeOfDay), 5);
    addRow(scoreboard, objective,
        "§f 月相: §b" + ApocalypseManager.getMoonPhaseName(ApocalypseManager.getMoonPhase(level)), 4);
    addRow(scoreboard, objective,
        "§f 今日: " + eventColor(event) + event.getDisplayName(), 3);
    addRow(scoreboard, objective,
        "§f 状态: " + (active ? "§c● 进行中" : "§7○ 未发生"), 2);
    addRow(scoreboard, objective, "§f 进化: §eLV." + tier, 1);
  }

  private void addRow(Scoreboard scoreboard, Objective objective, String text, int score) {
    scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(text), objective).set(score);
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

  private void applyBlueMoonLuck(MinecraftServer server, ServerLevel level) {
    var config = CraftingDeadSurvival.serverConfig;
    // 超级蓝月：幸运等级 +1（更强）
    int amplifier = config.blueMoonLuckAmplifier.get()
        + (ApocalypseManager.isSuperBlueMoon(level) ? 1 : 0);
    // 超级蓝月：持续时长加倍
    int duration = ApocalypseManager.isSuperBlueMoon(level) ? 2400 : 1200;
    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
      if (player.level().dimension() != Level.OVERWORLD) {
        continue;
      }
      var instance = player.getEffect(MobEffects.LUCK);
      if (instance == null || instance.getDuration() < 600) {
        player.addEffect(new MobEffectInstance(MobEffects.LUCK, duration, amplifier, false, false));
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
    EntityType<? extends Zombie> type = pickZombieType(level.random);
    var zombie = type.spawn(level, ground, MobSpawnType.EVENT);
    if (zombie != null) {
      // 进化由 MobSpawnEvent.FinalizeSpawn 钩子统一处理
      zombie.setPersistenceRequired();
    }
  }

  private static EntityType<? extends Zombie> pickZombieType(net.minecraft.util.RandomSource random) {
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
  // 血月：禁止苦力怕 / 蜘蛛 / 洞穴蜘蛛 / 女巫生成
  // ================================================================================

  @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST)
  public void handleMobSpawnFinalize(MobSpawnEvent.FinalizeSpawn event) {
    var level = event.getLevel();
    if (level.isClientSide() || !CraftingDeadSurvival.serverConfig.moonEventsEnabled.get()) {
      return;
    }
    if (!ApocalypseManager.isBloodMoon(level.getLevel())) {
      return;
    }
    if (ApocalypseManager.isForbiddenMob(event.getEntity().getType())) {
      event.setSpawnCancelled(true);
    }
  }

  // ================================================================================
  // 血月：禁止睡觉
  // ================================================================================

  @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST)
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

  @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW)
  public void handleCropGrow(BlockEvent.CropGrowEvent.Post event) {
    if (!CraftingDeadSurvival.serverConfig.moonEventsEnabled.get()) {
      return;
    }
    if (!(event.getLevel() instanceof ServerLevel serverLevel)
        || !ApocalypseManager.isYellowMoon(serverLevel)) {
      return;
    }
    double chance = CraftingDeadSurvival.serverConfig.yellowMoonGrowthBoostChance.get();
    // 超级黄月：生长加速概率翻倍
    if (ApocalypseManager.isSuperYellowMoon(serverLevel)) {
      chance = Math.min(1.0D, chance * 2.0D);
    }
    if (chance > 0.0D && serverLevel.random.nextFloat() < chance) {
      event.getState().randomTick(serverLevel, event.getPos(), serverLevel.random);
    }
  }

  // ================================================================================
  // 击杀：击杀信息广播 + 概率掉落原版 / 其他模组物品
  // ================================================================================

  @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW)
  public void handleLivingDeath(LivingDeathEvent event) {
    var victim = event.getEntity();
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
          : BuiltInRegistries.ITEM.getKey(weapon.getItem());
      // TaCZ 枪械：统一物品 tacz:modern_kinetic_gun，按 GunId 解析真实枪名翻译组件
      Component weaponName = QualityHelper.getTaCZGunDisplayName(weapon);
      String gunId = QualityHelper.getTaCZGunId(weapon);
      SurvivalNetworkChannel.PLAY.getSimpleChannel().send(
          new SurvivalKillFeedMessage(killerPlayer.getDisplayName(), victim.getDisplayName(),
              weaponId, weapon.getCount(), weaponName, gunId),
          PacketDistributor.ALL.noArg());
    }

    // 击杀掉落
    if (CraftingDeadSurvival.serverConfig.killDropsEnabled.get()
        && level.random.nextFloat() < CraftingDeadSurvival.serverConfig.killDropChance.get()
            .floatValue()) {
      this.spawnKillLoot(victim, (ServerLevel) level);
    }
  }

  private void spawnKillLoot(net.minecraft.world.entity.LivingEntity victim, ServerLevel level) {
    List<Item> pool = getLootPool();
    if (pool.isEmpty()) {
      return;
    }
    var config = CraftingDeadSurvival.serverConfig;
    Item item = pool.get(level.random.nextInt(pool.size()));
    int count = Mth.nextInt(level.random, config.killDropMin.get(), config.killDropMax.get());
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
