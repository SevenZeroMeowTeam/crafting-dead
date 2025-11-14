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

public class SniperZombieHandler extends ZombieHandler {

  public SniperZombieHandler(BasicLivingExtension<Zombie> extension) {
    super(extension);
  }

  @Override
  protected ItemStack createHeldItem() {
    var random = new Random();
    var gunStack = ModItems.DMR.get().getDefaultInstance();
    gunStack.getCapability(Gun.CAPABILITY).ifPresent(gun -> {
      var magazineStack = ModItems.DMR_MAGAZINE.get().getDefaultInstance();
      gun.setAmmoProvider(new RefillableAmmoProvider(magazineStack, 1, false));
      Map<GunCraftSlotType, Supplier<Attachment>> possibleAttachments = Map.of(
          GunCraftSlotType.OVERBARREL_ATTACHMENT, Attachments.HP_SCOPE,
          GunCraftSlotType.UNDERBARREL_ATTACHMENT, Attachments.BIPOD,
          GunCraftSlotType.MUZZLE_ATTACHMENT, Attachments.SUPPRESSOR);
      Map<GunCraftSlotType, Attachment> attachments = new HashMap<>();
      for (var entry : possibleAttachments.entrySet()) {
        if (random.nextBoolean()) {
          attachments.put(entry.getKey(), entry.getValue().get());
        }
      }
      gun.setAttachments(attachments);
    });
    return gunStack;
  }

  @Override
  protected ItemStack createClothingItem() {
    return ModItems.TAC_GHILLIE_CLOTHING.get().getDefaultInstance();
  }

  @Override
  protected ItemStack createHatItem() {
    return ModItems.GHILLIE_HAT.get().getDefaultInstance();
  }

  @Override
  protected ItemStack createVestItem() {
    return this.createFilledVestItem(
        CraftingDeadSurvival.serverConfig.sniperZombieVestEquipChance.get().floatValue(),
        ModItems.GHILLIE_TACTICAL_VEST.get().getDefaultInstance(),
        this.createVestLootId("sniper_zombie_vest_loot"));
  }

  @Override
  public void applyEquipmentDropChances() {
    var zombie = extension.entity();
    extension.setEquipmentDropChance(Equipment.Slot.CLOTHING,
        CraftingDeadSurvival.serverConfig.sniperZombieClothingDropChance.get().floatValue());
    extension.setEquipmentDropChance(Equipment.Slot.HAT,
        CraftingDeadSurvival.serverConfig.sniperZombieHatDropChance.get().floatValue());
    extension.setEquipmentDropChance(Equipment.Slot.VEST,
        CraftingDeadSurvival.serverConfig.sniperZombieVestDropChance.get().floatValue());
    extension.setEquipmentDropChance(Equipment.Slot.BACKPACK,
        CraftingDeadSurvival.serverConfig.sniperZombieBackpackDropChance.get().floatValue());
    zombie.setDropChance(EquipmentSlot.MAINHAND,
        CraftingDeadSurvival.serverConfig.sniperZombieHandDropChance.get().floatValue());
    zombie.setDropChance(EquipmentSlot.OFFHAND,
        CraftingDeadSurvival.serverConfig.sniperZombieHandDropChance.get().floatValue());
  }
}
