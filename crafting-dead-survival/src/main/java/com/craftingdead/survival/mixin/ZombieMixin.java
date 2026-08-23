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

package com.craftingdead.survival.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.craftingdead.core.world.entity.extension.LivingExtension;
import com.craftingdead.survival.world.entity.extension.ZombieHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

@Mixin(Zombie.class)
public abstract class ZombieMixin extends Monster {

  /**
   * 尸体保留时长（tick）。默认 120 秒 = 2400 tick。
   */
  @Unique
  private static final long CORPSE_DESPAWN_TICKS = 2400L;

  /**
   * 尸体应被移除的游戏时间。负数表示非尸体状态（正常存活/立即移除）。
   */
  @Unique
  private long corpseDespawnTick = -1L;

  protected ZombieMixin(EntityType<? extends Monster> type, Level level) {
    super(type, level);
  }

  @Override
  public float getWalkTargetValue(BlockPos pos, LevelReader level) {
    return 0.0F;
  }

  /**
   * 僵尸死亡时记录尸体保留截止时间（仅服务端）。
   */
  @Inject(at = @At("HEAD"), method = "die")
  public void onDeath(DamageSource source, CallbackInfo callbackInfo) {
    if (!this.level().isClientSide()) {
      this.corpseDespawnTick = this.level().getGameTime() + CORPSE_DESPAWN_TICKS;
    }
  }

  /**
   * 倒地物理：死亡后正常播放倒地动画，随后保持躺倒状态保留尸体
   * （保留碰撞箱、可被推动），直到保留时间结束才移除实体。
   */
  @Inject(at = @At("HEAD"), method = "tickDeath", cancellable = true)
  public void onTickDeath(CallbackInfo callbackInfo) {
    if (!this.level().isClientSide() && this.corpseDespawnTick >= 0L) {
      this.deathTime++;
      if (this.deathTime >= 20) {
        // 保持完全躺倒状态（防止 MC 默认在 20 tick 后移除实体）
        this.deathTime = 20;
        if (this.level().getGameTime() >= this.corpseDespawnTick) {
          this.remove(Entity.RemovalReason.DISCARDED);
        }
      }
      callbackInfo.cancel();
    }
  }

  @Inject(at = @At("RETURN"), method = "setBaby")
  public void setBaby(boolean baby, CallbackInfo callbackInfo) {
    var zombie = (Zombie) (Object) this;
    zombie.getCapability(LivingExtension.CAPABILITY).resolve()
        .flatMap(extension -> extension.getHandler(ZombieHandler.TYPE))
        .ifPresent(handler -> handler.handleSetBaby(baby));
  }

  @Inject(at = @At("RETURN"), method = "convertsInWater", cancellable = true)
  public void convertsInWater(CallbackInfoReturnable<Boolean> callbackInfo) {
    callbackInfo.setReturnValue(false);
  }

  @Inject(at = @At("RETURN"), method = "isSunSensitive", cancellable = true)
  public void isSunSensitive(CallbackInfoReturnable<Boolean> callbackInfo) {
    callbackInfo.setReturnValue(false);
  }

  @Inject(at = @At("RETURN"), method = "populateDefaultEquipmentSlots")
  public void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty,
      CallbackInfo callbackInfo) {
    var zombie = (Zombie) (Object) this;
    zombie.getCapability(LivingExtension.CAPABILITY).resolve()
        .flatMap(extension -> extension.getHandler(ZombieHandler.TYPE))
        .ifPresent(handler -> handler.populateDefaultEquipmentSlots(difficulty));
  }
}
