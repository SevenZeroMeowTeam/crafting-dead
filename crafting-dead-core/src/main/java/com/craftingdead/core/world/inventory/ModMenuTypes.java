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

package com.craftingdead.core.world.inventory;

import com.craftingdead.core.CraftingDead;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.Registries;

public class ModMenuTypes {

  public static final DeferredRegister<MenuType<?>> deferredRegister =
      DeferredRegister.create(Registries.MENU, CraftingDead.ID);

  public static final DeferredHolder<MenuType<?>, MenuType<EquipmentMenu>> EQUIPMENT =
      deferredRegister.register("equipment", () -> new MenuType<EquipmentMenu>(EquipmentMenu::new, FeatureFlags.DEFAULT_FLAGS));

  public static final DeferredHolder<MenuType<?>, MenuType<CraftingMenu>> CRAFTING =
      deferredRegister.register("crafting", () -> new MenuType<CraftingMenu>(CraftingMenu::new, FeatureFlags.DEFAULT_FLAGS));

  public static final DeferredHolder<MenuType<?>, MenuType<GenericMenu>> VEST =
      deferredRegister.register("vest", () -> new MenuType<GenericMenu>(GenericMenu::createVest, FeatureFlags.DEFAULT_FLAGS));

  public static final DeferredHolder<MenuType<?>, MenuType<GenericMenu>> SMALL_BACKPACK =
      deferredRegister.register("small_backpack", () -> new MenuType<GenericMenu>(GenericMenu::createSmallBackpack, FeatureFlags.DEFAULT_FLAGS));

  public static final DeferredHolder<MenuType<?>, MenuType<GenericMenu>> MEDIUM_BACKPACK =
      deferredRegister.register("medium_backpack", () -> new MenuType<GenericMenu>(GenericMenu::createMediumBackpack, FeatureFlags.DEFAULT_FLAGS));

  public static final DeferredHolder<MenuType<?>, MenuType<GenericMenu>> LARGE_BACKPACK =
      deferredRegister.register("large_backpack", () -> new MenuType<GenericMenu>(GenericMenu::createLargeBackpack, FeatureFlags.DEFAULT_FLAGS));

  public static final DeferredHolder<MenuType<?>, MenuType<GenericMenu>> GUN_BAG =
      deferredRegister.register("gun_bag", () -> new MenuType<GenericMenu>(GenericMenu::createGunBag, FeatureFlags.DEFAULT_FLAGS));
}