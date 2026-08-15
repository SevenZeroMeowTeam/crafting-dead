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

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * A damage source that carries kill feed information (killer, weapon, headshot).
 * Adapted for Minecraft 1.20.1 where {@code EntityDamageSource} was removed.
 */
public class KillFeedDamageSource extends DamageSource implements KillFeedProvider {

  private final LivingEntity killer;
  private final ItemStack itemStack;
  private final KillFeedEntry.Type killFeedType;

  public KillFeedDamageSource(LivingEntity killer, ItemStack itemStack,
      KillFeedEntry.Type killFeedType) {
    // Use the killer's damage sources to resolve the damage type holder from the
    // live registry access. RegistryAccess.EMPTY must NOT be used here as it
    // contains no registries and would throw "Missing registry: minecraft:damage_type".
    super(killer.damageSources().mobAttack(killer).typeHolder());
    this.killer = killer;
    this.itemStack = itemStack;
    this.killFeedType = killFeedType;
  }

  @Override
  public LivingEntity getEntity() {
    return this.killer;
  }

  @Override
  public LivingEntity getDirectEntity() {
    return this.killer;
  }

  @Override
  public KillFeedEntry createKillFeedEntry(Player player) {
    return new KillFeedEntry(this.killer.getId(), this.killer.getDisplayName(),
        player.getDisplayName(), this.itemStack, this.killFeedType);
  }
}
