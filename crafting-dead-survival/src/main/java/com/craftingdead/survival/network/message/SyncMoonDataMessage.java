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

package com.craftingdead.survival.network.message;

import com.craftingdead.survival.client.MoonDataHolder;
import com.craftingdead.survival.world.moon.MoonEventType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * 服务端 → 客户端的月亮 / 天数同步消息。
 */
public record SyncMoonDataMessage(int day, int timeOfDay, int moonPhase, int evolutionTier,
    MoonEventType eventType, boolean active) {

  public void encode(FriendlyByteBuf out) {
    out.writeVarInt(this.day);
    out.writeVarInt(this.timeOfDay);
    out.writeVarInt(this.moonPhase);
    out.writeVarInt(this.evolutionTier);
    out.writeEnum(this.eventType);
    out.writeBoolean(this.active);
  }

  public static SyncMoonDataMessage decode(FriendlyByteBuf in) {
    return new SyncMoonDataMessage(
        in.readVarInt(),
        in.readVarInt(),
        in.readVarInt(),
        in.readVarInt(),
        in.readEnum(MoonEventType.class),
        in.readBoolean());
  }

  public static void handle(SyncMoonDataMessage msg, CustomPayloadEvent.Context ctx) {
    ctx.enqueueWork(() -> {
      if (FMLEnvironment.dist.isClient()) {
        MoonDataHolder.update(msg);
      }
    });
  }
}
