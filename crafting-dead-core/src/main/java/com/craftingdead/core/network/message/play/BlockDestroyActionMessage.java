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

import com.craftingdead.core.network.NetworkChannel;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public record BlockDestroyActionMessage(BlockPos pos) {

  public static void encode(BlockDestroyActionMessage msg, FriendlyByteBuf buf) {
    buf.writeBlockPos(msg.pos());
  }

  public static BlockDestroyActionMessage decode(FriendlyByteBuf buf) {
    return new BlockDestroyActionMessage(buf.readBlockPos());
  }

  public static void handle(BlockDestroyActionMessage msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      var player = ctx.get().getSender();
      if (player != null) {
        var level = player.getLevel();
        var state = level.getBlockState(msg.pos);
        if (!state.isAir()) {
          NetworkChannel.PLAY.getSimpleChannel().send(PacketDistributor.TRACKING_CHUNK.with(() ->
              level.getChunkAt(msg.pos())), new BlockDestroyParticleMessage(msg.pos(), state));
          player.getLevel().setBlock(msg.pos, Blocks.AIR.defaultBlockState(), 3);
        }
      }
    });
    ctx.get().setPacketHandled(true);
  }
}

