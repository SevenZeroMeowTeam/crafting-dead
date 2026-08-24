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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

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

  // ================================================================================
  // 品质读写
  // ================================================================================

  public static ItemQuality getQuality(ItemStack stack) {
    if (stack.isEmpty() || !stack.has(DataComponents.CUSTOM_DATA)) {
      return null;
    }
    CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).getUnsafe();
    if (tag.contains(QUALITY_KEY)) {
      return ItemQuality.byName(tag.getString(QUALITY_KEY));
    }
    return null;
  }

  public static void setQuality(ItemStack stack, ItemQuality quality) {
    CustomData.update(DataComponents.CUSTOM_DATA, stack,
        tag -> tag.putString(QUALITY_KEY, quality.getName()));
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
    if (stack.isEmpty() || !stack.has(DataComponents.CUSTOM_DATA)) {
      return null;
    }
    CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).getUnsafe();
    if (tag.contains(TOOL_MATERIAL_KEY)) {
      return ToolMaterialType.byName(tag.getString(TOOL_MATERIAL_KEY));
    }
    return null;
  }

  public static void setToolMaterial(ItemStack stack, ToolMaterialType material) {
    CustomData.update(DataComponents.CUSTOM_DATA, stack,
        tag -> tag.putString(TOOL_MATERIAL_KEY, material.getName()));
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
        || stack.is(ItemTags.PICKAXES) || stack.is(ItemTags.SHOVELS)
        || stack.is(ItemTags.AXES) || stack.is(ItemTags.HOES);
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
        || stack.is(ItemTags.SWORDS);
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
        || stack.is(ItemTags.SWORDS);
  }

  // ================================================================================
  // 附魔 / 耐久
  // ================================================================================

  /**
   * 给物品附加指定数量的随机附魔属性（仅选择对该物品适用的附魔）。
   */
  public static void applyRandomEnchantments(ItemStack stack, Level level, int count) {
    if (stack.isEmpty() || level == null) {
      return;
    }
    Registry<Enchantment> registry = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
    List<Holder<Enchantment>> applicable = new ArrayList<>();
    for (Holder<Enchantment> holder : registry.holders().toList()) {
      try {
        if (holder.value().canEnchant(stack)) {
          applicable.add(holder);
        }
      } catch (Exception ignored) {
        // 某些模组附魔可能抛出异常，忽略
      }
    }
    if (applicable.isEmpty()) {
      return;
    }
    Collections.shuffle(applicable, ThreadLocalRandom.current());

    ItemEnchantments.Mutable enchantments =
        new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
    int added = 0;
    for (Holder<Enchantment> holder : applicable) {
      if (added >= count) {
        break;
      }
      try {
        enchantments.set(holder, Math.max(1, holder.value().getMaxLevel()));
        added++;
      } catch (Exception ignored) {
        // 忽略不兼容的附魔
      }
    }
    EnchantmentHelper.setEnchantments(stack, enchantments.toImmutable());
  }

  /**
   * 设置物品无耐久（-1）：不可破坏，永不消耗耐久。
   */
  public static void setUnbreakable(ItemStack stack) {
    if (stack.isEmpty()) {
      return;
    }
    stack.set(DataComponents.UNBREAKABLE, new Unbreakable(true));
  }

  /**
   * 是否是创造弹药箱（该物品在工具类中引用了本类，避免循环依赖直接按物品判定）。
   */
  public static boolean isCreativeAmmoBox(ItemStack stack) {
    return !stack.isEmpty() && stack.is(
        com.craftingdead.core.world.item.ModItems.CREATIVE_AMMO_BOX.get());
  }

  /**
   * 获取服务器等级（用于附魔查询），可能为 null。
   */
  public static ServerLevel asServerLevel(Level level) {
    return level instanceof ServerLevel serverLevel ? serverLevel : null;
  }

  /**
   * 生成一件预先装好弹匣、装上指定配件的枪械物品（用于奖励）。
   */
  public static void prepareRewardGun(ItemStack gunStack, ItemStack magazineStack,
      com.craftingdead.core.world.item.gun.attachment.Attachment attachment) {
    if (gunStack.isEmpty()) {
      return;
    }
    gunStack.getCapability(com.craftingdead.core.world.item.gun.Gun.CAPABILITY)
        .ifPresent(gun -> {
          if (!magazineStack.isEmpty()) {
            magazineStack.getCapability(
                com.craftingdead.core.world.item.gun.magazine.Magazine.CAPABILITY)
                .ifPresent(magazine -> magazine.setSize(magazine.getMaxSize()));
            gun.setAmmoProvider(
                new com.craftingdead.core.world.item.gun.ammoprovider.MagazineAmmoProvider(
                    magazineStack));
          }
          if (attachment != null) {
            gun.setAttachments(java.util.Map.of(
                com.craftingdead.core.world.inventory.GunCraftSlotType.OVERBARREL_ATTACHMENT,
                attachment));
          }
        });
  }
}
