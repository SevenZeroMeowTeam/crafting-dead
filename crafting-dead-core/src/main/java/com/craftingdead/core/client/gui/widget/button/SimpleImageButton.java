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

package com.craftingdead.core.client.gui.widget.button;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SimpleImageButton extends Button {

  private ResourceLocation textureLocation;

  public SimpleImageButton(int x, int y, int width, int height,
      ResourceLocation textureLocation, Component text, Button.OnPress actionListener) {
    super(x, y, width, height, text, actionListener, Button.DEFAULT_NARRATION);
    this.textureLocation = textureLocation;
  }

  @Override
  public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
    guiGraphics.blit(this.textureLocation, this.getX(), this.getY(), 0.0F, 0.0F, this.width, this.height, this.width, this.height);
    if (this.isHoveredOrFocused()) {
      final int opacity = Math.min((int) (this.alpha * 0.5F * 255.0F), 255);
      guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height,
          0xFFFFFF + (opacity << 24));
    }
  }
}
