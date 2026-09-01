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


import com.craftingdead.core.world.entity.extension.PlayerExtension;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.Supplier;

public record DamageHandcuffsMessage() implements CustomPacketPayload {

  public static final CustomPacketPayload.Type<DamageHandcuffsMessage> TYPE =
      new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CraftingDead.ID, "damage_handcuffs_message"));

  public static final StreamCodec<FriendlyByteBuf, DamageHandcuffsMessage> STREAM_CODEC =
      StreamCodec.of((FriendlyByteBuf buf, DamageHandcuffsMessage msg) -> msg.encode(buf), DamageHandcuffsMessage::decode);

  @Override
  public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }


  public void encode(FriendlyByteBuf buf) {
  }

  public static DamageHandcuffsMessage decode(FriendlyByteBuf buf) {
    return new DamageHandcuffsMessage();
  }

  public static void handle(DamageHandcuffsMessage msg, IPayloadContext context) {
    context.enqueueWork(() -> {
      ServerPlayer player = (ServerPlayer) context.player();
      if (player != null) {
        var playerExtension = PlayerExtension.getOrThrow(player);
        if (playerExtension.isHandcuffed()) {
          playerExtension.handcuffInteract();
        }
      }
    });
  }
}

