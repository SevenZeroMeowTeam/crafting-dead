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

package com.craftingdead.core.server;

import com.craftingdead.core.ModDist;
import net.minecraft.core.RegistryAccess;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.server.ServerLifecycleHooks;

public class ServerDist implements ModDist {

  public ServerDist() {
    // Auto-updater moved to crafting-dead-updater mod (separate optional mod)
    // Server-side updater is now handled by the crafting-dead-updater mod
    // This keeps the main Crafting Dead mods CurseForge-compliant
  }

  @SuppressWarnings("deprecation")
  @Override
  public RegistryAccess registryAccess() {
    if (ServerLifecycleHooks.getCurrentServer() != null) {
      return ServerLifecycleHooks.getCurrentServer().registryAccess();
    }
    return ModDist.super.registryAccess();
  }
}
