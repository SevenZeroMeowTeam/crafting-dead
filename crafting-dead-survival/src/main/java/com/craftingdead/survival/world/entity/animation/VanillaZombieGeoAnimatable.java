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

package com.craftingdead.survival.world.entity.animation;

import com.craftingdead.survival.world.entity.monster.ModZombie;
import net.minecraft.world.entity.EntityType;
import software.bernie.geckolib.animatable.GeoReplacedEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * 用于替换原版 {@code EntityType.ZOMBIE} 渲染的 GeckoLib 单例动画对象，
 * 使其也能使用与 {@link ModZombie} 相同的标准人形动画。
 */
public class VanillaZombieGeoAnimatable implements GeoReplacedEntity {

  public static final VanillaZombieGeoAnimatable INSTANCE = new VanillaZombieGeoAnimatable();

  private final AnimatableInstanceCache animatableInstanceCache =
      GeckoLibUtil.createInstanceCache(this);

  private VanillaZombieGeoAnimatable() {}

  @Override
  public EntityType<?> getReplacingEntityType() {
    return EntityType.ZOMBIE;
  }

  @Override
  public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    controllers.add(ZombieAnimations.createController(this));
  }

  @Override
  public AnimatableInstanceCache getAnimatableInstanceCache() {
    return this.animatableInstanceCache;
  }
}
