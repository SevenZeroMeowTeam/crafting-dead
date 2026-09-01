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


import com.craftingdead.core.world.effect.ModMobEffects;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ParachuteSyncMessage(int entityId, boolean hasParachute) implements CustomPacketPayload {

  public static final CustomPacketPayload.Type<ParachuteSyncMessage> TYPE =
      new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CraftingDead.ID, "parachute_sync_message"));

  public static final StreamCodec<FriendlyByteBuf, ParachuteSyncMessage> STREAM_CODEC =
      StreamCodec.of((FriendlyByteBuf buf, ParachuteSyncMessage msg) -> msg.encode(buf), ParachuteSyncMessage::decode);

  @Override
  public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }


  public void encode(FriendlyByteBuf buf) {
    buf.writeInt(this.entityId());
    buf.writeBoolean(this.hasParachute());
  }

  public static ParachuteSyncMessage decode(FriendlyByteBuf buf) {
    return new ParachuteSyncMessage(buf.readInt(), buf.readBoolean());
  }

  public static void handle(ParachuteSyncMessage packet, IPayloadContext ctx) {
    ctx.enqueueWork(() -> {
      assert Minecraft.getInstance().level != null;
      Entity entity = Minecraft.getInstance().level.getEntity(packet.entityId());
      if (entity instanceof LivingEntity livingEntity) {
        if (packet.hasParachute()) {
          livingEntity.addEffect(new MobEffectInstance(ModMobEffects.PARACHUTE));
        } else {
          livingEntity.removeEffect(ModMobEffects.PARACHUTE);
        }
      }
    });
  }
}
