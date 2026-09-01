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

import com.craftingdead.core.world.item.ModItems;
import com.craftingdead.core.world.item.gun.Gun;
import com.craftingdead.core.world.item.gun.ammoprovider.AmmoProvider;
import com.craftingdead.core.world.item.gun.ammoprovider.MagazineAmmoProvider;
import com.craftingdead.core.world.item.gun.ammoprovider.RefillableAmmoProvider;
import com.craftingdead.core.world.item.gun.magazine.Magazine;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

/**
 * 品质系统主事件处理器。
 *
 * <p>功能：
 * <ul>
 *   <li>合成工具 / 武器 / 盔甲时随机品质（自动兼容其他模组物品）</li>
 *   <li>合成镐 / 铲 / 斧 / 锄时随机材质</li>
 *   <li>品质越高伤害越高；剑固定 50 伤害且无冷却</li>
 *   <li>玩家首次进入世界给予初始奖励（新人奖励箱）</li>
 *   <li>创造弹药箱：持有时主手枪械无限弹药</li>
 *   <li>品质 / 材质悬浮提示</li>
 * </ul>
 */
public class QualityEventHandler {

  public static final QualityEventHandler INSTANCE = new QualityEventHandler();

  private QualityEventHandler() {}

  // ================================================================================
  // 合成：随机品质 + 随机属性（附魔）
  // ================================================================================

  @SubscribeEvent
  public void handleItemCrafted(PlayerEvent.ItemCraftedEvent event) {
    if (event.getEntity().level().isClientSide()) {
      return;
    }
    ItemStack result = event.getCrafting();
    if (result.isEmpty()) {
      return;
    }
    // 神话配方产物：保留神话品质，并附加全部适用附魔的满级（神话无视游戏规则）。
    // 注意：不能走下方随机品质，否则会覆盖神话品质。
    if (QualityHelper.isMythic(result)) {
      QualityHelper.applyFullEnchantments(result, event.getEntity().level());
      return;
    }
    // 普通合成工具 / 武器 / 盔甲：
    //  - 品质随机（按物品基础材质加权，材质越高优秀品质概率越高）
    //  - 属性（附魔）随机（品质越高，附魔数量越多、等级越高）
    // 注：合成物品不再随机分配"工具材质"——随机材质仅用于初始奖励装备。
    QualityHelper.applyRandomQualityForCrafted(result);
    QualityHelper.applyRandomAttributes(result, event.getEntity().level());
  }

  // ================================================================================
  // 伤害：品质倍率 + 剑 50 伤害无 CD + 工具材质加成
  // ================================================================================

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void handleLivingDamage(LivingDamageEvent.Pre event) {
    if (event.getEntity().level().isClientSide()) {
      return;
    }
    DamageSource source = event.getSource();
    if (!(source.getDirectEntity() instanceof LivingEntity attacker)) {
      return;
    }
    ItemStack weapon = attacker.getMainHandItem();
    if (weapon.isEmpty()) {
      return;
    }

    float amount = event.getNewDamage();
    float qualityMultiplier = QualityHelper.getQualityDamageMultiplier(weapon);

    if (QualityHelper.isSword(weapon)) {
      // 剑：固定 50 伤害（品质越高越高），无视攻击冷却（无 CD）
      amount = 50.0F * qualityMultiplier;
    } else if (QualityHelper.isQualityItem(weapon)) {
      amount *= qualityMultiplier;
    }

    // 工具材质攻击力加成（剑不叠加，已固定 50）
    ToolMaterialType material = QualityHelper.getToolMaterial(weapon);
    if (material != null && !QualityHelper.isSword(weapon)) {
      amount += material.getAttackBonus() * qualityMultiplier;
    }

    if (amount != event.getNewDamage()) {
      event.setNewDamage(amount);
    }
  }

  // ================================================================================
  // 玩家首次进入世界：给予新人奖励箱
  // ================================================================================

  @SubscribeEvent
  public void handlePlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
    if (event.getEntity().level().isClientSide()) {
      return;
    }
    Player player = event.getEntity();
    CompoundTag data = player.getPersistentData();
    if (data.getBoolean(QualityHelper.STARTER_GIVEN_KEY)) {
      return;
    }
    data.putBoolean(QualityHelper.STARTER_GIVEN_KEY, true);

    ItemStack rewardBox = new ItemStack(ModItems.STARTER_REWARD_BOX.get());
    if (!player.getInventory().add(rewardBox)) {
      player.drop(rewardBox, false);
    }
    player.displayClientMessage(
        Component.translatable("message.craftingdead.starter_given")
            .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
        true);
  }

  // ================================================================================
  // 创造弹药箱：持有时主手枪械无限弹药
  // ================================================================================

  @SubscribeEvent
  public void handlePlayerTick(PlayerTickEvent.Post event) {
    Player player = (Player) event.getEntity();
    if (player.level().isClientSide()) {
      return;
    }

    boolean hasBox = QualityHelper.isCreativeAmmoBox(player.getMainHandItem())
        || QualityHelper.isCreativeAmmoBox(player.getOffhandItem());

    ItemStack mainHand = player.getMainHandItem();
    var gun = mainHand.getCapability(Gun.CAPABILITY);
    if (gun != null) {
      AmmoProvider provider = gun.getAmmoProvider();
      boolean infiniteActive =
          provider instanceof RefillableAmmoProvider refillable && refillable.hasInfiniteAmmo();
      if (hasBox && !infiniteActive) {
        // 只有枪械已装填弹匣时才启用无限弹药：若枪械当前没有可用的弹匣
        // （弹匣栈为空或没有 Magazine 能力），强行切换为 RefillableAmmoProvider 后，
        // 玩家换弹完成时 getExpectedMagazine() 会抛出 "No magazine capability"，
        // 导致服务器 "Ticking player" 崩溃（见 logs 2026-08-24 crash）。
        ItemStack magazineStack = provider.getMagazineStack();
        if (!magazineStack.isEmpty()
            && magazineStack.getCapability(Magazine.CAPABILITY) != null) {
          gun.setAmmoProvider(new RefillableAmmoProvider(magazineStack, 0, true));
        }
      } else if (!hasBox && infiniteActive) {
        gun.setAmmoProvider(new MagazineAmmoProvider(provider.getMagazineStack()));
      }
    }
  }

  // ================================================================================
  // 悬浮提示：品质 + 工具材质
  // ================================================================================

  @SubscribeEvent
  public void handleTooltip(ItemTooltipEvent event) {
    ItemStack stack = event.getItemStack();
    if (stack.isEmpty()) {
      return;
    }

    ItemQuality quality = QualityHelper.getQuality(stack);
    if (quality != null) {
      event.getToolTip().add(1, quality.getDisplayName());
      if (quality == ItemQuality.MYTHIC) {
        // 神话专属描述：无视游戏规则
        event.getToolTip().add(2,
            Component.translatable("quality.craftingdead.mythic_tooltip")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
      }
    }

    ToolMaterialType material = QualityHelper.getToolMaterial(stack);
    if (material != null) {
      event.getToolTip().add(
          Component.translatable("tool_material.craftingdead.prefix")
              .withStyle(ChatFormatting.GRAY)
              .append(material.getDisplayName()));
    }
  }
}
