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


import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;
import com.craftingdead.core.world.item.equipment.Equipment;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public class StorageItem extends EquipmentItem {

  public static final int MAX_ROWS_TO_SHOW = 6;

  public static final net.minecraft.resources.ResourceLocation ARMOR_MODIFIER_ID =
      net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("craftingdead", "storage_armor_modifier");

  private final Multimap<Holder<Attribute>, AttributeModifier> attributeModifiers;
  private final Equipment.Slot slot;
  private final int itemRows;
  private final ItemHandlerMenuConstructor menuConstructor;
  private final Component component;

  public int getItemRows() {
    return this.itemRows;
  }

  public StorageItem(Properties properties) {
    super(properties);
    this.attributeModifiers = properties.attributeModifiers.build();
    this.slot = properties.slot;
    this.itemRows = properties.itemRows;
    this.menuConstructor = properties.menuConstructor;
    this.component = properties.component;
  }

  

  @Override
  public void appendHoverText(ItemStack itemStack, net.minecraft.world.item.Item.TooltipContext world, List<Component> lines,
      TooltipFlag tooltipFlag) {
    super.appendHoverText(itemStack, world, lines, tooltipFlag);

    if (this.component != null) {
      lines.add(this.component.copy().withStyle(ChatFormatting.GRAY));
    }

    var itemHandler = itemStack.getCapability(Capabilities.ItemHandler.ITEM);
    if (itemHandler != null) {
      int itemsBeyondLimit = 0;
      int itemsDisplayed = 0;

      for (int i = 0; i < itemHandler.getSlots(); i++) {
            var stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
              if (itemsDisplayed++ >= MAX_ROWS_TO_SHOW) {
                itemsBeyondLimit++;
                continue;
              }
              var amountText = Component.literal(stack.getCount() + "x ")
                  .withStyle(ChatFormatting.DARK_GRAY);
              var itemText = stack.getHoverName().plainCopy().withStyle(ChatFormatting.GRAY);
              // First item
              if (itemsDisplayed == 1) {
                lines.add(Component.literal(" "));
                lines.add(Component.translatable("storage_item.contents")
                    .withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
              }
              lines.add(amountText.append(itemText));
            }
          }

      if (itemsBeyondLimit > 0) {
        lines.add(Component.literal(". . . + " + itemsBeyondLimit)
            .withStyle(ChatFormatting.RED));
      }
    }
  }

  public static class Properties extends Item.Properties {

    private final ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> attributeModifiers =
        ImmutableMultimap.builder();
    private Equipment.Slot slot;
    private int itemRows;
    private ItemHandlerMenuConstructor menuConstructor;
    private Component component;

    public Properties attributeModifier(Holder<Attribute> attribute, AttributeModifier modifier) {
      this.attributeModifiers.put(attribute, modifier);
      return this;
    }

    public Properties slot(Equipment.Slot slot) {
      this.slot = slot;
      return this;
    }

    public Properties itemRows(int itemRows) {
      this.itemRows = itemRows;
      return this;
    }

    public Properties menuConstructor(ItemHandlerMenuConstructor menuConstructor) {
      this.menuConstructor = menuConstructor;
      return this;
    }

    public Properties toolTip(Component component) {
      this.component = component;
      return this;
    }
  }

  @FunctionalInterface
  public interface ItemHandlerMenuConstructor {

    @Nullable
    AbstractContainerMenu createMenu(int windowId, Inventory inventory, IItemHandler itemHandler);
  }

  public class Storage implements Equipment, MenuConstructor {

    private final ItemStack stack;

    public Storage(ItemStack stack) {
      this.stack = stack;
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
      // 使用数据组件承载的物品栏处理器，确保容器内容随物品保存 / 加载。
      IItemHandler itemHandler = this.stack.getCapability(Capabilities.ItemHandler.ITEM);
      if (itemHandler == null) {
        itemHandler = new ItemStackHandler(StorageItem.this.itemRows * 9);
      }
      return StorageItem.this.menuConstructor.createMenu(windowId, inventory, itemHandler);
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> attributeModifiers() {
      return StorageItem.this.attributeModifiers;
    }

    @Override
    public boolean isValidForSlot(Slot slot) {
      return slot == StorageItem.this.slot;
    }
  }
}
