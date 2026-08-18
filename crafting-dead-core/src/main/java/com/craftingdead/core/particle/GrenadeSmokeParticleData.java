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
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class GrenadeSmokeParticleData implements ParticleOptions {

  public static final MapCodec<GrenadeSmokeParticleData> CODEC =
      RecordCodecBuilder.mapCodec(instance -> instance
          .group(
              com.mojang.serialization.Codec.FLOAT.fieldOf("red")
                  .forGetter(GrenadeSmokeParticleData::getRed),
              com.mojang.serialization.Codec.FLOAT.fieldOf("green")
                  .forGetter(GrenadeSmokeParticleData::getGreen),
              com.mojang.serialization.Codec.FLOAT.fieldOf("blue")
                  .forGetter(GrenadeSmokeParticleData::getBlue),
              com.mojang.serialization.Codec.FLOAT.fieldOf("scale")
                  .forGetter(GrenadeSmokeParticleData::getScale))
          .apply(instance, GrenadeSmokeParticleData::new));

  public static final StreamCodec<RegistryFriendlyByteBuf, GrenadeSmokeParticleData>
      STREAM_CODEC =
      StreamCodec.composite(
          StreamCodecHelper.FLOAT, GrenadeSmokeParticleData::getRed,
          StreamCodecHelper.FLOAT, GrenadeSmokeParticleData::getGreen,
          StreamCodecHelper.FLOAT, GrenadeSmokeParticleData::getBlue,
          StreamCodecHelper.FLOAT, GrenadeSmokeParticleData::getScale,
          GrenadeSmokeParticleData::new);

  private final float red;
  private final float green;
  private final float blue;
  private final float scale;

  public GrenadeSmokeParticleData(float red, float green, float blue, float scale) {
    this.red = red;
    this.green = green;
    this.blue = blue;
    this.scale = scale;
  }

  @Override
  public ParticleType<GrenadeSmokeParticleData> getType() {
    return ModParticleTypes.GRENADE_SMOKE.get();
  }

  public float getRed() {
    return this.red;
  }

  public float getGreen() {
    return this.green;
  }

  public float getBlue() {
    return this.blue;
  }

  public float getScale() {
    return this.scale;
  }
}
