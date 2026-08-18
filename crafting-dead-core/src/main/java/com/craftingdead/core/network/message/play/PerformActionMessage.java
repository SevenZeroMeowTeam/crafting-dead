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

import java.util.function.Supplier;
import com.craftingdead.core.network.NetworkUtil;
import com.craftingdead.core.world.action.ActionType;
import com.craftingdead.core.world.entity.extension.LivingExtension;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.network.CustomPayloadEvent;

public record PerformActionMessage(ActionType<?> actionType, int performerEntityId,
    FriendlyByteBuf buf) {

  public void encode(FriendlyByteBuf out) {
    out.writeResourceLocation(
        com.craftingdead.core.world.action.ActionTypes.REGISTRY.get().getKey(this.actionType));
    out.writeVarInt(this.performerEntityId);
    out.writeVarInt(this.buf.readableBytes());
    out.writeBytes(this.buf);
    this.buf.release();
  }

  public static PerformActionMessage decode(FriendlyByteBuf in) {
    var actionType = com.craftingdead.core.world.action.ActionTypes.REGISTRY.get()
        .getValue(in.readResourceLocation());
    return new PerformActionMessage(java.util.Objects.requireNonNull(actionType), in.readVarInt(),
        new FriendlyByteBuf(in.readBytes(in.readVarInt())));
  }

  public static void handle(PerformActionMessage msg, CustomPayloadEvent.Context ctx) {
    ctx.enqueueWork(() -> {
      final var performerEntity =
          NetworkUtil.getEntityOrSender(ctx, msg.performerEntityId, LivingEntity.class);
      final var performer = LivingExtension.getOrThrow(performerEntity);
      final var serverSide = ctx.isServerSide();
      if (!serverSide || msg.actionType.isTriggeredByClient()) {
        performer.performAction(msg.actionType.decode(performer, msg.buf), serverSide);
      }
      msg.buf.release();
    });
  }
}
