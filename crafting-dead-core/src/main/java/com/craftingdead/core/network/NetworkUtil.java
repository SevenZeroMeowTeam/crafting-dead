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

import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class NetworkUtil {

  public static Entity getEntityOrSender(CustomPayloadEvent.Context context, int entityId) {
    return getEntityOrSender(context, entityId, Entity.class);
  }

  public static <T extends Entity> T getEntityOrSender(CustomPayloadEvent.Context context,
      int entityId, Class<T> clazz) {
    if (context.isClientSide()) {
      return getEntity(context, entityId, clazz);
    }
    if (clazz.isInstance(context.getSender())) {
      return clazz.cast(context.getSender());
    }
    throw new IllegalStateException("Sender is not instance of: " + clazz.getName());
  }

  public static Entity getEntity(CustomPayloadEvent.Context context, int entityId) {
    return getEntity(context, entityId, Entity.class);
  }

  public static <T extends Entity> T getEntity(CustomPayloadEvent.Context context, int entityId,
      Class<T> clazz) {
    return LogicalSidedProvider.CLIENTWORLD
        .get(context.isClientSide() ? net.minecraftforge.fml.LogicalSide.CLIENT
            : net.minecraftforge.fml.LogicalSide.SERVER)
        .map(level -> level.getEntity(entityId))
        .filter(clazz::isInstance)
        .map(clazz::cast)
        .orElseThrow(() -> new IllegalStateException(
            String.format("Entity with ID %s of type %s is absent from client level", entityId,
                clazz.getName())));
  }
}
