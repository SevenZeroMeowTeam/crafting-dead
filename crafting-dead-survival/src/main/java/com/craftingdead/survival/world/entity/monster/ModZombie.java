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

package com.craftingdead.survival.world.entity.monster;

import com.craftingdead.survival.CraftingDeadSurvival;
import com.craftingdead.survival.world.entity.animation.ZombieAnimations;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ModZombie extends Zombie implements GeoEntity {

  /**
   * 断肢状态同步（服务端 → 客户端），供 Physics Mod 死亡布娃娃联动使用：
   * 对应部位已在战斗中断裂时，死亡布娃娃上该部位不会连接（直接掉落）。
   */
  private static final EntityDataAccessor<Boolean> DATA_HEAD_BROKEN =
      SynchedEntityData.defineId(ModZombie.class, EntityDataSerializers.BOOLEAN);
  private static final EntityDataAccessor<Boolean> DATA_ARM_BROKEN =
      SynchedEntityData.defineId(ModZombie.class, EntityDataSerializers.BOOLEAN);
  private static final EntityDataAccessor<Boolean> DATA_WAIST_BROKEN =
      SynchedEntityData.defineId(ModZombie.class, EntityDataSerializers.BOOLEAN);
  private static final EntityDataAccessor<Boolean> DATA_LEG_BROKEN =
      SynchedEntityData.defineId(ModZombie.class, EntityDataSerializers.BOOLEAN);

  private final AnimatableInstanceCache animatableInstanceCache =
      GeckoLibUtil.createInstanceCache(this);

  public ModZombie(EntityType<? extends Zombie> zombie, Level level) {
    super(zombie, level);
  }

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.getEntityData().define(DATA_HEAD_BROKEN, false);
    this.getEntityData().define(DATA_ARM_BROKEN, false);
    this.getEntityData().define(DATA_WAIST_BROKEN, false);
    this.getEntityData().define(DATA_LEG_BROKEN, false);
  }

  public boolean isHeadBroken() {
    return this.getEntityData().get(DATA_HEAD_BROKEN);
  }

  public void setHeadBroken(boolean broken) {
    this.getEntityData().set(DATA_HEAD_BROKEN, broken);
  }

  public boolean isArmBroken() {
    return this.getEntityData().get(DATA_ARM_BROKEN);
  }

  public void setArmBroken(boolean broken) {
    this.getEntityData().set(DATA_ARM_BROKEN, broken);
  }

  public boolean isWaistBroken() {
    return this.getEntityData().get(DATA_WAIST_BROKEN);
  }

  public void setWaistBroken(boolean broken) {
    this.getEntityData().set(DATA_WAIST_BROKEN, broken);
  }

  public boolean isLegBroken() {
    return this.getEntityData().get(DATA_LEG_BROKEN);
  }

  public void setLegBroken(boolean broken) {
    this.getEntityData().set(DATA_LEG_BROKEN, broken);
  }

  @Override
  public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    controllers.add(ZombieAnimations.createController(this));
  }

  @Override
  public AnimatableInstanceCache getAnimatableInstanceCache() {
    return this.animatableInstanceCache;
  }

  @Override
  public boolean doHurtTarget(Entity target) {
    boolean hurt = super.doHurtTarget(target);
    if (hurt && !this.level().isClientSide()) {
      this.triggerAnim("controller", "attack");
    }
    return hurt;
  }

  @Override
  public boolean convertsInWater() {
    return false;
  }

  @Override
  public boolean isSunSensitive() {
    return false;
  }

  @Override
  public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
      MobSpawnType spawnType, @Nullable SpawnGroupData groupData, @Nullable CompoundTag tag) {
    groupData = super.finalizeSpawn(level, difficulty, spawnType, groupData, tag);
    var config = CraftingDeadSurvival.serverConfig;
    // 僵尸 AI 增强：按配置概率可以破坏门追击玩家。破门 AI（BreakDoorGoal）每 tick
    // 检查路径，大量僵尸全部破门会显著增加服务器开销，因此默认仅约一半僵尸破门。
    this.setCanBreakDoors(this.random.nextFloat() < config.zombieBreakDoorChance.get().floatValue());
    // 按配置扩大追踪范围（默认 32 格），避免全部僵尸超远寻路造成卡顿
    double followRangeValue = config.zombieFollowRange.get();
    AttributeInstance followRange = this.getAttribute(Attributes.FOLLOW_RANGE);
    if (followRange != null && followRange.getBaseValue() < followRangeValue) {
      followRange.setBaseValue(followRangeValue);
    }
    return groupData;
  }
}
