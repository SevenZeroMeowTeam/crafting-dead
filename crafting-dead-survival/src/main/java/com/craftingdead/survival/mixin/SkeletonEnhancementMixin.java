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

import com.craftingdead.survival.CraftingDeadSurvival;
import com.craftingdead.survival.world.entity.SurvivalEntityTypes;
import com.craftingdead.survival.world.entity.projectile.HomingBigArrow;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 增强骷髅 AI 的兼容补丁（作用于所有原版/模组骷髅）。
 *
 * <p>增强内容：
 * <ul>
 *   <li>更大的追踪范围（FOLLOW_RANGE 至少 48 格），让骷髅更早发现并锁定玩家；</li>
 *   <li>每次远程攻击有概率（约 22%）发射 {@link HomingBigArrow} 跟踪大号箭矢
 *       （高伤害、飞行中持续转向追踪目标），其余情况维持原版射击。</li>
 * </ul>
 *
 * <p>远程攻击由 {@code AbstractSkeleton.performRangedAttack} 触发，方法声明在
 * {@code AbstractSkeleton} 本身，因此可以安全地 mixin 注入。
 */
@Mixin(AbstractSkeleton.class)
public abstract class SkeletonEnhancementMixin extends Monster implements RangedAttackMob {

  /** 发射跟踪大号箭矢的概率。 */
  private static final float HOMING_ARROW_CHANCE = 0.22F;

  protected SkeletonEnhancementMixin(EntityType<? extends Monster> type, Level level) {
    super(type, level);
  }

  /**
   * 骷髅 AI 增强：按配置扩大追踪范围（默认 32 格）。
   */
  @Inject(method = "registerGoals", at = @At("RETURN"))
  private void craftingdead$enhanceSkeletonGoals(CallbackInfo ci) {
    double followRangeValue = CraftingDeadSurvival.serverConfig.zombieFollowRange.get();
    AttributeInstance followRange = this.getAttribute(Attributes.FOLLOW_RANGE);
    if (followRange != null && followRange.getBaseValue() < followRangeValue) {
      followRange.setBaseValue(followRangeValue);
    }
  }

  /**
   * 概率发射跟踪大号箭矢，替换原版射箭。
   */
  @Inject(method = "performRangedAttack", at = @At("HEAD"), cancellable = true)
  private void craftingdead$maybeShootHomingBigArrow(LivingEntity target, float distanceFactor,
      CallbackInfo ci) {
    if (!this.level().isClientSide() && this.random.nextFloat() < HOMING_ARROW_CHANCE) {
      this.craftingdead$shootHomingBigArrow(target);
      ci.cancel();
    }
  }

  /**
   * 瞄准目标并发射一发跟踪大号箭矢（高精度、不可拾取）。
   */
  private void craftingdead$shootHomingBigArrow(LivingEntity target) {
    HomingBigArrow arrow = new HomingBigArrow(
        SurvivalEntityTypes.HOMING_BIG_ARROW.get(), (LivingEntity) (Object) this,
        this.level(), ItemStack.EMPTY);
    arrow.setHomingTarget(target);
    arrow.setOwner(this);
    arrow.pickup = AbstractArrow.Pickup.DISALLOWED;

    // 高精度瞄准：inaccuracy = 0
    Vec3 viewVector = this.getViewVector(1.0F);
    double dx = target.getX() - (this.getX() + viewVector.x * 0.5D);
    double dy = target.getY() + (double) target.getEyeHeight() - this.getEyeY();
    double dz = target.getZ() - (this.getZ() + viewVector.z * 0.5D);
    double dist = Math.sqrt(dx * dx + dz * dz);
    float velocity = (float) (dist < 16.0D ? 2.0D : 3.2D);
    arrow.shoot(dx, dy, dz, velocity, 0.0F);

    this.level().addFreshEntity(arrow);
  }
}
