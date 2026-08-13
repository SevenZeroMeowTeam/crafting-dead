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

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Defensive fix for crashes caused by mods that register a BucketItem whose
 * {@code getFluid()} returns null (e.g. when the Thirst mod renders fluid
 * containers in the creative inventory). A null fluid makes Forge's
 * FluidStack constructor throw IllegalArgumentException and crashes the game.
 */
@Mixin(BucketItem.class)
public abstract class BucketItemMixin {

  @Inject(method = "getFluid", at = @At("RETURN"), cancellable = true, remap = false)
  private void craftingdead$ensureNonNullFluid(CallbackInfoReturnable<Fluid> cir) {
    if (cir.getReturnValue() == null) {
      cir.setReturnValue(Fluids.EMPTY);
    }
  }
}
