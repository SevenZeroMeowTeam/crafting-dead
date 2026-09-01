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


import java.util.function.Supplier;
import com.craftingdead.core.CraftingDead;
import com.craftingdead.core.world.damagesource.KillFeedEntry;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record AddKillFeedEntryMessage(KillFeedEntry entry) implements CustomPacketPayload {

  public static final CustomPacketPayload.Type<AddKillFeedEntryMessage> TYPE =
      new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CraftingDead.ID, "add_kill_feed_entry_message"));

  public static final StreamCodec<FriendlyByteBuf, AddKillFeedEntryMessage> STREAM_CODEC =
      StreamCodec.of((FriendlyByteBuf buf, AddKillFeedEntryMessage msg) -> msg.encode(buf), AddKillFeedEntryMessage::decode);

  @Override
  public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }


  public void encode(FriendlyByteBuf out) {
    this.entry.encode(out);
  }

  public static AddKillFeedEntryMessage decode(FriendlyByteBuf in) {
    return new AddKillFeedEntryMessage(KillFeedEntry.decode(in));
  }

  public static void handle(AddKillFeedEntryMessage msg, IPayloadContext ctx) {
    ctx.enqueueWork(() -> CraftingDead.getInstance().getClientDist().getIngameGui()
        .addKillFeedEntry(msg.entry));
  }
}
