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

package com.craftingdead.immerse.world.item.hydration;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

/**
 * Stub class for immerse Hydration capability.
 * This allows compilation without the immerse module.
 * The actual implementation will be loaded from the immerse mod at runtime.
 */
public interface Hydration {
  
  Capability<Hydration> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

  static Hydration fixed(int water) {
    return new Hydration() {
      @Override
      public int getWater() {
        return water;
      }
    };
  }

  int getWater();
}
