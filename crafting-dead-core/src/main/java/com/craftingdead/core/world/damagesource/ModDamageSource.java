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

package com.craftingdead.core.world.damagesource;

import org.jetbrains.annotations.Nullable;
import com.craftingdead.core.world.entity.grenade.Grenade;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ModDamageSource {

  public static final String BULLET_HEADSHOT_DAMAGE_TYPE = "bullet.headshot";
  public static final String BULLET_BODY_DAMAGE_TYPE = "bullet";
  public static final String BLEEDING = "bleeding";

  public static DamageSource gun(LivingEntity source, boolean headshot) {
    // TODO: Register custom damage types (bullet, bullet.headshot) as data-driven DamageType entries
    return source.damageSources().mobAttack(source);
  }

  public static DamageSource grenade(Grenade grenade, @Nullable Entity thrower) {
    // TODO: Register custom damage type (grenade) as data-driven DamageType entry
    if (thrower instanceof LivingEntity livingThrower) {
      return livingThrower.damageSources().mobAttack(livingThrower);
    }
    return grenade.damageSources().generic();
  }

  public static DamageSource bleeding() {
    return new DamageSource(
        net.minecraft.core.RegistryAccess.EMPTY.registryOrThrow(
            net.minecraft.core.registries.Registries.DAMAGE_TYPE)
            .getHolderOrThrow(net.minecraft.world.damagesource.DamageTypes.GENERIC))
        ;
  }

  /**
   * Creates an explosion damage source without difficulty scaling.
   */
  public static DamageSource causeUnscaledExplosionDamage(@Nullable LivingEntity source) {
    if (source != null) {
      return source.damageSources().mobAttack(source);
    }
    return new DamageSource(
        net.minecraft.core.RegistryAccess.EMPTY.registryOrThrow(
            net.minecraft.core.registries.Registries.DAMAGE_TYPE)
            .getHolderOrThrow(net.minecraft.world.damagesource.DamageTypes.GENERIC));
  }

  /**
   * Hurt an entity without doing knockback.
   *
   * @param victim - the victim
   * @param source - the source
   * @param amount - the amount of dmg
   * @return the result from {@link Entity#hurt(DamageSource, float)}.
   */
  public static boolean hurtWithoutKnockback(Entity victim, DamageSource source, float amount) {
    if (victim instanceof LivingEntity livingHit) {
      var livingResistance = livingHit.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
      var previousResistance = livingResistance.getBaseValue();

      livingResistance.setBaseValue(Integer.MAX_VALUE);
      var attackResult = livingHit.hurt(source, amount);

      livingResistance.setBaseValue(previousResistance);
      return attackResult;
    }
    return victim.hurt(source, amount);
  }
}
