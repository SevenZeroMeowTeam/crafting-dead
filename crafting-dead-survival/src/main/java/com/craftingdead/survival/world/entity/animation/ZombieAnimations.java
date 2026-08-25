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

import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

/**
 * 所有僵尸共享的标准人形动画（与 zombie.animation.json 对应，GeckoLib 3 版本）。
 */
public final class ZombieAnimations {

  private ZombieAnimations() {}

  /**
   * 创建僵尸主控制器：移动时播放行走动画，静止时播放闲置动画。
   */
  public static <T extends IAnimatable> AnimationController<T> createController(T animatable) {
    return new AnimationController<>(animatable, "controller", 5, event -> {
      if (event.isMoving()) {
        event.getController().setAnimation(
            new AnimationBuilder().addAnimation("animation.zombie.walk", true));
      } else {
        event.getController().setAnimation(
            new AnimationBuilder().addAnimation("animation.zombie.idle", true));
      }
      return PlayState.CONTINUE;
    });
  }
}
