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

package com.craftingdead.core.world.item.crafting;

import com.craftingdead.core.CraftingDead;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.Registries;

public class ModRecipeSerializers {

  public static final DeferredRegister<RecipeSerializer<?>> deferredRegister =
      DeferredRegister.create(Registries.RECIPE_SERIALIZER, CraftingDead.ID);

  public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> UPGRADE_MAGAZINE =
      deferredRegister.register("upgrade_magazine",
          UpgradeMagazineRecipe.Serializer::new);

  public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> DUPLICATE_MAGAZINE =
      deferredRegister.register("duplicate_magazine",
          () -> new SimpleCraftingRecipeSerializer<>(DuplicateMagazineRecipe::new));

  public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> MYTHIC_UPGRADE =
      deferredRegister.register("mythic_upgrade",
          MythicUpgradeRecipe.Serializer::new);
}
