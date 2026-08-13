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

import com.craftingdead.survival.world.item.SurvivalItems;
import dev.ghen.thirst.api.ThirstHelper;
import dev.ghen.thirst.foundation.common.capability.ModCapabilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.ModList;

/**
 * Optional compatibility layer for the "Thirst was Taken" mod.
 * All methods are safe to call even when the mod is not installed.
 */
public class ThirstWasTakenCompat {

  private static final int MAX_THIRST = 20;

  private static final boolean LOADED = ModList.get().isLoaded("thirst");

  private ThirstWasTakenCompat() {}

  public static boolean isLoaded() {
    return LOADED;
  }

  /**
   * Whether the player still needs water according to Thirst was Taken.
   */
  public static boolean isThirsty(Player player) {
    if (!LOADED) {
      return true;
    }
    return player.getCapability(ModCapabilities.PLAYER_THIRST)
        .map(thirst -> thirst.getThirst() < MAX_THIRST)
        .orElse(true);
  }

  /**
   * Registers Crafting Dead drinks so that Thirst was Taken restores thirst
   * when they are consumed.
   */
  public static void registerDrinks() {
    if (!LOADED) {
      return;
    }
    registerDrink(SurvivalItems.WATER_BOTTLE.get(), 8, 8);
    registerDrink(SurvivalItems.WATER_CANTEEN.get(), 9, 9);
    registerDrink(SurvivalItems.FLASK.get(), 7, 7);
    registerDrink(SurvivalItems.ICED_TEA.get(), 4, 4);
    registerDrink(SurvivalItems.IRON_BREW.get(), 4, 4);
    registerDrink(SurvivalItems.JUICE_POUCH.get(), 4, 4);
    registerDrink(SurvivalItems.LEMON_SODA.get(), 4, 4);
    registerDrink(SurvivalItems.MILK_CARTON.get(), 6, 6);
    registerDrink(SurvivalItems.ORANGE_SODA.get(), 4, 4);
    registerDrink(SurvivalItems.PEPE_SODA.get(), 4, 4);
    registerDrink(SurvivalItems.SPRITE.get(), 6, 6);
    registerDrink(SurvivalItems.COLA.get(), 6, 6);
    registerDrink(SurvivalItems.ZOMBIE_ENERGY.get(), 4, 4);
  }

  private static void registerDrink(Item item, int thirst, int quenched) {
    ThirstHelper.addDrink(item, thirst, quenched);
  }
}
