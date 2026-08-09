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

package com.craftingdead.core.world.item.equipment;

import com.craftingdead.core.world.item.ClothingItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Event listener that automatically applies protection attributes to clothing items.
 * This ensures all clothing in the game has appropriate protection values based on their tier.
 * 
 * @author Crafting Dead Team
 */
@Mod.EventBusSubscriber
public class ClothingProtectionInitializer {

  /**
   * Applies protection attributes to clothing items when they enter the world.
   * This ensures loot, mob drops, and spawned items get protection values.
   */
//  @SubscribeEvent
//  public static void onItemEntitySpawn(EntityJoinWorldEvent event) {
//    if (event.getWorld().isClientSide()) {
//      return; // Only process on server side
//    }
//
//    Entity entity = event.getEntity();
//    if (entity instanceof ItemEntity itemEntity) {
//      ItemStack itemStack = itemEntity.getItem();
//      initializeClothingProtection(itemStack);
//    }
//  }

  /**
   * Applies protection attributes when players craft clothing items.
   */
//  @SubscribeEvent
//  public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
//    ItemStack itemStack = event.getCrafting();
//    initializeClothingProtection(itemStack);
//  }

  /**
   * Applies protection attributes when players pick up clothing items.
   * This is a backup to ensure all clothing gets initialized.
   */
//  @SubscribeEvent
//  public static void onItemPickup(EntityItemPickupEvent event) {
//    ItemStack itemStack = event.getItem().getItem();
//    initializeClothingProtection(itemStack);
//  }

  /**
   * Initializes protection attributes on a clothing item if it doesn't have them yet.
   * 
   * @param itemStack the item stack to initialize
   */
  private static void initializeClothingProtection(ItemStack itemStack) {
    if (itemStack.isEmpty() || !(itemStack.getItem() instanceof ClothingItem clothingItem)) {
      return;
    }

    // Only apply if not already initialized
    if (ClothingProtection.hasProtectionAttributes(itemStack)) {
      return;
    }

    // Apply protection based on clothing type
    ClothingItem.ClothingType type = clothingItem.getClothingType();
    ClothingProtectionPresets.applyByType(itemStack, type);
  }
}
