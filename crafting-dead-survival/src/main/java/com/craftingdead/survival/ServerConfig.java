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

package com.craftingdead.survival;

import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig {

  // ================================================================================
  // Game-Settings Values
  // ================================================================================

  public final ModConfigSpec.BooleanValue allowSupplyDropBreak;
  public final ModConfigSpec.IntValue supplyDropDuration;
    public final ModConfigSpec.BooleanValue showSubtitles;

  // ================================================================================
  // Loot Values
  // ================================================================================

  public final ModConfigSpec.BooleanValue lootEnabled;
  public final ModConfigSpec.BooleanValue civilianLootEnabled;
  public final ModConfigSpec.BooleanValue rareCivilianLootEnabled;
  public final ModConfigSpec.BooleanValue medicalLootEnabled;
  public final ModConfigSpec.BooleanValue policeLootEnabled;
  public final ModConfigSpec.BooleanValue militaryLootEnabled;
  public final ModConfigSpec.IntValue civilianLootRefreshDelayTicks;
  public final ModConfigSpec.IntValue rareCivilianLootRefreshDelayTicks;
  public final ModConfigSpec.IntValue medicalLootRefreshDelayTicks;
  public final ModConfigSpec.IntValue policeLootRefreshDelayTicks;
  public final ModConfigSpec.IntValue militaryLootRefreshDelayTicks;

  // ================================================================================
  // Zombies Values
  // ================================================================================

  public final ModConfigSpec.BooleanValue zombiesEnabled;
  public final ModConfigSpec.BooleanValue advancedZombiesEnabled;
  public final ModConfigSpec.BooleanValue tankZombiesEnabled;
  public final ModConfigSpec.BooleanValue fastZombiesEnabled;
  public final ModConfigSpec.BooleanValue weakZombiesEnabled;
  public final ModConfigSpec.DoubleValue advancedZombieMaxHealth;
  public final ModConfigSpec.DoubleValue tankZombieMaxHealth;
  public final ModConfigSpec.DoubleValue fastZombieMaxHealth;
  public final ModConfigSpec.DoubleValue weakZombieMaxHealth;
  public final ModConfigSpec.DoubleValue policeZombieMaxHealth;
  public final ModConfigSpec.DoubleValue alfaZombieMaxHealth;
  public final ModConfigSpec.DoubleValue bountyHunterZombieMaxHealth;
  public final ModConfigSpec.DoubleValue desertRaiderZombieMaxHealth;
  public final ModConfigSpec.DoubleValue fireFighterZombieMaxHealth;
  public final ModConfigSpec.DoubleValue hazmatZombieMaxHealth;
  public final ModConfigSpec.DoubleValue juggernautZombieMaxHealth;
  public final ModConfigSpec.DoubleValue minerZombieMaxHealth;
  public final ModConfigSpec.DoubleValue ninjaZombieMaxHealth;
  public final ModConfigSpec.DoubleValue pilotZombieMaxHealth;
  public final ModConfigSpec.DoubleValue scoutZombieMaxHealth;
  public final ModConfigSpec.DoubleValue sniperZombieMaxHealth;
  public final ModConfigSpec.DoubleValue soldierZombieMaxHealth;
  public final ModConfigSpec.DoubleValue swatZombieMaxHealth;
  public final ModConfigSpec.DoubleValue doctorZombieMaxHealth;
  public final ModConfigSpec.DoubleValue giantZombieMaxHealth;
  public final ModConfigSpec.DoubleValue advancedZombieAttackDamage;
  public final ModConfigSpec.DoubleValue tankZombieAttackDamage;
  public final ModConfigSpec.DoubleValue fastZombieAttackDamage;
  public final ModConfigSpec.DoubleValue weakZombieAttackDamage;
  public final ModConfigSpec.DoubleValue hazmatZombieAttackDamage;
  public final ModConfigSpec.DoubleValue scoutZombieAttackDamage;
  public final ModConfigSpec.DoubleValue policeZombieGunAccuracy;
  public final ModConfigSpec.DoubleValue alfaZombieGunAccuracy;
  public final ModConfigSpec.DoubleValue bountyHunterZombieGunAccuracy;
  public final ModConfigSpec.DoubleValue desertRaiderZombieGunAccuracy;
  public final ModConfigSpec.DoubleValue giantZombieGunAccuracy;
  public final ModConfigSpec.DoubleValue juggernautZombieGunAccuracy;
  public final ModConfigSpec.DoubleValue pilotZombieGunAccuracy;
  public final ModConfigSpec.DoubleValue sniperZombieGunAccuracy;
  public final ModConfigSpec.DoubleValue soldierZombieGunAccuracy;
  public final ModConfigSpec.DoubleValue swatZombieGunAccuracy;
  public final ModConfigSpec.DoubleValue doctorZombieAttackDamage;
  public final ModConfigSpec.DoubleValue giantZombieAttackDamage;
  public final ModConfigSpec.DoubleValue alfaZombieAttackDistance;
  public final ModConfigSpec.DoubleValue bountyHunterZombieAttackDistance;
  public final ModConfigSpec.DoubleValue desertRaiderZombieAttackDistance;
  public final ModConfigSpec.DoubleValue giantZombieAttackDistance;
  public final ModConfigSpec.DoubleValue juggernautZombieAttackDistance;
  public final ModConfigSpec.DoubleValue pilotZombieAttackDistance;
  public final ModConfigSpec.DoubleValue policeZombieAttackDistance;
  public final ModConfigSpec.DoubleValue sniperZombieAttackDistance;
  public final ModConfigSpec.DoubleValue soldierZombieAttackDistance;
  public final ModConfigSpec.DoubleValue swatZombieAttackDistance;
  public final ModConfigSpec.DoubleValue alfaZombieVestEquipChance;
  public final ModConfigSpec.DoubleValue desertRaiderVestEquipChance;
  public final ModConfigSpec.DoubleValue juggernautZombieVestEquipChance;
  public final ModConfigSpec.DoubleValue pilotZombieVestEquipChance;
  public final ModConfigSpec.DoubleValue sniperZombieVestEquipChance;
  public final ModConfigSpec.DoubleValue soldierZombieVestEquipChance;
  public final ModConfigSpec.DoubleValue swatZombieVestEquipChance;
  public final ModConfigSpec.DoubleValue alfaZombieBackpackEquipChance;
  public final ModConfigSpec.DoubleValue desertRaiderBackpackEquipChance;
  public final ModConfigSpec.DoubleValue juggernautZombieBackpackEquipChance;
  public final ModConfigSpec.DoubleValue scoutZombieBackpackEquipChance;
  public final ModConfigSpec.DoubleValue soldierZombieBackpackEquipChance;
  public final ModConfigSpec.DoubleValue swatZombieBackpackEquipChance;
  public final ModConfigSpec.IntValue advancedZombieSpawnWeight;
  public final ModConfigSpec.IntValue tankZombieSpawnWeight;
  public final ModConfigSpec.IntValue fastZombieSpawnWeight;
  public final ModConfigSpec.IntValue weakZombieSpawnWeight;
  public final ModConfigSpec.IntValue advancedZombieMinSpawn;
  public final ModConfigSpec.IntValue tankZombieMinSpawn;
  public final ModConfigSpec.IntValue fastZombieMinSpawn;
  public final ModConfigSpec.IntValue weakZombieMinSpawn;
  public final ModConfigSpec.IntValue advancedZombieMaxSpawn;
  public final ModConfigSpec.IntValue tankZombieMaxSpawn;
  public final ModConfigSpec.IntValue fastZombieMaxSpawn;
  public final ModConfigSpec.IntValue weakZombieMaxSpawn;
  public final ModConfigSpec.DoubleValue zombieHatSpawnChance;
  public final ModConfigSpec.DoubleValue zombieHandSpawnChance;
  public final ModConfigSpec.DoubleValue zombieClothingSpawnChance;
  public final ModConfigSpec.DoubleValue zombieHatDropChance;
  public final ModConfigSpec.DoubleValue zombieVestDropChance;
  public final ModConfigSpec.DoubleValue zombieBackpackDropChance;
  public final ModConfigSpec.DoubleValue zombieHandDropChance;
  public final ModConfigSpec.DoubleValue zombieClothingDropChance;
  public final ModConfigSpec.DoubleValue alfaZombieClothingDropChance;
  public final ModConfigSpec.DoubleValue alfaZombieHatDropChance;
  public final ModConfigSpec.DoubleValue alfaZombieVestDropChance;
  public final ModConfigSpec.DoubleValue alfaZombieBackpackDropChance;
  public final ModConfigSpec.DoubleValue alfaZombieHandDropChance;
  public final ModConfigSpec.DoubleValue bountyHunterZombieClothingDropChance;
  public final ModConfigSpec.DoubleValue bountyHunterZombieHatDropChance;
  public final ModConfigSpec.DoubleValue bountyHunterZombieVestDropChance;
  public final ModConfigSpec.DoubleValue bountyHunterZombieBackpackDropChance;
  public final ModConfigSpec.DoubleValue bountyHunterZombieHandDropChance;
  public final ModConfigSpec.DoubleValue desertRaiderZombieClothingDropChance;
  public final ModConfigSpec.DoubleValue desertRaiderZombieHatDropChance;
  public final ModConfigSpec.DoubleValue desertRaiderZombieVestDropChance;
  public final ModConfigSpec.DoubleValue desertRaiderZombieBackpackDropChance;
  public final ModConfigSpec.DoubleValue desertRaiderZombieHandDropChance;
  public final ModConfigSpec.DoubleValue doctorZombieClothingDropChance;
  public final ModConfigSpec.DoubleValue doctorZombieHatDropChance;
  public final ModConfigSpec.DoubleValue doctorZombieVestDropChance;
  public final ModConfigSpec.DoubleValue doctorZombieBackpackDropChance;
  public final ModConfigSpec.DoubleValue doctorZombieHandDropChance;
  public final ModConfigSpec.DoubleValue fireFighterZombieClothingDropChance;
  public final ModConfigSpec.DoubleValue fireFighterZombieHatDropChance;
  public final ModConfigSpec.DoubleValue fireFighterZombieVestDropChance;
  public final ModConfigSpec.DoubleValue fireFighterZombieBackpackDropChance;
  public final ModConfigSpec.DoubleValue fireFighterZombieHandDropChance;
  public final ModConfigSpec.DoubleValue giantZombieClothingDropChance;
  public final ModConfigSpec.DoubleValue giantZombieHatDropChance;
  public final ModConfigSpec.DoubleValue giantZombieVestDropChance;
  public final ModConfigSpec.DoubleValue giantZombieBackpackDropChance;
  public final ModConfigSpec.DoubleValue giantZombieHandDropChance;
  public final ModConfigSpec.DoubleValue hazmatZombieClothingDropChance;
  public final ModConfigSpec.DoubleValue hazmatZombieHatDropChance;
  public final ModConfigSpec.DoubleValue hazmatZombieVestDropChance;
  public final ModConfigSpec.DoubleValue hazmatZombieBackpackDropChance;
  public final ModConfigSpec.DoubleValue hazmatZombieHandDropChance;
  public final ModConfigSpec.DoubleValue juggernautZombieClothingDropChance;
  public final ModConfigSpec.DoubleValue juggernautZombieHatDropChance;
  public final ModConfigSpec.DoubleValue juggernautZombieVestDropChance;
  public final ModConfigSpec.DoubleValue juggernautZombieBackpackDropChance;
  public final ModConfigSpec.DoubleValue juggernautZombieHandDropChance;
  public final ModConfigSpec.DoubleValue minerZombieClothingDropChance;
  public final ModConfigSpec.DoubleValue minerZombieHatDropChance;
  public final ModConfigSpec.DoubleValue minerZombieVestDropChance;
  public final ModConfigSpec.DoubleValue minerZombieBackpackDropChance;
  public final ModConfigSpec.DoubleValue minerZombieHandDropChance;
  public final ModConfigSpec.DoubleValue ninjaZombieClothingDropChance;
  public final ModConfigSpec.DoubleValue ninjaZombieHatDropChance;
  public final ModConfigSpec.DoubleValue ninjaZombieVestDropChance;
  public final ModConfigSpec.DoubleValue ninjaZombieBackpackDropChance;
  public final ModConfigSpec.DoubleValue ninjaZombieHandDropChance;
  public final ModConfigSpec.DoubleValue pilotZombieClothingDropChance;
  public final ModConfigSpec.DoubleValue pilotZombieHatDropChance;
  public final ModConfigSpec.DoubleValue pilotZombieVestDropChance;
  public final ModConfigSpec.DoubleValue pilotZombieBackpackDropChance;
  public final ModConfigSpec.DoubleValue pilotZombieHandDropChance;
  public final ModConfigSpec.DoubleValue policeZombieClothingDropChance;
  public final ModConfigSpec.DoubleValue policeZombieHatDropChance;
  public final ModConfigSpec.DoubleValue policeZombieVestDropChance;
  public final ModConfigSpec.DoubleValue policeZombieBackpackDropChance;
  public final ModConfigSpec.DoubleValue policeZombieHandDropChance;
  public final ModConfigSpec.DoubleValue scoutZombieClothingDropChance;
  public final ModConfigSpec.DoubleValue scoutZombieHatDropChance;
  public final ModConfigSpec.DoubleValue scoutZombieVestDropChance;
  public final ModConfigSpec.DoubleValue scoutZombieBackpackDropChance;
  public final ModConfigSpec.DoubleValue scoutZombieHandDropChance;
  public final ModConfigSpec.DoubleValue sniperZombieClothingDropChance;
  public final ModConfigSpec.DoubleValue sniperZombieHatDropChance;
  public final ModConfigSpec.DoubleValue sniperZombieVestDropChance;
  public final ModConfigSpec.DoubleValue sniperZombieBackpackDropChance;
  public final ModConfigSpec.DoubleValue sniperZombieHandDropChance;
  public final ModConfigSpec.DoubleValue soldierZombieClothingDropChance;
  public final ModConfigSpec.DoubleValue soldierZombieHatDropChance;
  public final ModConfigSpec.DoubleValue soldierZombieVestDropChance;
  public final ModConfigSpec.DoubleValue soldierZombieBackpackDropChance;
  public final ModConfigSpec.DoubleValue soldierZombieHandDropChance;
  public final ModConfigSpec.DoubleValue swatZombieClothingDropChance;
  public final ModConfigSpec.DoubleValue swatZombieHatDropChance;
  public final ModConfigSpec.DoubleValue swatZombieVestDropChance;
  public final ModConfigSpec.DoubleValue swatZombieBackpackDropChance;
  public final ModConfigSpec.DoubleValue swatZombieHandDropChance;
  public final ModConfigSpec.DoubleValue zombieAttackKnockback;
  public final ModConfigSpec.DoubleValue fastZombieSpeed;

  // ================================================================================
  // Zombie Spawn Multipliers
  // ================================================================================

  public final ModConfigSpec.DoubleValue globalZombieSpawnMultiplier;
  public final ModConfigSpec.DoubleValue civilianZombieSpawnMultiplier;
  public final ModConfigSpec.DoubleValue militaryZombieSpawnMultiplier;
  public final ModConfigSpec.DoubleValue policeZombieSpawnMultiplier;
  public final ModConfigSpec.DoubleValue medicZombieSpawnMultiplier;

  // ================================================================================
  // Abilities Values
  // ================================================================================

  public final ModConfigSpec.BooleanValue brokenLegsEnabled;
  public final ModConfigSpec.DoubleValue brokenLegChance;
  public final ModConfigSpec.BooleanValue bleedingEnabled;
  public final ModConfigSpec.BooleanValue infectionEnabled;

  // ================================================================================
  // Explosives Values
  // ================================================================================

  public final ModConfigSpec.BooleanValue pipeBombEnabled;
  public final ModConfigSpec.EnumValue<Level.ExplosionInteraction> pipeBombBlockInteraction;
  public final ModConfigSpec.DoubleValue pipeBombRadius;
  public final ModConfigSpec.DoubleValue pipeBombKnockbackMultiplier;
  public final ModConfigSpec.DoubleValue pipeBombDamageMultiplier;
  public final ModConfigSpec.IntValue pipeBombTicksBeforeActivation;

  // ================================================================================
  // Food and Drink Values
  // ================================================================================

  public final ModConfigSpec.DoubleValue foodNutritionMultiplier;
  public final ModConfigSpec.DoubleValue foodSaturationMultiplier;
  public final ModConfigSpec.DoubleValue drinkHydrationMultiplier;

  // ================================================================================
  // Moon Events / Apocalypse Values
  // ================================================================================

  public final ModConfigSpec.BooleanValue moonEventsEnabled;
  public final ModConfigSpec.BooleanValue scoreboardEnabled;
  public final ModConfigSpec.BooleanValue zombieEvolutionEnabled;
  public final ModConfigSpec.IntValue evolutionIntervalDays;
  public final ModConfigSpec.DoubleValue evolutionHealthPerTier;
  public final ModConfigSpec.DoubleValue evolutionDamagePerTier;
  public final ModConfigSpec.DoubleValue evolutionSpeedPerTier;
  public final ModConfigSpec.BooleanValue moonPhaseZombieStrengthEnabled;
  public final ModConfigSpec.DoubleValue moonPhaseZombieStrengthFactor;
  public final ModConfigSpec.DoubleValue evolvedZombieHeldItemChance;
  public final ModConfigSpec.DoubleValue evolvedZombieHeldItemPerTier;
  public final ModConfigSpec.IntValue bloodMoonSpawnIntervalTicks;
  public final ModConfigSpec.IntValue bloodMoonSpawnCount;
  public final ModConfigSpec.IntValue bloodMoonMaxZombiesNear;
  public final ModConfigSpec.DoubleValue bloodMoonExtraEvolutionChance;
  public final ModConfigSpec.DoubleValue superBloodMoonExtraEvolutionChance;
  public final ModConfigSpec.IntValue blueMoonLuckAmplifier;
  public final ModConfigSpec.DoubleValue yellowMoonGrowthBoostChance;
  public final ModConfigSpec.BooleanValue hordeEnabled;
  public final ModConfigSpec.IntValue hordeIntervalDays;
  public final ModConfigSpec.IntValue hordeDayOffset;
  public final ModConfigSpec.IntValue hordeWaveCount;
  public final ModConfigSpec.IntValue hordeWaveIntervalTicks;
  public final ModConfigSpec.IntValue hordeSpawnPerWave;
  public final ModConfigSpec.IntValue hordeMaxZombiesNear;
  public final ModConfigSpec.IntValue hordeInitialWaveDelayTicks;
  public final ModConfigSpec.BooleanValue killFeedEnabled;
  public final ModConfigSpec.BooleanValue killDropsEnabled;
  public final ModConfigSpec.DoubleValue killDropChance;
  public final ModConfigSpec.IntValue killDropMin;
  public final ModConfigSpec.IntValue killDropMax;

  // Zombie Performance / AI Values
  // ================================================================================

  /** 僵尸可破门的概率（0 = 全部不破门，1 = 全部破门）。破门 AI 开销大，大量僵尸时应降低。 */
  public final ModConfigSpec.DoubleValue zombieBreakDoorChance;
  /** 僵尸追踪距离（格）。越大寻路越频繁，服务器开销越大。 */
  public final ModConfigSpec.DoubleValue zombieFollowRange;

  public ServerConfig(ModConfigSpec.Builder builder) {
    // Game-Settings configuration
    builder
        .comment("General Game-Settings")
        .push("game-settings");
    {
      this.allowSupplyDropBreak = builder
          .translation("options.craftingdeadsurvival.server.allow_supply_drop_break")
          .comment("If true supply drops can be destroyed by a left-click")
          .define("allowSupplyDropBreak", true);

      this.supplyDropDuration = builder
          .translation("options.craftingdeadsurvival.server.supply_drop_duration")
          .comment("Duration in seconds before a Supply Drop disappears from the world")
          .defineInRange("supplyDropDuration", 1200, 1, Integer.MAX_VALUE);

      this.showSubtitles = builder
          .translation("options.craftingdeadsurvival.server.show_subtitles")
          .comment("If true, allow the subtitles overlay to render for players")
          .define("showSubtitles", true);
    }
    builder.pop();

    // Loot configuration
    builder
        .comment("Tweak loot spawning and delays")
        .push("loot");
    {
      this.lootEnabled = builder
          .translation("options.craftingdeadsurvival.server.loot.enable")
          .comment("Defines if loot can be respawned (applies to all loots)")
          .define("lootEnabled", true);
      this.civilianLootEnabled = builder
          .translation("options.craftingdeadsurvival.server.loot.civilian_loot")
          .comment("Defines if Civilian Loot can be respawned")
          .define("civilianLootEnabled", true);
      this.rareCivilianLootEnabled = builder
          .translation("options.craftingdeadsurvival.server.loot.civilian_rare_loot")
          .comment("Defines if Civilian Rare Loot can be respawned")
          .define("rareCivilianLootEnabled", true);
      this.medicalLootEnabled = builder
          .translation("options.craftingdeadsurvival.server.loot.medical_loot")
          .comment("Defines if Medical Loot can be respawned")
          .define("medicalLootEnabled", true);
      this.policeLootEnabled = builder
          .translation("options.craftingdeadsurvival.server.loot.police_loot")
          .comment("Defines if Police Loot can be respawned")
          .define("policeLootEnabled", true);
      this.militaryLootEnabled = builder
          .translation("options.craftingdeadsurvival.server.loot.military_loot")
          .comment("Defines if Military Loot can be respawned")
          .define("militaryLootEnabled", true);
      this.civilianLootRefreshDelayTicks = builder
          .translation("options.craftingdeadsurvival.server.loot.civilian_loot_respawn_tick")
          .defineInRange("civilianLootRefreshDelayTicks", 1000, 0, Integer.MAX_VALUE);
      this.rareCivilianLootRefreshDelayTicks = builder
          .translation("options.craftingdeadsurvival.server.loot.civilian_rare_loot_respawn_tick")
          .defineInRange("rareCivilianLootRefreshDelayTicks", 1000, 0, Integer.MAX_VALUE);
      this.medicalLootRefreshDelayTicks = builder
          .translation("options.craftingdeadsurvival.server.loot.medical_loot_respawn_tick")
          .defineInRange("medicalLootRefreshDelayTicks", 1000, 0, Integer.MAX_VALUE);
      this.policeLootRefreshDelayTicks = builder
          .translation("options.craftingdeadsurvival.server.loot.police_loot_respawn_tick")
          .defineInRange("policeLootRefreshDelayTicks", 1000, 0, Integer.MAX_VALUE);
      this.militaryLootRefreshDelayTicks = builder
          .translation("options.craftingdeadsurvival.server.loot.military_loot_respawn_tick")
          .defineInRange("militaryLootRefreshDelayTicks", 1000, 0, Integer.MAX_VALUE);
    }
    builder.pop();

    // Zombies configuration
    builder
        .comment("Change every aspect of all zombies",
            "WARNING: Most changes only affects newly spawned zombies. Previously spawned zombies will retain their old settings.")
        .push("zombies");
    {
      this.advancedZombieMaxHealth = builder
          .translation("options.craftingdeadsurvival.server.zombies.advanced_zombie.health")
          .comment("Defines how much health the zombie has (2 health points = 1 heart)")
          .defineInRange("advancedZombieMaxHealth", 20.0D, 1.0D, 1024.0D);
      this.tankZombieMaxHealth = builder
          .translation("options.craftingdeadsurvival.server.zombies.tank_zombie.health")
          .comment("Defines how much health the zombie has (2 health points = 1 heart)")
          .defineInRange("tankZombieMaxHealth", 100.0D, 1.0D, 1024.0D);
      this.fastZombieMaxHealth = builder
          .translation("options.craftingdeadsurvival.server.zombies.fast_zombie.health")
          .comment("Defines how much health the zombie has (2 health points = 1 heart)")
          .defineInRange("fastZombieMaxHealth", 10.0D, 1.0D, 1024.0D);
      this.weakZombieMaxHealth = builder
          .translation("options.craftingdeadsurvival.server.zombies.weak_zombie.health")
          .comment("Defines how much health the zombie has (2 health points = 1 heart)")
          .defineInRange("weakZombieMaxHealth", 5.0D, 1.0D, 1024.0D);
      this.alfaZombieMaxHealth = builder
          .translation("options.craftingdeadsurvival.server.zombies.alfa_zombie.health")
          .comment("Defines how much health the Alfa Zombie has (2 health points = 1 heart)")
          .defineInRange("alfaZombieMaxHealth", 20.0D, 1.0D, 1024.0D);
      this.bountyHunterZombieMaxHealth = builder
          .translation("options.craftingdeadsurvival.server.zombies.bounty_hunter_zombie.health")
          .comment("Defines how much health the Bounty Hunter Zombie has (2 health points = 1 heart)")
          .defineInRange("bountyHunterZombieMaxHealth", 20.0D, 1.0D, 1024.0D);
      this.desertRaiderZombieMaxHealth = builder
          .translation("options.craftingdeadsurvival.server.zombies.desert_raider_zombie.health")
          .comment("Defines how much health the Desert Raider Zombie has (2 health points = 1 heart)")
          .defineInRange("desertRaiderZombieMaxHealth", 20.0D, 1.0D, 1024.0D);
      this.fireFighterZombieMaxHealth = builder
          .translation("options.craftingdeadsurvival.server.zombies.fire_fighter_zombie.health")
          .comment("Defines how much health the Fire Fighter Zombie has (2 health points = 1 heart)")
          .defineInRange("fireFighterZombieMaxHealth", 20.0D, 1.0D, 1024.0D);
      this.hazmatZombieMaxHealth = builder
          .translation("options.craftingdeadsurvival.server.zombies.hazmat_zombie.health")
          .comment("Defines how much health the Hazmat Zombie has (2 health points = 1 heart)")
          .defineInRange("hazmatZombieMaxHealth", 20.0D, 1.0D, 1024.0D);
      this.juggernautZombieMaxHealth = builder
          .translation("options.craftingdeadsurvival.server.zombies.juggernaut_zombie.health")
          .comment("Defines how much health the Juggernaut Zombie has (2 health points = 1 heart)")
          .defineInRange("juggernautZombieMaxHealth", 20.0D, 1.0D, 1024.0D);
      this.minerZombieMaxHealth = builder
          .translation("options.craftingdeadsurvival.server.zombies.miner_zombie.health")
          .comment("Defines how much health the Miner Zombie has (2 health points = 1 heart)")
          .defineInRange("minerZombieMaxHealth", 20.0D, 1.0D, 1024.0D);
      this.ninjaZombieMaxHealth = builder
          .translation("options.craftingdeadsurvival.server.zombies.ninja_zombie.health")
          .comment("Defines how much health the Ninja Zombie has (2 health points = 1 heart)")
          .defineInRange("ninjaZombieMaxHealth", 20.0D, 1.0D, 1024.0D);
      this.pilotZombieMaxHealth = builder
          .translation("options.craftingdeadsurvival.server.zombies.pilot_zombie.health")
          .comment("Defines how much health the Pilot Zombie has (2 health points = 1 heart)")
          .defineInRange("pilotZombieMaxHealth", 20.0D, 1.0D, 1024.0D);
      this.scoutZombieMaxHealth = builder
          .translation("options.craftingdeadsurvival.server.zombies.scout_zombie.health")
          .comment("Defines how much health the Scout Zombie has (2 health points = 1 heart)")
          .defineInRange("scoutZombieMaxHealth", 20.0D, 1.0D, 1024.0D);
      this.sniperZombieMaxHealth = builder
          .translation("options.craftingdeadsurvival.server.zombies.sniper_zombie.health")
          .comment("Defines how much health the Sniper Zombie has (2 health points = 1 heart)")
          .defineInRange("sniperZombieMaxHealth", 20.0D, 1.0D, 1024.0D);
      this.soldierZombieMaxHealth = builder
          .translation("options.craftingdeadsurvival.server.zombies.soldier_zombie.health")
          .comment("Defines how much health the Soldier Zombie has (2 health points = 1 heart)")
          .defineInRange("soldierZombieMaxHealth", 20.0D, 1.0D, 1024.0D);
      this.swatZombieMaxHealth = builder
          .translation("options.craftingdeadsurvival.server.zombies.swat_zombie.health")
          .comment("Defines how much health the Swat Zombie has (2 health points = 1 heart)")
          .defineInRange("swatZombieMaxHealth", 20.0D, 1.0D, 1024.0D);
      this.policeZombieMaxHealth = builder
          .translation("options.craftingdeadsurvival.server.zombies.police_zombie.health")
          .comment("Defines how much health the zombie has (2 health points = 1 heart)")
          .defineInRange("policeZombieMaxHealth", 20.0D, 1.0D, 1024.0D);
      this.doctorZombieMaxHealth = builder
          .translation("options.craftingdeadsurvival.server.zombies.doctor_zombie.health")
          .comment("Defines how much health the zombie has (2 health points = 1 heart)")
          .defineInRange("doctorZombieMaxHealth", 20.0D, 1.0D, 1024.0D);
      this.giantZombieMaxHealth = builder
          .translation("options.craftingdeadsurvival.server.zombies.giant_zombie.health")
          .comment("Defines how much health the zombie has (2 health points = 1 heart)")
          .defineInRange("giantZombieMaxHealth", 100.0D, 1.0D, 1024.0D);
      this.advancedZombieAttackDamage = builder
          .translation("options.craftingdeadsurvival.server.zombies.advanced_zombie.damage")
          .comment("Defines how much damage the zombie deals (2 damage points points = 1 heart)")
          .defineInRange("advancedZombieAttackDamage", 3.0D, 0.0D, 2048.0D);
      this.tankZombieAttackDamage = builder
          .translation("options.craftingdeadsurvival.server.zombies.tank_zombie.damage")
          .comment("Defines how much damage the zombie deals (2 damage points points = 1 heart)")
          .defineInRange("tankZombieAttackDamage", 15.0D, 0.0D, 2048.0D);
      this.fastZombieAttackDamage = builder
          .translation("options.craftingdeadsurvival.server.zombies.fast_zombie.damage")
          .comment("Defines how much damage the zombie deals (2 damage points points = 1 heart)")
          .defineInRange("fastZombieAttackDamage", 1.0D, 0.0D, 2048.0D);
      this.weakZombieAttackDamage = builder
          .translation("options.craftingdeadsurvival.server.zombies.weak_zombie.damage")
          .comment("Defines how much damage the zombie deals (2 damage points points = 1 heart)")
          .defineInRange("weakZombieAttackDamage", 2.0D, 0.0D, 2048.0D);
      this.hazmatZombieAttackDamage = builder
          .translation("options.craftingdeadsurvival.server.zombies.hazmat_zombie.damage")
          .comment("Defines how much damage the Hazmat Zombie deals (2 damage points = 1 heart)")
          .defineInRange("hazmatZombieAttackDamage", 3.0D, 0.0D, 2048.0D);
      this.scoutZombieAttackDamage = builder
          .translation("options.craftingdeadsurvival.server.zombies.scout_zombie.damage")
          .comment("Defines how much damage the Hazmat Zombie deals (2 damage points = 1 heart)")
          .defineInRange("scoutZombieAttackDamage", 3.0D, 0.0D, 2048.0D);
      this.doctorZombieAttackDamage = builder
          .translation("options.craftingdeadsurvival.server.zombies.doctor_zombie.damage")
          .comment("Defines how much damage the zombie deals (2 damage points points = 1 heart)")
          .defineInRange("doctorZombieAttackDamage", 3.0D, 0.0D, 2048.0D);
      this.giantZombieAttackDamage = builder
          .translation("options.craftingdeadsurvival.server.zombies.giant_zombie.damage")
          .comment("Defines how much damage the zombie deals (2 damage points points = 1 heart)")
          .defineInRange("giantZombieAttackDamage", 50.0D, 0.0D, 2048.0D);
      this.policeZombieGunAccuracy = builder
          .translation("options.craftingdeadsurvival.server.zombies.police_zombie.gun_accuracy")
          .comment("Defines the gun accuracy of the Alfa Zombie. A value of 1.0 means a 100% hit chance.")
          .defineInRange("policeZombieGunAccuracy", 0.2D, 0.01D, 1.0D);
      this.alfaZombieGunAccuracy = builder
          .translation("options.craftingdeadsurvival.server.zombies.alfa_zombie.gun_accuracy")
          .comment("Defines the gun accuracy of the Alfa Zombie. A value of 1.0 means a 100% hit chance.")
          .defineInRange("alfaZombieGunAccuracy", 0.2D, 0.01D, 1.0D);
      this.bountyHunterZombieGunAccuracy = builder
          .translation("options.craftingdeadsurvival.server.zombies.bounty_hunter_zombie.gun_accuracy")
          .comment("Defines the gun accuracy of the Bounty Hunter Zombie. A value of 1.0 means a 100% hit chance.")
          .defineInRange("bountyHunterZombieGunAccuracy", 0.2D, 0.01D, 1.0D);
      this.desertRaiderZombieGunAccuracy = builder
          .translation("options.craftingdeadsurvival.server.zombies.desert_raider_zombie.gun_accuracy")
          .comment("Defines the gun accuracy of the Desert Raider Zombie. A value of 1.0 means a 100% hit chance.")
          .defineInRange("desertRaiderZombieGunAccuracy", 0.2D, 0.01D, 1.0D);
      this.giantZombieGunAccuracy = builder
          .translation("options.craftingdeadsurvival.server.zombies.giant_zombie.gun_accuracy")
          .comment("Defines the gun accuracy of the Giant Zombie. A value of 1.0 means a 100% hit chance.")
          .defineInRange("giantZombieGunAccuracy", 0.2D, 0.01D, 1.0D);
      this.juggernautZombieGunAccuracy = builder
          .translation("options.craftingdeadsurvival.server.zombies.juggernaut_zombie.gun_accuracy")
          .comment("Defines the gun accuracy of the Juggernaut Zombie. A value of 1.0 means a 100% hit chance.")
          .defineInRange("juggernautZombieGunAccuracy", 0.2D, 0.01D, 1.0D);
      this.pilotZombieGunAccuracy = builder
          .translation("options.craftingdeadsurvival.server.zombies.pilot_zombie.gun_accuracy")
          .comment("Defines the gun accuracy of the Pilot Zombie. A value of 1.0 means a 100% hit chance.")
          .defineInRange("pilotZombieGunAccuracy", 0.2D, 0.01D, 1.0D);
      this.sniperZombieGunAccuracy = builder
          .translation("options.craftingdeadsurvival.server.zombies.sniper_zombie.gun_accuracy")
          .comment("Defines the gun accuracy of the Sniper Zombie. A value of 1.0 means a 100% hit chance.")
          .defineInRange("sniperZombieGunAccuracy", 0.2D, 0.01D, 1.0D);
      this.soldierZombieGunAccuracy = builder
          .translation("options.craftingdeadsurvival.server.zombies.soldier_zombie.gun_accuracy")
          .comment("Defines the gun accuracy of the Soldier Zombie. A value of 1.0 means a 100% hit chance.")
          .defineInRange("soldierZombieGunAccuracy", 0.2D, 0.01D, 1.0D);
      this.swatZombieGunAccuracy = builder
          .translation("options.craftingdeadsurvival.server.zombies.swat_zombie.gun_accuracy")
          .comment("Defines the gun accuracy of the Swat Zombie. A value of 1.0 means a 100% hit chance.")
          .defineInRange("swatZombieGunAccuracy", 0.2D, 0.01D, 1.0D);
      this.alfaZombieAttackDistance = builder
          .translation("options.craftingdeadsurvival.zombies.alfa_zombie.attack_distance")
          .comment("The distance at which the zombie stops approaching the player. (1.0 = 1 Block)")
          .defineInRange("alfaZombieAttackDistance", 25.0D, 1.0D, 50.0D);
      this.bountyHunterZombieAttackDistance = builder
          .translation("options.craftingdeadsurvival.zombies.bounty_hunter_zombie.attack_distance")
          .comment("The distance at which the zombie stops approaching the player. (1.0 = 1 Block)")
          .defineInRange("bountyHunterZombieAttackDistance", 25.0D, 1.0D, 50.0D);
      this.desertRaiderZombieAttackDistance = builder
          .translation("options.craftingdeadsurvival.zombies.desert_raider_zombie.attack_distance")
          .comment("The distance at which the zombie stops approaching the player. (1.0 = 1 Block)")
          .defineInRange("desertRaiderZombieAttackDistance", 25.0D, 1.0D, 50.0D);
      this.giantZombieAttackDistance = builder
          .translation("options.craftingdeadsurvival.zombies.giant_zombie.attack_distance")
          .comment("The distance at which the zombie stops approaching the player. (1.0 = 1 Block)")
          .defineInRange("giantZombieAttackDistance", 25.0D, 1.0D, 50.0D);
      this.juggernautZombieAttackDistance = builder
          .translation("options.craftingdeadsurvival.zombies.juggernaut_zombie.attack_distance")
          .comment("The distance at which the zombie stops approaching the player. (1.0 = 1 Block)")
          .defineInRange("juggernautZombieAttackDistance", 25.0D, 1.0D, 50.0D);
      this.pilotZombieAttackDistance = builder
          .translation("options.craftingdeadsurvival.zombies.pilot_zombie.attack_distance")
          .comment("The distance at which the zombie stops approaching the player. (1.0 = 1 Block)")
          .defineInRange("pilotZombieAttackDistance", 25.0D, 1.0D, 50.0D);
      this.policeZombieAttackDistance = builder
          .translation("options.craftingdeadsurvival.zombies.police_zombie.attack_distance")
          .comment("The distance at which the zombie stops approaching the player. (1.0 = 1 Block)")
          .defineInRange("policeZombieAttackDistance", 25.0D, 1.0D, 50.0D);
      this.sniperZombieAttackDistance = builder
          .translation("options.craftingdeadsurvival.zombies.sniper_zombie.attack_distance")
          .comment("The distance at which the zombie stops approaching the player. (1.0 = 1 Block)")
          .defineInRange("sniperZombieAttackDistance", 25.0D, 1.0D, 50.0D);
      this.soldierZombieAttackDistance = builder
          .translation("options.craftingdeadsurvival.zombies.soldier_zombie.attack_distance")
          .comment("The distance at which the zombie stops approaching the player. (1.0 = 1 Block)")
          .defineInRange("soldierZombieAttackDistance", 25.0D, 1.0D, 50.0D);
      this.swatZombieAttackDistance = builder
          .translation("options.craftingdeadsurvival.zombies.swat_zombie.attack_distance")
          .comment("The distance at which the zombie stops approaching the player. (1.0 = 1 Block)")
          .defineInRange("swatZombieAttackDistance", 25.0D, 1.0D, 50.0D);
      this.alfaZombieVestEquipChance = builder
          .translation("options.craftingdeadsurvival.zombies.alfa_zombie.vest_equip_chance")
          .comment("Chance that the Zombie spawns with a vest. (0.1 = 10%)")
          .defineInRange("alfaZombieVestEquipChance", 1.0, 0.01, 1.0);
      this.desertRaiderVestEquipChance = builder
          .translation("options.craftingdeadsurvival.zombies.desert_raider_zombie.vest_equip_chance")
          .comment("Chance that the Zombie spawns with a vest. (0.1 = 10%)")
          .defineInRange("desertRaiderVestEquipChance", 1.0, 0.01, 1.0);
      this.juggernautZombieVestEquipChance = builder
          .translation("options.craftingdeadsurvival.zombies.juggernaut_zombie.vest_equip_chance")
          .comment("Chance that the Zombie spawns with a vest. (0.1 = 10%)")
          .defineInRange("juggernautZombieVestEquipChance", 1.0, 0.01, 1.0);
      this.pilotZombieVestEquipChance = builder
          .translation("options.craftingdeadsurvival.zombies.pilot_zombie.vest_equip_chance")
          .comment("Chance that the Zombie spawns with a vest. (0.1 = 10%)")
          .defineInRange("pilotZombieVestEquipChance", 1.0, 0.01, 1.0);
      this.sniperZombieVestEquipChance = builder
          .translation("options.craftingdeadsurvival.zombies.sniper_zombie.vest_equip_chance")
          .comment("Chance that the Zombie spawns with a vest. (0.1 = 10%)")
          .defineInRange("sniperZombieVestEquipChance", 1.0, 0.01, 1.0);
      this.soldierZombieVestEquipChance = builder
          .translation("options.craftingdeadsurvival.zombies.soldier_zombie.vest_equip_chance")
          .comment("Chance that the Zombie spawns with a vest. (0.1 = 10%)")
          .defineInRange("soldierZombieVestEquipChance", 1.0, 0.01, 1.0);
      this.swatZombieVestEquipChance = builder
          .translation("options.craftingdeadsurvival.zombies.swat_zombie.vest_equip_chance")
          .comment("Chance that the Zombie spawns with a vest. (0.1 = 10%)")
          .defineInRange("swatZombieVestEquipChance", 1.0, 0.01, 1.0);
      this.alfaZombieBackpackEquipChance = builder
          .translation("options.craftingdeadsurvival.zombies.alfa_zombie.backpack_equip_chance")
          .comment("Chance that the zombie spawns with a backpack. (0.1 = 10%)")
          .defineInRange("alfaZombieBackpackEquipChance", 1.0, 0.01, 1.0);
      this.desertRaiderBackpackEquipChance = builder
          .translation("options.craftingdeadsurvival.zombies.desert_raider_zombie.backpack_equip_chance")
          .comment("Chance that the zombie spawns with a backpack. (0.1 = 10%)")
          .defineInRange("desertRaiderBackpackEquipChance", 1.0, 0.01, 1.0);
      this.juggernautZombieBackpackEquipChance = builder
          .translation("options.craftingdeadsurvival.zombies.juggernaut_zombie.backpack_equip_chance")
          .comment("Chance that the zombie spawns with a backpack. (0.1 = 10%)")
          .defineInRange("juggernautZombieBackpackEquipChance", 1.0, 0.01, 1.0);
      this.scoutZombieBackpackEquipChance = builder
          .translation("options.craftingdeadsurvival.zombies.scout_zombie.backpack_equip_chance")
          .comment("Chance that the zombie spawns with a backpack. (0.1 = 10%)")
          .defineInRange("scoutZombieBackpackEquipChance", 1.0, 0.01, 1.0);
      this.soldierZombieBackpackEquipChance = builder
          .translation("options.craftingdeadsurvival.zombies.soldier_zombie.backpack_equip_chance")
          .comment("Chance that the zombie spawns with a backpack. (0.1 = 10%)")
          .defineInRange("soldierZombieBackpackEquipChance", 1.0, 0.01, 1.0);
      this.swatZombieBackpackEquipChance = builder
          .translation("options.craftingdeadsurvival.zombies.swat_zombie.backpack_equip_chance")
          .comment("Chance that the zombie spawns with a backpack. (0.1 = 10%)")
          .defineInRange("swatZombieBackpackEquipChance", 1.0, 0.01, 1.0);
      this.fastZombieSpeed = builder
          .translation("options.craftingdeadsurvival.server.zombies.fast_zombie.speed")
          .comment("Defines how fast the zombie moves")
          .defineInRange("fastZombieSpeed", 0.33D, 0.0D, 2048.0D);

      builder
          .comment("Configure how zombies should spawn",
              "Minecraft's spawning is a weighted conditional system",
              "With a lower weight, rarer the zombie will be",
              "---------------------------------",
              "Minimum/Maximum spawn defines how much mobs will be spawned per group")
          .push("spawning");
      this.zombiesEnabled = builder
          .translation("options.craftingdeadsurvival.server.zombies.spawning.enable")
          .define("zombiesEnabled", true);
      this.advancedZombiesEnabled = builder
          .translation("options.craftingdeadsurvival.server.zombies.spawning.advanced_zombie.enable")
          .define("advancedZombiesEnabled", true);
      this.tankZombiesEnabled = builder
          .translation("options.craftingdeadsurvival.server.zombies.spawning.tank_zombie.enable")
          .define("tankZombiesEnabled", true);
      this.fastZombiesEnabled = builder
          .translation("options.craftingdeadsurvival.server.zombies.spawning.fast_zombie.enable")
          .define("fastZombiesEnabled", true);
      this.weakZombiesEnabled = builder
          .translation("options.craftingdeadsurvival.server.zombies.spawning.weak_zombie.enable")
          .define("weakZombiesEnabled", true);
      this.advancedZombieSpawnWeight = builder
          .translation("options.craftingdeadsurvival.server.zombies.spawning.advanced_zombie.weight")
          .defineInRange("advancedZombieSpawnWeight", 40, 1, Integer.MAX_VALUE);
      this.tankZombieSpawnWeight = builder
          .translation("options.craftingdeadsurvival.server.zombies.spawning.tank_zombie.weight")
          .defineInRange("tankZombieSpawnWeight", 5, 1, Integer.MAX_VALUE);
      this.fastZombieSpawnWeight = builder
          .translation("options.craftingdeadsurvival.server.zombies.spawning.fast_zombie.weight")
          .defineInRange("fastZombieSpawnWeight", 15, 1, Integer.MAX_VALUE);
      this.weakZombieSpawnWeight = builder
          .translation("options.craftingdeadsurvival.server.zombies.spawning.weak_zombie.weight")
          .defineInRange("weakZombieSpawnWeight", 30, 1, Integer.MAX_VALUE);
      this.advancedZombieMinSpawn = builder
          .translation("options.craftingdeadsurvival.server.zombies.spawning.advanced_zombie.min_spawn")
          .defineInRange("advancedZombieMinSpawn", 2, 1, Integer.MAX_VALUE);
      this.tankZombieMinSpawn = builder
          .translation("options.craftingdeadsurvival.server.zombies.spawning.tank_zombie.min_spawn")
          .defineInRange("tankZombieMinSpawn", 2, 1, Integer.MAX_VALUE);
      this.fastZombieMinSpawn = builder
          .translation("options.craftingdeadsurvival.server.zombies.spawning.fast_zombie.min_spawn")
          .defineInRange("fastZombieMinSpawn", 2, 1, Integer.MAX_VALUE);
      this.weakZombieMinSpawn = builder
          .translation("options.craftingdeadsurvival.server.zombies.spawning.weak_zombie.min_spawn")
          .defineInRange("weakZombieMinSpawn", 2, 1, Integer.MAX_VALUE);
      this.advancedZombieMaxSpawn = builder
          .translation("options.craftingdeadsurvival.server.zombies.spawning.advanced_zombie.max_spawn")
          .defineInRange("advancedZombieMaxSpawn", 8, 1, Integer.MAX_VALUE);
      this.tankZombieMaxSpawn = builder
          .translation("options.craftingdeadsurvival.server.zombies.spawning.tank_zombie.max_spawn")
          .defineInRange("tankZombieMaxSpawn", 4, 1, Integer.MAX_VALUE);
      this.fastZombieMaxSpawn = builder
          .translation("options.craftingdeadsurvival.server.zombies.spawning.fast_zombie.max_spawn")
          .defineInRange("fastZombieMaxSpawn", 4, 1, Integer.MAX_VALUE);
      this.weakZombieMaxSpawn = builder
          .translation("options.craftingdeadsurvival.server.zombies.spawning.weak_zombie.max_spawn")
          .defineInRange("weakZombieMaxSpawn", 12, 1, Integer.MAX_VALUE);
      builder.pop();

      builder.push("misc");
      this.zombieHatSpawnChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.zombie_hat_spawn_chance")
          .comment("Spawn chance percentage (1.0 = 100% chance)")
          .defineInRange("zombieHatSpawnChance", 0.05D, 0D, 1.0D);
      this.zombieHandSpawnChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.zombie_hand_spawn_chance")
          .comment("Spawn chance percentage (1.0 = 100% chance)")
          .defineInRange("zombieHandSpawnChance", 0.15D, 0D, 1.0D);
      this.zombieClothingSpawnChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.zombie_clothing_spawn_chance")
          .comment("Spawn chance percentage (1.0 = 100% chance)")
          .defineInRange("zombieClothingSpawnChance", 0.25D, 0D, 1.0D);
      this.zombieHatDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.zombie_hat_drop_chance")
          .comment("Drop chance percentage (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("zombieHatDropChance", 2.0D, 0D, 2.0D);
      this.zombieVestDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.zombie_vest_drop_chance")
          .comment("Drop chance percentage (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("zombieVestDropChance", 2.0D, 0D, 2.0D);
      this.zombieBackpackDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.zombie_backpack_drop_chance")
          .comment("Drop chance percentage (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("zombieBackpackDropChance", 2.0D, 0D, 2.0D);
      this.zombieHandDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.zombie_hand_drop_chance")
          .comment("Drop chance percentage (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("zombieHandDropChance", 0.085D, 0D, 2.0D);
      this.zombieClothingDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.zombie_clothing_drop_chance")
          .comment("Drop chance percentage (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("zombieClothingDropChance", 2.00D, 0D, 2.0D);
      this.alfaZombieClothingDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.alfa_zombie_clothing_drop_chance")
          .comment("Drop chance percentage of the ALFA Zombie Clothing Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("alfaZombieClothingDropChance", 2.0D, 0D, 2.0D);
      this.alfaZombieHatDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.alfa_zombie_hat_drop_chance")
          .comment("Drop chance percentage of the ALFA Zombie Hat Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("alfaZombieHatDropChance", 2.0D, 0D, 2.0D);
      this.alfaZombieVestDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.alfa_zombie_vest_drop_chance")
          .comment("Drop chance percentage of the ALFA Zombie Vest Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("alfaZombieVestDropChance", 2.0D, 0D, 2.0D);
      this.alfaZombieBackpackDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.alfa_zombie_backpack_drop_chance")
          .comment("Drop chance percentage of the ALFA Zombie Backpack Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("alfaZombieBackpackDropChance", 2.0D, 0D, 2.0D);
      this.alfaZombieHandDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.alfa_zombie_hand_drop_chance")
          .comment("Drop chance percentage of the ALFA Zombie Off- and Mainhand Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("alfaZombieHandDropChance", 2.0D, 0D, 2.0D);
      this.bountyHunterZombieClothingDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.bounty_hunter_zombie_clothing_drop_chance")
          .comment("Drop chance percentage of the Bounty Hunter Zombie Clothing Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("bountyHunterZombieClothingDropChance", 2.0D, 0D, 2.0D);
      this.bountyHunterZombieHatDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.bounty_hunter_zombie_hat_drop_chance")
          .comment("Drop chance percentage of the Bounty Hunter Zombie Hat Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("bountyHunterZombieHatDropChance", 2.0D, 0D, 2.0D);
      this.bountyHunterZombieVestDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.bounty_hunter_zombie_vest_drop_chance")
          .comment("Drop chance percentage of the Bounty Hunter Zombie Vest Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("bountyHunterZombieVestDropChance", 2.0D, 0D, 2.0D);
      this.bountyHunterZombieBackpackDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.bounty_hunter_zombie_backpack_drop_chance")
          .comment("Drop chance percentage of the Bounty Hunter Zombie Backpack Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("bountyHunterZombieBackpackDropChance", 2.0D, 0D, 2.0D);
      this.bountyHunterZombieHandDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.bounty_hunter_zombie_hand_drop_chance")
          .comment("Drop chance percentage of the Bounty Hunter Zombie Off- and Mainhand Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("bountyHunterZombieHandDropChance", 2.0D, 0D, 2.0D);
      this.desertRaiderZombieClothingDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.desert_raider_zombie_clothing_drop_chance")
          .comment("Drop chance percentage of the Desert Raider Zombie Clothing Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("desertRaiderZombieClothingDropChance", 2.0D, 0D, 2.0D);
      this.desertRaiderZombieHatDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.desert_raider_zombie_hat_drop_chance")
          .comment("Drop chance percentage of the Desert Raider Zombie Hat Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("desertRaiderZombieHatDropChance", 2.0D, 0D, 2.0D);
      this.desertRaiderZombieVestDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.desert_raider_zombie_vest_drop_chance")
          .comment("Drop chance percentage of the Desert Raider Zombie Vest Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("desertRaiderZombieVestDropChance", 2.0D, 0D, 2.0D);
      this.desertRaiderZombieBackpackDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.desert_raider_zombie_backpack_drop_chance")
          .comment("Drop chance percentage of the Desert Raider Zombie Backpack Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("desertRaiderZombieBackpackDropChance", 2.0D, 0D, 2.0D);
      this.desertRaiderZombieHandDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.desert_raider_zombie_hand_drop_chance")
          .comment("Drop chance percentage of the Desert Raider Zombie Off- and Mainhand Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("desertRaiderZombieHandDropChance", 2.0D, 0D, 2.0D);
      this.doctorZombieClothingDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.doctor_zombie_clothing_drop_chance")
          .comment("Drop chance percentage of the Doctor Zombie Clothing Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("doctorZombieClothingDropChance", 2.0D, 0D, 2.0D);
      this.doctorZombieHatDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.doctor_zombie_hat_drop_chance")
          .comment("Drop chance percentage of the Doctor Zombie Hat Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("doctorZombieHatDropChance", 2.0D, 0D, 2.0D);
      this.doctorZombieVestDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.doctor_zombie_vest_drop_chance")
          .comment("Drop chance percentage of the Doctor Zombie Vest Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("doctorZombieVestDropChance", 2.0D, 0D, 2.0D);
      this.doctorZombieBackpackDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.doctor_zombie_backpack_drop_chance")
          .comment("Drop chance percentage of the Doctor Zombie Backpack Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("doctorZombieBackpackDropChance", 2.0D, 0D, 2.0D);
      this.doctorZombieHandDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.doctor_zombie_hand_drop_chance")
          .comment("Drop chance percentage of the Doctor Zombie Off- and Mainhand Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("doctorZombieHandDropChance", 2.0D, 0D, 2.0D);
      this.fireFighterZombieClothingDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.fire_fighter_zombie_clothing_drop_chance")
          .comment("Drop chance percentage of the Fire Fighter Zombie Clothing Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("fireFighterZombieClothingDropChance", 2.0D, 0D, 2.0D);
      this.fireFighterZombieHatDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.fire_fighter_zombie_hat_drop_chance")
          .comment("Drop chance percentage of the Fire Fighter Zombie Hat Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("fireFighterZombieHatDropChance", 2.0D, 0D, 2.0D);
      this.fireFighterZombieVestDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.fire_fighter_zombie_vest_drop_chance")
          .comment("Drop chance percentage of the Fire Fighter Zombie Vest Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("fireFighterZombieVestDropChance", 2.0D, 0D, 2.0D);
      this.fireFighterZombieBackpackDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.fire_fighter_zombie_backpack_drop_chance")
          .comment("Drop chance percentage of the Fire Fighter Zombie Backpack Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("fireFighterZombieBackpackDropChance", 2.0D, 0D, 2.0D);
      this.fireFighterZombieHandDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.fire_fighter_zombie_hand_drop_chance")
          .comment("Drop chance percentage of the Fire Fighter Zombie Off- and Mainhand Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("fireFighterZombieHandDropChance", 2.0D, 0D, 2.0D);
      this.giantZombieClothingDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.giant_zombie_clothing_drop_chance")
          .comment("Drop chance percentage of the Giant Zombie Clothing Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("giantZombieClothingDropChance", 2.0D, 0D, 2.0D);
      this.giantZombieHatDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.giant_zombie_hat_drop_chance")
          .comment("Drop chance percentage of the Giant Zombie Hat Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("giantZombieHatDropChance", 2.0D, 0D, 2.0D);
      this.giantZombieVestDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.giant_zombie_vest_drop_chance")
          .comment("Drop chance percentage of the Giant Zombie Vest Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("giantZombieVestDropChance", 2.0D, 0D, 2.0D);
      this.giantZombieBackpackDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.giant_zombie_backpack_drop_chance")
          .comment("Drop chance percentage of the Giant Zombie Backpack Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("giantZombieBackpackDropChance", 2.0D, 0D, 2.0D);
      this.giantZombieHandDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.giant_zombie_hand_drop_chance")
          .comment("Drop chance percentage of the Giant Zombie Off- and Mainhand Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("giantZombieHandDropChance", 2.0D, 0D, 2.0D);
      this.hazmatZombieClothingDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.hazmat_zombie_clothing_drop_chance")
          .comment("Drop chance percentage of the Hazmat Zombie Clothing Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("hazmatZombieClothingDropChance", 2.0D, 0D, 2.0D);
      this.hazmatZombieHatDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.hazmat_zombie_hat_drop_chance")
          .comment("Drop chance percentage of the Hazmat Zombie Hat Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("hazmatZombieHatDropChance", 2.0D, 0D, 2.0D);
      this.hazmatZombieVestDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.hazmat_zombie_vest_drop_chance")
          .comment("Drop chance percentage of the Hazmat Zombie Vest Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("hazmatZombieVestDropChance", 2.0D, 0D, 2.0D);
      this.hazmatZombieBackpackDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.hazmat_zombie_backpack_drop_chance")
          .comment("Drop chance percentage of the Hazmat Zombie Backpack Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("hazmatZombieBackpackDropChance", 2.0D, 0D, 2.0D);
      this.hazmatZombieHandDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.hazmat_zombie_hand_drop_chance")
          .comment("Drop chance percentage of the Hazmat Zombie Off- and Mainhand Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("hazmatZombieHandDropChance", 2.0D, 0D, 2.0D);
      this.juggernautZombieClothingDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.juggernaut_zombie_clothing_drop_chance")
          .comment("Drop chance percentage of the Juggernaut Zombie Clothing Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("juggernautZombieClothingDropChance", 2.0D, 0D, 2.0D);
      this.juggernautZombieHatDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.juggernaut_zombie_hat_drop_chance")
          .comment("Drop chance percentage of the Juggernaut Zombie Hat Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("juggernautZombieHatDropChance", 2.0D, 0D, 2.0D);
      this.juggernautZombieVestDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.juggernaut_zombie_vest_drop_chance")
          .comment("Drop chance percentage of the Juggernaut Zombie Vest Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("juggernautZombieVestDropChance", 2.0D, 0D, 2.0D);
      this.juggernautZombieBackpackDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.juggernaut_zombie_backpack_drop_chance")
          .comment("Drop chance percentage of the Juggernaut Zombie Backpack Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("juggernautZombieBackpackDropChance", 2.0D, 0D, 2.0D);
      this.juggernautZombieHandDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.juggernaut_zombie_hand_drop_chance")
          .comment("Drop chance percentage of the Juggernaut Zombie Off- and Mainhand Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("juggernautZombieHandDropChance", 2.0D, 0D, 2.0D);
      this.minerZombieClothingDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.miner_zombie_clothing_drop_chance")
          .comment("Drop chance percentage of the Miner Zombie Clothing Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("minerZombieClothingDropChance", 2.0D, 0D, 2.0D);
      this.minerZombieHatDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.miner_zombie_hat_drop_chance")
          .comment("Drop chance percentage of the Miner Zombie Hat Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("minerZombieHatDropChance", 2.0D, 0D, 2.0D);
      this.minerZombieVestDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.miner_zombie_vest_drop_chance")
          .comment("Drop chance percentage of the Miner Zombie Vest Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("minerZombieVestDropChance", 2.0D, 0D, 2.0D);
      this.minerZombieBackpackDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.miner_zombie_backpack_drop_chance")
          .comment("Drop chance percentage of the Miner Zombie Backpack Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("minerZombieBackpackDropChance", 2.0D, 0D, 2.0D);
      this.minerZombieHandDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.miner_zombie_hand_drop_chance")
          .comment("Drop chance percentage of the Miner Zombie Off- and Mainhand Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("minerZombieHandDropChance", 2.0D, 0D, 2.0D);
      this.ninjaZombieClothingDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.ninja_zombie_clothing_drop_chance")
          .comment("Drop chance percentage of the Ninja Zombie Clothing Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("ninjaZombieClothingDropChance", 2.0D, 0D, 2.0D);
      this.ninjaZombieHatDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.ninja_zombie_hat_drop_chance")
          .comment("Drop chance percentage of the Ninja Zombie Hat Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("ninjaZombieHatDropChance", 2.0D, 0D, 2.0D);
      this.ninjaZombieVestDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.ninja_zombie_vest_drop_chance")
          .comment("Drop chance percentage of the Ninja Zombie Vest Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("ninjaZombieVestDropChance", 2.0D, 0D, 2.0D);
      this.ninjaZombieBackpackDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.ninja_zombie_backpack_drop_chance")
          .comment("Drop chance percentage of the Ninja Zombie Backpack Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("ninjaZombieBackpackDropChance", 2.0D, 0D, 2.0D);
      this.ninjaZombieHandDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.ninja_zombie_hand_drop_chance")
          .comment("Drop chance percentage of the Ninja Zombie Off- and Mainhand Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("ninjaZombieHandDropChance", 2.0D, 0D, 2.0D);
      this.pilotZombieClothingDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.pilot_zombie_clothing_drop_chance")
          .comment("Drop chance percentage of the Pilot Zombie Clothing Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("pilotZombieClothingDropChance", 2.0D, 0D, 2.0D);
      this.pilotZombieHatDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.pilot_zombie_hat_drop_chance")
          .comment("Drop chance percentage of the Pilot Zombie Hat Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("pilotZombieHatDropChance", 2.0D, 0D, 2.0D);
      this.pilotZombieVestDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.pilot_zombie_vest_drop_chance")
          .comment("Drop chance percentage of the Pilot Zombie Vest Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("pilotZombieVestDropChance", 2.0D, 0D, 2.0D);
      this.pilotZombieBackpackDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.pilot_zombie_backpack_drop_chance")
          .comment("Drop chance percentage of the Pilot Zombie Backpack Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("pilotZombieBackpackDropChance", 2.0D, 0D, 2.0D);
      this.pilotZombieHandDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.pilot_zombie_hand_drop_chance")
          .comment("Drop chance percentage of the Pilot Zombie Off- and Mainhand Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("pilotZombieHandDropChance", 2.0D, 0D, 2.0D);
      this.policeZombieClothingDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.police_zombie_clothing_drop_chance")
          .comment("Drop chance percentage of the Police Zombie Clothing Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("policeZombieClothingDropChance", 2.0D, 0D, 2.0D);
      this.policeZombieHatDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.police_zombie_hat_drop_chance")
          .comment("Drop chance percentage of the Police Zombie Hat Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("policeZombieHatDropChance", 2.0D, 0D, 2.0D);
      this.policeZombieVestDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.police_zombie_vest_drop_chance")
          .comment("Drop chance percentage of the Police Zombie Vest Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("policeZombieVestDropChance", 2.0D, 0D, 2.0D);
      this.policeZombieBackpackDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.police_zombie_backpack_drop_chance")
          .comment("Drop chance percentage of the Police Zombie Backpack Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("policeZombieBackpackDropChance", 2.0D, 0D, 2.0D);
      this.policeZombieHandDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.police_zombie_hand_drop_chance")
          .comment("Drop chance percentage of the Police Zombie Off- and Mainhand Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("policeZombieHandDropChance", 2.0D, 0D, 2.0D);
      this.scoutZombieClothingDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.scout_zombie_clothing_drop_chance")
          .comment("Drop chance percentage of the Scout Zombie Clothing Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("scoutZombieClothingDropChance", 2.0D, 0D, 2.0D);
      this.scoutZombieHatDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.scout_zombie_hat_drop_chance")
          .comment("Drop chance percentage of the Scout Zombie Hat Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("scoutZombieHatDropChance", 2.0D, 0D, 2.0D);
      this.scoutZombieVestDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.scout_zombie_vest_drop_chance")
          .comment("Drop chance percentage of the Scout Zombie Vest Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("scoutZombieVestDropChance", 2.0D, 0D, 2.0D);
      this.scoutZombieBackpackDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.scout_zombie_backpack_drop_chance")
          .comment("Drop chance percentage of the Scout Zombie Backpack Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("scoutZombieBackpackDropChance", 2.0D, 0D, 2.0D);
      this.scoutZombieHandDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.scout_zombie_hand_drop_chance")
          .comment("Drop chance percentage of the Scout Zombie Off- and Mainhand Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("scoutZombieHandDropChance", 2.0D, 0D, 2.0D);
      this.sniperZombieClothingDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.sniper_zombie_clothing_drop_chance")
          .comment("Drop chance percentage of the Sniper Zombie Clothing Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("sniperZombieClothingDropChance", 2.0D, 0D, 2.0D);
      this.sniperZombieHatDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.sniper_zombie_hat_drop_chance")
          .comment("Drop chance percentage of the Sniper Zombie Hat Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("sniperZombieHatDropChance", 2.0D, 0D, 2.0D);
      this.sniperZombieVestDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.sniper_zombie_vest_drop_chance")
          .comment("Drop chance percentage of the Sniper Zombie Vest Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("sniperZombieVestDropChance", 2.0D, 0D, 2.0D);
      this.sniperZombieBackpackDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.sniper_zombie_backpack_drop_chance")
          .comment("Drop chance percentage of the Sniper Zombie Backpack Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("sniperZombieBackpackDropChance", 2.0D, 0D, 2.0D);
      this.sniperZombieHandDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.sniper_zombie_hand_drop_chance")
          .comment("Drop chance percentage of the Sniper Zombie Off- and Mainhand Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("sniperZombieHandDropChance", 2.0D, 0D, 2.0D);
      this.soldierZombieClothingDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.soldier_zombie_clothing_drop_chance")
          .comment("Drop chance percentage of the Soldier Zombie Clothing Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("soldierZombieClothingDropChance", 2.0D, 0D, 2.0D);
      this.soldierZombieHatDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.soldier_zombie_hat_drop_chance")
          .comment("Drop chance percentage of the Soldier Zombie Hat Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("soldierZombieHatDropChance", 2.0D, 0D, 2.0D);
      this.soldierZombieVestDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.soldier_zombie_vest_drop_chance")
          .comment("Drop chance percentage of the Soldier Zombie Vest Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("soldierZombieVestDropChance", 2.0D, 0D, 2.0D);
      this.soldierZombieBackpackDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.soldier_zombie_backpack_drop_chance")
          .comment("Drop chance percentage of the Soldier Zombie Backpack Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("soldierZombieBackpackDropChance", 2.0D, 0D, 2.0D);
      this.soldierZombieHandDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.soldier_zombie_hand_drop_chance")
          .comment("Drop chance percentage of the Soldier Zombie Off- and Mainhand Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("soldierZombieHandDropChance", 2.0D, 0D, 2.0D);
      this.swatZombieClothingDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.swat_zombie_clothing_drop_chance")
          .comment("Drop chance percentage of the Swat Zombie Clothing Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("swatZombieClothingDropChance", 2.0D, 0D, 2.0D);
      this.swatZombieHatDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.swat_zombie_hat_drop_chance")
          .comment("Drop chance percentage of the Swat Zombie Hat Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("swatZombieHatDropChance", 2.0D, 0D, 2.0D);
      this.swatZombieVestDropChance = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.swat_zombie_vest_drop_chance")
          .comment("Drop chance percentage of the Swat Zombie Vest Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("swatZombieVestDropChance", 2.0D, 0D, 2.0D);
      this.swatZombieBackpackDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.swat_zombie_backpack_drop_chance")
          .comment("Drop chance percentage of the Swat Zombie Backpack Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("swatZombieBackpackDropChance", 2.0D, 0D, 2.0D);
      this.swatZombieHandDropChance = builder
          .translation("options.craftingdeadsurvival.zombies.misc.swat_zombie_hand_drop_chance")
          .comment("Drop chance percentage of the Swat Zombie Off- and Mainhand Slot (drop chance based on vanilla formula, use 2.0 for guarantee drop)")
          .defineInRange("swatZombieHandDropChance", 2.0D, 0D, 2.0D);
      this.zombieAttackKnockback = builder
          .translation("options.craftingdeadsurvival.server.zombies.misc.attack_knockback")
          .comment("Additional knockback given to all zombies")
          .defineInRange("zombieAttackKnockback", 0D, 0D, 5.0D);
      builder.pop();

      // Zombie Spawn Multipliers
      builder
          .comment("Global multipliers for zombie spawn rates",
              "These multiply the individual spawn weights, allowing easy difficulty tuning",
              "Example: globalZombieSpawnMultiplier=2.0 doubles all zombie spawns")
          .push("spawn_multipliers");
      {
        this.globalZombieSpawnMultiplier = builder
            .translation("options.craftingdeadsurvival.server.zombies.spawn_multipliers.global")
            .comment("Global multiplier applied to ALL zombie spawn rates (1.0 = normal, 0.5 = half spawns, 2.0 = double spawns)")
            .defineInRange("global_spawn_multiplier", 1.0D, 0.0D, 10.0D);

        this.civilianZombieSpawnMultiplier = builder
            .translation("options.craftingdeadsurvival.server.zombies.spawn_multipliers.civilian")
            .comment("Spawn rate multiplier for civilian zombie types (Advanced, Tank, Fast, Weak)")
            .defineInRange("civilian_spawn_multiplier", 1.0D, 0.0D, 10.0D);

        this.militaryZombieSpawnMultiplier = builder
            .translation("options.craftingdeadsurvival.server.zombies.spawn_multipliers.military")
            .comment("Spawn rate multiplier for military zombie types (Soldier, Juggernaut, Sniper, etc.)")
            .defineInRange("military_spawn_multiplier", 1.0D, 0.0D, 10.0D);

        this.policeZombieSpawnMultiplier = builder
            .translation("options.craftingdeadsurvival.server.zombies.spawn_multipliers.police")
            .comment("Spawn rate multiplier for police zombie types (Police, SWAT)")
            .defineInRange("police_spawn_multiplier", 1.0D, 0.0D, 10.0D);

        this.medicZombieSpawnMultiplier = builder
            .translation("options.craftingdeadsurvival.server.zombies.spawn_multipliers.medic")
            .comment("Spawn rate multiplier for medic/doctor zombie types")
            .defineInRange("medic_spawn_multiplier", 1.0D, 0.0D, 10.0D);
      }
      builder.pop();
    }
    builder.pop();

    // Abilities configuration
    builder
        .comment("Allows toggling some gameplay aspects")
        .push("abilities");
    {
      this.brokenLegsEnabled = builder
          .translation("options.craftingdeadsurvival.server.abilities.broken_leg")
          .comment("Defines if players can break their legs")
          .define("brokenLegsEnabled", true);
      this.brokenLegChance = builder
          .translation("options.craftingdeadsurvival.server.abilities.broken_leg.chance")
          .comment("Defines the chance of the player breaking his leg")
          .defineInRange("brokenLegChance", 0.25, 0.01, 0.50);
      this.bleedingEnabled = builder
          .translation("options.craftingdeadsurvival.server.abilities.bleed_effect")
          .comment("Defines if players can bleed")
          .define("bleedingEnabled", true);
      this.infectionEnabled = builder
          .translation("options.craftingdeadsurvival.server.abilities.infection_effect")
          .comment("Defines if players can be infected")
          .define("infectionEnabled", true);
    }
    builder.pop();

    // Explosives configuration
    builder.push("explosives");
    {
      this.pipeBombEnabled = builder
          .translation("options.craftingdeadsurvival.server.explosives.pipe_bomb.enable")
          .comment("Enables the usage of Pipe Bomb",
              "It wont prevent the ability to get Pipe Bombs, only the ability to use it")
          .define("pipeBombEnabled", true);
      this.pipeBombBlockInteraction = builder
          .translation("options.craftingdeadsurvival.server.explosives.pipe_grenade.mode")
          .comment("Defines how the explosion should interact with blocks",
              "NONE: No block interaction, blocks will remain unchanged",
              "BREAK: Blocks are broken, they will be dropped when exploded",
              "DESTROY: Blocks are destroyed, nothing will be dropped and only a crater will be left")
          .defineEnum("pipeBombBlockInteraction", Level.ExplosionInteraction.NONE);
      this.pipeBombRadius = builder
          .translation("options.craftingdeadsurvival.server.explosives.pipe_bomb.radius")
          .comment("The explosion radius (in blocks), it tells how big the explosion should be")
          .defineInRange("pipeBombRadius", 4D, 0.1D, 50D);
      this.pipeBombKnockbackMultiplier = builder
          .translation("options.craftingdeadsurvival.server.explosives.pipe_bomb.knockback")
          .comment("Defines how strong the explosion knockback should be (Multiplier)")
          .defineInRange("pipeBombKnockbackMultiplier", 1D, 0D, 30D);
      this.pipeBombDamageMultiplier = builder
          .translation("options.craftingdeadsurvival.server.explosives.pipe_bomb.damage")
          .comment("Multiplies the base damage given by the explosion (Multiplier)")
          .defineInRange("pipeBombDamageMultiplier", 1D, 0D, 30D);
      this.pipeBombTicksBeforeActivation = builder
          .translation("options.craftingdeadsurvival.server.explosives.pipe_bomb.activation_tick")
          .comment("How long before the bomb activates automatically (Ticks)")
          .defineInRange("pipeBombTicksBeforeActivation", 100, 0, 18000);
    }
    builder.pop();

    // Food and Drink configuration
    builder
        .comment("Configure food nutrition, saturation, and drink hydration values")
        .push("food-and-drink");
    {
      this.foodNutritionMultiplier = builder
          .translation("options.craftingdeadsurvival.server.food_drink.nutrition_multiplier")
          .comment("Multiplier for all food nutrition values (1.0 = default, 2.0 = double nutrition)")
          .defineInRange("foodNutritionMultiplier", 1.0D, 0.0D, 10.0D);

      this.foodSaturationMultiplier = builder
          .translation("options.craftingdeadsurvival.server.food_drink.saturation_multiplier")
          .comment("Multiplier for all food saturation values (1.0 = default, 2.0 = double saturation)")
          .defineInRange("foodSaturationMultiplier", 1.0D, 0.0D, 10.0D);

      this.drinkHydrationMultiplier = builder
          .translation("options.craftingdeadsurvival.server.food_drink.hydration_multiplier")
          .comment("Multiplier for all drink hydration/water values (1.0 = default, 2.0 = double hydration)")
          .defineInRange("drinkHydrationMultiplier", 1.0D, 0.0D, 10.0D);
    }
    builder.pop();

    // Moon Events / Apocalypse configuration
    builder
        .comment("末日生存：月亮事件、僵尸进化、计分板与击杀掉落")
        .push("moon-events");
    {
      this.moonEventsEnabled = builder
          .comment("启用血月 / 超级血月 / 蓝月 / 黄月等月亮事件")
          .define("moonEventsEnabled", true);
      this.scoreboardEnabled = builder
          .comment("在计分板侧边栏显示天数 / 时间 / 月相 / 事件 / 进化等级")
          .define("scoreboardEnabled", true);
      this.zombieEvolutionEnabled = builder
          .comment("启用僵尸进化（每 evolutionIntervalDays 天提升血量 / 攻击 / 速度）")
          .define("zombieEvolutionEnabled", true);
      this.evolutionIntervalDays = builder
          .comment("僵尸进化的间隔天数")
          .defineInRange("evolutionIntervalDays", 14, 1, 10000);
      this.evolutionHealthPerTier = builder
          .comment("每级进化提升的血量倍率（0.5 = 每级 +50% 血量）")
          .defineInRange("evolutionHealthPerTier", 0.5D, 0.0D, 100.0D);
      this.evolutionDamagePerTier = builder
          .comment("每级进化提升的攻击倍率（0.5 = 每级 +50% 攻击）")
          .defineInRange("evolutionDamagePerTier", 0.5D, 0.0D, 100.0D);
      this.evolutionSpeedPerTier = builder
          .comment("每级进化提升的速度倍率（0.05 = 每级 +5% 速度）")
          .defineInRange("evolutionSpeedPerTier", 0.05D, 0.0D, 10.0D);
      this.moonPhaseZombieStrengthEnabled = builder
          .comment("启用月相强度：月相决定僵尸强弱（满月最强、新月最弱，参考 Zombie Apocalypse 系列）")
          .define("moonPhaseZombieStrengthEnabled", true);
      this.moonPhaseZombieStrengthFactor = builder
          .comment("月相强度影响僵尸属性的系数（1.0 = 满月 25% 更强，新月 20% 更弱）")
          .defineInRange("moonPhaseZombieStrengthFactor", 1.0D, 0.0D, 10.0D);
      this.evolvedZombieHeldItemChance = builder
          .comment("进化僵尸（LV.1 起）手持物品的基础概率（0 = 从不，1 = 必持）")
          .defineInRange("evolvedZombieHeldItemChance", 0.25D, 0.0D, 1.0D);
      this.evolvedZombieHeldItemPerTier = builder
          .comment("每级进化额外增加的手持概率（0.05 = 每级 +5%），血月夜晚也会额外增加")
          .defineInRange("evolvedZombieHeldItemPerTier", 0.05D, 0.0D, 1.0D);
      this.bloodMoonSpawnIntervalTicks = builder
          .comment("血月额外生成僵尸的间隔（tick，20 tick = 1 秒）")
          .defineInRange("bloodMoonSpawnIntervalTicks", 100, 1, Integer.MAX_VALUE);
      this.bloodMoonSpawnCount = builder
          .comment("每次血月生成周期每个玩家周围额外生成的僵尸数量")
          .defineInRange("bloodMoonSpawnCount", 3, 0, 100);
      this.bloodMoonMaxZombiesNear = builder
          .comment("血月时玩家周围 48 格内允许存在的最大僵尸数量（防止卡顿）")
          .defineInRange("bloodMoonMaxZombiesNear", 40, 1, 500);
      this.bloodMoonExtraEvolutionChance = builder
          .comment("血月夜晚僵尸额外进化一级的概率（0.5 = 50%）")
          .defineInRange("bloodMoonExtraEvolutionChance", 0.5D, 0.0D, 1.0D);
      this.superBloodMoonExtraEvolutionChance = builder
          .comment("超级血月夜晚僵尸额外进化一级的概率（0.9 = 90%）")
          .defineInRange("superBloodMoonExtraEvolutionChance", 0.9D, 0.0D, 1.0D);
      this.blueMoonLuckAmplifier = builder
          .comment("蓝月时玩家获得的幸运效果等级（0 = 幸运 I）")
          .defineInRange("blueMoonLuckAmplifier", 0, 0, 255);
      this.yellowMoonGrowthBoostChance = builder
          .comment("黄月时农作物额外生长一次的概率（0.3 = 30%），仅在夜间至天亮生效")
          .defineInRange("yellowMoonGrowthBoostChance", 0.3D, 0.0D, 1.0D);
      this.hordeEnabled = builder
          .comment("是否启用尸潮（每隔一定天数在夜间触发，共多波，每波敌人不同）")
          .define("hordeEnabled", true);
      this.hordeIntervalDays = builder
          .comment("尸潮触发间隔（天）")
          .defineInRange("hordeIntervalDays", 14, 1, 1000);
      this.hordeDayOffset = builder
          .comment("尸潮在触发周期内第几天开始（0 起，默认 13 与血月同夜）")
          .defineInRange("hordeDayOffset", 13, 0, 1000);
      this.hordeWaveCount = builder
          .comment("尸潮总波数")
          .defineInRange("hordeWaveCount", 5, 1, 20);
      this.hordeWaveIntervalTicks = builder
          .comment("每波之间的间隔（tick，20 tick = 1 秒）")
          .defineInRange("hordeWaveIntervalTicks", 2400, 40, 24000);
      this.hordeSpawnPerWave = builder
          .comment("尸潮每波在玩家周围生成的僵尸数量")
          .defineInRange("hordeSpawnPerWave", 6, 1, 100);
      this.hordeMaxZombiesNear = builder
          .comment("尸潮时玩家周围允许存在的最大僵尸数量（防止卡顿）")
          .defineInRange("hordeMaxZombiesNear", 60, 1, 500);
      this.hordeInitialWaveDelayTicks = builder
          .comment("尸潮第一波生成的延迟（tick）")
          .defineInRange("hordeInitialWaveDelayTicks", 200, 0, 12000);
      this.killFeedEnabled = builder
          .comment("在左上角显示击杀信息（玩家用什么武器击杀了什么）")
          .define("killFeedEnabled", true);
      this.killDropsEnabled = builder
          .comment("击杀时概率掉落原版 / 其他模组物品")
          .define("killDropsEnabled", true);
      this.zombieBreakDoorChance = builder
          .comment("僵尸可破门的概率（0 = 全部不破门，1 = 全部破门）。")
          .comment("破门 AI（BreakDoorGoal）每 tick 检查路径，大量僵尸时开销大；")
          .comment("降低该值可显著减少服务器卡顿，推荐 0.5")
          .defineInRange("zombieBreakDoorChance", 0.5D, 0.0D, 1.0D);
      this.zombieFollowRange = builder
          .comment("僵尸追踪距离（格）。追踪范围越大寻路越频繁、服务器开销越大，")
          .comment("推荐 32（原版为 16，破门追击场景 32 已足够）")
          .defineInRange("zombieFollowRange", 32.0D, 8.0D, 128.0D);
      this.killDropChance = builder
          .comment("每次击杀掉落物品的概率（0.15 = 15%）")
          .defineInRange("killDropChance", 0.15D, 0.0D, 1.0D);
      this.killDropMin = builder
          .comment("击杀掉落物品的最小数量")
          .defineInRange("killDropMin", 1, 1, 64);
      this.killDropMax = builder
          .comment("击杀掉落物品的最大数量")
          .defineInRange("killDropMax", 3, 1, 64);
    }
    builder.pop();
  }
}
