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
import com.craftingdead.core.quality.QualityHelper;
import com.craftingdead.core.quality.ToolMaterialType;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 新人奖励箱。
 *
 * <p>玩家首次进入世界时由系统自动发放。右键使用后开出：
 * <ul>
 *   <li>TaCZ（Timeless and Classics Zero）模组随机枪械（预装填弹匣）</li>
 *   <li>该枪对应的可装配件（瞄具 / 枪口 / 枪托 / 握把 / 激光 / 扩容弹匣，每槽一件）</li>
 *   <li>TaCZ 全类型创造弹药盒（包含所有弹药类型且无限量）</li>
 *   <li>原版弓：全套附魔（力量 V / 冲击 II / 火矢 / 无限 / 耐久 III / 经验修补）、无耐久</li>
 *   <li>箭 ×1（配合无限附魔使用，一支即可）</li>
 *   <li>随机材质的近战武器（剑）+ 随机品质</li>
 *   <li>随机材质的镐子 + 随机品质</li>
 *   <li>随机材质的胸甲 + 随机品质</li>
 * </ul>
 *
 * <p>初始装备武器使用其他模组的枪械（而非本模组自带武器）；
 * 由于 TaCZ 不含弓与箭，另给予一把全套附魔（含无限）、无耐久的弓和一支箭。
 * 武器 / 工具 / 盔甲的材质为随机，品质为随机。
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

    // 1. TaCZ（其他模组）：随机枪械 + 对应备件（每槽一件配件）
    //    弹药由全类型创造弹药盒（包含所有弹药类型且无限）替代；
    //    若未安装 TaCZ 则返回空列表，跳过（弓作为兜底远程武器仍然发放）。
    List<ItemStack> taCzKit = QualityHelper.createTaCZStarterKit(level.random);
    ItemStack taCzGun = taCzKit.isEmpty() ? ItemStack.EMPTY : taCzKit.get(0);
    List<ItemStack> taCzAttachments =
        taCzKit.size() > 1 ? taCzKit.subList(1, taCzKit.size()) : List.of();
    ItemStack taCzAmmoBox = QualityHelper.createTaCZAllTypeCreativeAmmoBox();

    // 2. 原版弓：全套附魔（含无限）+ 无耐久
    ItemStack bow = new ItemStack(Items.BOW);
    QualityHelper.applyFullEnchantments(bow, level);
    QualityHelper.setUnbreakable(bow);

    // 3. 箭：一支即可（无限附魔下射击不消耗箭矢）
    ItemStack arrow = new ItemStack(Items.ARROW, 1);

    // 4. 初始奖励武器 / 工具 / 盔甲：材质随机 + 品质随机 + 属性（附魔）随机
    ItemStack melee = QualityHelper.createSwordOfMaterial(ToolMaterialType.rollRandom());
    ItemStack pickaxe = QualityHelper.createPickaxeOfMaterial(ToolMaterialType.rollRandom());
    ItemStack chestplate = QualityHelper.createChestplateOfMaterial(ToolMaterialType.rollRandom());
    QualityHelper.applyRandomAttributes(melee, level);
    QualityHelper.applyRandomAttributes(pickaxe, level);
    QualityHelper.applyRandomAttributes(chestplate, level);

    if (player instanceof ServerPlayer serverPlayer) {
      if (!taCzGun.isEmpty()) {
        this.give(serverPlayer, taCzGun);
      }
      for (ItemStack attachment : taCzAttachments) {
        if (!attachment.isEmpty()) {
          this.give(serverPlayer, attachment);
        }
      }
      if (!taCzAmmoBox.isEmpty()) {
        this.give(serverPlayer, taCzAmmoBox);
      }
      this.give(serverPlayer, bow);
      this.give(serverPlayer, arrow);
      this.give(serverPlayer, melee);
      this.give(serverPlayer, pickaxe);
      this.give(serverPlayer, chestplate);
      serverPlayer.displayClientMessage(
          Component.translatable("message.craftingdead.reward_box_opened")
              .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
          true);
    }

    box.shrink(1);
    return InteractionResultHolder.success(box);
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
