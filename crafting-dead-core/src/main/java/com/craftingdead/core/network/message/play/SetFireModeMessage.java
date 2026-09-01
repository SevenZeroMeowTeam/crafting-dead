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
import com.craftingdead.core.CraftingDead;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;


import java.util.function.Supplier;
import com.craftingdead.core.network.NetworkUtil;
import com.craftingdead.core.world.entity.extension.LivingExtension;
import com.craftingdead.core.world.item.gun.FireMode;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetFireModeMessage(int entityId, FireMode fireMode) implements CustomPacketPayload {

  public static final CustomPacketPayload.Type<SetFireModeMessage> TYPE =
      new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CraftingDead.ID, "set_fire_mode_message"));

  public static final StreamCodec<FriendlyByteBuf, SetFireModeMessage> STREAM_CODEC =
      StreamCodec.of((FriendlyByteBuf buf, SetFireModeMessage msg) -> msg.encode(buf), SetFireModeMessage::decode);

  @Override
  public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }


  public void encode(FriendlyByteBuf out) {
    out.writeVarInt(this.entityId);
    out.writeEnum(this.fireMode);
  }

  public static SetFireModeMessage decode(FriendlyByteBuf in) {
    return new SetFireModeMessage(in.readVarInt(), in.readEnum(FireMode.class));
  }

  public static void handle(SetFireModeMessage msg, IPayloadContext ctx) {
    ctx.enqueueWork(() -> {
      var extension = NetworkUtil.getEntityOrSender(ctx, msg.entityId)
          .getCapability(LivingExtension.CAPABILITY);
      if (extension != null) {
        var gun = extension.mainHandGun();
        if (gun != null) {
          gun.setFireMode(extension, msg.fireMode, ctx.flow().isServerbound());
        }
      }
    });
  }
}
