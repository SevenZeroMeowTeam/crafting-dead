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

package com.craftingdead.core.capability;

import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.Nullable;

public class CapabilityUtil {

  public static <T> Predicate<Entity> capabilityPresent(EntityCapability<T, @Nullable Void> capability) {
    return entity -> entity.getCapability(capability) != null;
  }

  @Nullable
  public static <T, R extends T> R get(EntityCapability<T, @Nullable Void> capability, Entity entity,
      Class<R> clazz) {
    final var value = entity.getCapability(capability);
    return clazz.isInstance(value) ? clazz.cast(value) : null;
  }

  public static <T, R extends T> R getOrThrow(EntityCapability<T, @Nullable Void> capability, Entity entity,
      Class<R> clazz) {
    final var value = get(capability, entity, clazz);
    if (value == null) {
      throw new IllegalStateException("Expecting capability: " + capability.name());
    }
    return value;
  }

  @Nullable
  public static <T, R extends T> R get(ItemCapability<T, @Nullable Void> capability, ItemStack stack,
      Class<R> clazz) {
    final var value = stack.getCapability(capability);
    return clazz.isInstance(value) ? clazz.cast(value) : null;
  }

  public static <T, R extends T> R getOrThrow(ItemCapability<T, @Nullable Void> capability, ItemStack stack,
      Class<R> clazz) {
    final var value = get(capability, stack, clazz);
    if (value == null) {
      throw new IllegalStateException("Expecting capability: " + capability.name());
    }
    return value;
  }
}
