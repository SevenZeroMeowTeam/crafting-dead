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

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record FlashParticleOptions(float red, float green, float blue, float scale)
    implements ParticleOptions {

  public static final MapCodec<FlashParticleOptions> CODEC =
      RecordCodecBuilder.mapCodec(instance -> instance
          .group(
              com.mojang.serialization.Codec.FLOAT.fieldOf("red")
                  .forGetter(FlashParticleOptions::red),
              com.mojang.serialization.Codec.FLOAT.fieldOf("green")
                  .forGetter(FlashParticleOptions::green),
              com.mojang.serialization.Codec.FLOAT.fieldOf("blue")
                  .forGetter(FlashParticleOptions::blue),
              com.mojang.serialization.Codec.FLOAT.fieldOf("scale")
                  .forGetter(FlashParticleOptions::scale))
          .apply(instance, FlashParticleOptions::new));

  public static final StreamCodec<RegistryFriendlyByteBuf, FlashParticleOptions> STREAM_CODEC =
      StreamCodec.composite(
          StreamCodecHelper.FLOAT, FlashParticleOptions::red,
          StreamCodecHelper.FLOAT, FlashParticleOptions::green,
          StreamCodecHelper.FLOAT, FlashParticleOptions::blue,
          StreamCodecHelper.FLOAT, FlashParticleOptions::scale,
          FlashParticleOptions::new);

  @Override
  public ParticleType<FlashParticleOptions> getType() {
    return ModParticleTypes.RGB_FLASH.get();
  }
}
