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

package com.craftingdead.survival.client;

import com.craftingdead.core.network.message.play.OpenEquipmentMenuMessage;
import com.craftingdead.core.world.item.GunItem;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Handles right-click interactions with guns displayed in item frames.
 * Allows players to inspect gun stats and attachments without removing the gun from the frame.
 * Preserves vanilla rotation behavior (left-click) and prevents firing/reloading.
 */
@OnlyIn(Dist.CLIENT)
public class ItemFrameGunInteractionHandler {

  /**
   * Handles entity-specific interactions when a player right-clicks on an entity.
   * If the entity is an ItemFrame containing a gun, opens the gun's inspection menu.
   * 
   * @param event The entity interaction event
   */
  @SubscribeEvent
  public void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
    // Only process on client side
    if (event.getEntity().level().isClientSide()) {
      // Check if the interacted entity is an ItemFrame
      if (event.getTarget() instanceof ItemFrame itemFrame) {
        ItemStack frameItem = itemFrame.getItem();
        
        // Check if the item frame contains a gun
        if (frameItem.getItem() instanceof GunItem) {
          // Cancel the event to prevent default behavior (removing item from frame)
          event.setCanceled(true);
          
          // Open the equipment/inspection menu for the gun
          // This will show gun stats, attachments, and allow inspection
          PacketDistributor.sendToServer(new OpenEquipmentMenuMessage());
          
          // Note: The gun remains in the item frame and cannot be fired or reloaded
          // from this interaction. This is purely for display and inspection purposes.
        }
      }
    }
  }
}
