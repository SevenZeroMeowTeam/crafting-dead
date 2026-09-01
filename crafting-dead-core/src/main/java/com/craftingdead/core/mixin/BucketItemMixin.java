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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Defensive fix for crashes caused by mods that register a BucketItem with a null
 * fluid. Since 1.21 {@link BucketItem} no longer exposes {@code getFluid()}; the fluid
 * is stored in the public final {@code content} field. A null {@code content} makes
 * callers such as the fluid stack constructors throw IllegalArgumentException and
 * crashes the game (e.g. when the Thirst mod renders fluid containers in the creative
 * inventory). This mixin replaces a null {@code content} with {@link Fluids#EMPTY}
 * right after the constructor runs.
 */
@Mixin(BucketItem.class)
public abstract class BucketItemMixin {

  @Shadow
  @Final
  @Mutable
  private Fluid content;

  @Inject(method = "<init>", at = @At("TAIL"), remap = false)
  private void craftingdead$ensureNonNullFluid(CallbackInfo ci) {
    if (this.content == null) {
      this.content = Fluids.EMPTY;
    }
  }
}
