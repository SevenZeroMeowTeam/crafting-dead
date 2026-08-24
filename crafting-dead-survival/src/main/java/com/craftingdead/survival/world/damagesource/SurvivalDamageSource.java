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

package com.craftingdead.survival.world.damagesource;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;

public class SurvivalDamageSource {

  /**
   * 1.20.1 中 DamageSource 需要 Holder，且数据驱动的伤害类型在服务端准备完成后才可用，
   * 因此改为运行时解析：从实体所在世界的注册表中取 damage_type holder。
   * 若注册表/类型不可用则回退到原版 generic，避免实体 tick 中抛异常导致服务器崩溃。
   */
  public static DamageSource infection(LivingEntity entity) {
    var holder = entity.level().registryAccess()
        .registry(Registries.DAMAGE_TYPE)
        .flatMap(registry -> registry.getHolder(DamageTypes.GENERIC))
        .orElse(null);
    return holder != null ? new DamageSource(holder) : entity.damageSources().generic();
  }
}
