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

package com.craftingdead.survival.compat;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;
import toughasnails.api.thirst.ThirstHelper;

/**
 * Optional compatibility layer for Tough As Nails thirst system.
 * All methods are safe to call even when Tough As Nails is not installed.
 */
public class ToughAsNailsCompat {

  private static final boolean LOADED = ModList.get().isLoaded("toughasnails");

  private ToughAsNailsCompat() {}

  public static boolean isLoaded() {
    return LOADED;
  }

  /**
   * Whether the player still needs water according to Tough As Nails.
   */
  public static boolean isThirsty(Player player) {
    return LOADED && ThirstHelper.getThirst(player).isThirsty();
  }

  /**
   * Restores thirst in Tough As Nails when the player drinks.
   *
   * @param water amount of water restored (same scale as immerse hydration)
   * @param saturation hydration modifier multiplier
   */
  public static void drink(Player player, int water, float saturation) {
    if (LOADED) {
      ThirstHelper.getThirst(player).drink(water, saturation);
    }
  }
}
