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

package com.craftingdead.core.quality;

import com.craftingdead.core.world.item.ClothingItem;
import com.craftingdead.core.world.item.MeleeWeaponItem;
import com.craftingdead.core.world.item.ModItems;
import com.craftingdead.core.world.inventory.GunCraftSlotType;
import com.craftingdead.core.world.item.gun.Gun;
import com.craftingdead.core.world.item.gun.ammoprovider.MagazineAmmoProvider;
import com.craftingdead.core.world.item.gun.attachment.Attachment;
import com.craftingdead.core.world.item.gun.magazine.Magazine;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 品质系统的通用工具方法：读写物品 NBT、判定物品类型（自动兼容其他模组物品）、
 * 随机附魔、设置无耐久（-1）等。
 */
public final class QualityHelper {

  /** 品质在物品自定义 NBT 中的键。 */
  public static final String QUALITY_KEY = "cd_quality";
  /** 工具材质在物品自定义 NBT 中的键。 */
  public static final String TOOL_MATERIAL_KEY = "cd_tool_material";
  /** 玩家是否已领取初始奖励的持久化 NBT 键。 */
  public static final String STARTER_GIVEN_KEY = "cd_starter_given";

  private QualityHelper() {}

  // 1.19.2 的 ItemTags 没有工具/剑常量，手动创建（数据包标签在 minecraft:swords 等）
  private static final TagKey<Item> TAG_SWORDS =
      ItemTags.create(new ResourceLocation("minecraft", "swords"));
  private static final TagKey<Item> TAG_PICKAXES =
      ItemTags.create(new ResourceLocation("minecraft", "pickaxes"));
  private static final TagKey<Item> TAG_SHOVELS =
      ItemTags.create(new ResourceLocation("minecraft", "shovels"));
  private static final TagKey<Item> TAG_AXES =
      ItemTags.create(new ResourceLocation("minecraft", "axes"));
  private static final TagKey<Item> TAG_HOES =
      ItemTags.create(new ResourceLocation("minecraft", "hoes"));

  // ================================================================================
  // 品质读写
  // ================================================================================

  public static ItemQuality getQuality(ItemStack stack) {
    if (stack.isEmpty() || !stack.hasTag()) {
      return null;
    }
    CompoundTag tag = stack.getTag();
    if (tag != null && tag.contains(QUALITY_KEY)) {
      return ItemQuality.byName(tag.getString(QUALITY_KEY));
    }
    return null;
  }

  public static void setQuality(ItemStack stack, ItemQuality quality) {
    stack.getOrCreateTag().putString(QUALITY_KEY, quality.getName());
  }

  public static boolean hasQuality(ItemStack stack) {
    return getQuality(stack) != null;
  }

  public static float getQualityDamageMultiplier(ItemStack stack) {
    ItemQuality quality = getQuality(stack);
    return quality == null ? 1.0F : quality.getDamageMultiplier();
  }

  /**
   * 给物品随机一个品质（只有工具 / 武器 / 盔甲才需要）。
   */
  public static void applyRandomQuality(ItemStack stack) {
    if (isQualityItem(stack)) {
      setQuality(stack, ItemQuality.rollRandom());
    }
  }

  // ================================================================================
  // 工具材质读写
  // ================================================================================

  public static ToolMaterialType getToolMaterial(ItemStack stack) {
    if (stack.isEmpty() || !stack.hasTag()) {
      return null;
    }
    CompoundTag tag = stack.getTag();
    if (tag != null && tag.contains(TOOL_MATERIAL_KEY)) {
      return ToolMaterialType.byName(tag.getString(TOOL_MATERIAL_KEY));
    }
    return null;
  }

  public static void setToolMaterial(ItemStack stack, ToolMaterialType material) {
    stack.getOrCreateTag().putString(TOOL_MATERIAL_KEY, material.getName());
  }

  /**
   * 如果物品是镐 / 铲 / 斧 / 锄头，则随机分配一种材质。
   */
  public static void applyRandomToolMaterial(ItemStack stack) {
    if (isTool(stack)) {
      setToolMaterial(stack, ToolMaterialType.rollRandom());
    }
  }

  // ================================================================================
  // 物品类型判定（自动检测其他模组的工具 / 武器 / 盔甲）
  // ================================================================================

