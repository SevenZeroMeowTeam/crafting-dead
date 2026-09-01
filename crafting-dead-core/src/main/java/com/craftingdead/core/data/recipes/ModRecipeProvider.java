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

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import com.craftingdead.core.CraftingDead;
import com.craftingdead.core.tags.ModItemTags;
import com.craftingdead.core.world.item.ModItems;
import com.craftingdead.core.world.item.crafting.DuplicateMagazineRecipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;

public class ModRecipeProvider extends RecipeProvider {

  public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
    super(output, registries);
  }

  @Override
  protected void buildRecipes(RecipeOutput output) {
    UpgradeMagazineRecipeBuilder
        .create(Ingredient.of(ModItems.STANAG_DRUM_MAGAZINE.get()),
            ModItems.STANAG_BOX_MAGAZINE.get())
        .unlockedBy("has_stanag_drum_magazine", has(ModItems.STANAG_DRUM_MAGAZINE.get()))
        .save(output);

    UpgradeMagazineRecipeBuilder
        .create(Ingredient.of(ModItems.STANAG_30_ROUND_MAGAZINE.get()),
            ModItems.STANAG_DRUM_MAGAZINE.get())
        .unlockedBy("has_stanag_30_round_magazine", has(ModItems.STANAG_30_ROUND_MAGAZINE.get()))
        .save(output);

    UpgradeMagazineRecipeBuilder
        .create(Ingredient.of(ModItems.STANAG_20_ROUND_MAGAZINE.get()),
            ModItems.STANAG_30_ROUND_MAGAZINE.get())
        .unlockedBy("has_stanag_20_round_magazine", has(ModItems.STANAG_20_ROUND_MAGAZINE.get()))
        .save(output);

    UpgradeMagazineRecipeBuilder
        .create(Ingredient.of(ModItems.MP5A5_21_ROUND_MAGAZINE.get()),
            ModItems.MP5A5_35_ROUND_MAGAZINE.get())
        .unlockedBy("has_mp5a5_21_round_magazine", has(ModItems.MP5A5_21_ROUND_MAGAZINE.get()))
        .save(output);

    UpgradeMagazineRecipeBuilder
        .create(Ingredient.of(ModItems.MAC10_MAGAZINE.get()),
            ModItems.MAC10_EXTENDED_MAGAZINE.get())
        .unlockedBy("has_mac10_magazine", has(ModItems.MAC10_MAGAZINE.get()))
        .save(output);

    UpgradeMagazineRecipeBuilder
        .create(Ingredient.of(ModItems.RPK_MAGAZINE.get()),
            ModItems.RPK_DRUM_MAGAZINE.get())
        .unlockedBy("has_rpk_magazine", has(ModItems.RPK_MAGAZINE.get()))
        .save(output);

    SpecialRecipeBuilder.special(category -> new DuplicateMagazineRecipe(category))
        .save(output, CraftingDead.ID + ":duplicate_magazine");

    // ================================================================================
    // Attachments
    // ================================================================================

    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.ACOG_SIGHT.get())
        .pattern("gig")
        .pattern("iii")
        .define('g', Items.GLASS)
        .define('i', Items.IRON_ORE)
        .unlockedBy("has_glass", has(Items.GLASS))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.RED_DOT_SIGHT.get())
        .pattern("g  ")
        .pattern("iii")
        .define('g', Items.GLASS)
        .define('i', Items.IRON_ORE)
        .unlockedBy("has_glass", has(Items.GLASS))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.EOTECH_SIGHT.get())
        .pattern("gr ")
        .pattern("iri")
        .define('g', Items.GLASS)
        .define('i', Items.IRON_ORE)
        .define('r', Items.REDSTONE)
        .unlockedBy("has_redstone", has(Items.REDSTONE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.LP_SCOPE.get())
        .pattern("iii")
        .pattern("g g")
        .pattern("iii")
        .define('g', Items.GLASS)
        .define('i', Items.IRON_ORE)
        .unlockedBy("has_glass", has(Items.GLASS))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.HP_SCOPE.get())
        .pattern("iii")
        .pattern("grg")
        .pattern("iii")
        .define('g', Items.GLASS)
        .define('i', Items.IRON_ORE)
        .define('r', Items.REDSTONE)
        .unlockedBy("has_redstone", has(Items.REDSTONE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.SUPPRESSOR.get())
        .pattern("isi")
        .pattern("isi")
        .pattern("isi")
        .define('i', Items.IRON_ORE)
        .define('s', Items.STRING)
        .unlockedBy("has_iron_ore", has(Items.IRON_ORE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.TACTICAL_GRIP.get())
        .pattern(" i ")
        .pattern(" i ")
        .pattern(" i ")
        .define('i', Items.IRON_ORE)
        .unlockedBy("has_iron_ore", has(Items.IRON_ORE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.BIPOD.get())
        .pattern(" i ")
        .pattern("i i")
        .pattern("i i")
        .define('i', Items.IRON_ORE)
        .unlockedBy("has_iron_ore", has(Items.IRON_ORE))
        .save(output);

    // ================================================================================
    // Assault Rifles
    // ================================================================================

    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.M4A1.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.SMALL_STOCK.get())
        .define('b', ModItems.MEDIUM_BODY.get())
        .define('c', ModItems.MEDIUM_BARREL.get())
        .define('d', Items.LIGHT_GRAY_DYE)
        .define('e', ModItems.MEDIUM_HANDLE.get())
        .unlockedBy("has_light_gray_dye", has(Items.LIGHT_GRAY_DYE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.SCARL.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.SMALL_STOCK.get())
        .define('b', ModItems.MEDIUM_BODY.get())
        .define('c', ModItems.MEDIUM_BARREL.get())
        .define('d', Items.ORANGE_DYE)
        .define('e', ModItems.MEDIUM_HANDLE.get())
        .unlockedBy("has_orange_dye", has(Items.ORANGE_DYE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.AK47.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.SMALL_STOCK.get())
        .define('b', ModItems.MEDIUM_BODY.get())
        .define('c', ModItems.MEDIUM_BARREL.get())
        .define('d', Items.BROWN_DYE)
        .define('e', ModItems.MEDIUM_HANDLE.get())
        .unlockedBy("has_brown_dye", has(Items.BROWN_DYE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.ACR.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.SMALL_STOCK.get())
        .define('b', ModItems.MEDIUM_BODY.get())
        .define('c', ModItems.MEDIUM_BARREL.get())
        .define('d', Items.RED_DYE)
        .define('e', ModItems.MEDIUM_HANDLE.get())
        .unlockedBy("has_red_dye", has(Items.RED_DYE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.FNFAL.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.SMALL_STOCK.get())
        .define('b', ModItems.MEDIUM_BODY.get())
        .define('c', ModItems.MEDIUM_BARREL.get())
        .define('d', Items.INK_SAC)
        .define('e', ModItems.MEDIUM_HANDLE.get())
        .unlockedBy("has_ink_sac", has(Items.INK_SAC))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.HK417.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.SMALL_STOCK.get())
        .define('b', ModItems.MEDIUM_BODY.get())
        .define('c', ModItems.MEDIUM_BARREL.get())
        .define('d', Items.MAGENTA_DYE)
        .define('e', ModItems.MEDIUM_HANDLE.get())
        .unlockedBy("has_magenta_dye", has(Items.MAGENTA_DYE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.MPT55.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.SMALL_STOCK.get())
        .define('b', ModItems.MEDIUM_BODY.get())
        .define('c', ModItems.MEDIUM_BARREL.get())
        .define('d', Items.YELLOW_DYE)
        .define('e', ModItems.MEDIUM_HANDLE.get())
        .unlockedBy("has_yellow_dye", has(Items.YELLOW_DYE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.M1GARAND.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.SMALL_STOCK.get())
        .define('b', ModItems.MEDIUM_BODY.get())
        .define('c', ModItems.MEDIUM_BARREL.get())
        .define('d', Items.LIME_DYE)
        .define('e', ModItems.MEDIUM_HANDLE.get())
        .unlockedBy("has_lime_dye", has(Items.LIME_DYE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.SPORTER22.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.SMALL_STOCK.get())
        .define('b', ModItems.MEDIUM_BODY.get())
        .define('c', ModItems.MEDIUM_BARREL.get())
        .define('d', Items.BLUE_DYE)
        .define('e', ModItems.MEDIUM_HANDLE.get())
        .unlockedBy("has_blue_dye", has(Items.BLUE_DYE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.G36C.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.SMALL_STOCK.get())
        .define('b', ModItems.MEDIUM_BODY.get())
        .define('c', ModItems.MEDIUM_BARREL.get())
        .define('d', Items.BLUE_DYE)
        .define('e', ModItems.MEDIUM_HANDLE.get())
        .unlockedBy("has_blue_dye", has(Items.BLUE_DYE))
        .save(output);

    // ================================================================================
    // Machine Guns
    // ================================================================================

    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.M240B.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.MEDIUM_STOCK.get())
        .define('b', ModItems.HEAVY_BODY.get())
        .define('c', ModItems.MEDIUM_BARREL.get())
        .define('d', Items.RED_DYE)
        .define('e', ModItems.HEAVY_HANDLE.get())
        .unlockedBy("has_red_dye", has(Items.RED_DYE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.RPK.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.MEDIUM_STOCK.get())
        .define('b', ModItems.HEAVY_BODY.get())
        .define('c', ModItems.MEDIUM_BARREL.get())
        .define('d', Items.ORANGE_DYE)
        .define('e', ModItems.HEAVY_HANDLE.get())
        .unlockedBy("has_orange_dye", has(Items.ORANGE_DYE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.MINIGUN.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.MEDIUM_STOCK.get())
        .define('b', ModItems.HEAVY_BODY.get())
        .define('c', ModItems.MEDIUM_BARREL.get())
        .define('d', Items.LIME_DYE)
        .define('e', ModItems.HEAVY_HANDLE.get())
        .unlockedBy("has_lime_dye", has(Items.LIME_DYE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.MK48MOD.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.MEDIUM_STOCK.get())
        .define('b', ModItems.HEAVY_BODY.get())
        .define('c', ModItems.MEDIUM_BARREL.get())
        .define('d', Items.BLUE_DYE)
        .define('e', ModItems.HEAVY_HANDLE.get())
        .unlockedBy("has_blue_dye", has(Items.BLUE_DYE))
        .save(output);

    // ================================================================================
    // Pistols
    // ================================================================================

    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.TASER.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.SMALL_STOCK.get())
        .define('b', ModItems.SMALL_BODY.get())
        .define('c', ModItems.SMALL_BARREL.get())
        .define('d', Items.RED_DYE)
        .define('e', ModItems.SMALL_HANDLE.get())
        .unlockedBy("has_red_dye", has(Items.RED_DYE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.M1911.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.SMALL_STOCK.get())
        .define('b', ModItems.SMALL_BODY.get())
        .define('c', ModItems.SMALL_BARREL.get())
        .define('d', Items.BLUE_DYE)
        .define('e', ModItems.SMALL_HANDLE.get())
        .unlockedBy("has_blue_dye", has(Items.BLUE_DYE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.G18.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.SMALL_STOCK.get())
        .define('b', ModItems.SMALL_BODY.get())
        .define('c', ModItems.SMALL_BARREL.get())
        .define('d', Items.LIME_DYE)
        .define('e', ModItems.SMALL_HANDLE.get())
        .unlockedBy("has_lime_dye", has(Items.LIME_DYE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.M9.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.SMALL_STOCK.get())
        .define('b', ModItems.SMALL_BODY.get())
        .define('c', ModItems.SMALL_BARREL.get())
        .define('d', Items.ORANGE_DYE)
        .define('e', ModItems.SMALL_HANDLE.get())
        .unlockedBy("has_orange_dye", has(Items.ORANGE_DYE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.DESERT_EAGLE.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.SMALL_STOCK.get())
        .define('b', ModItems.SMALL_BODY.get())
        .define('c', ModItems.SMALL_BARREL.get())
        .define('d', Items.GRAY_DYE)
        .define('e', ModItems.SMALL_HANDLE.get())
        .unlockedBy("has_gray_dye", has(Items.GRAY_DYE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.P250.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.SMALL_STOCK.get())
        .define('b', ModItems.SMALL_BODY.get())
        .define('c', ModItems.SMALL_BARREL.get())
        .define('d', Items.BROWN_DYE)
        .define('e', ModItems.SMALL_HANDLE.get())
        .unlockedBy("has_brown_dye", has(Items.BROWN_DYE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.MAGNUM.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.SMALL_STOCK.get())
        .define('b', ModItems.SMALL_BODY.get())
        .define('c', ModItems.SMALL_BARREL.get())
        .define('d', Items.LIGHT_GRAY_DYE)
        .define('e', ModItems.SMALL_HANDLE.get())
        .unlockedBy("has_light_gray_dye", has(Items.LIGHT_GRAY_DYE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.FN57.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.SMALL_STOCK.get())
        .define('b', ModItems.SMALL_BODY.get())
        .define('c', ModItems.SMALL_BARREL.get())
        .define('d', Items.YELLOW_DYE)
        .define('e', ModItems.SMALL_HANDLE.get())
        .unlockedBy("has_yellow_dye", has(Items.YELLOW_DYE))
        .save(output);

    // ================================================================================
    // Submachine Guns
    // ================================================================================

    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.MAC10.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.SMALL_STOCK.get())
        .define('b', ModItems.MEDIUM_BODY.get())
        .define('c', ModItems.SMALL_BARREL.get())
        .define('d', Items.GRAY_DYE)
        .define('e', ModItems.SMALL_HANDLE.get())
        .unlockedBy("has_gray_dye", has(Items.GRAY_DYE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.P90.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.SMALL_STOCK.get())
        .define('b', ModItems.MEDIUM_BODY.get())
        .define('c', ModItems.SMALL_BARREL.get())
        .define('d', Items.LIGHT_GRAY_DYE)
        .define('e', ModItems.SMALL_HANDLE.get())
        .unlockedBy("has_light_gray_dye", has(Items.LIGHT_GRAY_DYE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.VECTOR.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.SMALL_STOCK.get())
        .define('b', ModItems.MEDIUM_BODY.get())
        .define('c', ModItems.SMALL_BARREL.get())
        .define('d', Items.BLUE_DYE)
        .define('e', ModItems.SMALL_HANDLE.get())
        .unlockedBy("has_blue_dye", has(Items.BLUE_DYE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.MP5A5.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.SMALL_STOCK.get())
        .define('b', ModItems.MEDIUM_BODY.get())
        .define('c', ModItems.SMALL_BARREL.get())
        .define('d', Items.LIME_DYE)
        .define('e', ModItems.SMALL_HANDLE.get())
        .unlockedBy("has_lime_dye", has(Items.LIME_DYE))
        .save(output);

    // ================================================================================
    // Sniper Rifles
    // ================================================================================

    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.M107.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.MEDIUM_STOCK.get())
        .define('b', ModItems.MEDIUM_BODY.get())
        .define('c', ModItems.HEAVY_BARREL.get())
        .define('d', Items.RED_DYE)
        .define('e', ModItems.HEAVY_HANDLE.get())
        .unlockedBy("has_red_dye", has(Items.RED_DYE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.AS50.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.MEDIUM_STOCK.get())
        .define('b', ModItems.MEDIUM_BODY.get())
        .define('c', ModItems.HEAVY_BARREL.get())
        .define('d', Items.LIME_DYE)
        .define('e', ModItems.HEAVY_HANDLE.get())
        .unlockedBy("has_lime_dye", has(Items.LIME_DYE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.AWP.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.MEDIUM_STOCK.get())
        .define('b', ModItems.MEDIUM_BODY.get())
        .define('c', ModItems.HEAVY_BARREL.get())
        .define('d', Items.GREEN_DYE)
        .define('e', ModItems.HEAVY_HANDLE.get())
        .unlockedBy("has_green_dye", has(Items.GREEN_DYE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.DMR.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.MEDIUM_STOCK.get())
        .define('b', ModItems.MEDIUM_BODY.get())
        .define('c', ModItems.HEAVY_BARREL.get())
        .define('d', Items.LIGHT_GRAY_DYE)
        .define('e', ModItems.HEAVY_HANDLE.get())
        .unlockedBy("has_light_gray_dye", has(Items.LIGHT_GRAY_DYE))
        .save(output);

    // 98K 狙击步枪（奖励箱 / 合成获得）
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.KAR98K.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.MEDIUM_STOCK.get())
        .define('b', ModItems.MEDIUM_BODY.get())
        .define('c', ModItems.HEAVY_BARREL.get())
        .define('d', Items.BROWN_DYE)
        .define('e', ModItems.HEAVY_BOLT.get())
        .unlockedBy("has_brown_dye", has(Items.BROWN_DYE))
        .save(output);

    // 98K 专用倍镜
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.KAR98K_SCOPE.get())
        .pattern("igi")
        .pattern("grg")
        .pattern("igi")
        .define('g', Items.GLASS)
        .define('i', Items.IRON_ORE)
        .define('r', Items.REDSTONE)
        .unlockedBy("has_redstone", has(Items.REDSTONE))
        .save(output);

    // 创造弹药箱
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.CREATIVE_AMMO_BOX.get())
        .pattern("iii")
        .pattern("idi")
        .pattern("iii")
        .define('i', Items.IRON_INGOT)
        .define('d', Items.DIAMOND)
        .unlockedBy("has_diamond", has(Items.DIAMOND))
        .save(output);

    // ================================================================================
    // Shotguns
    // ================================================================================

    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.TRENCH_GUN.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.MEDIUM_STOCK.get())
        .define('b', ModItems.HEAVY_BODY.get())
        .define('c', ModItems.HEAVY_BARREL.get())
        .define('d', Items.OAK_PLANKS)
        .define('e', ModItems.MEDIUM_HANDLE.get())
        .unlockedBy("has_oak_planks", has(Items.OAK_PLANKS))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.MOSSBERG.get())
        .pattern(" d ")
        .pattern("abc")
        .pattern(" e ")
        .define('a', ModItems.MEDIUM_STOCK.get())
        .define('b', ModItems.HEAVY_BODY.get())
        .define('c', ModItems.HEAVY_BARREL.get())
        .define('d', Items.BLACK_DYE)
        .define('e', ModItems.MEDIUM_HANDLE.get())
        .unlockedBy("has_black_dye", has(Items.BLACK_DYE))
        .save(output);

    // ================================================================================
    // Grenades
    // ================================================================================

    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.FIRE_GRENADE.get())
        .pattern(" i ")
        .pattern("ifi")
        .pattern(" i ")
        .define('i', Tags.Items.INGOTS_IRON)
        .define('f', Items.FIRE_CHARGE)
        .unlockedBy("has_fire_charge", has(Items.FIRE_CHARGE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.SMOKE_GRENADE.get())
        .pattern("wiw")
        .pattern("igi")
        .pattern("wiw")
        .define('i', Tags.Items.INGOTS_IRON)
        .define('w', ItemTags.WOOL)
        .define('g', net.neoforged.neoforge.common.Tags.Items.GUNPOWDERS)
        .unlockedBy("has_gunpowder", has(net.neoforged.neoforge.common.Tags.Items.GUNPOWDERS))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.FLASH_GRENADE.get())
        .pattern("eie")
        .pattern("igi")
        .pattern("eie")
        .define('i', Tags.Items.INGOTS_IRON)
        .define('e', Items.FERMENTED_SPIDER_EYE)
        .define('g', net.neoforged.neoforge.common.Tags.Items.GUNPOWDERS)
        .unlockedBy("has_fermented_spider_eye", has(Items.FERMENTED_SPIDER_EYE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.DECOY_GRENADE.get())
        .pattern("nin")
        .pattern("igi")
        .pattern("nin")
        .define('i', Tags.Items.INGOTS_IRON)
        .define('n', Items.NOTE_BLOCK)
        .define('g', net.neoforged.neoforge.common.Tags.Items.GUNPOWDERS)
        .unlockedBy("has_note_block", has(Items.NOTE_BLOCK))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.FRAG_GRENADE.get())
        .pattern(" i ")
        .pattern("igi")
        .pattern(" i ")
        .define('i', Tags.Items.INGOTS_IRON)
        .define('g', net.neoforged.neoforge.common.Tags.Items.GUNPOWDERS)
        .unlockedBy("has_gunpowder", has(net.neoforged.neoforge.common.Tags.Items.GUNPOWDERS))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.C4_EXPLOSIVE.get())
        .pattern(" i ")
        .pattern("iti")
        .pattern(" i ")
        .define('i', Tags.Items.INGOTS_IRON)
        .define('t', Items.TNT)
        .unlockedBy("has_tnt", has(Items.TNT))
        .save(output);
    ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.STICKY_C4_EXPLOSIVE.get())
        .requires(ModItems.C4_EXPLOSIVE.get())
        .requires(Items.SLIME_BALL)
        .unlockedBy("has_c4", has(ModItems.C4_EXPLOSIVE.get()))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.REMOTE_DETONATOR.get())
        .pattern(" i ")
        .pattern("iri")
        .pattern("iii")
        .define('i', Tags.Items.INGOTS_IRON)
        .define('r', Tags.Items.DUSTS_REDSTONE)
        .unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
        .save(output);

    // ================================================================================
    // Medical
    // ================================================================================

    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.FIRST_AID_KIT.get())
        .pattern("sss")
        .pattern("sas")
        .pattern("sss")
        .define('s', Items.STRING)
        .define('a', Items.APPLE)
        .unlockedBy("has_string", has(Items.STRING))
        .unlockedBy("has_apple", has(Items.APPLE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.SYRINGE.get())
        .pattern("gag")
        .pattern("g g")
        .pattern("ggg")
        .define('g', Items.GLASS)
        .define('a', Items.ARROW)
        .unlockedBy("has_glass", has(Items.GLASS))
        .save(output);
    ShapelessRecipeBuilder.shapeless(RecipeCategory.BREWING, ModItems.BANDAGE.get())
        .requires(Items.STRING)
        .requires(Items.STRING)
        .unlockedBy("has_string", has(Items.STRING))
        .save(output);

    // ================================================================================
    // Melee Weapons
    // ================================================================================

    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.BOWIE_KNIFE.get())
        .pattern(" s ")
        .pattern("k  ")
        .define('s', Items.STICK)
        .define('k', ModItems.COMBAT_KNIFE.get())
        .unlockedBy("has_combat_knife", has(ModItems.COMBAT_KNIFE.get()))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.COMBAT_KNIFE.get())
        .pattern("  i")
        .pattern(" i ")
        .pattern("s  ")
        .define('s', Items.STICK)
        .define('i', Tags.Items.INGOTS_IRON)
        .unlockedBy("has_stick", has(Items.STICK))
        .save(output);

    // ================================================================================
    // Gun Parts
    // ================================================================================

    ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.SMALL_BARREL.get())
        .pattern("  i")
        .pattern(" i ")
        .pattern("i  ")
        .define('i', Items.IRON_INGOT)
        .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.SMALL_BODY.get())
        .pattern("iii")
        .pattern("iri")
        .pattern("ii ")
        .define('i', Items.IRON_INGOT)
        .define('r', Items.REDSTONE)
        .unlockedBy("has_redstone", has(Items.REDSTONE))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.SMALL_HANDLE.get())
        .pattern("iii")
        .pattern("il ")
        .define('i', Items.IRON_INGOT)
        .define('l', Items.LEVER)
        .unlockedBy("has_lever", has(Items.LEVER))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.SMALL_STOCK.get())
        .pattern("iii")
        .pattern("iii")
        .pattern("ii ")
        .define('i', Items.IRON_INGOT)
        .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.MEDIUM_BARREL.get())
        .pattern("iii")
        .pattern("ibi")
        .pattern("iii")
        .define('i', Items.IRON_INGOT)
        .define('b', ModItems.SMALL_BARREL.get())
        .unlockedBy("has_small_barrel", has(ModItems.SMALL_BARREL.get()))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.MEDIUM_BODY.get())
        .pattern("iii")
        .pattern("ibi")
        .pattern("iii")
        .define('i', Items.IRON_INGOT)
        .define('b', ModItems.SMALL_BODY.get())
        .unlockedBy("has_small_body", has(ModItems.SMALL_BODY.get()))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.MEDIUM_HANDLE.get())
        .pattern("iii")
        .pattern("ihi")
        .pattern("iii")
        .define('i', Items.IRON_INGOT)
        .define('h', ModItems.SMALL_HANDLE.get())
        .unlockedBy("has_small_handle", has(ModItems.SMALL_HANDLE.get()))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.MEDIUM_STOCK.get())
        .pattern("iii")
        .pattern("isi")
        .pattern("iii")
        .define('i', Items.IRON_INGOT)
        .define('s', ModItems.SMALL_STOCK.get())
        .unlockedBy("has_small_stock", has(ModItems.SMALL_STOCK.get()))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.HEAVY_BARREL.get())
        .pattern("iii")
        .pattern("ibi")
        .pattern("iii")
        .define('i', Items.IRON_INGOT)
        .define('b', ModItems.MEDIUM_BARREL.get())
        .unlockedBy("has_medium_barrel", has(ModItems.MEDIUM_BARREL.get()))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.HEAVY_BODY.get())
        .pattern("iii")
        .pattern("ibi")
        .pattern("iii")
        .define('i', Items.IRON_INGOT)
        .define('b', ModItems.MEDIUM_BODY.get())
        .unlockedBy("has_medium_body", has(ModItems.MEDIUM_BODY.get()))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.HEAVY_HANDLE.get())
        .pattern("iii")
        .pattern("ihi")
        .pattern("iii")
        .define('i', Items.IRON_INGOT)
        .define('h', ModItems.MEDIUM_HANDLE.get())
        .unlockedBy("has_medium_handle", has(ModItems.MEDIUM_HANDLE.get()))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.MEDIUM_BOLT.get())
        .pattern("iii")
        .pattern("ii ")
        .define('i', Items.IRON_INGOT)
        .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
        .save(output);
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.HEAVY_BOLT.get())
        .pattern("iii")
        .pattern("ibi")
        .pattern("iii")
        .define('i', Items.IRON_INGOT)
        .define('b', ModItems.MEDIUM_BOLT.get())
        .unlockedBy("has_medium_bolt", has(ModItems.MEDIUM_BOLT.get()))
        .save(output);

    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.IRON_INGOT, 8)
        .requires(ModItemTags.MAGAZINES)
        .unlockedBy("has_magazine", has(ModItemTags.MAGAZINES))
        .save(output, ResourceLocation.fromNamespaceAndPath(CraftingDead.ID, "iron_ingot_from_magazine"));
  }

  
}
