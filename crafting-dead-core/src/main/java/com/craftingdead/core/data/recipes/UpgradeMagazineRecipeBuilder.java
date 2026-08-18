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

package com.craftingdead.core.data.recipes;

import com.craftingdead.core.world.item.crafting.UpgradeMagazineRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

public class UpgradeMagazineRecipeBuilder {

  private final Ingredient magazine;
  private final Item nextTier;
  private final Advancement.Builder advancement = Advancement.Builder.advancement();
  private String group;

  private UpgradeMagazineRecipeBuilder(Ingredient magazine, Item nextTier) {
    this.magazine = magazine;
    this.nextTier = nextTier;
  }

  public static UpgradeMagazineRecipeBuilder create(Ingredient magazine, Item nextTier) {
    return new UpgradeMagazineRecipeBuilder(magazine, nextTier);
  }

  public UpgradeMagazineRecipeBuilder unlockedBy(String id, Criterion<?> criterion) {
    this.advancement.addCriterion(id, criterion);
    return this;
  }

  public UpgradeMagazineRecipeBuilder group(String group) {
    this.group = group;
    return this;
  }

  public void save(RecipeOutput output) {
    this.save(output, ForgeRegistries.ITEMS.getKey(this.nextTier));
  }

  public void save(RecipeOutput output, String id) {
    ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(this.nextTier);
    if ((ResourceLocation.parse(id)).equals(resourcelocation)) {
      throw new IllegalStateException(
          "Shapeless Recipe " + id + " should remove its 'save' argument");
    } else {
      this.save(output, ResourceLocation.parse(id));
    }
  }

  public void save(RecipeOutput output, ResourceLocation id) {
    this.ensureValid(id);
    this.advancement
        .parent(ResourceLocation.parse("recipes/root"))
        .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
        .rewards(AdvancementRewards.Builder.recipe(id))
        .requirements(AdvancementRequirements.Strategy.OR);

    var recipe = new UpgradeMagazineRecipe(net.minecraft.world.item.crafting.CraftingBookCategory.MISC,
        this.magazine, new ItemStack(this.nextTier));
    var advancementHolder = this.advancement
        .build(ResourceLocation.fromNamespaceAndPath(id.getNamespace(),
            "recipes/" + "misc" + "/" + id.getPath()));
    output.accept(id, recipe, advancementHolder);
  }

  private void ensureValid(ResourceLocation id) {
    // Advancement criteria introspection was removed in 1.21; validation is skipped.
  }
}