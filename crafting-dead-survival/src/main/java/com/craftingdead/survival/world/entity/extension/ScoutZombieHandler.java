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
import com.craftingdead.core.world.item.ModItems;
import com.craftingdead.core.world.item.equipment.Equipment;
import com.craftingdead.survival.CraftingDeadSurvival;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;

public class ScoutZombieHandler extends ZombieHandler {

  public ScoutZombieHandler(BasicLivingExtension<Zombie> extension) {
    super(extension);
  }

  @Override
  protected ItemStack createHeldItem() {
    return ModItems.BINOCULARS.get().getDefaultInstance();
  }

  @Override
  protected ItemStack createHatItem() {
    return ModItems.GHILLIE_HAT.get().getDefaultInstance();
  }

  @Override
  protected ItemStack createClothingItem() {
    return ModItems.FULL_GHILLIE_CLOTHING.get().getDefaultInstance();
  }

  @Override
  protected ItemStack createBackpackItem() {
    return this.createFilledBackpackItem(
        CraftingDeadSurvival.serverConfig.scoutZombieBackpackEquipChance.get().floatValue(),
        ModItems.MEDIUM_GHILLIE_BACKPACK.get().getDefaultInstance(),
        this.createBackpackLootId("scout_zombie_backpack_loot"));
  }

  @Override
  public void applyEquipmentDropChances() {
    var zombie = extension.entity();
    extension.setEquipmentDropChance(Equipment.Slot.CLOTHING,
        CraftingDeadSurvival.serverConfig.scoutZombieClothingDropChance.get().floatValue());
    extension.setEquipmentDropChance(Equipment.Slot.HAT,
        CraftingDeadSurvival.serverConfig.scoutZombieHatDropChance.get().floatValue());
    extension.setEquipmentDropChance(Equipment.Slot.VEST,
        CraftingDeadSurvival.serverConfig.scoutZombieVestDropChance.get().floatValue());
    extension.setEquipmentDropChance(Equipment.Slot.BACKPACK,
        CraftingDeadSurvival.serverConfig.scoutZombieBackpackDropChance.get().floatValue());
    zombie.setDropChance(EquipmentSlot.MAINHAND,
        CraftingDeadSurvival.serverConfig.scoutZombieHandDropChance.get().floatValue());
    zombie.setDropChance(EquipmentSlot.OFFHAND,
        CraftingDeadSurvival.serverConfig.scoutZombieHandDropChance.get().floatValue());
  }
}
