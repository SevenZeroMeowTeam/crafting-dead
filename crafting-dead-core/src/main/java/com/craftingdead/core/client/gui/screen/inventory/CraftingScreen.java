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


import net.minecraftforge.network.PacketDistributor;
import com.craftingdead.core.CraftingDead;
import com.craftingdead.core.network.NetworkChannel;
import com.craftingdead.core.network.message.play.OpenEquipmentMenuMessage;
import com.craftingdead.core.world.inventory.CraftingMenu;
import com.craftingdead.core.world.item.ModItems;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CraftingScreen extends EffectRenderingInventoryScreen<CraftingMenu> {

  private static final ResourceLocation CRAFTING =
      ResourceLocation.fromNamespaceAndPath(CraftingDead.ID, "textures/gui/container/crafting.png");
  private static final ResourceLocation EQUIPMENT =
      ResourceLocation.fromNamespaceAndPath(CraftingDead.ID, "textures/gui/container/equipment.png");

  private int oldMouseX;
  private int oldMouseY;

  public CraftingScreen(CraftingMenu menu, Inventory inventory, Component title) {
    super(menu, inventory, title);
  }

  @Override
  public void init() {
    super.init();
  }

  /**
   * Main render method, called every frame.
   */
  @Override
  public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
    super.render(guiGraphics, mouseX, mouseY, partialTicks);
    this.renderTooltip(guiGraphics, mouseX, mouseY);
    this.oldMouseX = mouseX;
    this.oldMouseY = mouseY;
  }

  /**
   * Renders the background of the crafting screen.
   */
  @Override
  protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
    RenderSystem.setShaderTexture(0, CRAFTING);
    guiGraphics.blit(CRAFTING, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

    RenderSystem.setShaderTexture(0, EQUIPMENT);
    guiGraphics.blit(EQUIPMENT, this.leftPos, this.topPos - 28, 183, 36, 28, 31);
    guiGraphics.blit(EQUIPMENT, this.leftPos + 30, this.topPos - 28, 211, 0, 29, 35);

    this.renderFakeItems(guiGraphics);
    this.renderInteractiveTooltips(guiGraphics, mouseX, mouseY);
    this.renderPlayerEntity(guiGraphics);
  }

  /**
   * Prevents rendering labels on the screen.
   */
  @Override
  protected void renderLabels(@NotNull GuiGraphics guiGraphics, int x, int y) {}

  /**
   * Renders fake items in the GUI.
   */
  private void renderFakeItems(GuiGraphics guiGraphics) {
    guiGraphics.renderItem(new ItemStack(ModItems.MEDIUM_BLUE_BACKPACK.get()),
        this.leftPos + 6, this.topPos - 20);
    guiGraphics.renderItem(new ItemStack(ModItems.PICKAXE.get()),
        this.leftPos + 36, this.topPos - 22);
  }

  /**
   * Renders tooltips for interactive elements based on mouse position.
   */
  private void renderInteractiveTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    if (this.isMouseOver(this.leftPos, this.topPos - 28, 29, 28, mouseX, mouseY)) {
      guiGraphics.renderTooltip(this.font, Component.translatable("inventory_inventory.information"), mouseX, mouseY);
    }
    if (this.isMouseOver(this.leftPos + 30, this.topPos - 28, 29, 28, mouseX, mouseY)) {
      guiGraphics.renderTooltip(this.font, Component.translatable("inventory_crafting.information"), mouseX, mouseY);
    }
  }

  /**
   * Renders the player's 3D model in the crafting screen.
   */
  private void renderPlayerEntity(GuiGraphics guiGraphics) {
    if (this.minecraft != null && this.minecraft.player != null) {
      int centerX = this.leftPos + 35;
      int centerY = this.topPos + 45;
      InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics,
          centerX - 37, centerY - 39, centerX + 37, centerY + 39,
          30, 0.0625F, this.oldMouseX, this.oldMouseY,
          this.minecraft.player);
    }
  }

  /**
   * Handles mouse click events.
   */
  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (this.isMouseOver(this.leftPos, this.topPos - 28, 29, 28, mouseX, mouseY)) {
      handleEquipmentMenuOpen();
      return true;
    }
    return super.mouseClicked(mouseX, mouseY, button);
  }

  /**
   * Checks if the mouse is over a given rectangular area.
   */
  private boolean isMouseOver(int x, int y, int width, int height, double mouseX, double mouseY) {
    return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
  }

  /**
   * Opens the equipment menu when the appropriate area is clicked.
   */
  private void handleEquipmentMenuOpen() {
    if (this.minecraft != null && this.minecraft.player != null) {
      this.minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.2F, 1.0F);
    }
    NetworkChannel.PLAY.getSimpleChannel().send(new OpenEquipmentMenuMessage(),
        PacketDistributor.SERVER.noArg());
  }

  /**
   * Called every tick to update the container.
   */
  @Override
  protected void containerTick() {
    super.containerTick();
    this.menu.updateResult();
  }
}

