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

package com.craftingdead.survival.world.entity;

import com.craftingdead.survival.CraftingDeadSurvival;
import com.craftingdead.survival.world.entity.grenade.PipeBomb;
import com.craftingdead.survival.world.entity.monster.ALFAZombieEntity;
import com.craftingdead.survival.world.entity.monster.BountyHunterZombieEntity;
import com.craftingdead.survival.world.entity.monster.DesertRaiderZombieEntity;
import com.craftingdead.survival.world.entity.monster.DoctorZombieEntity;
import com.craftingdead.survival.world.entity.monster.FastZombie;
import com.craftingdead.survival.world.entity.monster.FirefighterZombieEntity;
import com.craftingdead.survival.world.entity.monster.GiantZombie;
import com.craftingdead.survival.world.entity.monster.HazmatZombieEntity;
import com.craftingdead.survival.world.entity.monster.JuggernautZombieEntity;
import com.craftingdead.survival.world.entity.monster.MinerZombieEntity;
import com.craftingdead.survival.world.entity.monster.NinjaZombieEntity;
import com.craftingdead.survival.world.entity.monster.PilotZombieEntity;
import com.craftingdead.survival.world.entity.monster.PoliceZombieEntity;
import com.craftingdead.survival.world.entity.monster.ScoutZombieEntity;
import com.craftingdead.survival.world.entity.monster.SniperZombieEntity;
import com.craftingdead.survival.world.entity.monster.SoldierZombieEntity;
import com.craftingdead.survival.world.entity.monster.SwatZombieEntity;
import com.craftingdead.survival.world.entity.monster.TankZombie;
import com.craftingdead.survival.world.entity.monster.WeakZombie;
import com.craftingdead.survival.world.entity.projectile.HomingBigArrow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class SurvivalEntityTypes {

  public static final DeferredRegister<EntityType<?>> deferredRegister =
      DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CraftingDeadSurvival.ID);
  
  public static final RegistryObject<EntityType<PipeBomb>> PIPE_BOMB =
      deferredRegister.register("pipe_bomb", () -> create("pipe_bomb",
          EntityType.Builder
              .<PipeBomb>of(PipeBomb::new, MobCategory.MISC)
              .setTrackingRange(64)
              .setUpdateInterval(4)
              .sized(0.25F, 0.5F)
              .setShouldReceiveVelocityUpdates(false)));

  public static final RegistryObject<EntityType<SupplyDrop>> SUPPLY_DROP =
      deferredRegister.register("supply_drop",
          () -> create("supply_drop",
              EntityType.Builder.of(SupplyDrop::new, MobCategory.MISC)));

  public static final RegistryObject<EntityType<FastZombie>> FAST_ZOMBIE =
      deferredRegister.register("fast_zombie", () -> create("fast_zombie",
          EntityType.Builder
              .of(FastZombie::new, MobCategory.MONSTER)
              .setTrackingRange(64)
              .setUpdateInterval(3)
              .sized(0.6F, 1.95F)
              .setShouldReceiveVelocityUpdates(false)));

  public static final RegistryObject<EntityType<TankZombie>> TANK_ZOMBIE =
      deferredRegister.register("tank_zombie", () -> create("tank_zombie",
          EntityType.Builder
              .of(TankZombie::new, MobCategory.MONSTER)
              .setTrackingRange(64)
              .setUpdateInterval(3)
              .sized(0.6F, 1.95F)
              .setShouldReceiveVelocityUpdates(false)));

  public static final RegistryObject<EntityType<WeakZombie>> WEAK_ZOMBIE =
      deferredRegister.register("weak_zombie", () -> create("weak_zombie",
          EntityType.Builder
              .of(WeakZombie::new, MobCategory.MONSTER)
              .setTrackingRange(64)
              .setUpdateInterval(3)
              .sized(0.6F, 1.95F)
              .setShouldReceiveVelocityUpdates(false)));

  public static final RegistryObject<EntityType<PoliceZombieEntity>> POLICE_ZOMBIE =
      deferredRegister.register("police_zombie", () -> create("police_zombie",
          EntityType.Builder
              .of(PoliceZombieEntity::new, MobCategory.MONSTER)
              .setTrackingRange(64)
              .setUpdateInterval(3)
              .sized(0.6F, 1.95F)
              .setShouldReceiveVelocityUpdates(false)));

  public static final RegistryObject<EntityType<DoctorZombieEntity>> DOCTOR_ZOMBIE =
      deferredRegister.register("doctor_zombie", () -> create("doctor_zombie",
          EntityType.Builder
              .of(DoctorZombieEntity::new, MobCategory.MONSTER)
              .setTrackingRange(64)
              .setUpdateInterval(3)
              .sized(0.6F, 1.95F)
              .setShouldReceiveVelocityUpdates(false)));

  public static final RegistryObject<EntityType<GiantZombie>> GIANT_ZOMBIE =
      deferredRegister.register("giant_zombie", () -> create("giant_zombie",
          EntityType.Builder
              .of(GiantZombie::new,
                  MobCategory.MONSTER)
              .setTrackingRange(64)
              .setUpdateInterval(3)
              .sized(3.6F, 12.0F)
              .setShouldReceiveVelocityUpdates(false)));

  public static final RegistryObject<EntityType<ScoutZombieEntity>> SCOUT_ZOMBIE =
      deferredRegister.register("scout_zombie", () -> create("scout_zombie",
          EntityType.Builder
              .of(ScoutZombieEntity::new, MobCategory.MONSTER)
              .setTrackingRange(64)
              .setUpdateInterval(3)
              .sized(0.6F, 1.95F)
              .setShouldReceiveVelocityUpdates(false)));

  public static final RegistryObject<EntityType<SniperZombieEntity>> SNIPER_ZOMBIE =
      deferredRegister.register("sniper_zombie", () -> create("sniper_zombie",
          EntityType.Builder
              .of(SniperZombieEntity::new, MobCategory.MONSTER)
              .setTrackingRange(64)
              .setUpdateInterval(3)
              .sized(0.6F, 1.95F)
              .setShouldReceiveVelocityUpdates(false)));

  public static final RegistryObject<EntityType<PilotZombieEntity>> PILOT_ZOMBIE =
      deferredRegister.register("pilot_zombie", () -> create("pilot_zombie",
          EntityType.Builder
              .of(PilotZombieEntity::new, MobCategory.MONSTER)
              .setTrackingRange(64)
              .setUpdateInterval(3)
              .sized(0.6F, 1.95F)
              .setShouldReceiveVelocityUpdates(false)));

  public static final RegistryObject<EntityType<SoldierZombieEntity>> SOLDIER_ZOMBIE =
      deferredRegister.register("soldier_zombie", () -> create("soldier_zombie",
          EntityType.Builder
              .of(SoldierZombieEntity::new, MobCategory.MONSTER)
              .setTrackingRange(64)
              .setUpdateInterval(3)
              .sized(0.6F, 1.95F)
              .setShouldReceiveVelocityUpdates(false)));

  public static final RegistryObject<EntityType<NinjaZombieEntity>> NINJA_ZOMBIE =
      deferredRegister.register("ninja_zombie", () -> create("ninja_zombie",
          EntityType.Builder
              .of(NinjaZombieEntity::new, MobCategory.MONSTER)
              .setTrackingRange(64)
              .setUpdateInterval(3)
              .sized(0.6F, 1.95F)
              .setShouldReceiveVelocityUpdates(false)));

  public static final RegistryObject<EntityType<ALFAZombieEntity>> ALFA_ZOMBIE =
      deferredRegister.register("alfa_zombie", () -> create("alfa_zombie",
          EntityType.Builder
              .of(ALFAZombieEntity::new, MobCategory.MONSTER)
              .setTrackingRange(64)
              .setUpdateInterval(3)
              .sized(0.6F, 1.95F)
              .setShouldReceiveVelocityUpdates(false)));

  public static final RegistryObject<EntityType<BountyHunterZombieEntity>> BOUNTY_HUNTER_ZOMBIE =
      deferredRegister.register("bounty_hunter_zombie", () -> create("bounty_hunter_zombie",
          EntityType.Builder
              .of(BountyHunterZombieEntity::new, MobCategory.MONSTER)
              .setTrackingRange(64)
              .setUpdateInterval(3)
              .sized(0.6F, 1.95F)
              .setShouldReceiveVelocityUpdates(false)));

  public static final RegistryObject<EntityType<DesertRaiderZombieEntity>> DESERT_RAIDER_ZOMBIE =
      deferredRegister.register("desert_raider_zombie", () -> create("desert_raider_zombie",
          EntityType.Builder
              .of(DesertRaiderZombieEntity::new, MobCategory.MONSTER)
              .setTrackingRange(64)
              .setUpdateInterval(3)
              .sized(0.6F, 1.95F)
              .setShouldReceiveVelocityUpdates(false)));

  public static final RegistryObject<EntityType<FirefighterZombieEntity>> FIREFIGHTER_ZOMBIE =
      deferredRegister.register("firefighter_zombie", () -> create("firefighter_zombie",
          EntityType.Builder
              .of(FirefighterZombieEntity::new, MobCategory.MONSTER)
              .setTrackingRange(64)
              .setUpdateInterval(3)
              .sized(0.6F, 1.95F)
              .setShouldReceiveVelocityUpdates(false)));

  public static final RegistryObject<EntityType<HazmatZombieEntity>> HAZMAT_ZOMBIE =
      deferredRegister.register("hazmat_zombie", () -> create("hazmat_zombie",
          EntityType.Builder
              .of(HazmatZombieEntity::new, MobCategory.MONSTER)
              .setTrackingRange(64)
              .setUpdateInterval(3)
              .sized(0.6F, 1.95F)
              .setShouldReceiveVelocityUpdates(false)));

  public static final RegistryObject<EntityType<JuggernautZombieEntity>> JUGGERNAUT_ZOMBIE =
      deferredRegister.register("juggernaut_zombie", () -> create("juggernaut_zombie",
          EntityType.Builder
              .of(JuggernautZombieEntity::new, MobCategory.MONSTER)
              .setTrackingRange(64)
              .setUpdateInterval(3)
              .sized(0.6F, 1.95F)
              .setShouldReceiveVelocityUpdates(false)));

  public static final RegistryObject<EntityType<MinerZombieEntity>> MINER_ZOMBIE =
      deferredRegister.register("miner_zombie", () -> create("miner_zombie",
          EntityType.Builder
              .of(MinerZombieEntity::new, MobCategory.MONSTER)
              .setTrackingRange(64)
              .setUpdateInterval(3)
              .sized(0.6F, 1.95F)
              .setShouldReceiveVelocityUpdates(false)));

  public static final RegistryObject<EntityType<SwatZombieEntity>> SWAT_ZOMBIE =
      deferredRegister.register("swat_zombie", () -> create("swat_zombie",
          EntityType.Builder
              .of(SwatZombieEntity::new, MobCategory.MONSTER)
              .setTrackingRange(64)
              .setUpdateInterval(3)
              .sized(0.6F, 1.95F)
              .setShouldReceiveVelocityUpdates(false)));

  /** 骷髅有概率发射的「跟踪大号箭矢」。 */
  public static final RegistryObject<EntityType<HomingBigArrow>> HOMING_BIG_ARROW =
      deferredRegister.register("homing_big_arrow", () -> create("homing_big_arrow",
          EntityType.Builder
              .<HomingBigArrow>of(HomingBigArrow::new, MobCategory.MISC)
              .setTrackingRange(64)
              .setUpdateInterval(4)
              .sized(0.75F, 0.75F)
              .setShouldReceiveVelocityUpdates(true)));

  private static <T extends Entity> EntityType<T> create(String registryName,
      EntityType.Builder<T> builder) {
    return builder.build(new ResourceLocation(CraftingDeadSurvival.ID, registryName).toString());
  }
}
