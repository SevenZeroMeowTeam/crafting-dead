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
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;

public class SurvivalDamageSource {

  public static final ResourceKey<DamageType> INFECTION_TYPE =
      ResourceKey.create(Registries.DAMAGE_TYPE,
          ResourceLocation.fromNamespaceAndPath("craftingdeadsurvival", "infection"));

  /**
   * Creates the infection damage source at runtime, resolving the damage type holder
   * from the entity's live registry access. The damage_type registry is data-driven
   * (loaded from datapacks), so it must NOT be looked up via a static/frozen
   * RegistryAccess — that only contains static registries and would throw
   * "Missing registry: minecraft:damage_type".
   */
  public static DamageSource infection(LivingEntity entity) {
    var holder = entity.level().registryAccess()
        .registryOrThrow(Registries.DAMAGE_TYPE)
        .getHolderOrThrow(INFECTION_TYPE);
    return new DamageSource(holder);
  }
}
