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

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

public record BlockDestroyParticleMessage(BlockPos pos, BlockState state) {

  public static void encode(BlockDestroyParticleMessage msg, FriendlyByteBuf buf) {
    buf.writeBlockPos(msg.pos());
    buf.writeVarInt(Block.getId(msg.state()));
  }

  public static BlockDestroyParticleMessage decode(FriendlyByteBuf buf) {
    return new BlockDestroyParticleMessage(buf.readBlockPos(), Block.stateById(buf.readVarInt()));
  }

  public static void handle(BlockDestroyParticleMessage msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      var player = Minecraft.getInstance().player;
      if (player != null) {
        var state = player.getLevel().getBlockState(msg.pos);
        player.getLevel().addDestroyBlockEffect(msg.pos, state);
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
