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

package com.craftingdead.core.particle;

import com.craftingdead.core.CraftingDead;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.Registries;

public class ModParticleTypes {

  public static final DeferredRegister<ParticleType<?>> deferredRegister =
      DeferredRegister.create(Registries.PARTICLE_TYPE, CraftingDead.ID);

  public static final DeferredHolder<ParticleType<?>, ParticleType<GrenadeSmokeParticleData>> GRENADE_SMOKE =
      deferredRegister.register("grenade_smoke",
          () -> create(true, GrenadeSmokeParticleData.CODEC,
              GrenadeSmokeParticleData.STREAM_CODEC));

  public static final DeferredHolder<ParticleType<?>, ParticleType<FlashParticleOptions>> RGB_FLASH =
      deferredRegister.register("rgb_flash",
          () -> create(true, FlashParticleOptions.CODEC, FlashParticleOptions.STREAM_CODEC));

  private static <T extends ParticleOptions> ParticleType<T> create(boolean alwaysShow,
      MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
    return new ParticleType<T>(alwaysShow) {
      @Override
      public MapCodec<T> codec() {
        return codec;
      }

      @Override
      public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        return streamCodec;
      }
    };
  }
}
