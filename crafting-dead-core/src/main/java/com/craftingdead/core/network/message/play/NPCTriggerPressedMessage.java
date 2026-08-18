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

import com.craftingdead.core.network.NetworkUtil;
import com.craftingdead.core.world.entity.extension.LivingExtension;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

public record NPCTriggerPressedMessage(int entityId, boolean triggerPressed) {

  public void encode(FriendlyByteBuf out) {
    out.writeVarInt(this.entityId);
    out.writeBoolean(this.triggerPressed);
  }

  public static NPCTriggerPressedMessage decode(FriendlyByteBuf in) {
    return new NPCTriggerPressedMessage(in.readVarInt(), in.readBoolean());
  }

  public static void handle(NPCTriggerPressedMessage msg, CustomPayloadEvent.Context ctx) {
    ctx.enqueueWork(() -> NetworkUtil.getEntityOrSender(ctx, msg.entityId)
        .getCapability(LivingExtension.CAPABILITY)
        .ifPresent(extension -> extension.mainHandGun()
            .ifPresent(gun -> gun.setNPCTriggerPressed(extension, msg.triggerPressed,
                ctx.isServerSide(), 0))));
  }
}
