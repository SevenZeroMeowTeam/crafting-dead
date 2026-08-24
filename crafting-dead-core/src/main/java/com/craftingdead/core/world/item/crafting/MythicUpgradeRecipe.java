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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

/**
 * 神话升级配方（1.19.2 版）。
 *
 * <p>3x3 合成网格（缺一不可）：
 * <pre>
 * 下界合金锭  钻石      下界合金锭
 * 钻石        核心物品   钻石
 * 下界合金锭  钻石      下界合金锭
 * </pre>
 *
 * <p>消耗下界合金系列武器 / 工具 / 盔甲（中心核心物品），产出对应神话品质版本。
 * 神话系列无视游戏规则：无耐久（-1）、最高伤害倍率、神话贴图（CustomModelData）。
 */
public class MythicUpgradeRecipe extends ShapedRecipe {

  /** 展示用基准结果（JEI / 配方书显示为神话版本）。 */
  private final ItemStack baseResult;

  public MythicUpgradeRecipe(ResourceLocation id, int width, int height,
      NonNullList<Ingredient> recipeItems, ItemStack baseResult) {
    super(id, "", width, height, recipeItems, baseResult);
    this.baseResult = baseResult;
  }

  @Override
  public ItemStack assemble(CraftingContainer container) {
    // 找到网格中的核心物品（非下界合金锭、非钻石的物品即下界合金武器/工具/盔甲）
    ItemStack core = ItemStack.EMPTY;
    for (int i = 0; i < container.getContainerSize(); i++) {
      ItemStack stack = container.getItem(i);
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
  public ItemStack getResultItem() {
    // JEI / 配方书展示：神话版本
    ItemStack result = this.baseResult.copy();
    QualityHelper.applyMythicUpgrade(result);
    return result;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return ModRecipeSerializers.MYTHIC_UPGRADE.get();
  }

  public static class Serializer implements RecipeSerializer<MythicUpgradeRecipe> {

    @Override
    public MythicUpgradeRecipe fromJson(ResourceLocation id, JsonObject json) {
      // 解析 key（符号 -> 材料）
      Map<String, Ingredient> key = new HashMap<>();
      JsonObject keyObj = GsonHelper.getAsJsonObject(json, "key");
      for (Map.Entry<String, JsonElement> entry : keyObj.entrySet()) {
        if (entry.getKey().length() != 1) {
          throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey()
              + "' is an invalid symbol (must be 1 character only).");
        }
        if (" ".equals(entry.getKey())) {
          throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
        }
        key.put(entry.getKey(), Ingredient.fromJson(entry.getValue()));
      }
      key.put(" ", Ingredient.EMPTY);

      // 解析 pattern
      JsonArray patternArr = GsonHelper.getAsJsonArray(json, "pattern");
      String[] rows = new String[patternArr.size()];
      for (int i = 0; i < rows.length; i++) {
        rows[i] = GsonHelper.convertToString(patternArr.get(i), "pattern[" + i + "]");
      }
      if (rows.length == 0) {
        throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
      }
      int width = rows[0].length();
      int height = rows.length;
      NonNullList<Ingredient> ingredients = NonNullList.withSize(width * height, Ingredient.EMPTY);
      for (int i = 0; i < height; i++) {
        String row = rows[i];
        if (row.length() != width) {
          throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
        }
        for (int j = 0; j < width; j++) {
          String symbol = row.substring(j, j + 1);
          Ingredient ingredient = key.get(symbol);
          if (ingredient == null) {
            throw new JsonSyntaxException("Pattern references symbol '" + symbol
                + "' but it's not defined in the key");
          }
          ingredients.set(j + width * i, ingredient);
        }
      }

      ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
      return new MythicUpgradeRecipe(id, width, height, ingredients, result);
    }

    @Override
    public MythicUpgradeRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
      int width = buf.readVarInt();
      int height = buf.readVarInt();
      buf.readUtf(); // group
      NonNullList<Ingredient> ingredients = NonNullList.withSize(width * height, Ingredient.EMPTY);
      for (int i = 0; i < ingredients.size(); i++) {
        ingredients.set(i, Ingredient.fromNetwork(buf));
      }
      ItemStack result = buf.readItem();
      return new MythicUpgradeRecipe(id, width, height, ingredients, result);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, MythicUpgradeRecipe recipe) {
      buf.writeVarInt(recipe.getWidth());
      buf.writeVarInt(recipe.getHeight());
      buf.writeUtf(recipe.getGroup());
      for (Ingredient ingredient : recipe.getIngredients()) {
        ingredient.toNetwork(buf);
      }
      buf.writeItem(recipe.baseResult);
    }
  }
}
