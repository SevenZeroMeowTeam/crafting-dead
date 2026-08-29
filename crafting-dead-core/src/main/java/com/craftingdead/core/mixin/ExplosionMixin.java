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

package com.craftingdead.core.mixin;

import org.jetbrains.annotations.Nullable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.craftingdead.core.world.entity.ExplosionSource;

@Mixin(Explosion.class)
public class ExplosionMixin {

  @Nullable
  @SuppressWarnings("unchecked")
  private Entity getSource() {
    try {
      return (Entity) (Object) ObfuscationReflectionHelper.getPrivateValue(
          Explosion.class, (Explosion) (Object) this, "f_46016_");
    } catch (ObfuscationReflectionHelper.UnableToAccessFieldException e) {
      // f_46016_ is the SRG name of Explosion.source. If we can't reach it, treat it as null.
      return null;
    }
  }

  @Redirect(at = @At(value = "INVOKE",
      target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"),
      method = "explode")
  public boolean entityHurtProxy(Entity instance, DamageSource damageSource, float damage) {
    var damageMultiplier =
        (this.getSource() instanceof ExplosionSource source) ? source.getDamageMultiplier() : 1.0F;
    return instance.hurt(damageSource, damage * damageMultiplier);
  }
}
