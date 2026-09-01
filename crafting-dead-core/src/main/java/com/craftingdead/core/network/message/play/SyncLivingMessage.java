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
import com.craftingdead.core.network.NetworkUtil;
import com.craftingdead.core.world.entity.extension.LivingExtension;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncLivingMessage(int entityId, FriendlyByteBuf data) implements CustomPacketPayload {

  public static final CustomPacketPayload.Type<SyncLivingMessage> TYPE =
      new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CraftingDead.ID, "sync_living"));

  public static final StreamCodec<FriendlyByteBuf, SyncLivingMessage> STREAM_CODEC =
      StreamCodec.of((FriendlyByteBuf buf, SyncLivingMessage msg) -> msg.encode(buf), SyncLivingMessage::decode);

  @Override
  public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }

  public void encode(FriendlyByteBuf out) {
    out.writeVarInt(this.entityId);
    out.writeVarInt(this.data.readableBytes());
    // Copy without advancing the reader index: encode() may be invoked more than once
    // (NeoForge's GenericPacketSplitter encodes the packet once to measure its size,
    // then the real PacketEncoder encodes it again). Consuming this.data would send an
    // empty payload on the second pass and crash the client's decode with an
    // IndexOutOfBoundsException (EmptyByteBuf.readShort).
    out.writeBytes(this.data, this.data.readerIndex(), this.data.readableBytes());
  }

  public static SyncLivingMessage decode(FriendlyByteBuf in) {
    return new SyncLivingMessage(in.readVarInt(),
        new FriendlyByteBuf(Unpooled.wrappedBuffer(in.readBytes(in.readVarInt()))));
  }

  public static void handle(SyncLivingMessage msg, IPayloadContext ctx) {
    ctx.enqueueWork(() -> {
      var entity = NetworkUtil.getEntity(ctx, msg.entityId);
      var living = entity.getCapability(LivingExtension.CAPABILITY);
      if (living != null) {
        living.decode(new RegistryFriendlyByteBuf(msg.data, entity.level().registryAccess()));
      }
    });
  }
}
