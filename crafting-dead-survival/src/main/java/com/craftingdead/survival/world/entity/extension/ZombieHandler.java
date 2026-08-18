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

import com.craftingdead.core.world.item.gun.Gun;
import com.craftingdead.core.world.item.gun.ammoprovider.MagazineAmmoProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.craftingdead.core.world.entity.extension.BasicLivingExtension;
import com.craftingdead.core.world.entity.extension.LivingHandler;
import com.craftingdead.core.world.entity.extension.LivingHandlerType;
import com.craftingdead.core.world.item.equipment.Equipment;
import com.craftingdead.survival.CraftingDeadSurvival;
import com.craftingdead.survival.tags.SurvivalItemTags;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class ZombieHandler implements LivingHandler {

  public static final LivingHandlerType<ZombieHandler> TYPE =
      new LivingHandlerType<>(new ResourceLocation(CraftingDeadSurvival.ID, "zombie"));

  private static final UUID HEALTH_MODIFIER_BABY_UUID =
      UUID.fromString("69d754ea-1ae3-4684-bb69-51a29de92b9a");
  private static final AttributeModifier HEALTH_MODIFIER_BABY =
      new AttributeModifier(HEALTH_MODIFIER_BABY_UUID, "Baby health reduction", -1.5D,
          AttributeModifier.Operation.MULTIPLY_BASE);

  protected final BasicLivingExtension<Zombie> extension;

  private int textureIndex;

  public ZombieHandler(BasicLivingExtension<Zombie> extension) {
    this.extension = extension;
    this.textureIndex = extension.random().nextInt(23);
  }

  public int getTextureIndex() {
    return this.textureIndex;
  }

  public void handleSetBaby(boolean baby) {
    if (this.extension.level().isClientSide()) {
      return;
    }
    var zombie = this.extension.entity();
    if (zombie.getVehicle() instanceof Chicken) {
      zombie.remove(Entity.RemovalReason.DISCARDED);
      return;
    }
    var healthAttribute = zombie.getAttribute(Attributes.MAX_HEALTH);
    healthAttribute.removeModifier(HEALTH_MODIFIER_BABY);
    if (baby) {
      healthAttribute.addTransientModifier(HEALTH_MODIFIER_BABY);
    }
  }

  public void populateDefaultEquipmentSlots(DifficultyInstance difficulty) {
    var zombie = this.extension.entity();
    zombie.setItemSlot(EquipmentSlot.MAINHAND, this.createHeldItem());
    this.extension.setItemInSlot(Equipment.Slot.CLOTHING, this.createClothingItem());
    this.extension.setItemInSlot(Equipment.Slot.HAT, this.createHatItem());
    this.extension.setItemInSlot(Equipment.Slot.BACKPACK, this.createBackpackItem());
    this.extension.setItemInSlot(Equipment.Slot.VEST, this.createVestItem());
  }

  protected ItemStack createHeldItem() {
    return this
        .getRandomItem(SurvivalItemTags.ZOMBIE_HAND_LOOT,
            CraftingDeadSurvival.serverConfig.zombieHandSpawnChance.get().floatValue())
        .map(Item::getDefaultInstance)
        .orElse(ItemStack.EMPTY);
  }

  protected ItemStack createHatItem() {
    return this
        .getRandomItem(SurvivalItemTags.ZOMBIE_HAT_LOOT,
            CraftingDeadSurvival.serverConfig.zombieHatSpawnChance.get().floatValue())
        .map(Item::getDefaultInstance)
        .orElse(ItemStack.EMPTY);
  }

  protected ItemStack createClothingItem() {
    return this
        .getRandomItem(SurvivalItemTags.ZOMBIE_CLOTHING_LOOT,
            CraftingDeadSurvival.serverConfig.zombieClothingSpawnChance.get().floatValue())
        .map(Item::getDefaultInstance)
        .orElse(ItemStack.EMPTY);
  }

  protected ItemStack createBackpackItem() {
    return this
        .getRandomItem(SurvivalItemTags.ZOMBIE_BACKPACK_LOOT,
            1.0F)
        .map(Item::getDefaultInstance)
        .orElse(ItemStack.EMPTY);
  }

  protected ItemStack createVestItem() {
    return this
        .getRandomItem(SurvivalItemTags.ZOMBIE_VEST_LOOT,
            1.0F)
        .map(Item::getDefaultInstance)
        .orElse(ItemStack.EMPTY);
  }

  @SuppressWarnings("deprecation")
  protected Optional<Item> getRandomItem(TagKey<Item> tagKey, float probability) {
    var random = this.extension.random();
    return random.nextFloat() < probability
        ? Registry.ITEM.getTag(tagKey)
            .flatMap(tag -> tag.getRandomElement(random))
            .map(Holder::value)
        : Optional.empty();
  }

  protected ItemStack getRandomItem(List<Item> items) {
    if (items == null || items.isEmpty()) {
      return ItemStack.EMPTY;
    }
    return new ItemStack(items.get(this.extension.random().nextInt(items.size())));
  }

  protected ItemStack createFilledVestItem(float equipChance, ItemStack vestStack, ResourceLocation location) {
    if (this.extension.random().nextFloat() >= equipChance) {
      return ItemStack.EMPTY;
    }
    var level = (ServerLevel) this.extension.level();
    var lootTable = level.getServer().getLootTables().get(location);
    if (lootTable == LootTable.EMPTY) {
      return vestStack;
    }
    var lootParams = new LootContext.Builder(level)
        .withParameter(LootContextParams.ORIGIN,
            this.extension.entity().position())
        .create(LootContextParamSets.CHEST);
    List<ItemStack> loot = lootTable.getRandomItems(lootParams);
    vestStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(inv -> {
      List<Integer> slots = new ArrayList<>();
      for (int i = 0; i < inv.getSlots(); i++) {
        slots.add(i);
      }
      Collections.shuffle(slots, new java.util.Random(this.extension.random().nextLong()));
      for (int i = 0; i < loot.size() && i < slots.size(); i++) {
        inv.insertItem(slots.get(i), loot.get(i), false);
      }
    });
    return vestStack;
  }

  protected ItemStack createFilledBackpackItem(float equipChance, ItemStack backpackStack, ResourceLocation location) {
    if (this.extension.random().nextFloat() >= equipChance) {
      return ItemStack.EMPTY;
    }
    var level = (ServerLevel) this.extension.level();
    var lootTable = level.getServer().getLootTables().get(location);
    if (lootTable == LootTable.EMPTY) {
      return backpackStack;
    }
    var lootParams = new LootContext.Builder(level)
        .withParameter(LootContextParams.ORIGIN,
            this.extension.entity().position())
        .create(LootContextParamSets.CHEST);
    List<ItemStack> loot = lootTable.getRandomItems(lootParams);
    backpackStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(inv -> {
      List<Integer> slots = new ArrayList<>();
      for (int i = 0; i < inv.getSlots(); i++) {
        slots.add(i);
      }
      Collections.shuffle(slots, new java.util.Random(this.extension.random().nextLong()));
      for (int i = 0; i < loot.size() && i < slots.size(); i++) {
        inv.insertItem(slots.get(i), loot.get(i), false);
      }
    });
    return backpackStack;
  }

  public void applyEquipmentDropChances() {
    var zombie = extension.entity();
    extension.setEquipmentDropChance(Equipment.Slot.CLOTHING,
        CraftingDeadSurvival.serverConfig.zombieClothingDropChance.get().floatValue());
    extension.setEquipmentDropChance(Equipment.Slot.HAT,
        CraftingDeadSurvival.serverConfig.zombieHatDropChance.get().floatValue());
    extension.setEquipmentDropChance(Equipment.Slot.VEST,
        CraftingDeadSurvival.serverConfig.zombieVestDropChance.get().floatValue());
    extension.setEquipmentDropChance(Equipment.Slot.BACKPACK,
        CraftingDeadSurvival.serverConfig.zombieBackpackDropChance.get().floatValue());
    zombie.setDropChance(EquipmentSlot.MAINHAND,
        CraftingDeadSurvival.serverConfig.zombieHandDropChance.get().floatValue());
    zombie.setDropChance(EquipmentSlot.OFFHAND,
        CraftingDeadSurvival.serverConfig.zombieHandDropChance.get().floatValue());
  }

  protected ResourceLocation createVestLootId(String id) {
    return new ResourceLocation(CraftingDeadSurvival.ID, "vests/" + id);
  }

  protected ResourceLocation createBackpackLootId(String id) {
    return new ResourceLocation(CraftingDeadSurvival.ID, "backpacks/" + id);
  }

  @Override
  public boolean handleDeathLoot(DamageSource cause, Collection<ItemEntity> loot, int lootingLevel) {
    // This override ensures that if a zombie drops a gun, it will not have infinite ammo.
    // The dropped Gun will spawn with its #defaultMagazineStack
    loot.stream()
        .map(ItemEntity::getItem)
        .forEach(item -> item.getCapability(Gun.CAPABILITY).ifPresent(gun ->
            gun.setAmmoProvider(new MagazineAmmoProvider(gun.getDefaultMagazineStack()))));
    return false;
  }

  @Override
  public void encode(FriendlyByteBuf out, boolean writeAll) {
    out.writeVarInt(this.textureIndex);
  }

  @Override
  public void decode(FriendlyByteBuf in) {
    this.textureIndex = in.readVarInt();
  }

  @Override
  public boolean requiresSync() {
    return false;
  }

  @Override
  public CompoundTag serializeNBT() {
    var tag = new CompoundTag();
    tag.putInt("textureIndex", this.textureIndex);
    return tag;
  }

  @Override
  public void deserializeNBT(CompoundTag tag) {
    this.textureIndex = tag.getInt("textureIndex");
  }
}
