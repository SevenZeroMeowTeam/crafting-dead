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

package com.craftingdead.survival.world.entity.extension;

import com.craftingdead.core.world.entity.extension.BasicLivingExtension;
import com.craftingdead.core.world.inventory.GunCraftSlotType;
import com.craftingdead.core.world.item.ModItems;
import com.craftingdead.core.world.item.equipment.Equipment;
import com.craftingdead.core.world.item.gun.Gun;
import com.craftingdead.core.world.item.gun.ammoprovider.RefillableAmmoProvider;
import com.craftingdead.core.world.item.gun.attachment.Attachment;
import com.craftingdead.core.world.item.gun.attachment.Attachments;
import com.craftingdead.survival.CraftingDeadSurvival;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;

public class DesertRaiderZombieHandler extends ZombieHandler {

  public DesertRaiderZombieHandler(BasicLivingExtension<Zombie> extension) {
    super(extension);
  }

  @Override
  protected ItemStack createHeldItem() {
    var random = new Random();
    var gunStack = ModItems.ACR.get().getDefaultInstance();
    var gun = gunStack.getCapability(Gun.CAPABILITY);
if (gun != null) {

      var magazineStack = ModItems.ACR_MAGAZINE.get().getDefaultInstance();
      gun.setAmmoProvider(new RefillableAmmoProvider(magazineStack, 0, true));
      Map<GunCraftSlotType, Supplier<Attachment>> possibleAttachments = Map.of(
          GunCraftSlotType.OVERBARREL_ATTACHMENT, Attachments.RED_DOT_SIGHT,
          GunCraftSlotType.UNDERBARREL_ATTACHMENT, Attachments.TACTICAL_GRIP,
          GunCraftSlotType.MUZZLE_ATTACHMENT, Attachments.SUPPRESSOR);
      Map<GunCraftSlotType, Attachment> attachments = new HashMap<>();
      for (var entry : possibleAttachments.entrySet()) {
        if (random.nextBoolean()) {
          attachments.put(entry.getKey(), entry.getValue().get());
        }
      }
      gun.setAttachments(attachments);
    
}
    return gunStack;
  }

  @Override
  protected ItemStack createHatItem() {
    return ModItems.ARMY_HELMET.get().getDefaultInstance();
  }

  @Override
  protected ItemStack createClothingItem() {
    return ModItems.ARMY_DESERT_CLOTHING.get().getDefaultInstance();
  }

  @Override
  protected ItemStack createBackpackItem() {
    return this.createFilledBackpackItem(
        CraftingDeadSurvival.serverConfig.desertRaiderVestEquipChance.get().floatValue(),
        ModItems.LARGE_TAN_BACKPACK.get().getDefaultInstance(),
        this.createBackpackLootId("desert_raider_zombie_backpack_loot"));
  }

  @Override
  protected ItemStack createVestItem() {
    return this.createFilledVestItem(
        CraftingDeadSurvival.serverConfig.desertRaiderVestEquipChance.get().floatValue(),
        ModItems.TAN_TACTICAL_VEST.get().getDefaultInstance(),
        this.createVestLootId("desert_raider_zombie_vest_loot"));
  }

  @Override
  public void applyEquipmentDropChances() {
    var zombie = extension.entity();
    extension.setEquipmentDropChance(Equipment.Slot.CLOTHING,
        CraftingDeadSurvival.serverConfig.desertRaiderZombieClothingDropChance.get().floatValue());
    extension.setEquipmentDropChance(Equipment.Slot.HAT,
        CraftingDeadSurvival.serverConfig.desertRaiderZombieHatDropChance.get().floatValue());
    extension.setEquipmentDropChance(Equipment.Slot.VEST,
        CraftingDeadSurvival.serverConfig.desertRaiderZombieVestDropChance.get().floatValue());
    extension.setEquipmentDropChance(Equipment.Slot.BACKPACK,
        CraftingDeadSurvival.serverConfig.desertRaiderZombieBackpackDropChance.get().floatValue());
    zombie.setDropChance(EquipmentSlot.MAINHAND,
        CraftingDeadSurvival.serverConfig.desertRaiderZombieHandDropChance.get().floatValue());
    zombie.setDropChance(EquipmentSlot.OFFHAND,
        CraftingDeadSurvival.serverConfig.desertRaiderZombieHandDropChance.get().floatValue());
  }
}
