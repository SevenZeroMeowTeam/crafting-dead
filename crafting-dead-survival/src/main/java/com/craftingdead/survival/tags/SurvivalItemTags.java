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

package com.craftingdead.survival.tags;

import com.craftingdead.survival.CraftingDeadSurvival;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class SurvivalItemTags {

  public static final TagKey<Item> ZOMBIE_CLOTHING_LOOT = bind("zombie_clothing_loot");
  public static final TagKey<Item> ZOMBIE_HAT_LOOT = bind("zombie_hat_loot");
  public static final TagKey<Item> ZOMBIE_BACKPACK_LOOT = bind("zombie_backpack_loot");
  public static final TagKey<Item> ZOMBIE_VEST_LOOT = bind("zombie_vest_loot");
  public static final TagKey<Item> ZOMBIE_HAND_LOOT = bind("zombie_hand_loot");
  public static final TagKey<Item> ALFA_ZOMBIE_VEST_LOOT = bind("alfa_zombie_vest_loot");
  public static final TagKey<Item> DESERT_RAIDER_ZOMBIE_VEST_LOOT = bind("desert_raider_zombie_vest_loot");
  public static final TagKey<Item> JUGGERNAUT_ZOMBIE_VEST_LOOT = bind("juggernaut_zombie_vest_loot");
  public static final TagKey<Item> PILOT_ZOMBIE_VEST_LOOT = bind("pilot_zombie_vest_loot");
  public static final TagKey<Item> SNIPER_ZOMBIE_VEST_LOOT = bind("sniper_zombie_vest_loot");
  public static final TagKey<Item> SOLDIER_ZOMBIE_VEST_LOOT = bind("soldier_zombie_vest_loot");
  public static final TagKey<Item> SWAT_ZOMBIE_VEST_LOOT = bind("swat_zombie_vest_loot");
  public static final TagKey<Item> ALFA_ZOMBIE_BACKPACK_LOOT = bind("alfa_zombie_backpack_loot");
  public static final TagKey<Item> DESERT_RAIDER_ZOMBIE_BACKPACK_LOOT = bind("desert_raider_backpack_loot");
  public static final TagKey<Item> JUGGERNAUT_ZOMBIE_BACKPACK_LOOT = bind("juggernaut_zombie_backpack_loot");
  public static final TagKey<Item> SCOUT_ZOMBIE_BACKPACK_LOOT = bind("scout_zombie_backpack_loot");
  public static final TagKey<Item> SNIPER_ZOMBIE_BACKPACK_LOOT = bind("sniper_zombie_backpack_loot");
  public static final TagKey<Item> SOLDIER_ZOMBIE_BACKPACK_LOOT = bind("soldier_zombie_backpack_loot");
  public static final TagKey<Item> SWAT_ZOMBIE_BACKPACK_LOOT = bind("swat_zombie_backpack_loot");

  private static TagKey<Item> bind(String name) {
    return ItemTags.create(ResourceLocation.fromNamespaceAndPath(CraftingDeadSurvival.ID, name));
  }
}
