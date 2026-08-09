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
import com.craftingdead.core.world.item.gun.Gun;
import com.craftingdead.core.world.item.gun.ammoprovider.RefillableAmmoProvider;
import com.craftingdead.survival.CraftingDeadSurvival;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;

public class PoliceZombieHandler extends ZombieHandler {

  public PoliceZombieHandler(BasicLivingExtension<Zombie> extension) {
    super(extension);
  }

  @Override
  protected ItemStack createHeldItem() {
    var gunStack = ModItems.G18.get().getDefaultInstance();
    gunStack.getCapability(Gun.CAPABILITY).ifPresent(gun -> gun.setAmmoProvider(
        new RefillableAmmoProvider(ModItems.G18_MAGAZINE.get().getDefaultInstance(), 2, false)));
    return gunStack;
  }

  @Override
  protected ItemStack createClothingItem() {
    return ModItems.POLICE_CLOTHING.get().getDefaultInstance();
  }

  @Override
  protected ItemStack createHatItem() {
    return ItemStack.EMPTY;
  }

  @Override
  public void applyEquipmentDropChances() {
    var zombie = extension.entity();
    extension.setEquipmentDropChance(Equipment.Slot.CLOTHING,
        CraftingDeadSurvival.serverConfig.policeZombieClothingDropChance.get().floatValue());
    extension.setEquipmentDropChance(Equipment.Slot.HAT,
        CraftingDeadSurvival.serverConfig.policeZombieHatDropChance.get().floatValue());
    extension.setEquipmentDropChance(Equipment.Slot.VEST,
        CraftingDeadSurvival.serverConfig.policeZombieVestDropChance.get().floatValue());
    extension.setEquipmentDropChance(Equipment.Slot.BACKPACK,
        CraftingDeadSurvival.serverConfig.policeZombieBackpackDropChance.get().floatValue());
    zombie.setDropChance(EquipmentSlot.MAINHAND,
        CraftingDeadSurvival.serverConfig.policeZombieHandDropChance.get().floatValue());
    zombie.setDropChance(EquipmentSlot.OFFHAND,
        CraftingDeadSurvival.serverConfig.policeZombieHandDropChance.get().floatValue());
  }
}
