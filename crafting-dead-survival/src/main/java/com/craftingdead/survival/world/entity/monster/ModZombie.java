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

import com.craftingdead.survival.world.entity.animation.ZombieAnimations;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
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

  private final AnimatableInstanceCache animatableInstanceCache =
      GeckoLibUtil.createInstanceCache(this);

  public ModZombie(EntityType<? extends Zombie> zombie, Level level) {
    super(zombie, level);
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
    // 僵尸 AI 增强：可以破坏门追击玩家，并扩大追踪范围
    this.setCanBreakDoors(true);
    AttributeInstance followRange = this.getAttribute(Attributes.FOLLOW_RANGE);
    if (followRange != null && followRange.getBaseValue() < 40.0D) {
      followRange.setBaseValue(40.0D);
    }
    return groupData;
  }
}
