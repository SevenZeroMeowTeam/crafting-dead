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


import com.craftingdead.core.trauma.ProtectionConfig;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncProtectionConfigMessage(String serializedConfig) implements CustomPacketPayload {

  public static final CustomPacketPayload.Type<SyncProtectionConfigMessage> TYPE =
      new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CraftingDead.ID, "sync_protection_config_message"));

  public static final StreamCodec<FriendlyByteBuf, SyncProtectionConfigMessage> STREAM_CODEC =
      StreamCodec.of((FriendlyByteBuf buf, SyncProtectionConfigMessage msg) -> msg.encode(buf), SyncProtectionConfigMessage::decode);

  @Override
  public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }


  private static final int MAX_LENGTH = 1 << 15;

  public void encode(FriendlyByteBuf out) {
    out.writeUtf(Objects.requireNonNull(this.serializedConfig, "serializedConfig"), MAX_LENGTH);
  }

  public static SyncProtectionConfigMessage decode(FriendlyByteBuf in) {
    return new SyncProtectionConfigMessage(in.readUtf(MAX_LENGTH));
  }

  public static void handle(SyncProtectionConfigMessage msg, IPayloadContext ctx) {
    ctx.enqueueWork(() -> ProtectionConfig.applySerializedConfig(msg.serializedConfig));
  }
}
