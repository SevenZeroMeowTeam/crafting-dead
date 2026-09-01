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

package com.craftingdead.survival.particles;

import com.craftingdead.survival.CraftingDeadSurvival;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class SurvivalParticleTypes {

  public static final DeferredRegister<ParticleType<?>> deferredRegister =
      DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, CraftingDeadSurvival.ID);

  public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MILITARY_LOOT_GEN =
      deferredRegister.register("military_loot_gen", () -> new SimpleParticleType(false));

  public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MEDIC_LOOT_GEN =
      deferredRegister.register("medic_loot_gen", () -> new SimpleParticleType(false));

  public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CIVILIAN_LOOT_GEN =
      deferredRegister.register("civilian_loot_gen", () -> new SimpleParticleType(false));

  public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CIVILIAN_RARE_LOOT_GEN =
      deferredRegister.register("civilian_rare_loot_gen", () -> new SimpleParticleType(false));

  public static final DeferredHolder<ParticleType<?>, SimpleParticleType> POLICE_LOOT_GEN =
      deferredRegister.register("police_loot_gen", () -> new SimpleParticleType(false));
}
