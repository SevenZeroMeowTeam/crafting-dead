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

package com.craftingdead.core.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class SimpleButton extends Button {

  private int backgroundColour = 0x333333;

  public SimpleButton(int x, int y, int width, int height, Component text, OnPress action) {
    super(x, y, width, height, text, action, Button.DEFAULT_NARRATION);
    this.setFGColor(0xE3BE2B);
  }

  public void setBackgroundColour(int backgroundColour) {
    this.backgroundColour = backgroundColour;
  }

  @Override
  public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    final Minecraft minecraft = Minecraft.getInstance();
    guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.getWidth() + 1, this.getY() + this.getHeight() + 1,
        0x33000000);
    guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(),
        this.backgroundColour | Mth.ceil(this.alpha * 255.0F) << 24);
    guiGraphics.drawCenteredString(minecraft.font, this.getMessage(),
        this.getX() + this.getWidth() / 2, this.getY() + (this.getHeight() - 8) / 2,
        (this.active ? this.isHovered ? this.packedFGColor : 0xFFFFFF : 0xA0A0A0)
            | Mth.ceil(this.alpha * 255.0F) << 24);
  }
}
