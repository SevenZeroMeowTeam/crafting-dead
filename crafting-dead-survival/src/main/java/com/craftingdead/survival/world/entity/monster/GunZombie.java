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

import com.craftingdead.core.world.entity.ai.LookAtEntityGoal;
import com.craftingdead.core.world.entity.extension.LivingExtension;
import com.craftingdead.core.world.entity.grenade.FlashGrenadeEntity;
import com.craftingdead.core.world.item.gun.Gun;
import com.craftingdead.survival.world.entity.SurvivalPlayerHandler;
import net.minecraft.Util;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class GunZombie extends ModZombie implements RangedAttackMob {

  private RangedAttackGoal rangedAttackGoal;

  private long triggerPressedStartTime;

  private final float accuracy;

  public GunZombie(EntityType<? extends GunZombie> type, Level world, float accuracy) {
    super(type, world);
    this.accuracy = accuracy;
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.rangedAttackGoal = new RangedAttackGoal(this, 1.0D, 40, this.getStopDistanceFromPlayer()) {
      @Override
      public boolean canUse() {
        return super.canUse() && GunZombie.this.getMainHandItem()
            .getCapability(Gun.CAPABILITY) != null;
      }
    };
    this.goalSelector.addGoal(2, this.rangedAttackGoal);
    this.goalSelector.addGoal(4,
        new LookAtEntityGoal<>(this, FlashGrenadeEntity.class, 20.0F, 0.35F));
  }

  @Override
  protected void addBehaviourGoals() {
    super.addBehaviourGoals();
    this.targetSelector.addGoal(2,
        new NearestAttackableTargetGoal<>(this, Player.class, 5, false, false,
            targetEntity -> {
              var extension = targetEntity.getCapability(LivingExtension.CAPABILITY);
              return extension != null
                  && extension.getHandler(SurvivalPlayerHandler.TYPE)
                      .map(handler -> handler.getSoundLevel() >= targetEntity.distanceTo(this))
                      .orElse(false);
            }) {
          @Override
          public double getFollowDistance() {
            return 100.0D;
          }
        });
  }

  @Override
  public void tick() {
    super.tick();
    if (!this.level().isClientSide()) {
      var living = this.getCapability(LivingExtension.CAPABILITY);
      if (living != null) {
        var gun = living.mainHandGun();
        if (gun != null && gun.isTriggerPressed()
            && (!this.rangedAttackGoal.canContinueToUse() || (Util.getMillis()
                - this.triggerPressedStartTime > 200 + this.random.nextInt(400)))) {
          gun.setNPCTriggerPressed(living, false, true, this.accuracy);
        }
      }
    }
  }

  @Override
  public void performRangedAttack(@NotNull LivingEntity livingEntity, float distance) {
    if (!this.level().isClientSide()) {
      var living = this.getCapability(LivingExtension.CAPABILITY);
      if (living != null) {
        var gun = living.mainHandGun();
        if (gun != null) {
          this.triggerPressedStartTime = Util.getMillis();
          gun.setNPCTriggerPressed(living, true, true, this.accuracy);
        }
      }
    }
  }

  @Override
  public boolean isBaby() {
    return false;
  }

  /**
   *
   * @return 1.0F = 1 Block
   */
  public float getStopDistanceFromPlayer() {
    return 20.0F;
  }
}
