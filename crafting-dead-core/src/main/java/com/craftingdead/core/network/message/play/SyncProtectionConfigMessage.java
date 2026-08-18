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

import com.craftingdead.core.trauma.ProtectionConfig;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

public record SyncProtectionConfigMessage(String serializedConfig) {

  private static final int MAX_LENGTH = 1 << 15;

  public void encode(FriendlyByteBuf out) {
    out.writeUtf(Objects.requireNonNull(this.serializedConfig, "serializedConfig"), MAX_LENGTH);
  }

  public static SyncProtectionConfigMessage decode(FriendlyByteBuf in) {
    return new SyncProtectionConfigMessage(in.readUtf(MAX_LENGTH));
  }

  public static void handle(SyncProtectionConfigMessage msg, CustomPayloadEvent.Context ctx) {
    ctx.enqueueWork(() -> ProtectionConfig.applySerializedConfig(msg.serializedConfig));
  }
}
