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

import com.craftingdead.core.quality.QualityHelper;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

/**
 * 神话升级配方。
 *
 * <p>3x3 合成网格（缺一不可）：
 * <pre>
 * 下界合金锭  钻石      下界合金锭
 * 钻石        核心物品   钻石
 * 下界合金锭  钻石      下界合金锭
 * </pre>
 *
 * <p>消耗下界合金系列武器 / 工具 / 盔甲（中心核心物品），产出对应神话品质版本。
 * 神话系列无视游戏规则：无耐久（-1）、最高伤害倍率、神话贴图（custom_model_data）。
 */
public class MythicUpgradeRecipe extends ShapedRecipe {

  private final ShapedRecipePattern mythicPattern;
  /** 展示用基准结果（JEI / 配方书显示为神话版本）。 */
  private final ItemStack baseResult;

  public MythicUpgradeRecipe(CraftingBookCategory category, ShapedRecipePattern pattern,
      ItemStack baseResult) {
    super("", category, pattern, baseResult, true);
    this.mythicPattern = pattern;
    this.baseResult = baseResult;
  }

  public ShapedRecipePattern mythicPattern() {
    return this.mythicPattern;
  }

  @Override
  public ItemStack assemble(CraftingInput input, HolderLookup.Provider registryAccess) {
    // 找到网格中的核心物品（非下界合金锭、非钻石的物品即下界合金武器/工具/盔甲）
    ItemStack core = ItemStack.EMPTY;
    for (int i = 0; i < input.size(); i++) {
      ItemStack stack = input.getItem(i);
      if (!stack.isEmpty() && !stack.is(Items.NETHERITE_INGOT) && !stack.is(Items.DIAMOND)) {
        core = stack;
        break;
      }
    }
    if (core.isEmpty()) {
      return ItemStack.EMPTY;
    }
    ItemStack result = core.copy();
    QualityHelper.applyMythicUpgrade(result);
    return result;
  }

  @Override
  public ItemStack getResultItem(HolderLookup.Provider registryAccess) {
    ItemStack result = this.baseResult.copy();
    QualityHelper.applyMythicUpgrade(result);
    return result;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return ModRecipeSerializers.MYTHIC_UPGRADE.get();
  }

  public static class Serializer implements RecipeSerializer<MythicUpgradeRecipe> {

    private static final MapCodec<MythicUpgradeRecipe> CODEC =
        RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                CraftingBookCategory.CODEC.fieldOf("category")
                    .orElse(CraftingBookCategory.MISC).forGetter(r -> r.category()),
                ShapedRecipePattern.MAP_CODEC.forGetter(MythicUpgradeRecipe::mythicPattern),
                ItemStack.SIMPLE_ITEM_CODEC.fieldOf("result")
                    .forGetter(r -> r.baseResult))
            .apply(instance, MythicUpgradeRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, MythicUpgradeRecipe> STREAM_CODEC =
        StreamCodec.composite(
            CraftingBookCategory.STREAM_CODEC, MythicUpgradeRecipe::category,
            ShapedRecipePattern.STREAM_CODEC, MythicUpgradeRecipe::mythicPattern,
            ItemStack.STREAM_CODEC, r -> r.baseResult,
            MythicUpgradeRecipe::new);

    @Override
    public MapCodec<MythicUpgradeRecipe> codec() {
      return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, MythicUpgradeRecipe> streamCodec() {
      return STREAM_CODEC;
    }
  }
}
