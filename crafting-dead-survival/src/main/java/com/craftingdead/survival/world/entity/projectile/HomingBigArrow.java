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

package com.craftingdead.survival.world.entity.projectile;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * 骷髅（增强 AI）有概率发射的「跟踪大号箭矢」。
 *
 * <p>飞行途中会持续追踪目标（每 tick 平滑转向目标所在位置），拥有更大的实体尺寸与
 * 更高的基础伤害（6 点），命中时额外造成一次击退。目标丢失或已死亡时退化为普通箭矢
 * 沿当前方向继续飞行。
 */
public class HomingBigArrow extends AbstractArrow {

  private static final EntityDataAccessor<Optional<UUID>> DATA_HOMING_TARGET =
      SynchedEntityData.defineId(HomingBigArrow.class, EntityDataSerializers.OPTIONAL_UUID);

  private static final String TAG_HOMING_TARGET = "HomingTarget";

  public HomingBigArrow(EntityType<? extends HomingBigArrow> type, Level level) {
    super(type, level);
  }

  public HomingBigArrow(EntityType<? extends HomingBigArrow> type, LivingEntity owner, Level level) {
    super(type, owner, level);
    this.setBaseDamage(6.0D);
  }

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.entityData.define(DATA_HOMING_TARGET, Optional.empty());
  }

  @Override
  public ItemStack getPickupItem() {
    return new ItemStack(Items.ARROW);
  }

  /**
   * 设置箭矢追踪的目标。传入 {@code null} 可取消追踪。
   */
  public void setHomingTarget(@Nullable Entity target) {
    this.entityData.set(DATA_HOMING_TARGET,
        target == null ? Optional.empty() : Optional.of(target.getUUID()));
  }

  /**
   * 当前追踪的目标实体（仅服务端可从 UUID 解析）。
   */
  @Nullable
  public Entity getHomingTarget() {
    Optional<UUID> targetId = this.entityData.get(DATA_HOMING_TARGET);
    if (targetId.isPresent() && this.getLevel() instanceof ServerLevel serverLevel) {
      return serverLevel.getEntity(targetId.get());
    }
    return null;
  }

  @Override
  public void tick() {
    Entity target = this.getHomingTarget();
    if (target != null && target.isAlive() && !this.getLevel().isClientSide()) {
      Vec3 current = this.getDeltaMovement();
      Vec3 toTarget = target.getEyePosition().subtract(this.position()).normalize();
      double speed = current.length();
      // 平滑转向：保留大部分原有动量，叠加指向目标的修正，保持飞行速度不衰减
      Vec3 newMotion = current.scale(0.85D).add(toTarget.scale(0.45D))
          .normalize().scale(Math.max(speed, 2.2D));
      this.setDeltaMovement(newMotion);

      // 让箭矢朝向运动方向
      float yRot = (float) (Mth.atan2(newMotion.x, newMotion.z)
          * (double) (180F / (float) Math.PI));
      float xRot = (float) (Mth.atan2(newMotion.y, newMotion.horizontalDistance())
          * (double) (180F / (float) Math.PI));
      this.setYRot(yRot);
      this.setXRot(xRot);
      this.yRotO = this.getYRot();
      this.xRotO = this.getXRot();
    }
    super.tick();
  }

  @Override
  protected void onHitEntity(EntityHitResult result) {
    super.onHitEntity(result);
    if (!this.getLevel().isClientSide() && result.getEntity() instanceof LivingEntity living
        && living.isAlive()) {
      // 大号箭矢命中额外击退
      living.knockback(0.8D, this.getDeltaMovement().x(), this.getDeltaMovement().z());
    }
  }

  @Override
  public void addAdditionalSaveData(CompoundTag tag) {
    super.addAdditionalSaveData(tag);
    this.entityData.get(DATA_HOMING_TARGET).ifPresent(uuid -> tag.putUUID(TAG_HOMING_TARGET, uuid));
  }

  @Override
  public void readAdditionalSaveData(CompoundTag tag) {
    super.readAdditionalSaveData(tag);
    if (tag.hasUUID(TAG_HOMING_TARGET)) {
      this.entityData.set(DATA_HOMING_TARGET, Optional.of(tag.getUUID(TAG_HOMING_TARGET)));
    }
  }
}
