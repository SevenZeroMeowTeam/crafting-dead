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
import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {

  // ================================================================================
  // Game-Settings Values
  // ================================================================================

  public final ForgeConfigSpec.BooleanValue allowSupplyDropBreak;
  public final ForgeConfigSpec.IntValue supplyDropDuration;
    public final ForgeConfigSpec.BooleanValue showSubtitles;

  // ================================================================================
  // Loot Values
  // ================================================================================

  public final ForgeConfigSpec.BooleanValue lootEnabled;
  public final ForgeConfigSpec.BooleanValue civilianLootEnabled;
  public final ForgeConfigSpec.BooleanValue rareCivilianLootEnabled;
  public final ForgeConfigSpec.BooleanValue medicalLootEnabled;
  public final ForgeConfigSpec.BooleanValue policeLootEnabled;
  public final ForgeConfigSpec.BooleanValue militaryLootEnabled;
  public final ForgeConfigSpec.IntValue civilianLootRefreshDelayTicks;
  public final ForgeConfigSpec.IntValue rareCivilianLootRefreshDelayTicks;
  public final ForgeConfigSpec.IntValue medicalLootRefreshDelayTicks;
  public final ForgeConfigSpec.IntValue policeLootRefreshDelayTicks;
  public final ForgeConfigSpec.IntValue militaryLootRefreshDelayTicks;

  // ================================================================================
  // Zombies Values
  // ================================================================================

  public final ForgeConfigSpec.BooleanValue zombiesEnabled;
  public final ForgeConfigSpec.BooleanValue advancedZombiesEnabled;
  public final ForgeConfigSpec.BooleanValue tankZombiesEnabled;
  public final ForgeConfigSpec.BooleanValue fastZombiesEnabled;
  public final ForgeConfigSpec.BooleanValue weakZombiesEnabled;
  public final ForgeConfigSpec.DoubleValue advancedZombieMaxHealth;
  public final ForgeConfigSpec.DoubleValue tankZombieMaxHealth;
  public final ForgeConfigSpec.DoubleValue fastZombieMaxHealth;
  public final ForgeConfigSpec.DoubleValue weakZombieMaxHealth;
  public final ForgeConfigSpec.DoubleValue policeZombieMaxHealth;
  public final ForgeConfigSpec.DoubleValue alfaZombieMaxHealth;
  public final ForgeConfigSpec.DoubleValue bountyHunterZombieMaxHealth;
  public final ForgeConfigSpec.DoubleValue desertRaiderZombieMaxHealth;
  public final ForgeConfigSpec.DoubleValue fireFighterZombieMaxHealth;
  public final ForgeConfigSpec.DoubleValue hazmatZombieMaxHealth;
  public final ForgeConfigSpec.DoubleValue juggernautZombieMaxHealth;
  public final ForgeConfigSpec.DoubleValue minerZombieMaxHealth;
  public final ForgeConfigSpec.DoubleValue ninjaZombieMaxHealth;
  public final ForgeConfigSpec.DoubleValue pilotZombieMaxHealth;
  public final ForgeConfigSpec.DoubleValue scoutZombieMaxHealth;
  public final ForgeConfigSpec.DoubleValue sniperZombieMaxHealth;
  public final ForgeConfigSpec.DoubleValue soldierZombieMaxHealth;
  public final ForgeConfigSpec.DoubleValue swatZombieMaxHealth;
  public final ForgeConfigSpec.DoubleValue doctorZombieMaxHealth;
  public final ForgeConfigSpec.DoubleValue giantZombieMaxHealth;
  public final ForgeConfigSpec.DoubleValue advancedZombieAttackDamage;
  public final ForgeConfigSpec.DoubleValue tankZombieAttackDamage;
  public final ForgeConfigSpec.DoubleValue fastZombieAttackDamage;
  public final ForgeConfigSpec.DoubleValue weakZombieAttackDamage;
  public final ForgeConfigSpec.DoubleValue hazmatZombieAttackDamage;
  public final ForgeConfigSpec.DoubleValue scoutZombieAttackDamage;
  public final ForgeConfigSpec.DoubleValue policeZombieGunAccuracy;
  public final ForgeConfigSpec.DoubleValue alfaZombieGunAccuracy;
  public final ForgeConfigSpec.DoubleValue bountyHunterZombieGunAccuracy;
  public final ForgeConfigSpec.DoubleValue desertRaiderZombieGunAccuracy;
  public final ForgeConfigSpec.DoubleValue giantZombieGunAccuracy;
  public final ForgeConfigSpec.DoubleValue juggernautZombieGunAccuracy;
  public final ForgeConfigSpec.DoubleValue pilotZombieGunAccuracy;
  public final ForgeConfigSpec.DoubleValue sniperZombieGunAccuracy;
  public final ForgeConfigSpec.DoubleValue soldierZombieGunAccuracy;
  public final ForgeConfigSpec.DoubleValue swatZombieGunAccuracy;
  public final ForgeConfigSpec.DoubleValue doctorZombieAttackDamage;
  public final ForgeConfigSpec.DoubleValue giantZombieAttackDamage;
  public final ForgeConfigSpec.DoubleValue alfaZombieAttackDistance;
  public final ForgeConfigSpec.DoubleValue bountyHunterZombieAttackDistance;
  public final ForgeConfigSpec.DoubleValue desertRaiderZombieAttackDistance;
  public final ForgeConfigSpec.DoubleValue giantZombieAttackDistance;
  public final ForgeConfigSpec.DoubleValue juggernautZombieAttackDistance;
  public final ForgeConfigSpec.DoubleValue pilotZombieAttackDistance;
  public final ForgeConfigSpec.DoubleValue policeZombieAttackDistance;
  public final ForgeConfigSpec.DoubleValue sniperZombieAttackDistance;
  public final ForgeConfigSpec.DoubleValue soldierZombieAttackDistance;
  public final ForgeConfigSpec.DoubleValue swatZombieAttackDistance;
  public final ForgeConfigSpec.DoubleValue alfaZombieVestEquipChance;
  public final ForgeConfigSpec.DoubleValue desertRaiderVestEquipChance;
  public final ForgeConfigSpec.DoubleValue juggernautZombieVestEquipChance;
  public final ForgeConfigSpec.DoubleValue pilotZombieVestEquipChance;
  public final ForgeConfigSpec.DoubleValue sniperZombieVestEquipChance;
  public final ForgeConfigSpec.DoubleValue soldierZombieVestEquipChance;
  public final ForgeConfigSpec.DoubleValue swatZombieVestEquipChance;
  public final ForgeConfigSpec.DoubleValue alfaZombieBackpackEquipChance;
  public final ForgeConfigSpec.DoubleValue desertRaiderBackpackEquipChance;
  public final ForgeConfigSpec.DoubleValue juggernautZombieBackpackEquipChance;
  public final ForgeConfigSpec.DoubleValue scoutZombieBackpackEquipChance;
  public final ForgeConfigSpec.DoubleValue soldierZombieBackpackEquipChance;
  public final ForgeConfigSpec.DoubleValue swatZombieBackpackEquipChance;
  public final ForgeConfigSpec.IntValue advancedZombieSpawnWeight;
  public final ForgeConfigSpec.IntValue tankZombieSpawnWeight;
  public final ForgeConfigSpec.IntValue fastZombieSpawnWeight;
  public final ForgeConfigSpec.IntValue weakZombieSpawnWeight;
  public final ForgeConfigSpec.IntValue advancedZombieMinSpawn;
  public final ForgeConfigSpec.IntValue tankZombieMinSpawn;
  public final ForgeConfigSpec.IntValue fastZombieMinSpawn;
  public final ForgeConfigSpec.IntValue weakZombieMinSpawn;
  public final ForgeConfigSpec.IntValue advancedZombieMaxSpawn;
  public final ForgeConfigSpec.IntValue tankZombieMaxSpawn;
  public final ForgeConfigSpec.IntValue fastZombieMaxSpawn;
  public final ForgeConfigSpec.IntValue weakZombieMaxSpawn;
  public final ForgeConfigSpec.DoubleValue zombieHatSpawnChance;
  public final ForgeConfigSpec.DoubleValue zombieHandSpawnChance;
  public final ForgeConfigSpec.DoubleValue zombieClothingSpawnChance;
  public final ForgeConfigSpec.DoubleValue zombieHatDropChance;
  public final ForgeConfigSpec.DoubleValue zombieVestDropChance;
  public final ForgeConfigSpec.DoubleValue zombieBackpackDropChance;
  public final ForgeConfigSpec.DoubleValue zombieHandDropChance;
  public final ForgeConfigSpec.DoubleValue zombieClothingDropChance;
  public final ForgeConfigSpec.DoubleValue alfaZombieClothingDropChance;
  public final ForgeConfigSpec.DoubleValue alfaZombieHatDropChance;
  public final ForgeConfigSpec.DoubleValue alfaZombieVestDropChance;
  public final ForgeConfigSpec.DoubleValue alfaZombieBackpackDropChance;
  public final ForgeConfigSpec.DoubleValue alfaZombieHandDropChance;
  public final ForgeConfigSpec.DoubleValue bountyHunterZombieClothingDropChance;
  public final ForgeConfigSpec.DoubleValue bountyHunterZombieHatDropChance;
  public final ForgeConfigSpec.DoubleValue bountyHunterZombieVestDropChance;
  public final ForgeConfigSpec.DoubleValue bountyHunterZombieBackpackDropChance;
  public final ForgeConfigSpec.DoubleValue bountyHunterZombieHandDropChance;
  public final ForgeConfigSpec.DoubleValue desertRaiderZombieClothingDropChance;
  public final ForgeConfigSpec.DoubleValue desertRaiderZombieHatDropChance;
  public final ForgeConfigSpec.DoubleValue desertRaiderZombieVestDropChance;
  public final ForgeConfigSpec.DoubleValue desertRaiderZombieBackpackDropChance;
  public final ForgeConfigSpec.DoubleValue desertRaiderZombieHandDropChance;
  public final ForgeConfigSpec.DoubleValue doctorZombieClothingDropChance;
  public final ForgeConfigSpec.DoubleValue doctorZombieHatDropChance;
  public final ForgeConfigSpec.DoubleValue doctorZombieVestDropChance;
  public final ForgeConfigSpec.DoubleValue doctorZombieBackpackDropChance;
  public final ForgeConfigSpec.DoubleValue doctorZombieHandDropChance;
  public final ForgeConfigSpec.DoubleValue fireFighterZombieClothingDropChance;
  public final ForgeConfigSpec.DoubleValue fireFighterZombieHatDropChance;
  public final ForgeConfigSpec.DoubleValue fireFighterZombieVestDropChance;
  public final ForgeConfigSpec.DoubleValue fireFighterZombieBackpackDropChance;
  public final ForgeConfigSpec.DoubleValue fireFighterZombieHandDropChance;
  public final ForgeConfigSpec.DoubleValue giantZombieClothingDropChance;
  public final ForgeConfigSpec.DoubleValue giantZombieHatDropChance;
  public final ForgeConfigSpec.DoubleValue giantZombieVestDropChance;
  public final ForgeConfigSpec.DoubleValue giantZombieBackpackDropChance;
  public final ForgeConfigSpec.DoubleValue giantZombieHandDropChance;
  public final ForgeConfigSpec.DoubleValue hazmatZombieClothingDropChance;
  public final ForgeConfigSpec.DoubleValue hazmatZombieHatDropChance;
  public final ForgeConfigSpec.DoubleValue hazmatZombieVestDropChance;
  public final ForgeConfigSpec.DoubleValue hazmatZombieBackpackDropChance;
  public final ForgeConfigSpec.DoubleValue hazmatZombieHandDropChance;
  public final ForgeConfigSpec.DoubleValue juggernautZombieClothingDropChance;
  public final ForgeConfigSpec.DoubleValue juggernautZombieHatDropChance;
  public final ForgeConfigSpec.DoubleValue juggernautZombieVestDropChance;
  public final ForgeConfigSpec.DoubleValue juggernautZombieBackpackDropChance;
  public final ForgeConfigSpec.DoubleValue juggernautZombieHandDropChance;
  public final ForgeConfigSpec.DoubleValue minerZombieClothingDropChance;
  public final ForgeConfigSpec.DoubleValue minerZombieHatDropChance;
  public final ForgeConfigSpec.DoubleValue minerZombieVestDropChance;
  public final ForgeConfigSpec.DoubleValue minerZombieBackpackDropChance;
  public final ForgeConfigSpec.DoubleValue minerZombieHandDropChance;
  public final ForgeConfigSpec.DoubleValue ninjaZombieClothingDropChance;
  public final ForgeConfigSpec.DoubleValue ninjaZombieHatDropChance;
  public final ForgeConfigSpec.DoubleValue ninjaZombieVestDropChance;
  public final ForgeConfigSpec.DoubleValue ninjaZombieBackpackDropChance;
  public final ForgeConfigSpec.DoubleValue ninjaZombieHandDropChance;
  public final ForgeConfigSpec.DoubleValue pilotZombieClothingDropChance;
  public final ForgeConfigSpec.DoubleValue pilotZombieHatDropChance;
  public final ForgeConfigSpec.DoubleValue pilotZombieVestDropChance;
  public final ForgeConfigSpec.DoubleValue pilotZombieBackpackDropChance;
  public final ForgeConfigSpec.DoubleValue pilotZombieHandDropChance;
  public final ForgeConfigSpec.DoubleValue policeZombieClothingDropChance;
  public final ForgeConfigSpec.DoubleValue policeZombieHatDropChance;
  public final ForgeConfigSpec.DoubleValue policeZombieVestDropChance;
  public final ForgeConfigSpec.DoubleValue policeZombieBackpackDropChance;
  public final ForgeConfigSpec.DoubleValue policeZombieHandDropChance;
  public final ForgeConfigSpec.DoubleValue scoutZombieClothingDropChance;
  public final ForgeConfigSpec.DoubleValue scoutZombieHatDropChance;
  public final ForgeConfigSpec.DoubleValue scoutZombieVestDropChance;
  public final ForgeConfigSpec.DoubleValue scoutZombieBackpackDropChance;
  public final ForgeConfigSpec.DoubleValue scoutZombieHandDropChance;
  public final ForgeConfigSpec.DoubleValue sniperZombieClothingDropChance;
  public final ForgeConfigSpec.DoubleValue sniperZombieHatDropChance;
  public final ForgeConfigSpec.DoubleValue sniperZombieVestDropChance;
  public final ForgeConfigSpec.DoubleValue sniperZombieBackpackDropChance;
  public final ForgeConfigSpec.DoubleValue sniperZombieHandDropChance;
  public final ForgeConfigSpec.DoubleValue soldierZombieClothingDropChance;
  public final ForgeConfigSpec.DoubleValue soldierZombieHatDropChance;
  public final ForgeConfigSpec.DoubleValue soldierZombieVestDropChance;
  public final ForgeConfigSpec.DoubleValue soldierZombieBackpackDropChance;
  public final ForgeConfigSpec.DoubleValue soldierZombieHandDropChance;
  public final ForgeConfigSpec.DoubleValue swatZombieClothingDropChance;
  public final ForgeConfigSpec.DoubleValue swatZombieHatDropChance;
  public final ForgeConfigSpec.DoubleValue swatZombieVestDropChance;
  public final ForgeConfigSpec.DoubleValue swatZombieBackpackDropChance;
  public final ForgeConfigSpec.DoubleValue swatZombieHandDropChance;
  public final ForgeConfigSpec.DoubleValue zombieAttackKnockback;
  public final ForgeConfigSpec.DoubleValue fastZombieSpeed;

  // ================================================================================
  // Zombie Spawn Multipliers
  // ================================================================================

  public final ForgeConfigSpec.DoubleValue globalZombieSpawnMultiplier;
  public final ForgeConfigSpec.DoubleValue civilianZombieSpawnMultiplier;
  public final ForgeConfigSpec.DoubleValue militaryZombieSpawnMultiplier;
  public final ForgeConfigSpec.DoubleValue policeZombieSpawnMultiplier;
  public final ForgeConfigSpec.DoubleValue medicZombieSpawnMultiplier;

  // ================================================================================
  // Abilities Values
  // ================================================================================

  public final ForgeConfigSpec.BooleanValue brokenLegsEnabled;
  public final ForgeConfigSpec.DoubleValue brokenLegChance;
  public final ForgeConfigSpec.BooleanValue bleedingEnabled;
  public final ForgeConfigSpec.BooleanValue infectionEnabled;

  // ================================================================================
  // Explosives Values
  // ================================================================================

  public final ForgeConfigSpec.BooleanValue pipeBombEnabled;
  public final ForgeConfigSpec.EnumValue<Level.ExplosionInteraction> pipeBombBlockInteraction;
  public final ForgeConfigSpec.DoubleValue pipeBombRadius;
  public final ForgeConfigSpec.DoubleValue pipeBombKnockbackMultiplier;
  public final ForgeConfigSpec.DoubleValue pipeBombDamageMultiplier;
  public final ForgeConfigSpec.IntValue pipeBombTicksBeforeActivation;

  // ================================================================================
  // Food and Drink Values
  // ================================================================================

  public final ForgeConfigSpec.DoubleValue foodNutritionMultiplier;
  public final ForgeConfigSpec.DoubleValue foodSaturationMultiplier;
  public final ForgeConfigSpec.DoubleValue drinkHydrationMultiplier;

  // ================================================================================
  // Moon Events / Apocalypse Values
  // ================================================================================

  public final ForgeConfigSpec.BooleanValue moonEventsEnabled;
  public final ForgeConfigSpec.BooleanValue scoreboardEnabled;
  public final ForgeConfigSpec.BooleanValue zombieEvolutionEnabled;
  public final ForgeConfigSpec.IntValue evolutionIntervalDays;
  public final ForgeConfigSpec.DoubleValue evolutionHealthPerTier;
  public final ForgeConfigSpec.DoubleValue evolutionDamagePerTier;
  public final ForgeConfigSpec.DoubleValue evolutionSpeedPerTier;
  public final ForgeConfigSpec.IntValue bloodMoonSpawnIntervalTicks;
  public final ForgeConfigSpec.IntValue bloodMoonSpawnCount;
  public final ForgeConfigSpec.IntValue bloodMoonMaxZombiesNear;
  public final ForgeConfigSpec.DoubleValue bloodMoonExtraEvolutionChance;
  public final ForgeConfigSpec.DoubleValue superBloodMoonExtraEvolutionChance;
  public final ForgeConfigSpec.IntValue blueMoonLuckAmplifier;
  public final ForgeConfigSpec.DoubleValue yellowMoonGrowthBoostChance;
  public final ForgeConfigSpec.BooleanValue killFeedEnabled;
  public final ForgeConfigSpec.BooleanValue killDropsEnabled;
  public final ForgeConfigSpec.DoubleValue killDropChance;
  public final ForgeConfigSpec.IntValue killDropMin;
  public final ForgeConfigSpec.IntValue killDropMax;

  public ServerConfig(ForgeConfigSpec.Builder builder) {
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
          .comment("黄月时农作物额外生长一次的概率（0.3 = 30%）")
          .defineInRange("yellowMoonGrowthBoostChance", 0.3D, 0.0D, 1.0D);
      this.killFeedEnabled = builder
          .comment("在左上角显示击杀信息（玩家用什么武器击杀了什么）")
          .define("killFeedEnabled", true);
      this.killDropsEnabled = builder
          .comment("击杀时概率掉落原版 / 其他模组物品")
          .define("killDropsEnabled", true);
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
