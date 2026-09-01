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


import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.PacketDistributor;

public record BlockDestroyActionMessage(BlockPos pos) implements CustomPacketPayload {

  public static final CustomPacketPayload.Type<BlockDestroyActionMessage> TYPE =
      new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CraftingDead.ID, "block_destroy_action_message"));

  public static final StreamCodec<FriendlyByteBuf, BlockDestroyActionMessage> STREAM_CODEC =
      StreamCodec.of((FriendlyByteBuf buf, BlockDestroyActionMessage msg) -> msg.encode(buf), BlockDestroyActionMessage::decode);

  @Override
  public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }


  public void encode(FriendlyByteBuf buf) {
    buf.writeBlockPos(this.pos());
  }

  public static BlockDestroyActionMessage decode(FriendlyByteBuf buf) {
    return new BlockDestroyActionMessage(buf.readBlockPos());
  }

  public static void handle(BlockDestroyActionMessage msg, IPayloadContext ctx) {
    ctx.enqueueWork(() -> {
      var player = ctx.player();
      if (player != null) {
        var level = player.level();
        var state = level.getBlockState(msg.pos);
        if (!state.isAir()) {
          PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level,
              new ChunkPos(msg.pos()), new BlockDestroyParticleMessage(msg.pos(), state));
          player.level().setBlock(msg.pos, Blocks.AIR.defaultBlockState(), 3);
        }
      }
    });
    
  }
}

