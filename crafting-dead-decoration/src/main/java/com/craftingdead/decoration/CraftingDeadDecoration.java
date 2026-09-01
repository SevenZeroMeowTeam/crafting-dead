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

package com.craftingdead.decoration;

import org.slf4j.Logger;
import com.craftingdead.decoration.client.ClientDist;
import com.craftingdead.decoration.data.DecorationBlockModelProvider;
import com.craftingdead.decoration.data.DecorationBlockStateProvider;
import com.craftingdead.decoration.data.DecorationItemModelProvider;
import com.craftingdead.decoration.data.DecorationRecipeProvider;
import com.craftingdead.decoration.data.loot.DecorationLootTableProvider;
import com.craftingdead.decoration.world.item.DecorationItems;
import com.craftingdead.decoration.world.level.block.DecorationBlocks;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@Mod(CraftingDeadDecoration.ID)
public class CraftingDeadDecoration {

  public static final String ID = "craftingdeaddecoration";

  private static final Logger logger = LogUtils.getLogger();

  public CraftingDeadDecoration(IEventBus modEventBus) {
    if (FMLEnvironment.dist.isClient()) {
      new ClientDist(modEventBus);
    }

    modEventBus.addListener(this::handleGatherData);

    DecorationBlocks.deferredRegister.register(modEventBus);
    DecorationItems.deferredRegister.register(modEventBus);
    DecorationItems.CREATIVE_MODE_TABS.register(modEventBus);
  }

  private void handleGatherData(GatherDataEvent event) {
    var generator = event.getGenerator();
    var packOutput = generator.getPackOutput();
    var existingFileHelper = event.getExistingFileHelper();
    var lookupProvider = event.getLookupProvider();
    if (event.includeClient()) {
      generator.addProvider(true, new DecorationBlockModelProvider(packOutput, existingFileHelper));
      generator.addProvider(true, new DecorationBlockStateProvider(packOutput, existingFileHelper));
      generator.addProvider(true, new DecorationItemModelProvider(packOutput, existingFileHelper));
    }
    if (event.includeServer()) {
      generator.addProvider(true, new DecorationLootTableProvider(packOutput, lookupProvider));
      generator.addProvider(true, new DecorationRecipeProvider(packOutput, lookupProvider));
    }
  }
}
