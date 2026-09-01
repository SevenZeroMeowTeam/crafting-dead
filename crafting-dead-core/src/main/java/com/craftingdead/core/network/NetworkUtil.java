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

package com.craftingdead.core.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class NetworkUtil {

  public static Entity getEntityOrSender(IPayloadContext context, int entityId) {
    return getEntityOrSender(context, entityId, Entity.class);
  }

  public static <T extends Entity> T getEntityOrSender(IPayloadContext context,
      int entityId, Class<T> clazz) {
    if (context.flow() == net.minecraft.network.protocol.PacketFlow.CLIENTBOUND) {
      return getEntity(context, entityId, clazz);
    }
    if (clazz.isInstance(context.player())) {
      return clazz.cast(context.player());
    }
    throw new IllegalStateException("Sender is not instance of: " + clazz.getName());
  }

  public static Entity getEntity(IPayloadContext context, int entityId) {
    return getEntity(context, entityId, Entity.class);
  }

  public static <T extends Entity> T getEntity(IPayloadContext context, int entityId,
      Class<T> clazz) {
    Level level = getLevel(context);
    var entity = level.getEntity(entityId);
    return clazz.isInstance(entity) ? clazz.cast(entity) : null;
  }

  private static Level getLevel(IPayloadContext context) {
    if (context.flow() == net.minecraft.network.protocol.PacketFlow.CLIENTBOUND) {
      return Minecraft.getInstance().level;
    }
    Connection connection = context.connection();
    // Server side level is not available directly from the context; use the player's level.
    return context.player().level();
  }
}