  /**
   * 是否为工具（镐 / 铲 / 斧 / 锄头）。通过类与物品标签判定，
   * 因此自动兼容其他模组添加的工具。
   */
  public static boolean isTool(ItemStack stack) {
    if (stack.isEmpty()) {
      return false;
    }
    var item = stack.getItem();
    return item instanceof DiggerItem || item instanceof HoeItem
        || stack.is(TAG_PICKAXES) || stack.is(TAG_SHOVELS)
        || stack.is(TAG_AXES) || stack.is(TAG_HOES);
  }

  /**
   * 是否为近战武器 / 剑。自动兼容其他模组的剑类物品。
   */
  public static boolean isWeapon(ItemStack stack) {
    if (stack.isEmpty()) {
      return false;
    }
    var item = stack.getItem();
    return item instanceof SwordItem || item instanceof MeleeWeaponItem
        || stack.is(TAG_SWORDS);
  }

  /**
   * 是否为盔甲（含本模组的服装 / 背心 / 帽子）。自动兼容其他模组的盔甲。
   */
  public static boolean isArmor(ItemStack stack) {
    if (stack.isEmpty()) {
      return false;
    }
    var item = stack.getItem();
    return item instanceof ArmorItem || item instanceof ClothingItem;
  }

  /**
   * 是否为品质系统生效的物品（工具 / 武器 / 盔甲）。
   */
  public static boolean isQualityItem(ItemStack stack) {
    return isTool(stack) || isWeapon(stack) || isArmor(stack);
  }

  /**
   * 是否为剑类（用于“剑伤害 50、无 CD”规则）。
   */
  public static boolean isSword(ItemStack stack) {
    if (stack.isEmpty()) {
      return false;
    }
    var item = stack.getItem();
    return item instanceof SwordItem || item instanceof MeleeWeaponItem
        || stack.is(TAG_SWORDS);
  }

  // ================================================================================
  // 附魔 / 耐久
  // ================================================================================

  /**
   * 给物品附加指定数量的随机附魔属性（仅选择对该物品适用的附魔）。
   */
  public static void applyRandomEnchantments(ItemStack stack, Level level, int count) {
    if (stack.isEmpty()) {
      return;
    }
    List<Enchantment> applicable = new ArrayList<>();
    for (Enchantment enchantment : ForgeRegistries.ENCHANTMENTS.getValues()) {
      try {
        if (enchantment.canEnchant(stack)) {
          applicable.add(enchantment);
        }
      } catch (Exception ignored) {
        // 某些模组附魔可能抛出异常，忽略
      }
    }
    if (applicable.isEmpty()) {
      return;
    }
    Collections.shuffle(applicable, ThreadLocalRandom.current());

    int added = 0;
    for (Enchantment enchantment : applicable) {
      if (added >= count) {
        break;
      }
      try {
        stack.enchant(enchantment, Math.max(1, enchantment.getMaxLevel()));
        added++;
      } catch (Exception ignored) {
        // 忽略不兼容的附魔
      }
    }
  }

  /**
   * 设置物品无耐久（-1）：不可破坏，永不消耗耐久。
   */
  public static void setUnbreakable(ItemStack stack) {
    if (stack.isEmpty()) {
      return;
    }
    stack.getOrCreateTag().putBoolean("Unbreakable", true);
  }

  /**
   * 是否是创造弹药箱。
   */
  public static boolean isCreativeAmmoBox(ItemStack stack) {
    return !stack.isEmpty() && stack.is(ModItems.CREATIVE_AMMO_BOX.get());
  }

  /**
   * 生成一件预先装好弹匣、装上指定配件的枪械物品（用于奖励）。
   */
  public static void prepareRewardGun(ItemStack gunStack, ItemStack magazineStack,
      Attachment attachment) {
    if (gunStack.isEmpty()) {
      return;
    }
    gunStack.getCapability(Gun.CAPABILITY)
        .ifPresent(gun -> {
          if (!magazineStack.isEmpty()) {
            magazineStack.getCapability(Magazine.CAPABILITY)
                .ifPresent(magazine -> magazine.setSize(magazine.getMaxSize()));
            gun.setAmmoProvider(new MagazineAmmoProvider(magazineStack));
          }
          if (attachment != null) {
            gun.setAttachments(java.util.Map.of(GunCraftSlotType.OVERBARREL_ATTACHMENT,
                attachment));
          }
        });
  }
}
