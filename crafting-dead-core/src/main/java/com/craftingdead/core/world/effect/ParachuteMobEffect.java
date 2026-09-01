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

package com.craftingdead.core.world.effect;

import com.craftingdead.core.network.NetworkChannel;
import com.craftingdead.core.network.message.play.ParachuteSyncMessage;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.network.PacketDistributor;

public class ParachuteMobEffect extends MobEffect {

  public ParachuteMobEffect() {
    super(MobEffectCategory.BENEFICIAL, 0xFFEFD1);
    this.addAttributeModifier(Attributes.GRAVITY,
        net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("craftingdead", "parachute_gravity"),
        -0.07, AttributeModifier.Operation.ADD_VALUE);
  }

  @Override
  public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
    livingEntity.resetFallDistance();
    if (livingEntity.onGround() || livingEntity.isInWater()) {
      livingEntity.removeEffect(ModMobEffects.PARACHUTE);
      this.syncParachuteEffect(livingEntity, false);
      return false;
    }
    this.syncParachuteEffect(livingEntity, true);
    return true;
  }

  @Override
  public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
    return true;
  }

  private void syncParachuteEffect(LivingEntity entity, boolean hasParachute) {
    if (!entity.level().isClientSide()) {
      PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new ParachuteSyncMessage(entity.getId(), hasParachute));
    }
  }
}