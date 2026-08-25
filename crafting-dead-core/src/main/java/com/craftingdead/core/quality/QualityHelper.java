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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
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
  /** 神话物品使用的 custom_model_data 值（触发神话贴图模型覆盖）。 */
  public static final int MYTHIC_CUSTOM_MODEL_DATA = 15001;

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

  /**
   * 是否为神话品质。
   */
  public static boolean isMythic(ItemStack stack) {
    return getQuality(stack) == ItemQuality.MYTHIC;
  }

  /**
   * 获取物品的"基础材质等级"（0=未知/木，1=石，2=铁/金，3=钻石，4=下界合金）。
   * 用于合成时按材质高低调整品质出现概率：材质越高，优秀品质概率越高。
   */
  public static int getMaterialTier(ItemStack stack) {
    if (stack.isEmpty()) {
      return 0;
    }
    var item = stack.getItem();
    if (item instanceof DiggerItem digger) {
      return tierLevel(digger.getTier());
    }
    if (item instanceof SwordItem sword) {
      return tierLevel(sword.getTier());
    }
    if (item instanceof ArmorItem armor) {
      try {
        String material = armor.getMaterial().unwrapKey()
            .map(key -> key.location().getPath()).orElse("");
        return switch (material) {
          case "leather", "chainmail" -> 1;
          case "iron", "gold" -> 2;
          case "diamond" -> 3;
          case "netherite" -> 4;
          default -> 0;
        };
      } catch (Exception ignored) {
        return 0;
      }
    }
    return 0;
  }

  /**
   * 将工具等级映射为材质等级。模组自定义 Tier 无法直接比较，
   * 按使用次数估算（使用次数越多材质越好）。
   */
  private static int tierLevel(Tier tier) {
    if (tier == Tiers.NETHERITE) {
      return 4;
    }
    if (tier == Tiers.DIAMOND) {
      return 3;
    }
    if (tier == Tiers.IRON || tier == Tiers.GOLD) {
      return 2;
    }
    if (tier == Tiers.STONE) {
      return 1;
    }
    if (tier == Tiers.WOOD) {
      return 0;
    }
    int uses = tier.getUses();
    if (uses >= 2000) {
      return 4;
    }
    if (uses >= 1000) {
      return 3;
    }
    if (uses >= 500) {
      return 2;
    }
    if (uses >= 130) {
      return 1;
    }
    return 0;
  }

  /**
   * 按材质等级加权的品质随机抽取：材质越高，劣质/普通权重越低，高品质相对更容易出现。
   */
  public static ItemQuality rollQualityWeighted(int materialTier) {
    List<ItemQuality> qualities = List.of(ItemQuality.values());
    int penalty = Math.min(materialTier * 3, 10);
    int total = 0;
    int[] weights = new int[qualities.size()];
    for (int i = 0; i < qualities.size(); i++) {
      ItemQuality quality = qualities.get(i);
      // i 越大品质越低，被惩罚越多（劣质/普通权重降低）
      int reduction = (int) ((qualities.size() - 1 - i) / (double) (qualities.size() - 1) * penalty);
      weights[i] = Math.max(1, quality.getWeight() - reduction);
      total += weights[i];
    }
    int roll = ThreadLocalRandom.current().nextInt(total);
    for (int i = 0; i < qualities.size(); i++) {
      roll -= weights[i];
      if (roll < 0) {
        return qualities.get(i);
      }
    }
    return ItemQuality.COMMON;
  }

  /**
   * 合成物品品质随机：根据物品的基础材质加权（材质越高，优秀品质概率越高）。
   */
  public static void applyRandomQualityForCrafted(ItemStack stack) {
    if (isQualityItem(stack)) {
      setQuality(stack, rollQualityWeighted(getMaterialTier(stack)));
    }
  }

  /**
   * 将物品升级为神话品质：
   * <ul>
   *   <li>附加神话品质（最高伤害倍率）</li>
   *   <li>无耐久（-1），无视耐久规则</li>
   *   <li>设置 custom_model_data，触发神话贴图</li>
   * </ul>
   */
  public static void applyMythicUpgrade(ItemStack stack) {
    if (stack.isEmpty()) {
      return;
    }
    setQuality(stack, ItemQuality.MYTHIC);
    setUnbreakable(stack);
    // 1.21.1：custom_model_data 组件是 CustomModelData 记录类型
    stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(MYTHIC_CUSTOM_MODEL_DATA));
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

  /**
   * 按指定材质创建初始奖励用近战武器（剑），并附加随机品质。
   */
  public static ItemStack createSwordOfMaterial(ToolMaterialType material) {
    Item item = switch (material) {
      case WOOD -> Items.WOODEN_SWORD;
      case STONE -> Items.STONE_SWORD;
      case IRON -> Items.IRON_SWORD;
      case GOLD -> Items.GOLDEN_SWORD;
      case DIAMOND -> Items.DIAMOND_SWORD;
      case NETHERITE -> Items.NETHERITE_SWORD;
    };
    ItemStack stack = new ItemStack(item);
    setToolMaterial(stack, material);
    applyRandomQualityForCrafted(stack);
    return stack;
  }

  /**
   * 按指定材质创建初始奖励用镐子，并附加随机品质。
   */
  public static ItemStack createPickaxeOfMaterial(ToolMaterialType material) {
    Item item = switch (material) {
      case WOOD -> Items.WOODEN_PICKAXE;
      case STONE -> Items.STONE_PICKAXE;
      case IRON -> Items.IRON_PICKAXE;
      case GOLD -> Items.GOLDEN_PICKAXE;
      case DIAMOND -> Items.DIAMOND_PICKAXE;
      case NETHERITE -> Items.NETHERITE_PICKAXE;
    };
    ItemStack stack = new ItemStack(item);
    setToolMaterial(stack, material);
    applyRandomQualityForCrafted(stack);
    return stack;
  }

  /**
   * 按指定材质创建初始奖励用胸甲，并附加随机品质。
   */
  public static ItemStack createChestplateOfMaterial(ToolMaterialType material) {
    Item item = switch (material) {
      case WOOD -> Items.LEATHER_CHESTPLATE;
      case STONE -> Items.CHAINMAIL_CHESTPLATE;
      case IRON -> Items.IRON_CHESTPLATE;
      case GOLD -> Items.GOLDEN_CHESTPLATE;
      case DIAMOND -> Items.DIAMOND_CHESTPLATE;
      case NETHERITE -> Items.NETHERITE_CHESTPLATE;
    };
    ItemStack stack = new ItemStack(item);
    setToolMaterial(stack, material);
    applyRandomQualityForCrafted(stack);
    return stack;
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
   * 给物品附加指定数量的随机附魔属性（仅选择对该物品适用的附魔，满级）。
   */
  public static void applyRandomEnchantments(ItemStack stack, Level level, int count) {
    applyRandomEnchantments(stack, level, count, Integer.MAX_VALUE);
  }

  /**
   * 给物品附加指定数量的随机附魔属性（仅选择对该物品适用的附魔）。
   *
   * @param levelCap 附魔等级上限（1..levelCap 内随机），品质越高可出现的等级越高
   */
  public static void applyRandomEnchantments(ItemStack stack, Level level, int count,
      int levelCap) {
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
        int maxLevel = Math.max(1, holder.value().getMaxLevel());
        int levelToApply = Math.min(maxLevel,
            1 + ThreadLocalRandom.current().nextInt(Math.max(1, levelCap)));
        enchantments.set(holder, levelToApply);
        added++;
      } catch (Exception ignored) {
        // 忽略不兼容的附魔
      }
    }
    EnchantmentHelper.setEnchantments(stack, enchantments.toImmutable());
  }

  /**
   * 按物品品质随机"属性"（附魔）：品质越高，附魔数量越多、等级越高。
   * 神话品质附加全部适用附魔的满级（无视游戏规则）。
   */
  public static void applyRandomAttributes(ItemStack stack, Level level) {
    if (stack.isEmpty() || level == null || !isQualityItem(stack)) {
      return;
    }
    ItemQuality quality = getQuality(stack);
    if (quality == null) {
      return;
    }
    if (quality == ItemQuality.MYTHIC) {
      applyFullEnchantments(stack, level);
      return;
    }
    int count = 0;
    int levelCap = 1;
    switch (quality) {
      case LEGENDARY -> {
        count = 3 + ThreadLocalRandom.current().nextInt(2);
        levelCap = 4;
      }
      case HERO -> {
        count = 3;
        levelCap = 4;
      }
      case EPIC -> {
        count = 2 + ThreadLocalRandom.current().nextInt(2);
        levelCap = 3;
      }
      case RARE -> {
        count = 2;
        levelCap = 3;
      }
      case EXCELLENT -> {
        count = 1 + ThreadLocalRandom.current().nextInt(2);
        levelCap = 2;
      }
      case COMMON -> {
        count = ThreadLocalRandom.current().nextInt(2);
        levelCap = 1;
      }
      case POOR -> count = 0;
      default -> count = 0;
    }
    if (count > 0) {
      applyRandomEnchantments(stack, level, count, levelCap);
    }
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
   * 给物品附加所有适用附魔的最大等级（如弓的全套附魔：力量 V / 冲击 II / 火矢 /
   * 无限 / 耐久 III / 经验修补）。用于"新人奖励弓"等初始装备。
   */
  public static void applyFullEnchantments(ItemStack stack, Level level) {
    if (stack.isEmpty() || level == null) {
      return;
    }
    Registry<Enchantment> registry = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
    ItemEnchantments.Mutable enchantments =
        new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
    for (Holder<Enchantment> holder : registry.holders().toList()) {
      try {
        if (holder.value().canEnchant(stack)) {
          enchantments.set(holder, Math.max(1, holder.value().getMaxLevel()));
        }
      } catch (Exception ignored) {
        // 忽略不兼容 / 异常的模组附魔
      }
    }
    EnchantmentHelper.setEnchantments(stack, enchantments.toImmutable());
  }

  // ================================================================================
  // 跨模组物品辅助（TaCZ 枪械等，按注册表 id 软引用，未安装对应模组时安全降级）
  // ================================================================================

  private static final Map<String, String> MOD_DISPLAY_NAME_CACHE = new HashMap<>();

  /**
   * 自动获取模组的显示名称（如 "tacz" -&gt; "TaCZ"、"minecraft" -&gt; "Minecraft"）。
   *
   * <p>自动从已加载的模组清单（ModList）中查询，对其他模组无需手动配置即可生效；
   * 未找到对应模组时回退为命名空间本身。结果会被缓存，避免重复查询。
   */
  public static String getModDisplayName(String namespace) {
    if (namespace == null || namespace.isEmpty()) {
      return "?";
    }
    return MOD_DISPLAY_NAME_CACHE.computeIfAbsent(namespace, ns -> {
      if ("minecraft".equals(ns)) {
        return "Minecraft";
      }
      try {
        return ModList.get().getModContainerById(ns)
            .map(container -> container.getModInfo().getDisplayName())
            .orElse(ns);
      } catch (Exception ignored) {
        return ns;
      }
    });
  }

  /**
   * 解析其他模组注册的物品（按注册表 id），未安装或不存在时返回 null。
   */
  public static Item resolveItem(String namespace, String path) {
    Item item =
        ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath(namespace, path));
    return (item == null || item == Items.AIR) ? null : item;
  }

  /**
   * 创建一把 TaCZ（Timeless and Classics Zero）枪械。
   *
   * <p>TaCZ 1.1.x 使用统一的 {@code tacz:modern_kinetic_gun} 物品，通过物品
   * CustomData 中的 {@code GunId} 区分具体枪械，{@code GunCurrentAmmoCount}
   * 记录枪内已装填的弹药数。未安装 TaCZ 或枪械不存在时返回空栈。
   *
   * @param gunId        枪械 id，如 "tacz:ak47"（可省略命名空间）
   * @param currentAmmo  枪内预装填的弹药数量（弹匣容量）
   */
  public static ItemStack createTaCZGun(String gunId, int currentAmmo) {
    Item gunItem = resolveItem("tacz", "modern_kinetic_gun");
    if (gunItem == null) {
      return ItemStack.EMPTY;
    }
    ItemStack stack = new ItemStack(gunItem);
    final String id = gunId.contains(":") ? gunId : "tacz:" + gunId;
    CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
      tag.putString("GunId", id);
      tag.putInt("GunCurrentAmmoCount", Math.max(0, currentAmmo));
    });
    return stack;
  }

  /**
   * 创建 TaCZ 弹药（{@code tacz:ammo} + CustomData 中的 {@code AmmoId}）。
   * 未安装 TaCZ 或弹药不存在时返回空栈。
   *
   * @param ammoId  弹药 id，如 "tacz:762x39"（可省略命名空间）
   * @param count   弹药数量（每发占一个物品）
   */
  public static ItemStack createTaCZAmmo(String ammoId, int count) {
    Item ammoItem = resolveItem("tacz", "ammo");
    if (ammoItem == null) {
      return ItemStack.EMPTY;
    }
    ItemStack stack = new ItemStack(ammoItem, Math.max(1, count));
    final String id = ammoId.contains(":") ? ammoId : "tacz:" + ammoId;
    CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putString("AmmoId", id));
    return stack;
  }

  /**
   * 创建 TaCZ 配件（{@code tacz:attachment} + CustomData 中的 {@code AttachmentId}）。
   * 未安装 TaCZ 或配件不存在时返回空栈。
   *
   * @param attachmentId  配件 id，如 "tacz:scope_standard_8x"（可省略命名空间）
   */
  public static ItemStack createTaCZAttachment(String attachmentId) {
    Item attachmentItem = resolveItem("tacz", "attachment");
    if (attachmentItem == null) {
      return ItemStack.EMPTY;
    }
    ItemStack stack = new ItemStack(attachmentItem);
    final String id = attachmentId.contains(":") ? attachmentId : "tacz:" + attachmentId;
    CustomData.update(DataComponents.CUSTOM_DATA, stack,
        tag -> tag.putString("AttachmentId", id));
    return stack;
  }

  /**
   * 创建 TaCZ 全类型创造弹药盒（{@code tacz:ammo_box} + CustomData {@code AllTypeCreative=true}）。
   *
   * <p>该弹药盒包含所有弹药类型且无限量，可直接为任意枪械供弹。未安装 TaCZ 时返回空栈。
   */
  public static ItemStack createTaCZAllTypeCreativeAmmoBox() {
    Item boxItem = resolveItem("tacz", "ammo_box");
    if (boxItem == null) {
      return ItemStack.EMPTY;
    }
    ItemStack stack = new ItemStack(boxItem);
    CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
      tag.putBoolean("AllTypeCreative", true);
      tag.putBoolean("Creative", true);
    });
    return stack;
  }

  /**
   * 从 TaCZ 默认枪包中随机抽取一把枪械，并按该枪弹匣容量预装填弹药。
   * 未安装 TaCZ 时返回空栈。
   */
  public static ItemStack createRandomTaCZGun(RandomSource random) {
    java.util.Set<String> gunIds = TaCZGunData.getGunIds();
    if (gunIds.isEmpty()) {
      return ItemStack.EMPTY;
    }
    List<String> ids = new ArrayList<>(gunIds);
    String gunId = ids.get(random.nextInt(ids.size()));
    return createTaCZGun(gunId, TaCZGunData.getAmmoAmount(gunId));
  }

  /**
   * 为指定枪械随机生成对应可装配件（备件），每个配件槽位一件：
   * 瞄具（scope/sight）、枪口（muzzle/刺刀）、枪托（stock）、握把（grip）、激光（laser）、
   * 扩容弹匣（extended_mag）。未安装 TaCZ 或枪械未知时返回空列表。
   */
  public static List<ItemStack> createRandomTaCZAttachments(String gunId, RandomSource random) {
    List<ItemStack> result = new ArrayList<>();
    if (gunId == null) {
      return result;
    }
    Map<String, List<String>> bySlot = new HashMap<>();
    for (String attachmentId : TaCZGunData.getAttachments(gunId)) {
      String slot = taCzAttachmentSlot(attachmentId);
      if (slot == null) {
        continue;
      }
      bySlot.computeIfAbsent(slot, s -> new ArrayList<>()).add(attachmentId);
    }
    for (List<String> candidates : bySlot.values()) {
      String pick = candidates.get(random.nextInt(candidates.size()));
      ItemStack stack = createTaCZAttachment(pick);
      if (!stack.isEmpty()) {
        result.add(stack);
      }
    }
    return result;
  }

  /**
   * 生成随机 TaCZ 枪械 + 对应备件（可装配件）的完整新手套装。
   * 第一项为枪械，其余为配件。未安装 TaCZ 时返回空列表。
   */
  public static List<ItemStack> createTaCZStarterKit(RandomSource random) {
    List<ItemStack> kit = new ArrayList<>();
    ItemStack gun = createRandomTaCZGun(random);
    if (gun.isEmpty()) {
      return kit;
    }
    kit.add(gun);
    CustomData gunData = gun.get(DataComponents.CUSTOM_DATA);
    CompoundTag gunTag = gunData == null ? null : gunData.copyTag();
    String gunId = gunTag == null ? null : gunTag.getString("GunId");
    kit.addAll(createRandomTaCZAttachments(gunId, random));
    return kit;
  }

  /**
   * 按配件 id 判断其所属槽位（用于每槽随机一件备件）。非槽位配件返回 null。
   */
  private static String taCzAttachmentSlot(String attachmentId) {
    if (attachmentId.contains("scope") || attachmentId.contains("sight")) {
      return "scope";
    }
    if (attachmentId.contains("muzzle") || attachmentId.contains("bayonet")) {
      return "muzzle";
    }
    if (attachmentId.contains("stock")) {
      return "stock";
    }
    if (attachmentId.contains("grip")) {
      return "grip";
    }
    if (attachmentId.contains("laser")) {
      return "laser";
    }
    if (attachmentId.contains("mag")) {
      return "mag";
    }
    return null;
  }

  /**
   * 是否属于 TaCZ 命名空间下的枪械物品（TaCZ 枪械统一为 {@code tacz:modern_kinetic_gun}）。
   */
  public static boolean isTaCZGun(ItemStack stack) {
    if (stack.isEmpty()) {
      return false;
    }
    ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.getItem());
    return key != null && "tacz".equals(key.getNamespace());
  }

  /**
   * 从物品 NBT 中的 {@code GunId} 构造 TaCZ 枪械的显示名翻译组件
   * （{@code {namespace}.gun.{path}.name}，如 {@code tacz.gun.m1014.name}），
   * 由客户端按本地语言渲染。无法解析 GunId 时返回 {@code null}。
   */
  @Nullable
  public static Component getTaCZGunDisplayName(ItemStack stack) {
    if (stack.isEmpty()) {
      return null;
    }
    CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
    CompoundTag tag = customData == null ? null : customData.copyTag();
    if (tag == null || !tag.contains("GunId")) {
      return null;
    }
    ResourceLocation gunId = ResourceLocation.tryParse(tag.getString("GunId"));
    if (gunId == null) {
      return null;
    }
    return Component.translatable(gunId.getNamespace() + ".gun." + gunId.getPath() + ".name");
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
