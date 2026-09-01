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
import com.craftingdead.core.world.item.gun.Gun;
import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncGunContainerSlotMessage(int entityId, int slot, FriendlyByteBuf data) implements CustomPacketPayload {

  public static final CustomPacketPayload.Type<SyncGunContainerSlotMessage> TYPE =
      new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CraftingDead.ID, "sync_gun_container_slot_message"));

  public static final StreamCodec<FriendlyByteBuf, SyncGunContainerSlotMessage> STREAM_CODEC =
      StreamCodec.of((FriendlyByteBuf buf, SyncGunContainerSlotMessage msg) -> msg.encode(buf), SyncGunContainerSlotMessage::decode);

  @Override
  public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }


  public SyncGunContainerSlotMessage(int entityId, int slot, Gun gun, boolean writeAll,
      RegistryAccess registryAccess) {
    this(entityId, slot,
        new RegistryFriendlyByteBuf(new FriendlyByteBuf(Unpooled.buffer()), registryAccess));
    gun.encode(this.data, writeAll);
  }

  public void encode(FriendlyByteBuf out) {
    out.writeVarInt(this.entityId);
    out.writeShort(this.slot);
    out.writeVarInt(this.data.readableBytes());
    out.writeBytes(this.data);
  }

  public static SyncGunContainerSlotMessage decode(FriendlyByteBuf in) {
    int entityId = in.readVarInt();
    int slot = in.readShort();
    byte[] data = new byte[in.readVarInt()];
    in.readBytes(data);
    return new SyncGunContainerSlotMessage(entityId, slot,
        new FriendlyByteBuf(Unpooled.wrappedBuffer(data)));
  }

  public static void handle(SyncGunContainerSlotMessage msg, IPayloadContext ctx) {
    ctx.enqueueWork(() -> {
      var gun = NetworkUtil.getEntity(
          ctx, msg.entityId, Player.class).inventoryMenu.getSlot(msg.slot).getItem()
              .getCapability(Gun.CAPABILITY);
      if (gun != null) {
        gun.decode(msg.data);
      }
    });
  }
}
