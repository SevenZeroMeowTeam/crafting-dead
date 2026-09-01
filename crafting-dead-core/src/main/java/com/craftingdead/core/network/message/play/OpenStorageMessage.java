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
import com.craftingdead.core.world.entity.extension.PlayerExtension;
import com.craftingdead.core.world.item.equipment.Equipment;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenStorageMessage(Equipment.Slot slot) implements CustomPacketPayload {

  public static final CustomPacketPayload.Type<OpenStorageMessage> TYPE =
      new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CraftingDead.ID, "open_storage_message"));

  public static final StreamCodec<FriendlyByteBuf, OpenStorageMessage> STREAM_CODEC =
      StreamCodec.of((FriendlyByteBuf buf, OpenStorageMessage msg) -> msg.encode(buf), OpenStorageMessage::decode);

  @Override
  public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }


  public void encode(FriendlyByteBuf out) {
    out.writeEnum(this.slot);
  }

  public static OpenStorageMessage decode(FriendlyByteBuf in) {
    return new OpenStorageMessage(in.readEnum(Equipment.Slot.class));
  }

  public static void handle(OpenStorageMessage msg, IPayloadContext ctx) {
    ctx.enqueueWork(
        () -> PlayerExtension.getOrThrow(ctx.player()).openMenu(msg.slot));
  }
}
