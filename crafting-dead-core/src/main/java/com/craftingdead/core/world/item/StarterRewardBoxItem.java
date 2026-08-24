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

package com.craftingdead.core.world.item;

import com.craftingdead.core.quality.QualityHelper;
import com.craftingdead.core.world.item.gun.attachment.Attachments;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 新人奖励箱。
 *
 * <p>玩家首次进入世界时由系统自动发放。右键使用后开出：
 * <ul>
 *   <li>Kar98k 98k 狙击步枪（已装弹匣、已装专用倍镜）</li>
 *   <li>Kar98k 专用倍镜</li>
 *   <li>创造弹药箱</li>
 * </ul>
 *
 * <p>开出的物品均附带 5 种随机附魔属性且无耐久（-1）。
 */
public class StarterRewardBoxItem extends Item {

  public StarterRewardBoxItem(Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    ItemStack box = player.getItemInHand(hand);
    if (level.isClientSide()) {
      return InteractionResultHolder.success(box);
    }

    // 1. Kar98k（已装弹匣 + 已装专用倍镜）
    ItemStack kar98k = new ItemStack(ModItems.KAR98K.get());
    ItemStack magazine = new ItemStack(ModItems.KAR98K_AMMUNITION.get());
    QualityHelper.prepareRewardGun(kar98k, magazine, Attachments.KAR98K_SCOPE.get());
    this.enchantAndUnbreakable(level, kar98k);

    // 2. Kar98k 专用倍镜
    ItemStack scope = new ItemStack(ModItems.KAR98K_SCOPE.get());
    this.enchantAndUnbreakable(level, scope);

    // 3. 创造弹药箱
    ItemStack ammoBox = new ItemStack(ModItems.CREATIVE_AMMO_BOX.get());
    this.enchantAndUnbreakable(level, ammoBox);

    if (player instanceof ServerPlayer serverPlayer) {
      this.give(serverPlayer, kar98k);
      this.give(serverPlayer, scope);
      this.give(serverPlayer, ammoBox);
      serverPlayer.displayClientMessage(
          Component.translatable("message.craftingdead.reward_box_opened")
              .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
          true);
    }

    box.shrink(1);
    return InteractionResultHolder.success(box);
  }

  private void enchantAndUnbreakable(Level level, ItemStack stack) {
    QualityHelper.applyRandomEnchantments(stack, level, 5);
    QualityHelper.setUnbreakable(stack);
  }

  private void give(ServerPlayer player, ItemStack stack) {
    if (!player.getInventory().add(stack)) {
      player.drop(stack, false);
    }
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip,
      @NotNull TooltipFlag flag) {
    super.appendHoverText(stack, level, tooltip, flag);
    tooltip.add(Component.translatable("item.craftingdead.starter_reward_box.tooltip")
        .withStyle(ChatFormatting.GOLD));
    tooltip.add(Component.translatable("item.craftingdead.starter_reward_box.contents")
        .withStyle(ChatFormatting.GRAY));
  }

  @Override
  public boolean isFoil(ItemStack stack) {
    return true;
  }
}
