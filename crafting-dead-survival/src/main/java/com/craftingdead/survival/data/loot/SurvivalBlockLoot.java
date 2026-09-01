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

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import com.craftingdead.core.world.item.ModItems;
import com.craftingdead.survival.world.item.SurvivalItems;
import com.craftingdead.survival.world.level.block.SurvivalBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.flag.FeatureFlags;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

public class SurvivalBlockLoot extends BlockLootSubProvider {

  protected SurvivalBlockLoot(HolderLookup.Provider provider) {
    super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
  }

  @Override
  protected void generate() {
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
        .map(DeferredHolder::get)
        .collect(Collectors.toList());
  }
}