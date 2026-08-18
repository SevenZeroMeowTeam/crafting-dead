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

package com.craftingdead.survival.data.loot;

import java.util.stream.Collectors;
import com.craftingdead.survival.world.level.block.SurvivalBlocks;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class SurvivalBlockLoot extends BlockLoot {

  @Override
  protected void addTables() {
    this.dropSelf(SurvivalBlocks.CIVILIAN_LOOT_GENERATOR.get());
    this.dropSelf(SurvivalBlocks.RARE_CIVILIAN_LOOT_GENERATOR.get());
    this.dropSelf(SurvivalBlocks.MEDICAL_LOOT_GENERATOR.get());
    this.dropSelf(SurvivalBlocks.MILITARY_LOOT_GENERATOR.get());
    this.dropSelf(SurvivalBlocks.POLICE_LOOT_GENERATOR.get());

    this.dropSelf(SurvivalBlocks.MILITARY_LOOT.get());
    this.dropSelf(SurvivalBlocks.MEDICAL_LOOT.get());
    this.dropSelf(SurvivalBlocks.CIVILIAN_LOOT.get());
    this.dropSelf(SurvivalBlocks.RARE_CIVILIAN_LOOT.get());
    this.dropSelf(SurvivalBlocks.POLICE_LOOT.get());
  }

  @Override
  protected @NotNull Iterable<Block> getKnownBlocks() {
    return SurvivalBlocks.deferredRegister.getEntries().stream()
        .map(RegistryObject::get)
        .collect(Collectors.toList());
  }
}