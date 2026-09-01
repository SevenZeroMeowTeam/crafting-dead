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

package com.craftingdead.core.world.inventory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

/**
 * 物品栏一键整理工具：合并可堆叠同类物品，再按物品名称排序。
 *
 * <p>用于容器（背包 / 背心 / 枪袋）与玩家背包。整理时尊重容器的槽位
 * 校验规则（如背心不允许放入枪械 / 储物类物品）。
 */
public final class InventorySorter {

  private static final Comparator<ItemStack> ITEM_COMPARATOR = Comparator
      .comparing((ItemStack stack) -> stack.getDescriptionId())
      .thenComparing(ItemStack::getCount);

  private InventorySorter() {}

  /**
   * 整理指定菜单：先整理容器内容，再整理玩家 36 格背包。
   */
  public static void sortMenu(AbstractMenu menu) {
    if (menu.getContents() != null) {
      sortItemHandler(menu.getContents(), menu::mayPlaceInContainer);
    }
    sortPlayerInventory(menu.getPlayerInventory());
  }

  /**
   * 整理玩家背包（主物品栏 + 快捷栏共 36 格），不影响盔甲与副手。
   */
  public static void sortPlayerInventory(@Nullable Container playerInventory) {
    if (playerInventory == null) {
      return;
    }
    int size = playerInventory.getContainerSize();
    List<ItemStack> stacks = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      ItemStack stack = playerInventory.getItem(i);
      if (!stack.isEmpty()) {
        stacks.add(stack);
      }
    }
    List<ItemStack> merged = mergeAndSort(stacks);
    for (int i = 0; i < size; i++) {
      playerInventory.setItem(i, i < merged.size() ? merged.get(i) : ItemStack.EMPTY);
    }
  }

  /**
   * 整理容器物品处理器：不满足槽位校验的物品留在原位，其余合并排序后从 0 号槽开始堆放。
   */
  private static void sortItemHandler(IItemHandler handler, @Nullable Predicate<ItemStack> filter) {
    if (!(handler instanceof IItemHandlerModifiable modifiable)) {
      return;
    }
    int size = handler.getSlots();
    boolean[] keepInPlace = new boolean[size];
    List<ItemStack> sortable = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      ItemStack stack = handler.getStackInSlot(i);
      if (stack.isEmpty()) {
        continue;
      }
      if (filter != null && !filter.test(stack)) {
        keepInPlace[i] = true;
        continue;
      }
      sortable.add(stack);
    }
    List<ItemStack> merged = mergeAndSort(sortable);
    int index = 0;
    for (int i = 0; i < size; i++) {
      if (keepInPlace[i]) {
        continue;
      }
      modifiable.setStackInSlot(i, index < merged.size() ? merged.get(index++) : ItemStack.EMPTY);
    }
  }

  /**
   * 合并可堆叠同类物品，然后按物品名称排序。
   */
  private static List<ItemStack> mergeAndSort(List<ItemStack> stacks) {
    List<ItemStack> result = new ArrayList<>();
    for (ItemStack stack : stacks) {
      if (stack.isEmpty()) {
        continue;
      }
      if (stack.getMaxStackSize() > 1) {
        for (ItemStack target : result) {
          if (target.isEmpty()) {
            continue;
          }
          if (ItemStack.isSameItemSameComponents(target, stack)) {
            int moved = Math.min(stack.getCount(), target.getMaxStackSize() - target.getCount());
            if (moved > 0) {
              target.grow(moved);
              stack.shrink(moved);
              if (stack.isEmpty()) {
                break;
              }
            }
          }
        }
      }
      if (!stack.isEmpty()) {
        result.add(stack);
      }
    }
    result.sort(ITEM_COMPARATOR);
    return result;
  }
}
