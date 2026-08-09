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

package com.craftingdead.immerse.game.survival;

import com.craftingdead.core.world.entity.extension.LivingHandlerType;
import com.craftingdead.core.world.entity.extension.PlayerExtension;
import com.craftingdead.core.world.entity.extension.PlayerHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * Stub class for immerse SurvivalPlayerHandler.
 * This allows compilation without the immerse module.
 * The actual implementation will be loaded from the immerse mod at runtime.
 */
public class SurvivalPlayerHandler implements PlayerHandler {
  
  public static final LivingHandlerType<SurvivalPlayerHandler> TYPE = 
      new LivingHandlerType<>(new ResourceLocation("craftingdeadimmerse", "survival_player"));

  public SurvivalPlayerHandler(PlayerExtension<?> player) {
    // Stub constructor
  }

  public int getWater() {
    return 0;
  }

  public int getMaxWater() {
    return 20;
  }

  @Override
  public void encode(FriendlyByteBuf out, boolean writeAll) {}

  @Override
  public void decode(FriendlyByteBuf in) {}

  @Override
  public boolean requiresSync() {
    return false;
  }
}
