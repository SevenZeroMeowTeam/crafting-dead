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
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;


import com.craftingdead.core.CraftingDead;
import com.craftingdead.core.trauma.TraumaSeverity;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TraumaPacket(TraumaSeverity severity, int aimSwayTicks, float aimSwayStrength) implements CustomPacketPayload {

  public static final CustomPacketPayload.Type<TraumaPacket> TYPE =
      new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CraftingDead.ID, "trauma_packet"));

  public static final StreamCodec<FriendlyByteBuf, TraumaPacket> STREAM_CODEC =
      StreamCodec.of((FriendlyByteBuf buf, TraumaPacket msg) -> msg.encode(buf), TraumaPacket::decode);

  @Override
  public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }


  public void encode(FriendlyByteBuf out) {
    out.writeVarInt(this.severity.ordinal());
    out.writeVarInt(this.aimSwayTicks);
    out.writeFloat(this.aimSwayStrength);
  }

  public static TraumaPacket decode(FriendlyByteBuf in) {
    var severity = TraumaSeverity.fromOrdinal(in.readVarInt());
    int aimSwayTicks = in.readVarInt();
    float aimSwayStrength = in.readFloat();
    return new TraumaPacket(severity, aimSwayTicks, aimSwayStrength);
  }

  public static void handle(TraumaPacket msg, IPayloadContext ctx) {
    ctx.enqueueWork(() -> CraftingDead.getInstance().getClientDist().handleTrauma(msg));
  }
}
