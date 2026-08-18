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

package com.craftingdead.core.network.message.play;

import com.craftingdead.core.world.entity.extension.PlayerExtension;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

public record RightClickStateMessage(boolean isDown, int ticks) {

  public static void encode(RightClickStateMessage msg, FriendlyByteBuf buf) {
    buf.writeBoolean(msg.isDown());
    buf.writeInt(msg.ticks());
  }

  public static RightClickStateMessage decode(FriendlyByteBuf buf) {
    return new RightClickStateMessage(buf.readBoolean(), buf.readInt());
  }

  public static void handle(RightClickStateMessage msg, CustomPayloadEvent.Context ctx) {
    ctx.enqueueWork(() -> {
      var player = ctx.getSender();
      if (player == null || !player.isAlive()) {
        return;
      }
      var extension = PlayerExtension.getOrThrow(player);
      if (msg.isDown()) {
        extension.setHoldingRightClick(true);
        int fixedTicks = msg.ticks() > 0 ? msg.ticks() : extension.getRightClickTicks();
        extension.setRightClickTicks(fixedTicks);
      } else {
        extension.setHoldingRightClick(false);
        extension.setRightClickTicks(0);
      }
    });
    ctx.setPacketHandled(true);
  }
}

