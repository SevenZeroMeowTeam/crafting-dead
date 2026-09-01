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


import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import com.craftingdead.core.world.entity.extension.EntitySnapshot;
import com.craftingdead.core.world.entity.extension.LivingExtension;
import com.craftingdead.core.world.entity.extension.PlayerExtension;
import com.craftingdead.core.world.item.gun.PendingHit;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ValidatePendingHitMessage(Map<Integer, Collection<PendingHit>> hits) implements CustomPacketPayload {

  public static final CustomPacketPayload.Type<ValidatePendingHitMessage> TYPE =
      new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CraftingDead.ID, "validate_pending_hit_message"));

  public static final StreamCodec<FriendlyByteBuf, ValidatePendingHitMessage> STREAM_CODEC =
      StreamCodec.of((FriendlyByteBuf buf, ValidatePendingHitMessage msg) -> msg.encode(buf), ValidatePendingHitMessage::decode);

  @Override
  public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }


  public void encode(FriendlyByteBuf out) {
    out.writeVarInt(this.hits.size());
    for (var hit : this.hits.entrySet()) {
      out.writeVarInt(hit.getKey());
      out.writeVarInt(hit.getValue().size());
      for (var value : hit.getValue()) {
        out.writeByte(value.tickOffset());
        value.playerSnapshot().encode(out);
        value.hitSnapshot().encode(out);
        out.writeVarLong(value.randomSeed());
        out.writeVarInt(value.shotCount());
      }
    }
  }

  public static ValidatePendingHitMessage decode(FriendlyByteBuf in) {
    final int hitsSize = in.readVarInt();
    var hits = new Int2ObjectLinkedOpenHashMap<Collection<PendingHit>>();
    for (int i = 0; i < hitsSize; i++) {
      int key = in.readVarInt();
      int valueSize = in.readVarInt();
      var value = new ObjectArrayList<PendingHit>();
      for (int j = 0; j < valueSize; j++) {
        value.add(new PendingHit(in.readByte(), EntitySnapshot.decode(in), EntitySnapshot.decode(in),
            in.readVarLong(), in.readVarInt()));
      }
      hits.put(key, value);
    }
    return new ValidatePendingHitMessage(hits);
  }

  public static void handle(ValidatePendingHitMessage msg, IPayloadContext ctx) {
    ctx.enqueueWork(() -> {
      var player = PlayerExtension.getOrThrow((ServerPlayer) ctx.player());
      var gun = player.mainHandGun();
      if (gun != null) {
        for (var hit : msg.hits.entrySet()) {
          var hitEntity = player.level().getEntity(hit.getKey());
          var hitLiving = hitEntity != null
              ? hitEntity.getCapability(LivingExtension.CAPABILITY) : null;
          if (hitLiving != null) {
            for (var value : hit.getValue()) {
              gun.validatePendingHit(player, hitLiving, value);
            }
          }
        }
      }
    });
  }
}
