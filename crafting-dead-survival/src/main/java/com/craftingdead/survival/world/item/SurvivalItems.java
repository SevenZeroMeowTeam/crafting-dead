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

package com.craftingdead.survival.world.item;

import com.craftingdead.core.util.FunctionalUtil;
import com.craftingdead.core.world.item.ActionItem;
import com.craftingdead.core.world.item.ArbitraryTooltips;
import com.craftingdead.core.world.item.GrenadeItem;
import com.craftingdead.core.world.item.MeleeWeaponItem;
import com.craftingdead.core.world.item.ToolItem;
import com.craftingdead.survival.CraftingDeadSurvival;
import com.craftingdead.survival.world.action.SurvivalActionTypes;
import com.craftingdead.survival.world.entity.SurvivalEntityTypes;
import com.craftingdead.survival.world.entity.grenade.PipeBomb;
import com.craftingdead.survival.world.item.ConsumableItem.Type;
import com.craftingdead.survival.world.level.block.SurvivalBlocks;
import com.craftingdead.survival.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SurvivalItems {

  public static final DeferredRegister<Item> deferredRegister =
      DeferredRegister.create(ForgeRegistries.ITEMS, CraftingDeadSurvival.ID);

  public static final CreativeModeTab TAB = new CreativeModeTab(CraftingDeadSurvival.ID) {

    @Override
    public ItemStack makeIcon() {
      return new ItemStack(RBI_SYRINGE.get());
    }
  };

  // ================================================================================
  // Loot
  // ================================================================================

  public static final RegistryObject<Item> MILITARY_LOOT_ITEM =
      deferredRegister.register("military_loot",
          () -> new BlockItem(SurvivalBlocks.MILITARY_LOOT.get(), new Item.Properties()
              .rarity(Rarity.EPIC)
              .tab(TAB)));

  public static final RegistryObject<Item> MEDIC_LOOT_ITEM =
      deferredRegister.register("medic_loot",
          () -> new BlockItem(SurvivalBlocks.MEDICAL_LOOT.get(), new Item.Properties()
              .rarity(Rarity.EPIC)
              .tab(TAB)));

  public static final RegistryObject<Item> CIVILIAN_LOOT_ITEM =
      deferredRegister.register("civilian_loot",
          () -> new BlockItem(SurvivalBlocks.CIVILIAN_LOOT.get(), new Item.Properties()
              .rarity(Rarity.EPIC)
              .tab(TAB)));

  public static final RegistryObject<Item> CIVILIAN_RARE_LOOT_ITEM =
      deferredRegister.register("civilian_rare_loot",
          () -> new BlockItem(SurvivalBlocks.RARE_CIVILIAN_LOOT.get(), new Item.Properties()
              .rarity(Rarity.EPIC)
              .tab(TAB)));

  public static final RegistryObject<Item> POLICE_LOOT_ITEM =
      deferredRegister.register("police_loot",
          () -> new BlockItem(SurvivalBlocks.POLICE_LOOT.get(), new Item.Properties()
              .rarity(Rarity.EPIC)
              .tab(TAB)));

  public static final RegistryObject<Item> MILITARY_LOOT_GEN_ITEM =
      deferredRegister.register("military_loot_gen",
          () -> new BlockItem(SurvivalBlocks.MILITARY_LOOT_GENERATOR.get(), new Item.Properties()
              .tab(TAB)));

  public static final RegistryObject<Item> MEDIC_LOOT_GEN_ITEM =
      deferredRegister.register("medic_loot_gen",
          () -> new BlockItem(SurvivalBlocks.MEDICAL_LOOT_GENERATOR.get(), new Item.Properties()
              .tab(TAB)));

  public static final RegistryObject<Item> CIVILIAN_LOOT_GEN_ITEM =
      deferredRegister.register("civilian_loot_gen",
          () -> new BlockItem(SurvivalBlocks.CIVILIAN_LOOT_GENERATOR.get(), new Item.Properties()
              .tab(TAB)));

  public static final RegistryObject<Item> CIVILIAN_RARE_LOOT_GEN_ITEM =
      deferredRegister.register("civilian_rare_loot_gen",
          () -> new BlockItem(SurvivalBlocks.RARE_CIVILIAN_LOOT_GENERATOR.get(),
              new Item.Properties()
                  .tab(TAB)));

  public static final RegistryObject<Item> POLICE_LOOT_GEN_ITEM =
      deferredRegister.register("police_loot_gen",
          () -> new BlockItem(SurvivalBlocks.POLICE_LOOT_GENERATOR.get(), new Item.Properties()
              .tab(TAB)));

  // ================================================================================
  // Supply Drop Radio
  // ================================================================================

  public static final RegistryObject<Item> MEDICAL_DROP_RADIO =
      deferredRegister.register("medical_drop_radio",
          () -> new SupplyDropRadioItem(
              (SupplyDropRadioItem.Properties) new SupplyDropRadioItem.Properties()
                  .setLootTable(BuiltInLootTables.MEDICAL_SUPPLY_DROP)
                  .stacksTo(1)
                  .tab(TAB)));

  public static final RegistryObject<Item> MILITARY_DROP_RADIO =
      deferredRegister.register("military_drop_radio",
          () -> new SupplyDropRadioItem(
              (SupplyDropRadioItem.Properties) new SupplyDropRadioItem.Properties()
                  .setLootTable(BuiltInLootTables.MILITARY_SUPPLY_DROP)
                  .stacksTo(1)
                  .tab(TAB)));

  // ================================================================================
  // Virus
  // ================================================================================

  public static final RegistryObject<GrenadeItem> PIPE_BOMB = deferredRegister.register("pipe_bomb",
      () -> new GrenadeItem((GrenadeItem.Properties) new GrenadeItem.Properties()
          .setGrenadeEntitySupplier(FunctionalUtil.nullsafeFunction(PipeBomb::new, PipeBomb::new))
          .setEnabledSupplier(CraftingDeadSurvival.serverConfig.pipeBombEnabled)
          .stacksTo(3)
          .tab(TAB)));

  public static final RegistryObject<Item> SPLINT = deferredRegister.register("splint",
      () -> new ActionItem(SurvivalActionTypes.USE_SPLINT, new Item.Properties()
          .stacksTo(3)
          .tab(TAB)));

  public static final RegistryObject<Item> MORPHINE_SYRINGE = deferredRegister.register("morphine_syringe",
      () -> new ActionItem(SurvivalActionTypes.USE_MORPHINE_SYRINGE, new Item.Properties()
          .stacksTo(3)
          .tab(TAB)));

  public static final RegistryObject<Item> RBI_SYRINGE = deferredRegister.register("rbi_syringe",
      () -> new ActionItem(SurvivalActionTypes.USE_RBI_SYRINGE, new ActionItem.Properties()
          .stacksTo(3)
          .tab(TAB)));

  public static final RegistryObject<Item> CURE_SYRINGE = deferredRegister.register("cure_syringe",
      () -> new ActionItem(SurvivalActionTypes.USE_CURE_SYRINGE, new ActionItem.Properties()
          .stacksTo(3)
          .tab(TAB)));

  // ================================================================================
  // Spawn Eggs
  // ================================================================================

  public static final RegistryObject<Item> GIANT_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("giant_zombie_spawn_egg",
          () -> new ForgeSpawnEggItem(SurvivalEntityTypes.GIANT_ZOMBIE, 0x000000, 0xFFFFFF,
              new Item.Properties().tab(TAB)));

  public static final RegistryObject<Item> FAST_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("fast_zombie_spawn_egg",
          () -> new ForgeSpawnEggItem(SurvivalEntityTypes.FAST_ZOMBIE, 0x000000, 0xFFFFFF,
              new Item.Properties().tab(TAB)));

  public static final RegistryObject<Item> TANK_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("tank_zombie_spawn_egg",
          () -> new ForgeSpawnEggItem(SurvivalEntityTypes.TANK_ZOMBIE, 0x000000, 0xFFFFFF,
              new Item.Properties().tab(TAB)));

  public static final RegistryObject<Item> WEAK_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("weak_zombie_spawn_egg",
          () -> new ForgeSpawnEggItem(SurvivalEntityTypes.WEAK_ZOMBIE, 0x000000, 0xFFFFFF,
              new Item.Properties().tab(TAB)));

  public static final RegistryObject<Item> POLICE_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("police_zombie_spawn_egg",
          () -> new ForgeSpawnEggItem(SurvivalEntityTypes.POLICE_ZOMBIE, 0x000000, 0xFFFFFF,
              new Item.Properties().tab(TAB)));

  public static final RegistryObject<Item> DOCTOR_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("doctor_zombie_spawn_egg",
          () -> new ForgeSpawnEggItem(SurvivalEntityTypes.DOCTOR_ZOMBIE, 0x000000, 0xFFFFFF,
              new Item.Properties().tab(TAB)));

  public static final RegistryObject<Item> SCOUT_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("scout_zombie_spawn_egg",
          () -> new ForgeSpawnEggItem(SurvivalEntityTypes.SCOUT_ZOMBIE, 0x000000, 0xFFFFFF,
              new Item.Properties().tab(TAB)));

  public static final RegistryObject<Item> SNIPER_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("sniper_zombie_spawn_egg",
          () -> new ForgeSpawnEggItem(SurvivalEntityTypes.SNIPER_ZOMBIE, 0x000000, 0xFFFFFF,
              new Item.Properties().tab(TAB)));

  public static final RegistryObject<Item> PILOT_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("pilot_zombie_spawn_egg",
          () -> new ForgeSpawnEggItem(SurvivalEntityTypes.PILOT_ZOMBIE, 0x000000, 0xFFFFFF,
              new Item.Properties().tab(TAB)));

  public static final RegistryObject<Item> SOLDIER_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("soldier_zombie_spawn_egg",
          () -> new ForgeSpawnEggItem(SurvivalEntityTypes.SOLDIER_ZOMBIE, 0x000000, 0xFFFFFF,
              new Item.Properties().tab(TAB)));

  public static final RegistryObject<Item> NINJA_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("ninja_zombie_spawn_egg",
          () -> new ForgeSpawnEggItem(SurvivalEntityTypes.NINJA_ZOMBIE, 0x000000, 0xFFFFFF,
              new Item.Properties().tab(TAB)));

  public static final RegistryObject<Item> ALFA_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("alfa_zombie_spawn_egg",
          () -> new ForgeSpawnEggItem(SurvivalEntityTypes.ALFA_ZOMBIE, 0x000000, 0xFFFFFF,
              new Item.Properties().tab(TAB)));

  public static final RegistryObject<Item> BOUNTY_HUNTER_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("bounty_hunter_zombie_spawn_egg",
          () -> new ForgeSpawnEggItem(SurvivalEntityTypes.BOUNTY_HUNTER_ZOMBIE, 0x000000, 0xFFFFFF,
              new Item.Properties().tab(TAB)));

  public static final RegistryObject<Item> DESERT_RAIDER_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("desert_raider_zombie_spawn_egg",
          () -> new ForgeSpawnEggItem(SurvivalEntityTypes.DESERT_RAIDER_ZOMBIE, 0x000000, 0xFFFFFF,
              new Item.Properties().tab(TAB)));

  public static final RegistryObject<Item> FIREFIGHTER_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("firefighter_zombie_spawn_egg",
          () -> new ForgeSpawnEggItem(SurvivalEntityTypes.FIREFIGHTER_ZOMBIE, 0x000000, 0xFFFFFF,
              new Item.Properties().tab(TAB)));

  public static final RegistryObject<Item> HAZMAT_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("hazmat_zombie_spawn_egg",
          () -> new ForgeSpawnEggItem(SurvivalEntityTypes.HAZMAT_ZOMBIE, 0x000000, 0xFFFFFF,
              new Item.Properties().tab(TAB)));

  public static final RegistryObject<Item> JUGGERNAUT_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("juggernaut_zombie_spawn_egg",
          () -> new ForgeSpawnEggItem(SurvivalEntityTypes.JUGGERNAUT_ZOMBIE, 0x000000, 0xFFFFFF,
              new Item.Properties().tab(TAB)));

  public static final RegistryObject<Item> MINER_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("miner_zombie_spawn_egg",
          () -> new ForgeSpawnEggItem(SurvivalEntityTypes.MINER_ZOMBIE, 0x000000, 0xFFFFFF,
              new Item.Properties().tab(TAB)));

  public static final RegistryObject<Item> SWAT_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("swat_zombie_spawn_egg",
          () -> new ForgeSpawnEggItem(SurvivalEntityTypes.SWAT_ZOMBIE, 0x000000, 0xFFFFFF,
              new Item.Properties().tab(TAB)));

  // ================================================================================
  // Consumable Items
  // ================================================================================

  public static final RegistryObject<Item> EMPTY_WATER_BOTTLE =
      deferredRegister.register("empty_water_bottle",
          () -> new MultiActionItem(new Item.Properties().tab(TAB).stacksTo(3),
              SurvivalActionTypes.FILL_WATER_BOTTLE_FROM_PUMP,
              SurvivalActionTypes.FILL_WATER_BOTTLE));

  public static final RegistryObject<Item> WATER_BOTTLE =
      deferredRegister.register("water_bottle",
          () -> new ConsumableItem(new Properties().tab(TAB).stacksTo(3), 0, 0, 8,
              EMPTY_WATER_BOTTLE, Type.ONLY_DRINK));

  public static final RegistryObject<Item> EMPTY_WATER_CANTEEN =
      deferredRegister.register("empty_water_canteen",
          () -> new MultiActionItem(new Item.Properties().tab(TAB).stacksTo(3),
              SurvivalActionTypes.FILL_WATER_CANTEEN_FROM_PUMP,
              SurvivalActionTypes.FILL_WATER_CANTEEN));

  public static final RegistryObject<Item> WATER_CANTEEN =
      deferredRegister.register("water_canteen",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 0, 0, 9,
              EMPTY_WATER_CANTEEN, Type.ONLY_DRINK));

  public static final RegistryObject<Item> EMPTY_FLASK =
      deferredRegister.register("empty_flask",
          () -> new MultiActionItem(new Item.Properties().tab(TAB).stacksTo(3),
              SurvivalActionTypes.FILL_FLASK_FROM_PUMP,
              SurvivalActionTypes.FILL_FLASK));

  public static final RegistryObject<Item> FLASK =
      deferredRegister.register("flask",
          () -> new ConsumableItem(new Properties().tab(TAB).stacksTo(3), 0, 0, 7, EMPTY_FLASK,
              Type.ONLY_DRINK));

  public static final RegistryObject<Item> EMPTY_ICED_TEA =
      deferredRegister.register("empty_iced_tea",
          () -> new Item(new Properties().tab(TAB).stacksTo(3)));

  public static final RegistryObject<Item> ICED_TEA =
      deferredRegister.register("iced_tea",
          () -> new ConsumableItem(new Properties().tab(TAB).stacksTo(3), 0, 0, 4, EMPTY_ICED_TEA,
              Type.ONLY_DRINK));

  public static final RegistryObject<Item> EMPTY_IRON_BREW =
      deferredRegister.register("empty_iron_brew",
          () -> new Item(new Properties().tab(TAB).stacksTo(3)));

  public static final RegistryObject<Item> IRON_BREW =
      deferredRegister.register("iron_brew",
          () -> new ConsumableItem(new Properties().tab(TAB).stacksTo(3), 0, 0, 4, EMPTY_IRON_BREW,
              Type.ONLY_DRINK));

  public static final RegistryObject<Item> EMPTY_JUICE_POUCH =
      deferredRegister.register("empty_juice_pouch",
          () -> new Item(new Properties().tab(TAB).stacksTo(3)));

  public static final RegistryObject<Item> JUICE_POUCH =
      deferredRegister.register("juice_pouch",
          () -> new ConsumableItem(new Properties().tab(TAB).stacksTo(3), 0, 0, 4,
              EMPTY_JUICE_POUCH, Type.ONLY_DRINK));

  public static final RegistryObject<Item> EMPTY_LEMON_SODA =
      deferredRegister.register("empty_lemon_soda",
          () -> new Item(new Properties().tab(TAB).stacksTo(3)));

  public static final RegistryObject<Item> LEMON_SODA =
      deferredRegister.register("lemon_soda",
          () -> new ConsumableItem(new Properties().tab(TAB).stacksTo(3), 0, 0, 4, EMPTY_LEMON_SODA,
              Type.ONLY_DRINK));

  public static final RegistryObject<Item> EMPTY_MILK_CARTON =
      deferredRegister.register("empty_milk_carton",
          () -> new Item(new Properties().tab(TAB).stacksTo(3)));

  public static final RegistryObject<Item> MILK_CARTON =
      deferredRegister.register("milk_carton",
          () -> new ConsumableItem(new Properties().tab(TAB).stacksTo(3), 0, 0, 6,
              EMPTY_MILK_CARTON, Type.ONLY_DRINK));

  public static final RegistryObject<Item> EMPTY_ORANGE_SODA =
      deferredRegister.register("empty_orange_soda",
          () -> new Item(new Properties().tab(TAB).stacksTo(3)));

  public static final RegistryObject<Item> ORANGE_SODA =
      deferredRegister.register("orange_soda",
          () -> new ConsumableItem(new Properties().tab(TAB).stacksTo(3), 0, 0, 4,
              EMPTY_ORANGE_SODA, Type.ONLY_DRINK));

  public static final RegistryObject<Item> EMPTY_PEPE_SODA =
      deferredRegister.register("empty_pepe_soda",
          () -> new Item(new Properties().tab(TAB).stacksTo(3)));

  public static final RegistryObject<Item> PEPE_SODA =
      deferredRegister.register("pepe_soda",
          () -> new ConsumableItem(new Properties().tab(TAB).stacksTo(3), 0, 0, 4, EMPTY_PEPE_SODA,
              Type.ONLY_DRINK));

  public static final RegistryObject<Item> EMPTY_SPRITE =
      deferredRegister.register("empty_sprite",
          () -> new Item(new Properties().tab(TAB).stacksTo(3)));

  public static final RegistryObject<Item> SPRITE =
      deferredRegister.register("sprite",
          () -> new ConsumableItem(new Properties().tab(TAB).stacksTo(3), 0, 0, 6, EMPTY_SPRITE,
              Type.ONLY_DRINK));

  public static final RegistryObject<Item> EMPTY_COLA =
      deferredRegister.register("empty_cola",
          () -> new Item(new Properties().tab(TAB).stacksTo(3)));

  public static final RegistryObject<Item> COLA =
      deferredRegister.register("cola",
          () -> new ConsumableItem(new Properties().tab(TAB).stacksTo(3), 0, 0, 6, EMPTY_COLA,
              Type.ONLY_DRINK));

  public static final RegistryObject<Item> EMPTY_ZOMBIE_ENERGY =
      deferredRegister.register("empty_zombie_energy",
          () -> new Item(new Properties().tab(TAB).stacksTo(3)));

  public static final RegistryObject<Item> ZOMBIE_ENERGY =
      deferredRegister.register("zombie_energy",
          () -> new ConsumableItem(new Properties().tab(TAB).stacksTo(3), 0, 0, 4,
              EMPTY_ZOMBIE_ENERGY, Type.ONLY_DRINK));

  public static final RegistryObject<Item> POWER_BAR =
      deferredRegister.register("power_bar",
          () -> new ConsumableItem(new Properties().tab(TAB).stacksTo(3), 4, 0.3F, 0, null,
              Type.ONLY_FOOD));

  public static final RegistryObject<Item> CANDY_BAR =
      deferredRegister.register("candy_bar",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 6, 0.3F, 0, null,
              Type.ONLY_FOOD));

  public static final RegistryObject<Item> CEREAL =
      deferredRegister.register("cereal",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 10, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final RegistryObject<Item> CANNED_SWEETCORN =
      deferredRegister.register("canned_sweetcorn",
          () -> new Item(new Item.Properties().tab(TAB).stacksTo(3)));

  public static final RegistryObject<Item> OPEN_CANNED_SWEETCORN =
      deferredRegister.register("open_canned_sweetcorn",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 6, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final RegistryObject<Item> CANNED_BEANS =
      deferredRegister.register("canned_beans",
          () -> new Item(new Item.Properties().tab(TAB).stacksTo(3)));

  public static final RegistryObject<Item> OPEN_CANNED_BEANS =
      deferredRegister.register("open_canned_beans",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 8, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final RegistryObject<Item> CANNED_TUNA =
      deferredRegister.register("canned_tuna",
          () -> new Item(new Item.Properties().tab(TAB).stacksTo(3)));

  public static final RegistryObject<Item> OPEN_CANNED_TUNA =
      deferredRegister.register("open_canned_tuna",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 6, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final RegistryObject<Item> CANNED_PEACHES =
      deferredRegister.register("canned_peaches",
          () -> new Item(new Item.Properties().tab(TAB).stacksTo(3)));

  public static final RegistryObject<Item> OPEN_CANNED_PEACHES =
      deferredRegister.register("open_canned_peaches",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 6, 0.6F, 1, null,
              Type.FOOD_AND_DRINK));

  public static final RegistryObject<Item> CANNED_PASTA =
      deferredRegister.register("canned_pasta",
          () -> new Item(new Item.Properties().tab(TAB).stacksTo(3)));

  public static final RegistryObject<Item> OPEN_CANNED_PASTA =
      deferredRegister.register("open_canned_pasta",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 6, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final RegistryObject<Item> CANNED_CORNED_BEEF =
      deferredRegister.register("canned_corned_beef",
          () -> new Item(new Item.Properties().tab(TAB).stacksTo(3)));

  public static final RegistryObject<Item> OPEN_CANNED_CORNED_BEEF =
      deferredRegister.register("open_canned_corned_beef",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 8, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final RegistryObject<Item> CANNED_CUSTARD =
      deferredRegister.register("canned_custard",
          () -> new Item(new Item.Properties().tab(TAB).stacksTo(3)));

  public static final RegistryObject<Item> OPEN_CANNED_CUSTARD =
      deferredRegister.register("open_canned_custard",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 4, 0.6F, 3, null,
              Type.FOOD_AND_DRINK));

  public static final RegistryObject<Item> CANNED_PICKLES =
      deferredRegister.register("canned_pickles",
          () -> new Item(new Item.Properties().tab(TAB).stacksTo(3)));

  public static final RegistryObject<Item> OPEN_CANNED_PICKLES =
      deferredRegister.register("open_canned_pickles",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 4, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final RegistryObject<Item> CANNED_DOG_FOOD =
      deferredRegister.register("canned_dog_food",
          () -> new Item(new Item.Properties().tab(TAB).stacksTo(3)));

  public static final RegistryObject<Item> OPEN_CANNED_DOG_FOOD =
      deferredRegister.register("open_canned_dog_food",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 2, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final RegistryObject<Item> CANNED_TOMATO_SOUP =
      deferredRegister.register("canned_tomato_soup",
          () -> new Item(new Item.Properties().tab(TAB).stacksTo(3)));

  public static final RegistryObject<Item> OPEN_CANNED_TOMATO_SOUP =
      deferredRegister.register("open_canned_tomato_soup",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 4, 0.6F, 2, null,
              Type.FOOD_AND_DRINK));

  public static final RegistryObject<Item> MRE =
      deferredRegister.register("mre",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 16, 0.6F, 7, null,
              Type.FOOD_AND_DRINK));

  public static final RegistryObject<Item> ORANGE =
      deferredRegister.register("orange",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 5, 0.6F, 2, null,
              Type.FOOD_AND_DRINK));

  public static final RegistryObject<Item> ROTTEN_ORANGE =
      deferredRegister.register("rotten_orange",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 2, 0.6F, 1, null,
              Type.FOOD_AND_DRINK));

  public static final RegistryObject<Item> PEAR =
      deferredRegister.register("pear",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 4, 0.6F, 2, null,
              Type.FOOD_AND_DRINK));

  public static final RegistryObject<Item> ROTTEN_PEAR =
      deferredRegister.register("rotten_pear",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 1, 0.6F, 1, null,
              Type.FOOD_AND_DRINK));

  public static final RegistryObject<Item> RICE_BAG =
      deferredRegister.register("rice_bag",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 8, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final RegistryObject<Item> ROTTEN_APPLE =
      deferredRegister.register("rotten_apple",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 1, 0.6F, 1, null,
              Type.FOOD_AND_DRINK));

  public static final RegistryObject<Item> NOODLES =
      deferredRegister.register("noodles",
          () -> new ConsumableItem(new Properties().tab(TAB).stacksTo(3), 6, 0.6F, 3, null,
              Type.FOOD_AND_DRINK));

  public static final RegistryObject<Item> ROTTEN_MELON_SLICE =
      deferredRegister.register("rotten_melon_slice",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 3, 0.6F, 1, null,
              Type.FOOD_AND_DRINK));

  public static final RegistryObject<Item> BLUEBERRY =
      deferredRegister.register("blueberry",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 4, 0.6F, 3, null,
              Type.FOOD_AND_DRINK));

  public static final RegistryObject<Item> ROTTEN_BLUEBERRY =
      deferredRegister.register("rotten_blueberry",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 1, 0.6F, 1, null,
              Type.FOOD_AND_DRINK));

  public static final RegistryObject<Item> RASPBERRY =
      deferredRegister.register("raspberry",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 3, 0.6F, 3, null,
              Type.FOOD_AND_DRINK));

  public static final RegistryObject<Item> ROTTEN_RASPBERRY =
      deferredRegister.register("rotten_raspberry",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 1, 0.6F, 1, null,
              Type.FOOD_AND_DRINK));

  public static final RegistryObject<Item> CHIPS =
      deferredRegister.register("chips",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 3, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final RegistryObject<Item> RANCH_CHIPS =
      deferredRegister.register("ranch_chips",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 3, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final RegistryObject<Item> CHEESY_CHIPS =
      deferredRegister.register("cheesy_chips",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 3, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final RegistryObject<Item> SALTED_CHIPS =
      deferredRegister.register("salted_chips",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 3, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final RegistryObject<Item> POPCORN =
      deferredRegister.register("popcorn",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 3, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final RegistryObject<Item> NUTTY_CEREAL =
      deferredRegister.register("nutty_cereal",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 10, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final RegistryObject<Item> EMERALD_CEREAL =
      deferredRegister.register("emerald_cereal",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 10, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final RegistryObject<Item> FLAKE_CEREAL =
      deferredRegister.register("flake_cereal",
          () -> new ConsumableItem(new Item.Properties().tab(TAB).stacksTo(3), 10, 0.6F, 0, null,
              Type.ONLY_FOOD));

  // ================================================================================
  // Tools
  // ================================================================================

  public static final RegistryObject<Item> CAN_OPENER =
      deferredRegister.register("can_opener", () -> new ToolItem(
          new Item.Properties().durability(8).tab(TAB)) {

        @Override
        public boolean hasContainerItem(ItemStack stack) {
          return !stack.isEmpty();
        }

        @Override
        public boolean isRepairable(ItemStack stack) {
          return false;
        }

        @Override
        public ItemStack getContainerItem(ItemStack itemStack) {
          if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
          }
          // Increment damage directly and check limits
          var copy = itemStack.copy();
          copy.setDamageValue(copy.getDamageValue() + 1);
          return copy.getDamageValue() >= copy.getMaxDamage() ? ItemStack.EMPTY : copy;
        }
      });

  public static final RegistryObject<Item> SCREWDRIVER =
      deferredRegister.register("screwdriver", () -> new ToolItem(
          new Item.Properties().durability(6).tab(TAB)) {

        @Override
        public boolean hasContainerItem(ItemStack stack) {
          return !stack.isEmpty();
        }

        @Override
        public boolean isRepairable(ItemStack stack) {
          return false;
        }

        @Override
        public ItemStack getContainerItem(ItemStack itemStack) {
          if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
          }
          // Increment damage directly and check limits
          var copy = itemStack.copy();
          copy.setDamageValue(copy.getDamageValue() + 1);
          return copy.getDamageValue() >= copy.getMaxDamage() ? ItemStack.EMPTY : copy;
        }
      });

  public static final RegistryObject<Item> MULTI_TOOL =
      deferredRegister.register("multi_tool", () -> new MeleeWeaponItem(8, -2.4F,
          new Item.Properties().durability(20).tab(TAB)) {

        @Override
        public boolean hasContainerItem(ItemStack stack) {
          return !stack.isEmpty();
        }

        @Override
        public boolean isRepairable(ItemStack stack) {
          return false;
        }

        @Override
        public ItemStack getContainerItem(ItemStack itemStack) {
          if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
          }
          // Increment damage directly and check limits
          var copy = itemStack.copy();
          copy.setDamageValue(copy.getDamageValue() + 1);
          return copy.getDamageValue() >= copy.getMaxDamage() ? ItemStack.EMPTY : copy;
        }
      });

  // ================================================================================
  // Miscellaneous
  // ================================================================================

  public static final RegistryObject<Item> ROPE =
      deferredRegister.register("rope",
          () -> new Item(new Item.Properties()
              .stacksTo(1)
              .tab(TAB)));

  static {
    var cannedFoodTooltip = new TranslatableComponent("canned_food.information")
        .withStyle(ChatFormatting.GRAY);
    var emptyCanteenFlaskTooltip  = new TranslatableComponent("empty_canteen_flask.information")
        .withStyle(ChatFormatting.AQUA);
    
    // Tools
    ArbitraryTooltips.registerTooltip(CAN_OPENER,
        new TranslatableComponent("can_opener.information")
            .withStyle(ChatFormatting.YELLOW));
    ArbitraryTooltips.registerTooltip(SCREWDRIVER,
        new TranslatableComponent("screwdriver.information")
            .withStyle(ChatFormatting.YELLOW));
    ArbitraryTooltips.registerTooltip(MULTI_TOOL,
        new TranslatableComponent("multi_tool.information")
            .withStyle(ChatFormatting.YELLOW));
    ArbitraryTooltips.registerTooltip(ROPE,
        new TranslatableComponent("rope.information")
            .withStyle(ChatFormatting.GRAY));
    
    // Medical items
    ArbitraryTooltips.registerTooltip(SPLINT,
        new TranslatableComponent("splint.information")
            .withStyle(ChatFormatting.GREEN));
    ArbitraryTooltips.registerTooltip(MORPHINE_SYRINGE,
        new TranslatableComponent("morphine_syringe.information")
            .withStyle(ChatFormatting.GREEN));
    ArbitraryTooltips.registerTooltip(RBI_SYRINGE,
        new TranslatableComponent("rbi_syringe.information")
            .withStyle(ChatFormatting.RED));
    ArbitraryTooltips.registerTooltip(CURE_SYRINGE,
        new TranslatableComponent("cure_syringe.information")
            .withStyle(ChatFormatting.GREEN));
    
    // Explosives and radios
    ArbitraryTooltips.registerTooltip(PIPE_BOMB,
        new TranslatableComponent("pipe_bomb.information")
            .withStyle(ChatFormatting.RED));
    ArbitraryTooltips.registerTooltip(MEDICAL_DROP_RADIO,
        new TranslatableComponent("medical_drop_radio.information")
            .withStyle(ChatFormatting.GREEN));
    ArbitraryTooltips.registerTooltip(MILITARY_DROP_RADIO,
        new TranslatableComponent("military_drop_radio.information")
            .withStyle(ChatFormatting.GOLD));
    
    // Loot blocks
    ArbitraryTooltips.registerTooltip(MILITARY_LOOT_ITEM,
        new TranslatableComponent("military_loot.information")
            .withStyle(ChatFormatting.DARK_GREEN));
    ArbitraryTooltips.registerTooltip(MILITARY_LOOT_GEN_ITEM,
        new TranslatableComponent("military_loot_gen.information")
            .withStyle(ChatFormatting.DARK_GRAY));
    ArbitraryTooltips.registerTooltip(POLICE_LOOT_ITEM,
        new TranslatableComponent("police_loot.information")
            .withStyle(ChatFormatting.BLUE));
    ArbitraryTooltips.registerTooltip(POLICE_LOOT_GEN_ITEM,
        new TranslatableComponent("police_loot_gen.information")
            .withStyle(ChatFormatting.DARK_GRAY));
    ArbitraryTooltips.registerTooltip(MEDIC_LOOT_ITEM,
        new TranslatableComponent("medic_loot.information")
            .withStyle(ChatFormatting.LIGHT_PURPLE));
    ArbitraryTooltips.registerTooltip(MEDIC_LOOT_GEN_ITEM,
        new TranslatableComponent("medic_loot_gen.information")
            .withStyle(ChatFormatting.DARK_GRAY));
    ArbitraryTooltips.registerTooltip(CIVILIAN_LOOT_ITEM,
        new TranslatableComponent("civilian_loot.information")
            .withStyle(ChatFormatting.WHITE));
    ArbitraryTooltips.registerTooltip(CIVILIAN_LOOT_GEN_ITEM,
        new TranslatableComponent("civilian_loot_gen.information")
            .withStyle(ChatFormatting.DARK_GRAY));
    ArbitraryTooltips.registerTooltip(CIVILIAN_RARE_LOOT_ITEM,
        new TranslatableComponent("civilian_rare_loot.information")
            .withStyle(ChatFormatting.YELLOW));
    ArbitraryTooltips.registerTooltip(CIVILIAN_RARE_LOOT_GEN_ITEM,
        new TranslatableComponent("civilian_rare_loot_gen.information")
            .withStyle(ChatFormatting.DARK_GRAY));
    
    // Canned foods (need to be opened)
    ArbitraryTooltips.registerTooltip(CANNED_SWEETCORN, cannedFoodTooltip);
    ArbitraryTooltips.registerTooltip(CANNED_BEANS, cannedFoodTooltip);
    ArbitraryTooltips.registerTooltip(CANNED_TUNA, cannedFoodTooltip);
    ArbitraryTooltips.registerTooltip(CANNED_PEACHES, cannedFoodTooltip);
    ArbitraryTooltips.registerTooltip(CANNED_PASTA, cannedFoodTooltip);
    ArbitraryTooltips.registerTooltip(CANNED_CORNED_BEEF, cannedFoodTooltip);
    ArbitraryTooltips.registerTooltip(CANNED_CUSTARD, cannedFoodTooltip);
    ArbitraryTooltips.registerTooltip(CANNED_PICKLES, cannedFoodTooltip);
    ArbitraryTooltips.registerTooltip(CANNED_DOG_FOOD, cannedFoodTooltip);
    ArbitraryTooltips.registerTooltip(CANNED_TOMATO_SOUP, cannedFoodTooltip);
    
    // Empty containers
    ArbitraryTooltips.registerTooltip(EMPTY_WATER_CANTEEN, emptyCanteenFlaskTooltip);
    ArbitraryTooltips.registerTooltip(EMPTY_FLASK, emptyCanteenFlaskTooltip);
    
    // Medical items
    ArbitraryTooltips.registerTooltip(SPLINT,
        new TranslatableComponent("splint.information")
            .withStyle(ChatFormatting.GRAY));
    ArbitraryTooltips.registerTooltip(MORPHINE_SYRINGE,
        new TranslatableComponent("morphine_syringe.information")
            .withStyle(ChatFormatting.GRAY));
    ArbitraryTooltips.registerTooltip(RBI_SYRINGE,
        new TranslatableComponent("rbi_syringe.information")
            .withStyle(ChatFormatting.GRAY));
    ArbitraryTooltips.registerTooltip(CURE_SYRINGE,
        new TranslatableComponent("cure_syringe.information")
            .withStyle(ChatFormatting.GRAY));
    
    // Drinks
    ArbitraryTooltips.registerTooltip(WATER_BOTTLE,
        new TranslatableComponent("water_bottle.information")
            .withStyle(ChatFormatting.AQUA));
    ArbitraryTooltips.registerTooltip(WATER_CANTEEN,
        new TranslatableComponent("water_canteen.information")
            .withStyle(ChatFormatting.AQUA));
    ArbitraryTooltips.registerTooltip(FLASK,
        new TranslatableComponent("flask.information")
            .withStyle(ChatFormatting.AQUA));
    ArbitraryTooltips.registerTooltip(ICED_TEA,
        new TranslatableComponent("iced_tea.information")
            .withStyle(ChatFormatting.AQUA));
    ArbitraryTooltips.registerTooltip(IRON_BREW,
        new TranslatableComponent("iron_brew.information")
            .withStyle(ChatFormatting.AQUA));
    ArbitraryTooltips.registerTooltip(JUICE_POUCH,
        new TranslatableComponent("juice_pouch.information")
            .withStyle(ChatFormatting.AQUA));
    ArbitraryTooltips.registerTooltip(LEMON_SODA,
        new TranslatableComponent("lemon_soda.information")
            .withStyle(ChatFormatting.AQUA));
    ArbitraryTooltips.registerTooltip(MILK_CARTON,
        new TranslatableComponent("milk_carton.information")
            .withStyle(ChatFormatting.AQUA));
    ArbitraryTooltips.registerTooltip(ORANGE_SODA,
        new TranslatableComponent("orange_soda.information")
            .withStyle(ChatFormatting.AQUA));
    ArbitraryTooltips.registerTooltip(PEPE_SODA,
        new TranslatableComponent("pepe_soda.information")
            .withStyle(ChatFormatting.AQUA));
    ArbitraryTooltips.registerTooltip(SPRITE,
        new TranslatableComponent("sprite.information")
            .withStyle(ChatFormatting.AQUA));
    ArbitraryTooltips.registerTooltip(COLA,
        new TranslatableComponent("cola.information")
            .withStyle(ChatFormatting.AQUA));
    ArbitraryTooltips.registerTooltip(ZOMBIE_ENERGY,
        new TranslatableComponent("zombie_energy.information")
            .withStyle(ChatFormatting.AQUA));
    
    // Snacks and packaged food
    ArbitraryTooltips.registerTooltip(POWER_BAR,
        new TranslatableComponent("power_bar.information")
            .withStyle(ChatFormatting.GOLD));
    ArbitraryTooltips.registerTooltip(CANDY_BAR,
        new TranslatableComponent("candy_bar.information")
            .withStyle(ChatFormatting.GOLD));
    ArbitraryTooltips.registerTooltip(CEREAL,
        new TranslatableComponent("cereal.information")
            .withStyle(ChatFormatting.GOLD));
    ArbitraryTooltips.registerTooltip(NUTTY_CEREAL,
        new TranslatableComponent("nutty_cereal.information")
            .withStyle(ChatFormatting.GOLD));
    ArbitraryTooltips.registerTooltip(EMERALD_CEREAL,
        new TranslatableComponent("emerald_cereal.information")
            .withStyle(ChatFormatting.GOLD));
    ArbitraryTooltips.registerTooltip(FLAKE_CEREAL,
        new TranslatableComponent("flake_cereal.information")
            .withStyle(ChatFormatting.GOLD));
    ArbitraryTooltips.registerTooltip(CHIPS,
        new TranslatableComponent("chips.information")
            .withStyle(ChatFormatting.GOLD));
    ArbitraryTooltips.registerTooltip(RANCH_CHIPS,
        new TranslatableComponent("ranch_chips.information")
            .withStyle(ChatFormatting.GOLD));
    ArbitraryTooltips.registerTooltip(CHEESY_CHIPS,
        new TranslatableComponent("cheesy_chips.information")
            .withStyle(ChatFormatting.GOLD));
    ArbitraryTooltips.registerTooltip(SALTED_CHIPS,
        new TranslatableComponent("salted_chips.information")
            .withStyle(ChatFormatting.GOLD));
    ArbitraryTooltips.registerTooltip(POPCORN,
        new TranslatableComponent("popcorn.information")
            .withStyle(ChatFormatting.GOLD));
    ArbitraryTooltips.registerTooltip(RICE_BAG,
        new TranslatableComponent("rice_bag.information")
            .withStyle(ChatFormatting.GOLD));
    ArbitraryTooltips.registerTooltip(NOODLES,
        new TranslatableComponent("noodles.information")
            .withStyle(ChatFormatting.GOLD));
    
    // Fresh and rotten fruits
    ArbitraryTooltips.registerTooltip(ORANGE,
        new TranslatableComponent("orange.information")
            .withStyle(ChatFormatting.GOLD));
    ArbitraryTooltips.registerTooltip(ROTTEN_ORANGE,
        new TranslatableComponent("rotten_orange.information")
            .withStyle(ChatFormatting.DARK_GRAY));
    ArbitraryTooltips.registerTooltip(PEAR,
        new TranslatableComponent("pear.information")
            .withStyle(ChatFormatting.GOLD));
    ArbitraryTooltips.registerTooltip(ROTTEN_PEAR,
        new TranslatableComponent("rotten_pear.information")
            .withStyle(ChatFormatting.DARK_GRAY));
    ArbitraryTooltips.registerTooltip(ROTTEN_APPLE,
        new TranslatableComponent("rotten_apple.information")
            .withStyle(ChatFormatting.DARK_GRAY));
    ArbitraryTooltips.registerTooltip(BLUEBERRY,
        new TranslatableComponent("blueberry.information")
            .withStyle(ChatFormatting.GOLD));
    ArbitraryTooltips.registerTooltip(ROTTEN_BLUEBERRY,
        new TranslatableComponent("rotten_blueberry.information")
            .withStyle(ChatFormatting.DARK_GRAY));
    ArbitraryTooltips.registerTooltip(RASPBERRY,
        new TranslatableComponent("raspberry.information")
            .withStyle(ChatFormatting.GOLD));
    ArbitraryTooltips.registerTooltip(ROTTEN_RASPBERRY,
        new TranslatableComponent("rotten_raspberry.information")
            .withStyle(ChatFormatting.DARK_GRAY));
    ArbitraryTooltips.registerTooltip(ROTTEN_MELON_SLICE,
        new TranslatableComponent("rotten_melon_slice.information")
            .withStyle(ChatFormatting.DARK_GRAY));
    
    // MRE
    ArbitraryTooltips.registerTooltip(MRE,
        new TranslatableComponent("mre.information")
            .withStyle(ChatFormatting.GOLD));
    
    // Opened canned foods
    ArbitraryTooltips.registerTooltip(OPEN_CANNED_SWEETCORN,
        new TranslatableComponent("open_canned_sweetcorn.information")
            .withStyle(ChatFormatting.GOLD));
    ArbitraryTooltips.registerTooltip(OPEN_CANNED_BEANS,
        new TranslatableComponent("open_canned_beans.information")
            .withStyle(ChatFormatting.GOLD));
    ArbitraryTooltips.registerTooltip(OPEN_CANNED_TUNA,
        new TranslatableComponent("open_canned_tuna.information")
            .withStyle(ChatFormatting.GOLD));
    ArbitraryTooltips.registerTooltip(OPEN_CANNED_PEACHES,
        new TranslatableComponent("open_canned_peaches.information")
            .withStyle(ChatFormatting.GOLD));
    ArbitraryTooltips.registerTooltip(OPEN_CANNED_PASTA,
        new TranslatableComponent("open_canned_pasta.information")
            .withStyle(ChatFormatting.GOLD));
    ArbitraryTooltips.registerTooltip(OPEN_CANNED_CORNED_BEEF,
        new TranslatableComponent("open_canned_corned_beef.information")
            .withStyle(ChatFormatting.GOLD));
    ArbitraryTooltips.registerTooltip(OPEN_CANNED_CUSTARD,
        new TranslatableComponent("open_canned_custard.information")
            .withStyle(ChatFormatting.GOLD));
    ArbitraryTooltips.registerTooltip(OPEN_CANNED_PICKLES,
        new TranslatableComponent("open_canned_pickles.information")
            .withStyle(ChatFormatting.GOLD));
    ArbitraryTooltips.registerTooltip(OPEN_CANNED_DOG_FOOD,
        new TranslatableComponent("open_canned_dog_food.information")
            .withStyle(ChatFormatting.DARK_GRAY));
    ArbitraryTooltips.registerTooltip(OPEN_CANNED_TOMATO_SOUP,
        new TranslatableComponent("open_canned_tomato_soup.information")
            .withStyle(ChatFormatting.GOLD));
  }
}
