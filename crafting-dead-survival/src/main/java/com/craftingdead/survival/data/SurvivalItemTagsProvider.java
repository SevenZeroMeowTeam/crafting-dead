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

package com.craftingdead.survival.data;

import java.util.concurrent.CompletableFuture;
import com.craftingdead.core.tags.ModItemTags;
import com.craftingdead.core.world.item.ModItems;
import com.craftingdead.survival.CraftingDeadSurvival;
import com.craftingdead.survival.tags.SurvivalItemTags;
import com.craftingdead.survival.world.item.SurvivalItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class SurvivalItemTagsProvider extends ItemTagsProvider {

  public SurvivalItemTagsProvider(PackOutput output,
      CompletableFuture<HolderLookup.Provider> lookupProvider,
      CompletableFuture<TagsProvider.TagLookup<net.minecraft.world.level.block.Block>> blockTags,
      ExistingFileHelper existingFileHelper) {
    super(output, lookupProvider, blockTags, CraftingDeadSurvival.ID, existingFileHelper);
  }

  @Override
  public void addTags(HolderLookup.Provider provider) {
    this.tag(ModItemTags.SYRINGES).add(SurvivalItems.RBI_SYRINGE.get(),
        SurvivalItems.CURE_SYRINGE.get());

    this.tag(SurvivalItemTags.ZOMBIE_CLOTHING_LOOT).addTag(ModItemTags.CLOTHING);
    this.tag(SurvivalItemTags.ZOMBIE_HAT_LOOT).addTag(ModItemTags.HATS);
    this.tag(SurvivalItemTags.ZOMBIE_BACKPACK_LOOT).addTag(ModItemTags.BACKPACK);
    this.tag(SurvivalItemTags.ZOMBIE_VEST_LOOT).addTag(ModItemTags.VEST);
    this.tag(SurvivalItemTags.ZOMBIE_HAND_LOOT).addTag(ModItemTags.MELEES);
    // Zombie-Loot
    this.tag(SurvivalItemTags.ALFA_ZOMBIE_VEST_LOOT).add(
        ModItems.AK47_30_ROUND_MAGAZINE.get(),
        ModItems.COMBAT_KNIFE.get(),
        ModItems.ADRENALINE_SYRINGE.get());
    this.tag(SurvivalItemTags.DESERT_RAIDER_ZOMBIE_VEST_LOOT).add(
        ModItems.ACR_MAGAZINE.get(),
        ModItems.COMBAT_KNIFE.get(),
        ModItems.ADRENALINE_SYRINGE.get(),
        ModItems.BANDAGE.get());
    this.tag(SurvivalItemTags.JUGGERNAUT_ZOMBIE_VEST_LOOT).add(
        ModItems.FLASH_GRENADE.get(),
        ModItems.M240B_MAGAZINE.get(),
        ModItems.BANDAGE.get(),
        ModItems.SMOKE_GRENADE.get(),
        ModItems.BOLT_CUTTERS.get(),
        SurvivalItems.MULTI_TOOL.get());
    this.tag(SurvivalItemTags.PILOT_ZOMBIE_VEST_LOOT).add(
        ModItems.M9_MAGAZINE.get(),
        ModItems.PARACHUTE.get(),
        ModItems.BANDAGE.get(),
        SurvivalItems.MULTI_TOOL.get(),
        SurvivalItems.MRE.get(),
        SurvivalItems.SCREWDRIVER.get());
    this.tag(SurvivalItemTags.SNIPER_ZOMBIE_VEST_LOOT).add(
        ModItems.DMR_MAGAZINE.get(),
        ModItems.BANDAGE.get(),
        ModItems.BINOCULARS.get(),
        ModItems.COMBAT_KNIFE.get(),
        SurvivalItems.MRE.get(),
        ModItems.FIRST_AID_KIT.get());
    this.tag(SurvivalItemTags.SOLDIER_ZOMBIE_VEST_LOOT).add(
        ModItems.TASER.get(),
        ModItems.TASER_CARTRIDGE.get(),
        ModItems.STANAG_20_ROUND_MAGAZINE.get(),
        ModItems.STANAG_30_ROUND_MAGAZINE.get(),
        ModItems.STANAG_DRUM_MAGAZINE.get(),
        ModItems.FRAG_GRENADE.get(),
        ModItems.FLASH_GRENADE.get(),
        ModItems.COMBAT_KNIFE.get(),
        ModItems.BANDAGE.get());
    this.tag(SurvivalItemTags.SWAT_ZOMBIE_VEST_LOOT).add(
        ModItems.STANAG_30_ROUND_MAGAZINE.get(),
        ModItems.STANAG_DRUM_MAGAZINE.get(),
        ModItems.COMBAT_KNIFE.get(),
        ModItems.BINOCULARS.get(),
        ModItems.DIRTY_RAG.get());
    this.tag(SurvivalItemTags.ALFA_ZOMBIE_BACKPACK_LOOT).add(
        ModItems.FIRST_AID_KIT.get(),
        SurvivalItems.WATER_BOTTLE.get(),
        SurvivalItems.MRE.get(),
        ModItems.BANDAGE.get(),
        ModItems.BINOCULARS.get(),
        ModItems.RED_DOT_SIGHT.get(),
        ModItems.SUPPRESSOR.get());
    this.tag(SurvivalItemTags.DESERT_RAIDER_ZOMBIE_BACKPACK_LOOT).add(
        SurvivalItems.WATER_CANTEEN.get(),
        SurvivalItems.MRE.get(),
        ModItems.BANDAGE.get(),
        SurvivalItems.ROPE.get(),
        ModItems.PICKAXE.get());
    this.tag(SurvivalItemTags.JUGGERNAUT_ZOMBIE_BACKPACK_LOOT).add(ItemStack.EMPTY.getItem());
    this.tag(SurvivalItemTags.SCOUT_ZOMBIE_BACKPACK_LOOT).add(
        ModItems.BINOCULARS.get(),
        SurvivalItems.CANNED_CORNED_BEEF.get(),
        SurvivalItems.CANNED_PEACHES.get(),
        SurvivalItems.CANNED_CUSTARD.get(),
        SurvivalItems.CAN_OPENER.get(),
        SurvivalItems.MULTI_TOOL.get(),
        SurvivalItems.SPLINT.get());
    this.tag(SurvivalItemTags.SNIPER_ZOMBIE_BACKPACK_LOOT).add(ItemStack.EMPTY.getItem());
    this.tag(SurvivalItemTags.SOLDIER_ZOMBIE_BACKPACK_LOOT).add(
        ModItems.FIRST_AID_KIT.get(),
        SurvivalItems.MRE.get(),
        SurvivalItems.CANNED_TOMATO_SOUP.get(),
        SurvivalItems.RICE_BAG.get(),
        SurvivalItems.CAN_OPENER.get(),
        ModItems.BANDAGE.get(),
        ModItems.ACOG_SIGHT.get(),
        ModItems.NV_GOGGLES_HAT.get());
    this.tag(SurvivalItemTags.SWAT_ZOMBIE_BACKPACK_LOOT).add(
        ModItems.C4_EXPLOSIVE.get(),
        ModItems.REMOTE_DETONATOR.get(),
        ModItems.FIRST_AID_KIT.get(),
        ModItems.SMOKE_GRENADE.get(),
        SurvivalItems.WATER_CANTEEN.get(),
        SurvivalItems.MRE.get(),
        ModItems.BANDAGE.get());
  }

  @Override
  public String getName() {
    return "Crafting Dead Survival Item Tags";
  }
}
