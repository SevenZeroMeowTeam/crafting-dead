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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;

/**
 * 僵尸尸体保留逻辑。
 *
 * <p>注意：Mixin 的 {@code @Inject} 按方法名解析时只能命中目标类自身声明的方法，
 * {@code die} 与 {@code tickDeath} 都声明于 {@link LivingEntity}（{@link Zombie}
 * 只是继承），因此本 mixin 必须挂在 {@link LivingEntity} 上，并通过
 * {@code instanceof Zombie} 守卫确保仅对僵尸生效。
 *
 * <p>mixin 类不能 {@code extends LivingEntity}（Mixin 注解处理器会报
 * "Superclass ... was not found in the hierarchy of target class"），需要访问
 * 受保护字段 {@code deathTime} 时应改用 {@code @Shadow}。
 */
@Mixin(LivingEntity.class)
public abstract class SurvivalLivingEntityMixin {

  @Shadow
  protected int deathTime;

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

  /**
   * 僵尸死亡时记录尸体保留截止时间（仅服务端）。
   */
  @Inject(at = @At("HEAD"), method = "die")
  private void craftingdead$onDeath(DamageSource source, CallbackInfo callbackInfo) {
    LivingEntity self = (LivingEntity) (Object) this;
    if (self instanceof Zombie && !self.level().isClientSide()) {
      this.corpseDespawnTick = self.level().getGameTime() + CORPSE_DESPAWN_TICKS;
    }
  }

  /**
   * 倒地物理：死亡后正常播放倒地动画，随后保持躺倒状态保留尸体
   * （保留碰撞箱、可被推动），直到保留时间结束才移除实体。
   */
  @Inject(at = @At("HEAD"), method = "tickDeath", cancellable = true)
  private void craftingdead$onTickDeath(CallbackInfo callbackInfo) {
    LivingEntity self = (LivingEntity) (Object) this;
    if (self instanceof Zombie && !self.level().isClientSide() && this.corpseDespawnTick >= 0L) {
      this.deathTime++;
      if (this.deathTime >= 20) {
        // 保持完全躺倒状态（防止 MC 默认在 20 tick 后移除实体）
        this.deathTime = 20;
        if (self.level().getGameTime() >= this.corpseDespawnTick) {
          self.remove(Entity.RemovalReason.DISCARDED);
        }
      }
      callbackInfo.cancel();
    }
  }
}
