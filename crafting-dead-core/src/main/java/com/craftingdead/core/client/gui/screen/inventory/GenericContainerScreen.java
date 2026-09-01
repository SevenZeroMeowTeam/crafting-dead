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

package com.craftingdead.core.client.gui.screen.inventory;


import net.neoforged.neoforge.network.PacketDistributor;
import com.craftingdead.core.CraftingDead;
import com.craftingdead.core.client.gui.widget.button.CompositeButton;
import com.craftingdead.core.network.NetworkChannel;
import com.craftingdead.core.network.message.play.OpenEquipmentMenuMessage;
import com.craftingdead.core.network.message.play.SortInventoryMessage;
import com.craftingdead.core.world.inventory.AbstractMenu;
import com.craftingdead.core.world.inventory.GenericMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

public class GenericContainerScreen extends AbstractContainerScreen<GenericMenu> {

  private static final ResourceLocation GENERIC_CONTAINER_TEXTURE =
      ResourceLocation.fromNamespaceAndPath(CraftingDead.ID, "textures/gui/container/generic_54.png");

  private static final ResourceLocation SORT_BUTTON_TEXTURE =
      ResourceLocation.fromNamespaceAndPath(CraftingDead.ID, "textures/gui/sort_button.png");

  private static final int TITLE_TEXT_COLOUR = 0x000000;
  // Implementations may change this field to another action for the return button
  @Nullable
  protected Consumer<Button> returnButtonAction = (button) -> PacketDistributor.sendToServer(new OpenEquipmentMenuMessage());
  private CompositeButton returnButton;
  private CompositeButton sortButton;

  public GenericContainerScreen(GenericMenu menu, Inventory playerInventory,
      Component title) {
    super(menu, playerInventory, title);
    this.imageHeight = 114 + this.menu.getRows() * AbstractMenu.SLOT_SIZE;
  }

  @Override
  public void init() {
    super.init();
    Consumer<Button> action = returnButtonAction == null ? (button) -> {} : returnButtonAction;
    this.returnButton = CompositeButton.button(this.leftPos + 157, this.topPos - 1, 12, 16,
            GENERIC_CONTAINER_TEXTURE)
        .setAtlasPos(244, 0)
        .setHoverAtlasPos(231, 0)
        .setInactiveAtlasPos(219, 0)
        .setAction(action::accept).build();
    this.returnButton.active = returnButtonAction != null;
    this.addRenderableWidget(returnButton);

    // 一键整理按钮：合并同类 + 按名称排序（容器 + 玩家背包）
    this.sortButton = CompositeButton.button(this.leftPos + 144, this.topPos - 1, 12, 16,
            SORT_BUTTON_TEXTURE)
        .setAtlasPos(0, 0)
        .setHoverAtlasPos(0, 16)
        .setInactiveAtlasPos(0, 32)
        .setAction((button) -> PacketDistributor.sendToServer(new SortInventoryMessage()))
        .build();
    this.addRenderableWidget(this.sortButton);
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
    super.render(guiGraphics, mouseX, mouseY, partialTicks);
    if (this.sortButton != null && this.sortButton.isHoveredOrFocused()) {
      guiGraphics.renderTooltip(this.font, Component.translatable("gui.craftingdead.sort"),
          mouseX, mouseY);
    }
    this.renderTooltip(guiGraphics, mouseX, mouseY);
  }

  @Override
  protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    guiGraphics.drawString(this.font, this.title, 8, 6, TITLE_TEXT_COLOUR, false);
    guiGraphics.drawString(this.font, this.playerInventoryTitle, 8,
        this.imageHeight - 96 + 2, TITLE_TEXT_COLOUR, false);
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShaderTexture(0, GENERIC_CONTAINER_TEXTURE);
    int x = (this.width - this.imageWidth) / 2;
    int y = (this.height - this.imageHeight - 8) / 2;
    int heightOffset = (6 * AbstractMenu.SLOT_SIZE + AbstractMenu.SLOT_SIZE)
        - (this.menu.getRows() * AbstractMenu.SLOT_SIZE + AbstractMenu.SLOT_SIZE);
    guiGraphics.blit(GENERIC_CONTAINER_TEXTURE, x, y, 0, 0, this.imageWidth, 21);
    guiGraphics.blit(GENERIC_CONTAINER_TEXTURE, x, y + 21, 0, 21 + heightOffset, this.imageWidth,
        96 + (this.menu.getRows() * AbstractMenu.SLOT_SIZE + AbstractMenu.SLOT_SIZE));
  }
}
