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
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record EnableCombatModeMessage(boolean enabled) implements CustomPacketPayload {

  public static final CustomPacketPayload.Type<EnableCombatModeMessage> TYPE =
      new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CraftingDead.ID, "enable_combat_mode_message"));

  public static final StreamCodec<FriendlyByteBuf, EnableCombatModeMessage> STREAM_CODEC =
      StreamCodec.of((FriendlyByteBuf buf, EnableCombatModeMessage msg) -> msg.encode(buf), EnableCombatModeMessage::decode);

  @Override
  public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }


  public void encode(FriendlyByteBuf out) {
    out.writeBoolean(this.enabled);
  }

  public static EnableCombatModeMessage decode(FriendlyByteBuf in) {
    return new EnableCombatModeMessage(in.readBoolean());
  }

  public static void handle(EnableCombatModeMessage msg, IPayloadContext ctx) {
    ctx.enqueueWork(
        () -> PlayerExtension.getOrThrow(ctx.player()).setCombatModeEnabled(msg.enabled));
  }
}
