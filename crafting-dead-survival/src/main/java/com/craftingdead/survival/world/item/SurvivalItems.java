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
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SurvivalItems {

  public static final DeferredRegister<Item> deferredRegister =
      DeferredRegister.create(BuiltInRegistries.ITEM, CraftingDeadSurvival.ID);

  // ================================================================================
  // Loot
  // ================================================================================

  public static final DeferredHolder<Item, ? extends Item> MILITARY_LOOT_ITEM =
      deferredRegister.register("military_loot",
          () -> new BlockItem(SurvivalBlocks.MILITARY_LOOT.get(), new Item.Properties()
              .rarity(Rarity.EPIC)
              ));

  public static final DeferredHolder<Item, ? extends Item> MEDIC_LOOT_ITEM =
      deferredRegister.register("medic_loot",
          () -> new BlockItem(SurvivalBlocks.MEDICAL_LOOT.get(), new Item.Properties()
              .rarity(Rarity.EPIC)
              ));

  public static final DeferredHolder<Item, ? extends Item> CIVILIAN_LOOT_ITEM =
      deferredRegister.register("civilian_loot",
          () -> new BlockItem(SurvivalBlocks.CIVILIAN_LOOT.get(), new Item.Properties()
              .rarity(Rarity.EPIC)
              ));

  public static final DeferredHolder<Item, ? extends Item> CIVILIAN_RARE_LOOT_ITEM =
      deferredRegister.register("civilian_rare_loot",
          () -> new BlockItem(SurvivalBlocks.RARE_CIVILIAN_LOOT.get(), new Item.Properties()
              .rarity(Rarity.EPIC)
              ));

  public static final DeferredHolder<Item, ? extends Item> POLICE_LOOT_ITEM =
      deferredRegister.register("police_loot",
          () -> new BlockItem(SurvivalBlocks.POLICE_LOOT.get(), new Item.Properties()
              .rarity(Rarity.EPIC)
              ));

  public static final DeferredHolder<Item, ? extends Item> MILITARY_LOOT_GEN_ITEM =
      deferredRegister.register("military_loot_gen",
          () -> new BlockItem(SurvivalBlocks.MILITARY_LOOT_GENERATOR.get(), new Item.Properties()
              ));

  public static final DeferredHolder<Item, ? extends Item> MEDIC_LOOT_GEN_ITEM =
      deferredRegister.register("medic_loot_gen",
          () -> new BlockItem(SurvivalBlocks.MEDICAL_LOOT_GENERATOR.get(), new Item.Properties()
              ));

  public static final DeferredHolder<Item, ? extends Item> CIVILIAN_LOOT_GEN_ITEM =
      deferredRegister.register("civilian_loot_gen",
          () -> new BlockItem(SurvivalBlocks.CIVILIAN_LOOT_GENERATOR.get(), new Item.Properties()
              ));

  public static final DeferredHolder<Item, ? extends Item> CIVILIAN_RARE_LOOT_GEN_ITEM =
      deferredRegister.register("civilian_rare_loot_gen",
          () -> new BlockItem(SurvivalBlocks.RARE_CIVILIAN_LOOT_GENERATOR.get(),
              new Item.Properties()
                  ));

  public static final DeferredHolder<Item, ? extends Item> POLICE_LOOT_GEN_ITEM =
      deferredRegister.register("police_loot_gen",
          () -> new BlockItem(SurvivalBlocks.POLICE_LOOT_GENERATOR.get(), new Item.Properties()
              ));

  // ================================================================================
  // Supply Drop Radio
  // ================================================================================

  public static final DeferredHolder<Item, ? extends Item> MEDICAL_DROP_RADIO =
      deferredRegister.register("medical_drop_radio",
          () -> new SupplyDropRadioItem(
              (SupplyDropRadioItem.Properties) new SupplyDropRadioItem.Properties()
                  .setLootTable(BuiltInLootTables.MEDICAL_SUPPLY_DROP)
                  .stacksTo(1)
                  ));

  public static final DeferredHolder<Item, ? extends Item> MILITARY_DROP_RADIO =
      deferredRegister.register("military_drop_radio",
          () -> new SupplyDropRadioItem(
              (SupplyDropRadioItem.Properties) new SupplyDropRadioItem.Properties()
                  .setLootTable(BuiltInLootTables.MILITARY_SUPPLY_DROP)
                  .stacksTo(1)
                  ));

  // ================================================================================
  // Virus
  // ================================================================================

  public static final DeferredHolder<Item, ? extends GrenadeItem> PIPE_BOMB = deferredRegister.register("pipe_bomb",
      () -> new GrenadeItem((GrenadeItem.Properties) new GrenadeItem.Properties()
          .setGrenadeEntitySupplier(FunctionalUtil.nullsafeFunction(PipeBomb::new, PipeBomb::new))
          .setEnabledSupplier(CraftingDeadSurvival.serverConfig.pipeBombEnabled)
          .stacksTo(3)
          ));

  public static final DeferredHolder<Item, ? extends Item> SPLINT = deferredRegister.register("splint",
      () -> new ActionItem(SurvivalActionTypes.USE_SPLINT, new Item.Properties()
          .stacksTo(3)
          ));

  public static final DeferredHolder<Item, ? extends Item> MORPHINE_SYRINGE = deferredRegister.register("morphine_syringe",
      () -> new ActionItem(SurvivalActionTypes.USE_MORPHINE_SYRINGE, new Item.Properties()
          .stacksTo(3)
          ));

  public static final DeferredHolder<Item, ? extends Item> RBI_SYRINGE = deferredRegister.register("rbi_syringe",
      () -> new ActionItem(SurvivalActionTypes.USE_RBI_SYRINGE, new ActionItem.Properties()
          .stacksTo(3)
          ));

  public static final DeferredHolder<Item, ? extends Item> CURE_SYRINGE = deferredRegister.register("cure_syringe",
      () -> new ActionItem(SurvivalActionTypes.USE_CURE_SYRINGE, new ActionItem.Properties()
          .stacksTo(3)
          ));

  // ================================================================================
  // Spawn Eggs
  // ================================================================================

  public static final DeferredHolder<Item, ? extends Item> GIANT_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("giant_zombie_spawn_egg",
          () -> new SpawnEggItem(SurvivalEntityTypes.GIANT_ZOMBIE.get(), 0x000000, 0xFFFFFF,
              new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> FAST_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("fast_zombie_spawn_egg",
          () -> new SpawnEggItem(SurvivalEntityTypes.FAST_ZOMBIE.get(), 0x000000, 0xFFFFFF,
              new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> TANK_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("tank_zombie_spawn_egg",
          () -> new SpawnEggItem(SurvivalEntityTypes.TANK_ZOMBIE.get(), 0x000000, 0xFFFFFF,
              new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> WEAK_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("weak_zombie_spawn_egg",
          () -> new SpawnEggItem(SurvivalEntityTypes.WEAK_ZOMBIE.get(), 0x000000, 0xFFFFFF,
              new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> POLICE_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("police_zombie_spawn_egg",
          () -> new SpawnEggItem(SurvivalEntityTypes.POLICE_ZOMBIE.get(), 0x000000, 0xFFFFFF,
              new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> DOCTOR_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("doctor_zombie_spawn_egg",
          () -> new SpawnEggItem(SurvivalEntityTypes.DOCTOR_ZOMBIE.get(), 0x000000, 0xFFFFFF,
              new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> SCOUT_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("scout_zombie_spawn_egg",
          () -> new SpawnEggItem(SurvivalEntityTypes.SCOUT_ZOMBIE.get(), 0x000000, 0xFFFFFF,
              new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> SNIPER_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("sniper_zombie_spawn_egg",
          () -> new SpawnEggItem(SurvivalEntityTypes.SNIPER_ZOMBIE.get(), 0x000000, 0xFFFFFF,
              new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> PILOT_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("pilot_zombie_spawn_egg",
          () -> new SpawnEggItem(SurvivalEntityTypes.PILOT_ZOMBIE.get(), 0x000000, 0xFFFFFF,
              new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> SOLDIER_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("soldier_zombie_spawn_egg",
          () -> new SpawnEggItem(SurvivalEntityTypes.SOLDIER_ZOMBIE.get(), 0x000000, 0xFFFFFF,
              new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> NINJA_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("ninja_zombie_spawn_egg",
          () -> new SpawnEggItem(SurvivalEntityTypes.NINJA_ZOMBIE.get(), 0x000000, 0xFFFFFF,
              new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> ALFA_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("alfa_zombie_spawn_egg",
          () -> new SpawnEggItem(SurvivalEntityTypes.ALFA_ZOMBIE.get(), 0x000000, 0xFFFFFF,
              new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> BOUNTY_HUNTER_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("bounty_hunter_zombie_spawn_egg",
          () -> new SpawnEggItem(SurvivalEntityTypes.BOUNTY_HUNTER_ZOMBIE.get(), 0x000000, 0xFFFFFF,
              new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> DESERT_RAIDER_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("desert_raider_zombie_spawn_egg",
          () -> new SpawnEggItem(SurvivalEntityTypes.DESERT_RAIDER_ZOMBIE.get(), 0x000000, 0xFFFFFF,
              new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> FIREFIGHTER_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("firefighter_zombie_spawn_egg",
          () -> new SpawnEggItem(SurvivalEntityTypes.FIREFIGHTER_ZOMBIE.get(), 0x000000, 0xFFFFFF,
              new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> HAZMAT_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("hazmat_zombie_spawn_egg",
          () -> new SpawnEggItem(SurvivalEntityTypes.HAZMAT_ZOMBIE.get(), 0x000000, 0xFFFFFF,
              new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> JUGGERNAUT_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("juggernaut_zombie_spawn_egg",
          () -> new SpawnEggItem(SurvivalEntityTypes.JUGGERNAUT_ZOMBIE.get(), 0x000000, 0xFFFFFF,
              new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> MINER_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("miner_zombie_spawn_egg",
          () -> new SpawnEggItem(SurvivalEntityTypes.MINER_ZOMBIE.get(), 0x000000, 0xFFFFFF,
              new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> SWAT_ZOMBIE_SPAWN_EGG =
      deferredRegister.register("swat_zombie_spawn_egg",
          () -> new SpawnEggItem(SurvivalEntityTypes.SWAT_ZOMBIE.get(), 0x000000, 0xFFFFFF,
              new Item.Properties()));

  // ================================================================================
  // Consumable Items
  // ================================================================================

  public static final DeferredHolder<Item, ? extends Item> EMPTY_WATER_BOTTLE =
      deferredRegister.register("empty_water_bottle",
          () -> new Item(new Item.Properties().stacksTo(3)));

  public static final DeferredHolder<Item, ? extends Item> WATER_BOTTLE =
      deferredRegister.register("water_bottle",
          () -> new ConsumableItem(new Properties().stacksTo(3), 0, 0, 8,
              EMPTY_WATER_BOTTLE, Type.ONLY_DRINK));

  public static final DeferredHolder<Item, ? extends Item> EMPTY_WATER_CANTEEN =
      deferredRegister.register("empty_water_canteen",
          () -> new ActionItem(SurvivalActionTypes.FILL_WATER_CANTEEN,
              new Item.Properties().stacksTo(3)));

  public static final DeferredHolder<Item, ? extends Item> WATER_CANTEEN =
      deferredRegister.register("water_canteen",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 0, 0, 9,
              EMPTY_WATER_CANTEEN, Type.ONLY_DRINK));

  public static final DeferredHolder<Item, ? extends Item> EMPTY_FLASK =
      deferredRegister.register("empty_flask",
          () -> new ActionItem(SurvivalActionTypes.FILL_FLASK,
              new Item.Properties().stacksTo(3)));

  public static final DeferredHolder<Item, ? extends Item> FLASK =
      deferredRegister.register("flask",
          () -> new ConsumableItem(new Properties().stacksTo(3), 0, 0, 7, EMPTY_FLASK,
              Type.ONLY_DRINK));

  public static final DeferredHolder<Item, ? extends Item> EMPTY_ICED_TEA =
      deferredRegister.register("empty_iced_tea",
          () -> new Item(new Properties().stacksTo(3)));

  public static final DeferredHolder<Item, ? extends Item> ICED_TEA =
      deferredRegister.register("iced_tea",
          () -> new ConsumableItem(new Properties().stacksTo(3), 0, 0, 4, EMPTY_ICED_TEA,
              Type.ONLY_DRINK));

  public static final DeferredHolder<Item, ? extends Item> EMPTY_IRON_BREW =
      deferredRegister.register("empty_iron_brew",
          () -> new Item(new Properties().stacksTo(3)));

  public static final DeferredHolder<Item, ? extends Item> IRON_BREW =
      deferredRegister.register("iron_brew",
          () -> new ConsumableItem(new Properties().stacksTo(3), 0, 0, 4, EMPTY_IRON_BREW,
              Type.ONLY_DRINK));

  public static final DeferredHolder<Item, ? extends Item> EMPTY_JUICE_POUCH =
      deferredRegister.register("empty_juice_pouch",
          () -> new Item(new Properties().stacksTo(3)));

  public static final DeferredHolder<Item, ? extends Item> JUICE_POUCH =
      deferredRegister.register("juice_pouch",
          () -> new ConsumableItem(new Properties().stacksTo(3), 0, 0, 4,
              EMPTY_JUICE_POUCH, Type.ONLY_DRINK));

  public static final DeferredHolder<Item, ? extends Item> EMPTY_LEMON_SODA =
      deferredRegister.register("empty_lemon_soda",
          () -> new Item(new Properties().stacksTo(3)));

  public static final DeferredHolder<Item, ? extends Item> LEMON_SODA =
      deferredRegister.register("lemon_soda",
          () -> new ConsumableItem(new Properties().stacksTo(3), 0, 0, 4, EMPTY_LEMON_SODA,
              Type.ONLY_DRINK));

  public static final DeferredHolder<Item, ? extends Item> EMPTY_MILK_CARTON =
      deferredRegister.register("empty_milk_carton",
          () -> new Item(new Properties().stacksTo(3)));

  public static final DeferredHolder<Item, ? extends Item> MILK_CARTON =
      deferredRegister.register("milk_carton",
          () -> new ConsumableItem(new Properties().stacksTo(3), 0, 0, 6,
              EMPTY_MILK_CARTON, Type.ONLY_DRINK));

  public static final DeferredHolder<Item, ? extends Item> EMPTY_ORANGE_SODA =
      deferredRegister.register("empty_orange_soda",
          () -> new Item(new Properties().stacksTo(3)));

  public static final DeferredHolder<Item, ? extends Item> ORANGE_SODA =
      deferredRegister.register("orange_soda",
          () -> new ConsumableItem(new Properties().stacksTo(3), 0, 0, 4,
              EMPTY_ORANGE_SODA, Type.ONLY_DRINK));

  public static final DeferredHolder<Item, ? extends Item> EMPTY_PEPE_SODA =
      deferredRegister.register("empty_pepe_soda",
          () -> new Item(new Properties().stacksTo(3)));

  public static final DeferredHolder<Item, ? extends Item> PEPE_SODA =
      deferredRegister.register("pepe_soda",
          () -> new ConsumableItem(new Properties().stacksTo(3), 0, 0, 4, EMPTY_PEPE_SODA,
              Type.ONLY_DRINK));

  public static final DeferredHolder<Item, ? extends Item> EMPTY_SPRITE =
      deferredRegister.register("empty_sprite",
          () -> new Item(new Properties().stacksTo(3)));

  public static final DeferredHolder<Item, ? extends Item> SPRITE =
      deferredRegister.register("sprite",
          () -> new ConsumableItem(new Properties().stacksTo(3), 0, 0, 6, EMPTY_SPRITE,
              Type.ONLY_DRINK));

  public static final DeferredHolder<Item, ? extends Item> EMPTY_COLA =
      deferredRegister.register("empty_cola",
          () -> new Item(new Properties().stacksTo(3)));

  public static final DeferredHolder<Item, ? extends Item> COLA =
      deferredRegister.register("cola",
          () -> new ConsumableItem(new Properties().stacksTo(3), 0, 0, 6, EMPTY_COLA,
              Type.ONLY_DRINK));

  public static final DeferredHolder<Item, ? extends Item> EMPTY_ZOMBIE_ENERGY =
      deferredRegister.register("empty_zombie_energy",
          () -> new Item(new Properties().stacksTo(3)));

  public static final DeferredHolder<Item, ? extends Item> ZOMBIE_ENERGY =
      deferredRegister.register("zombie_energy",
          () -> new ConsumableItem(new Properties().stacksTo(3), 0, 0, 4,
              EMPTY_ZOMBIE_ENERGY, Type.ONLY_DRINK));

  public static final DeferredHolder<Item, ? extends Item> POWER_BAR =
      deferredRegister.register("power_bar",
          () -> new ConsumableItem(new Properties().stacksTo(3), 4, 0.3F, 0, null,
              Type.ONLY_FOOD));

  public static final DeferredHolder<Item, ? extends Item> CANDY_BAR =
      deferredRegister.register("candy_bar",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 6, 0.3F, 0, null,
              Type.ONLY_FOOD));

  public static final DeferredHolder<Item, ? extends Item> CEREAL =
      deferredRegister.register("cereal",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 10, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final DeferredHolder<Item, ? extends Item> CANNED_SWEETCORN =
      deferredRegister.register("canned_sweetcorn",
          () -> new Item(new Item.Properties().stacksTo(3)));

  public static final DeferredHolder<Item, ? extends Item> OPEN_CANNED_SWEETCORN =
      deferredRegister.register("open_canned_sweetcorn",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 6, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final DeferredHolder<Item, ? extends Item> CANNED_BEANS =
      deferredRegister.register("canned_beans",
          () -> new Item(new Item.Properties().stacksTo(3)));

  public static final DeferredHolder<Item, ? extends Item> OPEN_CANNED_BEANS =
      deferredRegister.register("open_canned_beans",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 8, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final DeferredHolder<Item, ? extends Item> CANNED_TUNA =
      deferredRegister.register("canned_tuna",
          () -> new Item(new Item.Properties().stacksTo(3)));

  public static final DeferredHolder<Item, ? extends Item> OPEN_CANNED_TUNA =
      deferredRegister.register("open_canned_tuna",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 6, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final DeferredHolder<Item, ? extends Item> CANNED_PEACHES =
      deferredRegister.register("canned_peaches",
          () -> new Item(new Item.Properties().stacksTo(3)));

  public static final DeferredHolder<Item, ? extends Item> OPEN_CANNED_PEACHES =
      deferredRegister.register("open_canned_peaches",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 6, 0.6F, 1, null,
              Type.FOOD_AND_DRINK));

  public static final DeferredHolder<Item, ? extends Item> CANNED_PASTA =
      deferredRegister.register("canned_pasta",
          () -> new Item(new Item.Properties().stacksTo(3)));

  public static final DeferredHolder<Item, ? extends Item> OPEN_CANNED_PASTA =
      deferredRegister.register("open_canned_pasta",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 6, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final DeferredHolder<Item, ? extends Item> CANNED_CORNED_BEEF =
      deferredRegister.register("canned_corned_beef",
          () -> new Item(new Item.Properties().stacksTo(3)));

  public static final DeferredHolder<Item, ? extends Item> OPEN_CANNED_CORNED_BEEF =
      deferredRegister.register("open_canned_corned_beef",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 8, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final DeferredHolder<Item, ? extends Item> CANNED_CUSTARD =
      deferredRegister.register("canned_custard",
          () -> new Item(new Item.Properties().stacksTo(3)));

  public static final DeferredHolder<Item, ? extends Item> OPEN_CANNED_CUSTARD =
      deferredRegister.register("open_canned_custard",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 4, 0.6F, 3, null,
              Type.FOOD_AND_DRINK));

  public static final DeferredHolder<Item, ? extends Item> CANNED_PICKLES =
      deferredRegister.register("canned_pickles",
          () -> new Item(new Item.Properties().stacksTo(3)));

  public static final DeferredHolder<Item, ? extends Item> OPEN_CANNED_PICKLES =
      deferredRegister.register("open_canned_pickles",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 4, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final DeferredHolder<Item, ? extends Item> CANNED_DOG_FOOD =
      deferredRegister.register("canned_dog_food",
          () -> new Item(new Item.Properties().stacksTo(3)));

  public static final DeferredHolder<Item, ? extends Item> OPEN_CANNED_DOG_FOOD =
      deferredRegister.register("open_canned_dog_food",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 2, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final DeferredHolder<Item, ? extends Item> CANNED_TOMATO_SOUP =
      deferredRegister.register("canned_tomato_soup",
          () -> new Item(new Item.Properties().stacksTo(3)));

  public static final DeferredHolder<Item, ? extends Item> OPEN_CANNED_TOMATO_SOUP =
      deferredRegister.register("open_canned_tomato_soup",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 4, 0.6F, 2, null,
              Type.FOOD_AND_DRINK));

  public static final DeferredHolder<Item, ? extends Item> MRE =
      deferredRegister.register("mre",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 16, 0.6F, 7, null,
              Type.FOOD_AND_DRINK));

  public static final DeferredHolder<Item, ? extends Item> ORANGE =
      deferredRegister.register("orange",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 5, 0.6F, 2, null,
              Type.FOOD_AND_DRINK));

  public static final DeferredHolder<Item, ? extends Item> ROTTEN_ORANGE =
      deferredRegister.register("rotten_orange",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 2, 0.6F, 1, null,
              Type.FOOD_AND_DRINK));

  public static final DeferredHolder<Item, ? extends Item> PEAR =
      deferredRegister.register("pear",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 4, 0.6F, 2, null,
              Type.FOOD_AND_DRINK));

  public static final DeferredHolder<Item, ? extends Item> ROTTEN_PEAR =
      deferredRegister.register("rotten_pear",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 1, 0.6F, 1, null,
              Type.FOOD_AND_DRINK));

  public static final DeferredHolder<Item, ? extends Item> RICE_BAG =
      deferredRegister.register("rice_bag",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 8, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final DeferredHolder<Item, ? extends Item> ROTTEN_APPLE =
      deferredRegister.register("rotten_apple",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 1, 0.6F, 1, null,
              Type.FOOD_AND_DRINK));

  public static final DeferredHolder<Item, ? extends Item> NOODLES =
      deferredRegister.register("noodles",
          () -> new ConsumableItem(new Properties().stacksTo(3), 6, 0.6F, 3, null,
              Type.FOOD_AND_DRINK));

  public static final DeferredHolder<Item, ? extends Item> ROTTEN_MELON_SLICE =
      deferredRegister.register("rotten_melon_slice",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 3, 0.6F, 1, null,
              Type.FOOD_AND_DRINK));

  public static final DeferredHolder<Item, ? extends Item> BLUEBERRY =
      deferredRegister.register("blueberry",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 4, 0.6F, 3, null,
              Type.FOOD_AND_DRINK));

  public static final DeferredHolder<Item, ? extends Item> ROTTEN_BLUEBERRY =
      deferredRegister.register("rotten_blueberry",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 1, 0.6F, 1, null,
              Type.FOOD_AND_DRINK));

  public static final DeferredHolder<Item, ? extends Item> RASPBERRY =
      deferredRegister.register("raspberry",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 3, 0.6F, 3, null,
              Type.FOOD_AND_DRINK));

  public static final DeferredHolder<Item, ? extends Item> ROTTEN_RASPBERRY =
      deferredRegister.register("rotten_raspberry",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 1, 0.6F, 1, null,
              Type.FOOD_AND_DRINK));

  public static final DeferredHolder<Item, ? extends Item> CHIPS =
      deferredRegister.register("chips",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 3, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final DeferredHolder<Item, ? extends Item> RANCH_CHIPS =
      deferredRegister.register("ranch_chips",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 3, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final DeferredHolder<Item, ? extends Item> CHEESY_CHIPS =
      deferredRegister.register("cheesy_chips",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 3, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final DeferredHolder<Item, ? extends Item> SALTED_CHIPS =
      deferredRegister.register("salted_chips",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 3, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final DeferredHolder<Item, ? extends Item> POPCORN =
      deferredRegister.register("popcorn",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 3, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final DeferredHolder<Item, ? extends Item> NUTTY_CEREAL =
      deferredRegister.register("nutty_cereal",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 10, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final DeferredHolder<Item, ? extends Item> EMERALD_CEREAL =
      deferredRegister.register("emerald_cereal",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 10, 0.6F, 0, null,
              Type.ONLY_FOOD));

  public static final DeferredHolder<Item, ? extends Item> FLAKE_CEREAL =
      deferredRegister.register("flake_cereal",
          () -> new ConsumableItem(new Item.Properties().stacksTo(3), 10, 0.6F, 0, null,
              Type.ONLY_FOOD));

  // ================================================================================
  // Tools
  // ================================================================================

  public static final DeferredHolder<Item, ? extends Item> CAN_OPENER =
      deferredRegister.register("can_opener", () -> new ToolItem(
          new Item.Properties().durability(8)) {


        @Override
        public void appendHoverText(ItemStack stack,
            net.minecraft.world.item.Item.TooltipContext level, List<Component> tooltip,
            @NotNull TooltipFlag flag) {
          tooltip.add(Component.translatable("item.craftingdead.durability").append(" ").append(
                  Component.translatable(String.valueOf(stack.getMaxDamage() - stack.getDamageValue()))
                      .withStyle(style -> style.withColor(ChatFormatting.RED)))
              .withStyle(style -> style.withColor(ChatFormatting.GRAY)));
        }
      });

  public static final DeferredHolder<Item, ? extends Item> SCREWDRIVER =
      deferredRegister.register("screwdriver", () -> new ToolItem(
          new Item.Properties().durability(6)) {


        @Override
        public void appendHoverText(ItemStack stack,
            net.minecraft.world.item.Item.TooltipContext level, List<Component> tooltip,
            @NotNull TooltipFlag flag) {
          tooltip.add(Component.translatable("item.craftingdead.durability").append(" ").append(
                  Component.translatable(String.valueOf(stack.getMaxDamage() - stack.getDamageValue()))
                      .withStyle(style -> style.withColor(ChatFormatting.RED)))
              .withStyle(style -> style.withColor(ChatFormatting.GRAY)));
        }
      });

  public static final DeferredHolder<Item, ? extends Item> MULTI_TOOL =
      deferredRegister.register("multi_tool", () -> new MeleeWeaponItem(8, -2.4F,
          new Item.Properties().durability(20)) {

      });

  // ================================================================================
  // Miscellaneous
  // ================================================================================

  public static final DeferredHolder<Item, ? extends Item> ROPE =
      deferredRegister.register("rope",
          () -> new Item(new Item.Properties()
              .stacksTo(1)
              ));

  static {
    var canOpenerTooltip = Component.translatable("can_opener.information")
        .withStyle(ChatFormatting.GRAY);
    var cannedFoodTooltip = Component.translatable("canned_food.information")
        .withStyle(ChatFormatting.GRAY);
    var emptyCanteenFlaskTooltip  = Component.translatable("empty_canteen_flask.information")
        .withStyle(ChatFormatting.GRAY);
    ArbitraryTooltips.registerTooltip(CAN_OPENER, canOpenerTooltip);
    ArbitraryTooltips.registerTooltip(SCREWDRIVER, canOpenerTooltip);
    ArbitraryTooltips.registerTooltip(MULTI_TOOL, canOpenerTooltip);
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
    ArbitraryTooltips.registerTooltip(EMPTY_WATER_CANTEEN, emptyCanteenFlaskTooltip);
    ArbitraryTooltips.registerTooltip(EMPTY_FLASK, emptyCanteenFlaskTooltip);
    ArbitraryTooltips.registerTooltip(SPLINT,
        Component.translatable("splint.information")
            .withStyle(ChatFormatting.GRAY));
    ArbitraryTooltips.registerTooltip(MORPHINE_SYRINGE,
        Component.translatable("morphine_syringe.information")
            .withStyle(ChatFormatting.GRAY));
    ArbitraryTooltips.registerTooltip(RBI_SYRINGE,
        Component.translatable("rbi_syringe.information")
            .withStyle(ChatFormatting.GRAY));
    ArbitraryTooltips.registerTooltip(CURE_SYRINGE,
        Component.translatable("cure_syringe.information")
            .withStyle(ChatFormatting.GRAY));
    ArbitraryTooltips.registerTooltip(ROPE,
        Component.translatable("rope.information")
            .withStyle(ChatFormatting.GRAY));
  }

  public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
      DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CraftingDeadSurvival.ID);

  public static final DeferredHolder<CreativeModeTab, ? extends CreativeModeTab> TAB =
      CREATIVE_MODE_TABS.register("main",
          () -> CreativeModeTab.builder()
              .title(Component.translatable("craftingdeadsurvival"))
              .icon(() -> new ItemStack(RBI_SYRINGE.get()))
              .displayItems((params, output) -> {
                deferredRegister.getEntries().stream()
                    .map(DeferredHolder::get)
                    .forEach(output::accept);
              })
              .build());
}
