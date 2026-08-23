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

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Compatibility patch for TaCZ (Timeless &amp; Classics Guns) 1.1.8-hotfix-r5.
 * <p>
 * TaCZ's {@code LocalPlayerMixin} attaches the {@code IClientPlayerGunOperator} interface
 * to {@link LocalPlayer} but fails to implement the three charging-related methods
 * ({@code isCharging()}, {@code getChargeProgress()}, {@code chargeShoot(boolean)}).
 * When a charging gun (e.g. the Taurus 500) is rendered in first person, its Lua animation
 * calls {@code isCharging()} which results in an {@link AbstractMethodError}.
 * <p>
 * This mixin injects safe default implementations of those three methods into
 * {@link LocalPlayer}, restoring compatibility without requiring a compile-time dependency
 * on TaCZ. If TaCZ is not present the extra methods are harmless.
 */
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerTaczCompatMixin {

  public boolean isCharging() {
    return false;
  }

  public float getChargeProgress() {
    return 0.0F;
  }

  public void chargeShoot(boolean charge) {
    // No-op default for TaCZ's unimplemented charge mechanic.
  }
}
