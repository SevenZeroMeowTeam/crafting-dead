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

package com.craftingdead.core.world.item;

import com.craftingdead.core.CommonConfig;
import com.craftingdead.core.CraftingDead;
import com.craftingdead.core.ServerConfig;
import com.craftingdead.core.trauma.ProtectionConfig;
import com.craftingdead.core.client.animation.gun.InspectAnimation;
import com.craftingdead.core.client.animation.gun.ReloadAnimation;
import com.craftingdead.core.client.animation.gun.ShootAnimation;
import com.craftingdead.core.util.FunctionalUtil;
import com.craftingdead.core.world.action.ActionTypes;
import com.craftingdead.core.world.entity.grenade.C4Explosive;
import com.craftingdead.core.world.entity.grenade.DecoyGrenadeEntity;
import com.craftingdead.core.world.entity.grenade.FireGrenadeEntity;
import com.craftingdead.core.world.entity.grenade.FlashGrenadeEntity;
import com.craftingdead.core.world.entity.grenade.FragGrenade;
import com.craftingdead.core.world.entity.grenade.SmokeGrenadeEntity;
import com.craftingdead.core.world.inventory.GenericMenu;
import com.craftingdead.core.world.item.ClothingItem.ClothingType;
import com.craftingdead.core.world.item.combatslot.CombatSlot;
import com.craftingdead.core.world.item.combatslot.CombatSlotProvider;
import com.craftingdead.core.world.item.equipment.Equipment;
import com.craftingdead.core.world.item.equipment.SimpleClothing;
import com.craftingdead.core.world.item.equipment.SimpleHat;
import com.craftingdead.core.world.item.gun.Gun;
import com.craftingdead.core.world.item.gun.magazine.Magazine;
import com.craftingdead.core.world.item.gun.skin.Paint;
import com.craftingdead.core.world.item.scope.Scope;
import com.craftingdead.core.world.item.gun.GunAnimationEvent;
import com.craftingdead.core.world.item.gun.GunConfigurations;
import com.craftingdead.core.world.item.gun.aimable.AimableGunItem;
import com.craftingdead.core.world.item.gun.attachment.Attachments;
import com.craftingdead.core.world.item.gun.minigun.MinigunItem;
import com.craftingdead.core.world.item.gun.skin.Skins;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModItems {

  public static final DeferredRegister<Item> deferredRegister =
      DeferredRegister.create(Registries.ITEM, CraftingDead.ID);

  public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
      DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CraftingDead.ID);

  // ================================================================================
  // Paints
  // ================================================================================

  public static final DeferredHolder<Item, ? extends Item> VULCAN_PAINT =
      deferredRegister.register("vulcan_paint",
          () -> new PaintItem(Skins.VULCAN, new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> ASMO_PAINT =
      deferredRegister.register("asmo_paint",
          () -> new PaintItem(Skins.ASMO, new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> CANDY_APPLE_PAINT =
      deferredRegister.register("candy_apple_paint",
          () -> new PaintItem(Skins.CANDY_APPLE, new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> CYREX_PAINT =
      deferredRegister.register("cyrex_paint",
          () -> new PaintItem(Skins.CYREX, new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> DIAMOND_PAINT =
      deferredRegister.register("diamond_paint",
          () -> new PaintItem(Skins.DIAMOND, new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> DRAGON_PAINT =
      deferredRegister.register("dragon_paint",
          () -> new PaintItem(Skins.DRAGON, new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> FADE_PAINT =
      deferredRegister.register("fade_paint",
          () -> new PaintItem(Skins.FADE, new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> FURY_PAINT =
      deferredRegister.register("fury_paint",
          () -> new PaintItem(Skins.FURY, new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> GEM_PAINT =
      deferredRegister.register("gem_paint",
          () -> new PaintItem(Skins.GEM, new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> INFERNO_PAINT =
      deferredRegister.register("inferno_paint",
          () -> new PaintItem(Skins.INFERNO, new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> RUBY_PAINT =
      deferredRegister.register("ruby_paint",
          () -> new PaintItem(Skins.RUBY, new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> SCORCHED_PAINT =
      deferredRegister.register("scorched_paint",
          () -> new PaintItem(Skins.SCORCHED, new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> SLAUGHTER_PAINT =
      deferredRegister.register("slaughter_paint",
          () -> new PaintItem(Skins.SLAUGHTER, new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> UV_PAINT =
      deferredRegister.register("uv_paint",
          () -> new PaintItem(Skins.UV, new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> HYPER_BEAST_PAINT =
      deferredRegister.register("hyper_beast_paint",
          () -> new PaintItem(Skins.HYPER_BEAST, new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> EMPEROR_DRAGON_PAINT =
      deferredRegister.register("emperor_dragon_paint",
          () -> new PaintItem(Skins.EMPEROR_DRAGON, new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> NUCLEAR_WINTER_PAINT =
      deferredRegister.register("nuclear_winter_paint",
          () -> new PaintItem(Skins.NUCLEAR_WINTER, new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> MONARCH_PAINT =
      deferredRegister.register("monarch_paint",
          () -> new PaintItem(Skins.MONARCH, new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> LOVELACE_PAINT =
      deferredRegister.register("lovelace_paint",
          () -> new PaintItem(Skins.LOVELACE, new Item.Properties()
              .stacksTo(1)
              ));
  // ================================================================================
  // Magazines
  // ================================================================================

  public static final DeferredHolder<Item, ? extends MagazineItem> STANAG_BOX_MAGAZINE =
      deferredRegister.register("stanag_box_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(85)
              .setArmorPenetration(0.4F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> STANAG_DRUM_MAGAZINE =
      deferredRegister.register("stanag_drum_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(45)
              .setArmorPenetration(0.4F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> STANAG_30_ROUND_MAGAZINE =
      deferredRegister.register("stanag_30_round_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(30)
              .setArmorPenetration(0.4F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> STANAG_20_ROUND_MAGAZINE =
      deferredRegister.register("stanag_20_round_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(20)
              .setArmorPenetration(0.4F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> MPT55_MAGAZINE =
      deferredRegister.register("mpt55_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(30)
              .setArmorPenetration(0.4F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> AK47_30_ROUND_MAGAZINE =
      deferredRegister.register("ak47_30_round_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(30)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> FNFAL_MAGAZINE =
      deferredRegister.register("fnfal_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(20)
              .setArmorPenetration(0.55F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> ACR_MAGAZINE =
      deferredRegister.register("acr_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(20)
              .setArmorPenetration(0.5F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> G36C_MAGAZINE =
      deferredRegister.register("g36c_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(30)
              .setArmorPenetration(0.45F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> HK417_MAGAZINE =
      deferredRegister.register("hk417_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(30)
              .setArmorPenetration(0.47F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> M1911_MAGAZINE =
      deferredRegister.register("m1911_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(7)
              .setArmorPenetration(0.08F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> G18_MAGAZINE =
      deferredRegister.register("g18_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(20)
              .setArmorPenetration(0.08F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> M9_MAGAZINE =
      deferredRegister.register("m9_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(15)
              .setArmorPenetration(0.08F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> DESERT_EAGLE_MAGAZINE =
      deferredRegister.register("desert_eagle_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(13)
              .setArmorPenetration(0.35F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> P250_MAGAZINE =
      deferredRegister.register("p250_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(12)
              .setArmorPenetration(0.08F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> MAGNUM_AMMUNITION =
      deferredRegister.register("magnum_ammunition",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(6)
              .setArmorPenetration(0.65F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> FN57_MAGAZINE =
      deferredRegister.register("fn57_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(20)
              .setArmorPenetration(0.09F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> P90_MAGAZINE =
      deferredRegister.register("p90_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(50)
              .setArmorPenetration(0.15F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> VECTOR_MAGAZINE =
      deferredRegister.register("vector_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(30)
              .setArmorPenetration(0.15F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> MP5A5_35_ROUND_MAGAZINE =
      deferredRegister.register("mp5a5_35_round_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(35)
              .setArmorPenetration(0.15F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> MP5A5_21_ROUND_MAGAZINE =
      deferredRegister.register("mp5a5_21_round_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(21)
              .setArmorPenetration(0.15F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> MAC10_EXTENDED_MAGAZINE =
      deferredRegister.register("mac10_extended_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(45)
              .setArmorPenetration(0.15F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> MAC10_MAGAZINE =
      deferredRegister.register("mac10_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(30)
              .setArmorPenetration(0.15F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> SPORTER22_MAGAZINE =
      deferredRegister.register("sporter22_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(30)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> M107_MAGAZINE =
      deferredRegister.register("m107_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(10)
              .setArmorPenetration(0.65F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> AS50_MAGAZINE =
      deferredRegister.register("as50_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(10)
              .setArmorPenetration(0.65F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> M1GARAND_AMMUNITION =
      deferredRegister.register("m1garand_ammunition",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(8)
              .setArmorPenetration(0.95F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> AWP_MAGAZINE =
      deferredRegister.register("awp_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(10)
              .setArmorPenetration(0.95F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> KAR98K_AMMUNITION =
      deferredRegister.register("kar98k_ammunition",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(5)
              .setArmorPenetration(0.85F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> TRENCH_GUN_SHELLS =
      deferredRegister.register("trench_gun_shells",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(6)
              .setArmorPenetration(0.35F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> MOSSBERG_SHELLS =
      deferredRegister.register("mossberg_shells",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(8)
              .setArmorPenetration(0.3F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> DMR_MAGAZINE =
      deferredRegister.register("dmr_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(5)
              .setArmorPenetration(0.65F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> TASER_CARTRIDGE =
      deferredRegister.register("taser_cartridge",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(3)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> M240B_MAGAZINE =
      deferredRegister.register("m240b_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(150)
              .setArmorPenetration(0.5F)
              .stacksTo(1)
              ));


  public static final DeferredHolder<Item, ? extends MagazineItem> RPK_DRUM_MAGAZINE =
      deferredRegister.register("rpk_drum_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(100)
              .setArmorPenetration(0.5F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> RPK_MAGAZINE =
      deferredRegister.register("rpk_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(60)
              .setArmorPenetration(0.5F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> MINIGUN_MAGAZINE =
      deferredRegister.register("minigun_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(350)
              .setArmorPenetration(0.3F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends MagazineItem> MK48MOD_MAGAZINE =
      deferredRegister.register("mk48mod_magazine",
          () -> new MagazineItem((MagazineItem.Properties) new MagazineItem.Properties()
              .setSize(150)
              .setArmorPenetration(0.52F)
              .stacksTo(1)
              ));

  // ================================================================================
  // Attachments
  // ================================================================================

  public static final DeferredHolder<Item, ? extends AttachmentItem> RED_DOT_SIGHT =
      deferredRegister.register("red_dot_sight",
          () -> new AttachmentItem(Attachments.RED_DOT_SIGHT, new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends AttachmentItem> ACOG_SIGHT =
      deferredRegister.register("acog_sight",
          () -> new AttachmentItem(Attachments.ACOG_SIGHT, new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends AttachmentItem> LP_SCOPE =
      deferredRegister.register("lp_scope",
          () -> new AttachmentItem(Attachments.LP_SCOPE, new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends AttachmentItem> HP_SCOPE =
      deferredRegister.register("hp_scope",
          () -> new AttachmentItem(Attachments.HP_SCOPE, new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends AttachmentItem> SUPPRESSOR =
      deferredRegister.register("suppressor",
          () -> new AttachmentItem(Attachments.SUPPRESSOR, new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends AttachmentItem> TACTICAL_GRIP =
      deferredRegister.register("tactical_grip",
          () -> new AttachmentItem(Attachments.TACTICAL_GRIP, new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends AttachmentItem> BIPOD =
      deferredRegister.register("bipod",
          () -> new AttachmentItem(Attachments.BIPOD, new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends AttachmentItem> EOTECH_SIGHT =
      deferredRegister.register("eotech_sight",
          () -> new AttachmentItem(Attachments.EOTECH_SIGHT, new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends AttachmentItem> KAR98K_SCOPE =
      deferredRegister.register("kar98k_scope",
          () -> new AttachmentItem(Attachments.KAR98K_SCOPE, new Item.Properties()
              .stacksTo(1)
              ));

  // ================================================================================
  // Assault Rifles
  // ================================================================================

  public static final DeferredHolder<Item, ? extends GunItem> M4A1 =
      deferredRegister.register("m4a1",
          () -> AimableGunItem.builder(GunConfigurations.M4A1.getKey())
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::rifle)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.STANAG_20_ROUND_MAGAZINE)
              .addAcceptedMagazine(ModItems.STANAG_30_ROUND_MAGAZINE)
              .addAcceptedMagazine(ModItems.STANAG_DRUM_MAGAZINE)
              .addAcceptedMagazine(ModItems.STANAG_BOX_MAGAZINE)
              .addAcceptedAttachment(Attachments.RED_DOT_SIGHT)
              .addAcceptedAttachment(Attachments.ACOG_SIGHT)
              .addAcceptedAttachment(Attachments.LP_SCOPE)
              .addAcceptedAttachment(Attachments.HP_SCOPE)
              .addAcceptedAttachment(Attachments.TACTICAL_GRIP)
              .addAcceptedAttachment(Attachments.BIPOD)
              .addAcceptedAttachment(Attachments.SUPPRESSOR)
              .addAcceptedAttachment(Attachments.EOTECH_SIGHT)
              .build());

  public static final DeferredHolder<Item, ? extends GunItem> SCARL =
      deferredRegister.register("scarl",
          () -> AimableGunItem.builder(GunConfigurations.SCARL.getKey())
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::rifle)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.STANAG_20_ROUND_MAGAZINE)
              .addAcceptedMagazine(ModItems.STANAG_30_ROUND_MAGAZINE)
              .addAcceptedMagazine(ModItems.STANAG_DRUM_MAGAZINE)
              .addAcceptedMagazine(ModItems.STANAG_BOX_MAGAZINE)
              .addAcceptedAttachment(Attachments.RED_DOT_SIGHT)
              .addAcceptedAttachment(Attachments.ACOG_SIGHT)
              .addAcceptedAttachment(Attachments.LP_SCOPE)
              .addAcceptedAttachment(Attachments.HP_SCOPE)
              .addAcceptedAttachment(Attachments.TACTICAL_GRIP)
              .addAcceptedAttachment(Attachments.BIPOD)
              .addAcceptedAttachment(Attachments.SUPPRESSOR)
              .addAcceptedAttachment(Attachments.EOTECH_SIGHT)
              .build());

  public static final DeferredHolder<Item, ? extends GunItem> AK47 =
      deferredRegister.register("ak47",
          () -> AimableGunItem.builder(GunConfigurations.AK47.getKey())
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::rifle)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.AK47_30_ROUND_MAGAZINE)
              .addAcceptedAttachment(Attachments.RED_DOT_SIGHT)
              .addAcceptedAttachment(Attachments.ACOG_SIGHT)
              .addAcceptedAttachment(Attachments.LP_SCOPE)
              .addAcceptedAttachment(Attachments.HP_SCOPE)
              .addAcceptedAttachment(Attachments.TACTICAL_GRIP)
              .addAcceptedAttachment(Attachments.BIPOD)
              .addAcceptedAttachment(Attachments.EOTECH_SIGHT)
              .addAcceptedAttachment(Attachments.SUPPRESSOR)
              .build());

  public static final DeferredHolder<Item, ? extends GunItem> FNFAL =
      deferredRegister.register("fnfal",
          () -> AimableGunItem.builder(GunConfigurations.FNFAL.getKey())
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::rifle)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.FNFAL_MAGAZINE)
              .addAcceptedAttachment(Attachments.RED_DOT_SIGHT)
              .addAcceptedAttachment(Attachments.ACOG_SIGHT)
              .addAcceptedAttachment(Attachments.LP_SCOPE)
              .addAcceptedAttachment(Attachments.HP_SCOPE)
              .addAcceptedAttachment(Attachments.TACTICAL_GRIP)
              .addAcceptedAttachment(Attachments.BIPOD)
              .addAcceptedAttachment(Attachments.SUPPRESSOR)
              .build());

  public static final DeferredHolder<Item, ? extends GunItem> ACR =
      deferredRegister.register("acr",
          () -> AimableGunItem.builder(GunConfigurations.ACR.getKey())
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::rifle)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.ACR_MAGAZINE)
              .addAcceptedAttachment(Attachments.RED_DOT_SIGHT)
              .addAcceptedAttachment(Attachments.SUPPRESSOR)
              .addAcceptedAttachment(Attachments.LP_SCOPE)
              .addAcceptedAttachment(Attachments.HP_SCOPE)
              .addAcceptedAttachment(Attachments.TACTICAL_GRIP)
              .addAcceptedAttachment(Attachments.EOTECH_SIGHT)
              .build());

  public static final DeferredHolder<Item, ? extends GunItem> HK417 =
      deferredRegister.register("hk417",
          () -> AimableGunItem.builder(GunConfigurations.HK417.getKey())
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::rifle)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.HK417_MAGAZINE)
              .addAcceptedAttachment(Attachments.RED_DOT_SIGHT)
              .addAcceptedAttachment(Attachments.TACTICAL_GRIP)
              .addAcceptedAttachment(Attachments.ACOG_SIGHT)
              .addAcceptedAttachment(Attachments.LP_SCOPE)
              .addAcceptedAttachment(Attachments.HP_SCOPE)
              .addAcceptedAttachment(Attachments.SUPPRESSOR)
              .addAcceptedAttachment(Attachments.EOTECH_SIGHT)
              .build());

  public static final DeferredHolder<Item, ? extends GunItem> MPT55 =
      deferredRegister.register("mpt55",
          () -> AimableGunItem.builder(GunConfigurations.MPT55.getKey())
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::rifle)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.MPT55_MAGAZINE)
              .addAcceptedAttachment(Attachments.RED_DOT_SIGHT)
              .addAcceptedAttachment(Attachments.ACOG_SIGHT)
              .addAcceptedAttachment(Attachments.LP_SCOPE)
              .addAcceptedAttachment(Attachments.SUPPRESSOR)
              .build());

  public static final DeferredHolder<Item, ? extends GunItem> M1GARAND =
      deferredRegister.register("m1garand",
          () -> AimableGunItem.builder(GunConfigurations.M1GARAND.getKey())
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::autoSniper)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.M1GARAND_AMMUNITION)
              .addAcceptedAttachment(Attachments.LP_SCOPE)
              .addAcceptedAttachment(Attachments.HP_SCOPE)
              .addAcceptedAttachment(Attachments.BIPOD)
              .build());

  public static final DeferredHolder<Item, ? extends GunItem> SPORTER22 =
      deferredRegister.register("sporter22",
          () -> AimableGunItem.builder(GunConfigurations.SPORTER22.getKey())
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::rifle)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.SPORTER22_MAGAZINE)
              .addAcceptedAttachment(Attachments.RED_DOT_SIGHT)
              .addAcceptedAttachment(Attachments.ACOG_SIGHT)
              .addAcceptedAttachment(Attachments.LP_SCOPE)
              .addAcceptedAttachment(Attachments.HP_SCOPE)
              .addAcceptedAttachment(Attachments.TACTICAL_GRIP)
              .addAcceptedAttachment(Attachments.BIPOD)
              .addAcceptedAttachment(Attachments.SUPPRESSOR)
              .build());

  public static final DeferredHolder<Item, ? extends GunItem> G36C =
      deferredRegister.register("g36c",
          () -> AimableGunItem.builder(GunConfigurations.G36C.getKey())
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::rifle)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.G36C_MAGAZINE)
              .addAcceptedAttachment(Attachments.RED_DOT_SIGHT)
              .addAcceptedAttachment(Attachments.TACTICAL_GRIP)
              .addAcceptedAttachment(Attachments.ACOG_SIGHT)
              .addAcceptedAttachment(Attachments.EOTECH_SIGHT)
              .build());

  // ================================================================================
  // Machine Guns
  // ================================================================================

  public static final DeferredHolder<Item, ? extends GunItem> M240B =
      deferredRegister.register("m240b",
          () -> AimableGunItem.builder(GunConfigurations.M240B.getKey())
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::submachineGun)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.M240B_MAGAZINE)
              .addAcceptedAttachment(Attachments.RED_DOT_SIGHT)
              .addAcceptedAttachment(Attachments.TACTICAL_GRIP)
              .addAcceptedAttachment(Attachments.BIPOD)
              .addAcceptedAttachment(Attachments.SUPPRESSOR)
              .addAcceptedAttachment(Attachments.EOTECH_SIGHT)
              .build());

  public static final DeferredHolder<Item, ? extends GunItem> RPK =
      deferredRegister.register("rpk",
          () -> AimableGunItem.builder(GunConfigurations.RPK.getKey())
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::rifle)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.RPK_MAGAZINE)
              .addAcceptedMagazine(ModItems.RPK_DRUM_MAGAZINE)
              .addAcceptedAttachment(Attachments.RED_DOT_SIGHT)
              .addAcceptedAttachment(Attachments.SUPPRESSOR)
              .addAcceptedAttachment(Attachments.ACOG_SIGHT)
              .build());

  public static final DeferredHolder<Item, ? extends GunItem> MINIGUN =
      deferredRegister.register("minigun",
          () -> MinigunItem.builder(GunConfigurations.MINIGUN.getKey())
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::submachineGun)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.MINIGUN_MAGAZINE)
              .setTriggerPredicate(Gun::isPerformingSecondaryAction)
              .build());

  public static final DeferredHolder<Item, ? extends GunItem> MK48MOD =
      deferredRegister.register("mk48mod",
          () -> AimableGunItem.builder(GunConfigurations.MK48MOD.getKey())
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::submachineGun)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.MK48MOD_MAGAZINE)
              .addAcceptedAttachment(Attachments.RED_DOT_SIGHT)
              .addAcceptedAttachment(Attachments.TACTICAL_GRIP)
              .addAcceptedAttachment(Attachments.BIPOD)
              .addAcceptedAttachment(Attachments.SUPPRESSOR)
              .addAcceptedAttachment(Attachments.EOTECH_SIGHT)
              .build());

  // ================================================================================
  // Pistols
  // ================================================================================

  public static final DeferredHolder<Item, ? extends GunItem> TASER =
      deferredRegister.register("taser",
          () -> AimableGunItem.builder(GunConfigurations.TASER.getKey())
              .setCombatSlot(CombatSlot.SECONDARY)
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::pistol)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .putReloadAnimation(ReloadAnimation::new)
              .setDefaultMagazine(ModItems.TASER_CARTRIDGE)
              .build());

  public static final DeferredHolder<Item, ? extends GunItem> M1911 =
      deferredRegister.register("m1911",
          () -> AimableGunItem.builder(GunConfigurations.M1911.getKey())
              .setCombatSlot(CombatSlot.SECONDARY)
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::pistol)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.M1911_MAGAZINE)
              .addAcceptedAttachment(Attachments.RED_DOT_SIGHT)
              .addAcceptedAttachment(Attachments.SUPPRESSOR)
              .build());

  public static final DeferredHolder<Item, ? extends GunItem> G18 =
      deferredRegister.register("g18",
          () -> AimableGunItem.builder(GunConfigurations.G18.getKey())
              .setCombatSlot(CombatSlot.SECONDARY)
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::pistol)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.G18_MAGAZINE)
              .addAcceptedAttachment(Attachments.RED_DOT_SIGHT)
              .addAcceptedAttachment(Attachments.SUPPRESSOR)
              .build());

  public static final DeferredHolder<Item, ? extends GunItem> M9 =
      deferredRegister.register("m9",
          () -> AimableGunItem.builder(GunConfigurations.M9.getKey())
              .setCombatSlot(CombatSlot.SECONDARY)
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::pistol)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.M9_MAGAZINE)
              .addAcceptedAttachment(Attachments.SUPPRESSOR)
              .build());

  public static final DeferredHolder<Item, ? extends GunItem> DESERT_EAGLE =
      deferredRegister.register("desert_eagle",
          () -> AimableGunItem.builder(GunConfigurations.DESERT_EAGLE.getKey())
              .setCombatSlot(CombatSlot.SECONDARY)
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::pistol)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.DESERT_EAGLE_MAGAZINE)
              .build());

  public static final DeferredHolder<Item, ? extends GunItem> P250 =
      deferredRegister.register("p250",
          () -> AimableGunItem.builder(GunConfigurations.P250.getKey())
              .setCombatSlot(CombatSlot.SECONDARY)
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::pistol)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.P250_MAGAZINE)
              .addAcceptedAttachment(Attachments.RED_DOT_SIGHT)
              .addAcceptedAttachment(Attachments.SUPPRESSOR)
              .build());

  public static final DeferredHolder<Item, ? extends GunItem> MAGNUM =
      deferredRegister.register("magnum",
          () -> AimableGunItem.builder(GunConfigurations.MAGNUM.getKey())
              .setCombatSlot(CombatSlot.SECONDARY)
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::pistol)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.MAGNUM_AMMUNITION)
              .build());

  public static final DeferredHolder<Item, ? extends GunItem> FN57 =
      deferredRegister.register("fn57",
          () -> AimableGunItem.builder(GunConfigurations.FN57.getKey())
              .setCombatSlot(CombatSlot.SECONDARY)
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::pistol)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.FN57_MAGAZINE)
              .addAcceptedAttachment(Attachments.SUPPRESSOR)
              .build());

  // ================================================================================
  // Submachine Guns
  // ================================================================================

  public static final DeferredHolder<Item, ? extends GunItem> MAC10 =
      deferredRegister.register("mac10",
          () -> AimableGunItem.builder(GunConfigurations.MAC10.getKey())
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::submachineGun)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.MAC10_MAGAZINE)
              .addAcceptedMagazine(ModItems.MAC10_EXTENDED_MAGAZINE)
              .addAcceptedAttachment(Attachments.RED_DOT_SIGHT)
              .addAcceptedAttachment(Attachments.SUPPRESSOR)
              .build());

  public static final DeferredHolder<Item, ? extends GunItem> P90 =
      deferredRegister.register("p90",
          () -> AimableGunItem.builder(GunConfigurations.P90.getKey())
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::submachineGun)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.P90_MAGAZINE)
              .addAcceptedAttachment(Attachments.RED_DOT_SIGHT)
              .addAcceptedAttachment(Attachments.ACOG_SIGHT)
              .addAcceptedAttachment(Attachments.SUPPRESSOR)
              .build());

  public static final DeferredHolder<Item, ? extends GunItem> VECTOR =
      deferredRegister.register("vector",
          () -> AimableGunItem.builder(GunConfigurations.VECTOR.getKey())
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::submachineGun)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.VECTOR_MAGAZINE)
              .addAcceptedAttachment(Attachments.RED_DOT_SIGHT)
              .addAcceptedAttachment(Attachments.ACOG_SIGHT)
              .addAcceptedAttachment(Attachments.SUPPRESSOR)
              .build());

  public static final DeferredHolder<Item, ? extends GunItem> MP5A5 =
      deferredRegister.register("mp5a5",
          () -> AimableGunItem.builder(GunConfigurations.MP5A5.getKey())
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::submachineGun)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.MP5A5_21_ROUND_MAGAZINE)
              .addAcceptedMagazine(ModItems.MP5A5_35_ROUND_MAGAZINE)
              .addAcceptedAttachment(Attachments.RED_DOT_SIGHT)
              .addAcceptedAttachment(Attachments.ACOG_SIGHT)
              .addAcceptedAttachment(Attachments.SUPPRESSOR)
              .build());

  // ================================================================================
  // Sniper Rifles
  // ================================================================================

  public static final DeferredHolder<Item, ? extends GunItem> M107 =
      deferredRegister.register("m107",
          () -> AimableGunItem.builder(GunConfigurations.M107.getKey())
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::autoSniper)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .putReloadAnimation(ReloadAnimation::new)
              .setDefaultMagazine(ModItems.M107_MAGAZINE)
              .addAcceptedAttachment(Attachments.RED_DOT_SIGHT)
              .addAcceptedAttachment(Attachments.ACOG_SIGHT)
              .addAcceptedAttachment(Attachments.LP_SCOPE)
              .addAcceptedAttachment(Attachments.HP_SCOPE)
              .addAcceptedAttachment(Attachments.TACTICAL_GRIP)
              .addAcceptedAttachment(Attachments.BIPOD)
              .build());

  public static final DeferredHolder<Item, ? extends GunItem> AS50 =
      deferredRegister.register("as50",
          () -> AimableGunItem.builder(GunConfigurations.AS50.getKey())
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::autoSniper)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.AS50_MAGAZINE)
              .addAcceptedAttachment(Attachments.RED_DOT_SIGHT)
              .addAcceptedAttachment(Attachments.ACOG_SIGHT)
              .addAcceptedAttachment(Attachments.LP_SCOPE)
              .addAcceptedAttachment(Attachments.HP_SCOPE)
              .addAcceptedAttachment(Attachments.TACTICAL_GRIP)
              .addAcceptedAttachment(Attachments.BIPOD)
              .build());

  public static final DeferredHolder<Item, ? extends GunItem> AWP =
      deferredRegister.register("awp",
          () -> AimableGunItem.builder(GunConfigurations.AWP.getKey())
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::boltActionSniper)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.AWP_MAGAZINE)
              .addAcceptedAttachment(Attachments.LP_SCOPE)
              .addAcceptedAttachment(Attachments.HP_SCOPE)
              .addAcceptedAttachment(Attachments.BIPOD)
              .addAcceptedAttachment(Attachments.SUPPRESSOR)
              .build());

  public static final DeferredHolder<Item, ? extends GunItem> DMR =
      deferredRegister.register("dmr",
          () -> AimableGunItem.builder(GunConfigurations.DMR.getKey())
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::autoSniper)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.DMR_MAGAZINE)
              .addAcceptedAttachment(Attachments.LP_SCOPE)
              .addAcceptedAttachment(Attachments.HP_SCOPE)
              .addAcceptedAttachment(Attachments.BIPOD)
              .addAcceptedAttachment(Attachments.ACOG_SIGHT)
              .addAcceptedAttachment(Attachments.SUPPRESSOR)
              .build());

  // Kar98k 98k 狙击步枪（奖励箱 / 合成获得，配专用倍镜）
  public static final DeferredHolder<Item, ? extends GunItem> KAR98K =
      deferredRegister.register("kar98k",
          () -> AimableGunItem.builder(GunConfigurations.KAR98K.getKey())
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::boltActionSniper)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.KAR98K_AMMUNITION)
              .addAcceptedAttachment(Attachments.KAR98K_SCOPE)
              .build());

  // ================================================================================
  // Shotguns
  // ================================================================================

  public static final DeferredHolder<Item, ? extends GunItem> TRENCH_GUN =
      deferredRegister.register("trench_gun",
          () -> AimableGunItem.builder(GunConfigurations.TRENCH_GUN.getKey())
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::shotGun)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.TRENCH_GUN_SHELLS)
              .build());

  public static final DeferredHolder<Item, ? extends GunItem> MOSSBERG =
      deferredRegister.register("mossberg",
          () -> AimableGunItem.builder(GunConfigurations.MOSSBERG.getKey())
              .putAnimation(GunAnimationEvent.SHOOT, ShootAnimation::shotGun)
              .putReloadAnimation(ReloadAnimation::new)
              .putAnimation(GunAnimationEvent.INSPECT, InspectAnimation::new)
              .setDefaultMagazine(ModItems.MOSSBERG_SHELLS)
              .build());

  // ================================================================================
  // Grenades
  // ================================================================================

  public static final DeferredHolder<Item, ? extends GrenadeItem> FIRE_GRENADE =
      deferredRegister.register("fire_grenade",
          () -> new GrenadeItem((GrenadeItem.Properties) new GrenadeItem.Properties()
              .setGrenadeEntitySupplier(
                  FunctionalUtil.nullsafeFunction(FireGrenadeEntity::new, FireGrenadeEntity::new))
              .setEnabledSupplier(ServerConfig.instance.explosivesFireGrenadeEnabled::get)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends GrenadeItem> SMOKE_GRENADE =
      deferredRegister.register("smoke_grenade",
          () -> new GrenadeItem((GrenadeItem.Properties) new GrenadeItem.Properties()
              .setGrenadeEntitySupplier(
                  FunctionalUtil.nullsafeFunction(SmokeGrenadeEntity::new, SmokeGrenadeEntity::new))
              .setEnabledSupplier(ServerConfig.instance.explosivesSmokeGrenadeEnabled::get)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends GrenadeItem> FLASH_GRENADE =
      deferredRegister.register("flash_grenade",
          () -> new GrenadeItem((GrenadeItem.Properties) new GrenadeItem.Properties()
              .setGrenadeEntitySupplier(
                  FunctionalUtil.nullsafeFunction(FlashGrenadeEntity::new, FlashGrenadeEntity::new))
              .setEnabledSupplier(ServerConfig.instance.explosivesFlashGrenadeEnabled::get)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends GrenadeItem> DECOY_GRENADE =
      deferredRegister.register("decoy_grenade",
          () -> new GrenadeItem((GrenadeItem.Properties) new GrenadeItem.Properties()
              .setGrenadeEntitySupplier(
                  FunctionalUtil.nullsafeFunction(DecoyGrenadeEntity::new, DecoyGrenadeEntity::new))
              .setEnabledSupplier(ServerConfig.instance.explosivesDecoyGrenadeEnabled::get)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends GrenadeItem> FRAG_GRENADE =
      deferredRegister.register("frag_grenade",
          () -> new GrenadeItem((GrenadeItem.Properties) new GrenadeItem.Properties()
              .setGrenadeEntitySupplier(
                  FunctionalUtil.nullsafeFunction(FragGrenade::new, FragGrenade::new))
              .setEnabledSupplier(ServerConfig.instance.explosivesFragGrenadeEnabled::get)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends GrenadeItem> C4_EXPLOSIVE =
      deferredRegister.register("c4_explosive",
          () -> new GrenadeItem((GrenadeItem.Properties) new GrenadeItem.Properties()
              .setGrenadeEntitySupplier(
                  FunctionalUtil.nullsafeFunction(C4Explosive::new, C4Explosive::new))
              .setEnabledSupplier(ServerConfig.instance.explosivesC4Enabled::get)
              .setThrowSpeed(0.75F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends GrenadeItem> STICKY_C4_EXPLOSIVE =
      deferredRegister.register("sticky_c4_explosive",
          () -> new GrenadeItem((GrenadeItem.Properties) new GrenadeItem.Properties()
              .setGrenadeEntitySupplier(
                  FunctionalUtil.nullsafeFunction(C4Explosive::new, C4Explosive::new))
              .setEnabledSupplier(ServerConfig.instance.explosivesC4Enabled::get)
              .setSticky(true)
              .setThrowSpeed(0.75F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> REMOTE_DETONATOR =
      deferredRegister.register("remote_detonator",
          () -> new RemoteDetonatorItem(new Item.Properties()
              .stacksTo(1)
              ));

  // ================================================================================
  // Weapon
  // ================================================================================

  public static final DeferredHolder<Item, ? extends Item> CROWBAR = deferredRegister.register("crowbar",
      () -> new MeleeWeaponItem(50, 16.0F, new Item.Properties()
          .durability(100)
          ));

  public static final DeferredHolder<Item, ? extends Item> BAT = deferredRegister.register("bat",
      () -> new MeleeWeaponItem(50, 16.0F, new Item.Properties()
          .durability(55)
          ));

  public static final DeferredHolder<Item, ? extends Item> KATANA = deferredRegister.register("katana",
      () -> new MeleeWeaponItem(50, 16.0F, new Item.Properties()
          .durability(40)
          ));

  public static final DeferredHolder<Item, ? extends Item> PIPE = deferredRegister.register("pipe",
      () -> new MeleeWeaponItem(50, 16.0F, new Item.Properties()
          .durability(60)
          ));

  public static final DeferredHolder<Item, ? extends Item> RUSTY_PIPE = deferredRegister.register("rusty_pipe",
      () -> new MeleeWeaponItem(50, 16.0F, new Item.Properties()
          .durability(20)
          ));

  public static final DeferredHolder<Item, ? extends Item> FIRE_AXE = deferredRegister.register("fire_axe",
      () -> new ModAxeItem(Tiers.IRON, 1.0F, 14, -2.4F, new Item.Properties()
          .durability(100)
          ));

  public static final DeferredHolder<Item, ? extends Item> CHAINSAW = deferredRegister.register("chainsaw",
      () -> new ModAxeItem(Tiers.IRON, 2.0F,8, -2.4F, new Item.Properties()
          .durability(75)
          ));

  public static final DeferredHolder<Item, ? extends Item> BOWIE_KNIFE = deferredRegister.register("bowie_knife",
      () -> new MeleeWeaponItem(50, 16.0F, new Item.Properties()
          .durability(20)
          ));

  public static final DeferredHolder<Item, ? extends Item> GOLF_CLUB = deferredRegister.register("golf_club",
      () -> new MeleeWeaponItem(50, 16.0F, new Item.Properties()
          .durability(40)
          ));

  public static final DeferredHolder<Item, ? extends Item> NIGHT_STICK = deferredRegister.register("night_stick",
      () -> new MeleeWeaponItem(50, 16.0F, new Item.Properties()
          .durability(70)
          ));

  public static final DeferredHolder<Item, ? extends Item> SLEDGEHAMMER = deferredRegister.register("sledgehammer",
      () -> new ModPickaxeItem(Tiers.IRON, 0.55F, 10, -2.4F,
          new Item.Properties()
              .durability(110)
              ));

  public static final DeferredHolder<Item, ? extends Item> NAIL_BAT = deferredRegister.register("nail_bat",
      () -> new MeleeWeaponItem(50, 16.0F, new Item.Properties()
          .durability(55)
          ));

  public static final DeferredHolder<Item, ? extends Item> SHOVEL = deferredRegister.register("shovel",
      () -> new ModShovelItem(Tiers.IRON, 1.0F, 8, -2.4F, new Item.Properties()
          .durability(70)
          ));

  public static final DeferredHolder<Item, ? extends Item> HATCHET = deferredRegister.register("hatchet",
      () -> new ModAxeItem(Tiers.IRON, 1.4F, 16, -2.4F, new Item.Properties()
          .durability(40)
          ));

  public static final DeferredHolder<Item, ? extends Item> BROADSWORD = deferredRegister.register("broadsword",
      () -> new MeleeWeaponItem(50, 16.0F, new Item.Properties()
          .durability(55)
          ));

  public static final DeferredHolder<Item, ? extends Item> MACHETE = deferredRegister.register("machete",
      () -> new MeleeWeaponItem(50, 16.0F, new Item.Properties()
          .durability(70)
          ));

  public static final DeferredHolder<Item, ? extends Item> WEAPONIZED_SCYTHE =
      deferredRegister.register("weaponized_scythe",
          () -> new MeleeWeaponItem(50, 16.0F, new Item.Properties()
              .durability(40)
              ));

  public static final DeferredHolder<Item, ? extends Item> SCYTHE = deferredRegister.register("scythe",
      () -> new MeleeWeaponItem(50, 16.0F, new Item.Properties()
          .durability(20)
          ));

  public static final DeferredHolder<Item, ? extends Item> PICKAXE = deferredRegister.register("pickaxe",
      () -> new ModPickaxeItem(Tiers.IRON, 0.8F, 10, -2.4F, new Item.Properties()
          .durability(210)
          ));

  public static final DeferredHolder<Item, ? extends Item> BO_STAFF = deferredRegister.register("bo_staff",
      () -> new MeleeWeaponItem(50, 16.0F, new Item.Properties()
          .durability(70)
          ));

  public static final DeferredHolder<Item, ? extends Item> WRENCH = deferredRegister.register("wrench",
      () -> new MeleeWeaponItem(50, 16.0F, new Item.Properties()
          .durability(120)
          ));

  public static final DeferredHolder<Item, ? extends Item> FRYING_PAN = deferredRegister.register("frying_pan",
      () -> new MeleeWeaponItem(50, 16.0F, new Item.Properties()
          .durability(80)
          ));

  public static final DeferredHolder<Item, ? extends Item> BOLT_CUTTERS = deferredRegister.register("bolt_cutters",
      () -> new BoltCuttersItem(40, 9, -2.4F, new Item.Properties()
          .durability(50)
          ));

  public static final DeferredHolder<Item, ? extends Item> COMBAT_KNIFE = deferredRegister.register("combat_knife",
      () -> new MeleeWeaponItem(50, 16.0F, new Item.Properties()
          .durability(100)
          ));

  public static final DeferredHolder<Item, ? extends Item> STEEL_BAT = deferredRegister.register("steel_bat",
      () -> new MeleeWeaponItem(50, 16.0F, new Item.Properties()
          .durability(180)
          ));

  public static final DeferredHolder<Item, ? extends Item> CLEAVER = deferredRegister.register("cleaver",
      () -> new MeleeWeaponItem(50, 16.0F, new Item.Properties()
          .durability(80)
          ));

  public static final DeferredHolder<Item, ? extends Item> BROKEN_BOTTLE =
      deferredRegister.register("broken_bottle",
          () -> new MeleeWeaponItem(50, 16.0F, new Item.Properties()
              .durability(10)
              ));

  // ================================================================================
  // 自定义奖励 / 弹药物品
  // ================================================================================

  public static final DeferredHolder<Item, ? extends Item> CREATIVE_AMMO_BOX =
      deferredRegister.register("creative_ammo_box",
          () -> new CreativeAmmoBoxItem(new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> STARTER_REWARD_BOX =
      deferredRegister.register("starter_reward_box",
          () -> new StarterRewardBoxItem(new Item.Properties()
              .stacksTo(1)
              ));

  // ================================================================================
  // Vests
  // ================================================================================

  public static final DeferredHolder<Item, ? extends Item> BLACK_TACTICAL_VEST =
      deferredRegister.register("black_tactical_vest", ModItems::weakVest);

  public static final DeferredHolder<Item, ? extends Item> GHILLIE_TACTICAL_VEST =
      deferredRegister.register("ghillie_tactical_vest", ModItems::weakVest);

  public static final DeferredHolder<Item, ? extends Item> GREEN_TACTICAL_VEST =
      deferredRegister.register("green_tactical_vest", ModItems::weakVest);

  public static final DeferredHolder<Item, ? extends Item> GREY_TACTICAL_VEST =
      deferredRegister.register("grey_tactical_vest", ModItems::weakVest);

  public static final DeferredHolder<Item, ? extends Item> RIOT_VEST =
      deferredRegister.register("riot_vest", ModItems::strongVest);

  public static final DeferredHolder<Item, ? extends Item> TAN_TACTICAL_VEST =
      deferredRegister.register("tan_tactical_vest", ModItems::weakVest);

  // ================================================================================
  // Hats, Helmets and Masks
  // ================================================================================

  public static final DeferredHolder<Item, ? extends Item> ARMY_HELMET = deferredRegister.register("army_helmet",
      () -> new HatItem((HatItem.Properties) hatArmor(2.0F, 1.0F)
          .setHeadshotReductionPercentage(0.2F)
          .stacksTo(1)
          ));

  public static final DeferredHolder<Item, ? extends Item> BEANIE_HAT = deferredRegister.register("beanie_hat",
      () -> new HatItem((HatItem.Properties) new HatItem.Properties()
          .stacksTo(1)
          ));

  public static final DeferredHolder<Item, ? extends Item> BLACK_BALLISTIC_HAT =
      deferredRegister.register("black_ballistic_hat",
          () -> new HatItem((HatItem.Properties) hatArmor(2.0F, 1.0F)
              .setHeadshotReductionPercentage(0.2F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> FIREMAN_CHIEF_HAT =
      deferredRegister.register("chief_fireman_hat",
          () -> new HatItem((HatItem.Properties) new HatItem.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> BLUE_HARD_HAT =
      deferredRegister.register("blue_hard_hat",
          () -> new HatItem((HatItem.Properties) hatArmor(2.0F, 0.0F)
              .setHeadshotReductionPercentage(0.2F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> BUNNY_HAT = deferredRegister.register("bunny_hat",
      () -> new HatItem((HatItem.Properties) new HatItem.Properties()
          .stacksTo(1)
          ));

  public static final DeferredHolder<Item, ? extends Item> CAMO_HELMET = deferredRegister.register("camo_helmet",
      () -> new HatItem((HatItem.Properties) hatArmor(2.0F, 1.0F)
          .setHeadshotReductionPercentage(0.2F)
          .stacksTo(1)
          ));

  public static final DeferredHolder<Item, ? extends Item> CLONE_HAT = deferredRegister.register("clone_hat",
      () -> new HatItem((HatItem.Properties) hatArmor(2.0F, 1.0F)
          .setHeadshotReductionPercentage(0.2F)
          .stacksTo(1)
          ));

  public static final DeferredHolder<Item, ? extends Item> COMBAT_BDU_HELMET =
      deferredRegister.register("combat_bdu_helmet",
          () -> new HatItem((HatItem.Properties) hatArmor(2.0F, 1.0F)
              .setHeadshotReductionPercentage(0.2F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> COOKIE_MASK = deferredRegister.register("cookie_mask",
      () -> new HatItem((HatItem.Properties) new HatItem.Properties()
          .stacksTo(1)
          ));

  public static final DeferredHolder<Item, ? extends Item> COW_MASK = deferredRegister.register("cow_mask",
      () -> new HatItem((HatItem.Properties) new HatItem.Properties()
          .stacksTo(1)
          ));

  public static final DeferredHolder<Item, ? extends Item> CREEPER_MASK = deferredRegister.register("creeper_mask",
      () -> new HatItem((HatItem.Properties) new HatItem.Properties()
          .stacksTo(1)
          ));

  public static final DeferredHolder<Item, ? extends Item> DEADPOOL_MASK =
      deferredRegister.register("deadpool_mask",
          () -> new HatItem((HatItem.Properties) new HatItem.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> DOCTOR_MASK = deferredRegister.register("doctor_mask",
      () -> new HatItem((HatItem.Properties) new HatItem.Properties()
          .stacksTo(1)
          ));

  public static final DeferredHolder<Item, ? extends Item> FIREMAN_HAT = deferredRegister.register("fireman_hat",
      () -> new HatItem((HatItem.Properties) new HatItem.Properties()
          .stacksTo(1)
          ));

  public static final DeferredHolder<Item, ? extends Item> GAS_MASK = deferredRegister.register("gas_mask",
      () -> new HatItem((HatItem.Properties) new HatItem.Properties()
          .setImmuneToFlashes(true)
          .setImmuneToGas(true)
          .stacksTo(1)
          ));

  public static final DeferredHolder<Item, ? extends Item> GHILLIE_HAT = deferredRegister.register("ghillie_hat",
      () -> new HatItem((HatItem.Properties) new HatItem.Properties()
          .stacksTo(1)
          ));

  public static final DeferredHolder<Item, ? extends Item> GREEN_ARMY_HELMET =
      deferredRegister.register("green_army_helmet",
          () -> new HatItem((HatItem.Properties) hatArmor(2.0F, 1.0F)
              .setHeadshotReductionPercentage(0.2F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> GREEN_BALLISTIC_HELMET =
      deferredRegister.register("green_ballistic_helmet",
          () -> new HatItem((HatItem.Properties) hatArmor(2.0F, 1.0F)
              .setHeadshotReductionPercentage(0.2F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> GREEN_HARD_HAT =
      deferredRegister.register("green_hard_hat",
          () -> new HatItem((HatItem.Properties) hatArmor(2.0F, 0.0F)
              .setHeadshotReductionPercentage(0.2F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> GREY_ARMY_HELMET =
      deferredRegister.register("grey_army_helmet",
          () -> new HatItem((HatItem.Properties) hatArmor(2.0F, 1.0F)
              .setHeadshotReductionPercentage(0.2F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> HACKER_MASK = deferredRegister.register("hacker_mask",
      () -> new HatItem((HatItem.Properties) new HatItem.Properties()
          .stacksTo(1)
          ));

  public static final DeferredHolder<Item, ? extends Item> HAZMAT_HAT = deferredRegister.register("hazmat_hat",
      () -> new HatItem((HatItem.Properties) new HatItem.Properties()
          .setImmuneToFlashes(true)
          .stacksTo(1)
          ));

  public static final DeferredHolder<Item, ? extends Item> JUGGERNAUT_HELMET =
      deferredRegister.register("juggernaut_helmet",
          () -> new HatItem((HatItem.Properties) new HatItem.Properties()
              .setHeadshotReductionPercentage(0.2F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> KNIGHT_HAT = deferredRegister.register("knight_hat",
      () -> new HatItem((HatItem.Properties) new HatItem.Properties()
          .stacksTo(1)
          ));

  public static final DeferredHolder<Item, ? extends Item> MILITARY_HAZMAT_HAT =
      deferredRegister.register("military_hazmat_hat",
          () -> new HatItem((HatItem.Properties) new HatItem.Properties()
              .setImmuneToFlashes(true)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> NINJA_HAT = deferredRegister.register("ninja_hat",
      () -> new HatItem((HatItem.Properties) new HatItem.Properties()
          .stacksTo(1)
          ));

  public static final DeferredHolder<Item, ? extends Item> NV_GOGGLES_HAT =
      deferredRegister.register("nv_goggles_hat",
          () -> new HatItem((HatItem.Properties) new HatItem.Properties()
              .setNightVision(true)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> ORANGE_HARD_HAT =
      deferredRegister.register("orange_hard_hat",
          () -> new HatItem((HatItem.Properties) hatArmor(2.0F, 0.0F)
              .setHeadshotReductionPercentage(0.2F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> PAYDAY_MASK = deferredRegister.register("payday_mask",
      () -> new HatItem((HatItem.Properties) new HatItem.Properties()
          .stacksTo(1)
          ));

  public static final DeferredHolder<Item, ? extends Item> PAYDAY2_MASK = deferredRegister.register("payday2_mask",
      () -> new HatItem((HatItem.Properties) new HatItem.Properties()
          .stacksTo(1)
          ));

  public static final DeferredHolder<Item, ? extends Item> PILOT_HELMET = deferredRegister.register("pilot_helmet",
      () -> new HatItem((HatItem.Properties) new HatItem.Properties()
          .stacksTo(1)
          ));

  public static final DeferredHolder<Item, ? extends Item> PUMPKIN_MASK = deferredRegister.register("pumpkin_mask",
      () -> new HatItem((HatItem.Properties) new HatItem.Properties()
          .stacksTo(1)
          ));

  public static final DeferredHolder<Item, ? extends Item> RADAR_CAP = deferredRegister.register("radar_cap",
      () -> new HatItem((HatItem.Properties) new HatItem.Properties()
          .stacksTo(1)
          ));

  public static final DeferredHolder<Item, ? extends Item> RIOT_HAT = deferredRegister.register("riot_hat",
      () -> new HatItem((HatItem.Properties) hatArmor(2.0F, 1.0F)
          .setHeadshotReductionPercentage(0.2F)
          .stacksTo(1)
          ));

  public static final DeferredHolder<Item, ? extends Item> SANTA_HAT = deferredRegister.register("santa_hat",
      () -> new HatItem((HatItem.Properties) new HatItem.Properties()
          .stacksTo(1)
          ));

  public static final DeferredHolder<Item, ? extends Item> SCUBA_MASK = deferredRegister.register("scuba_mask",
      () -> new HatItem((HatItem.Properties) new HatItem.Properties()
          .waterBreathing(true)
          .stacksTo(1)
          ));

  public static final DeferredHolder<Item, ? extends Item> SHEEP_MASK = deferredRegister.register("sheep_mask",
      () -> new HatItem((HatItem.Properties) new HatItem.Properties()
          .stacksTo(1)
          ));

  public static final DeferredHolder<Item, ? extends Item> SKI_MASK = deferredRegister.register("ski_mask",
      () -> new HatItem((HatItem.Properties) new HatItem.Properties()
          .setImmuneToFlashes(true)
          .stacksTo(1)
          ));

  public static final DeferredHolder<Item, ? extends Item> SPETSNAZ_HELMET =
      deferredRegister.register("spetsnaz_helmet",
          () -> new HatItem((HatItem.Properties) hatArmor(2.0F, 1.0F)
              .setHeadshotReductionPercentage(0.2F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> TOP_HAT = deferredRegister.register("top_hat",
      () -> new HatItem((HatItem.Properties) new HatItem.Properties()
          .stacksTo(1)
          ));

  public static final DeferredHolder<Item, ? extends Item> TRAPPER_HAT = deferredRegister.register("trapper_hat",
      () -> new HatItem((HatItem.Properties) new HatItem.Properties()
          .stacksTo(1)
          ));

  public static final DeferredHolder<Item, ? extends Item> USHANKA_HAT = deferredRegister.register("ushanka_hat",
      () -> new HatItem((HatItem.Properties) new HatItem.Properties()
          .stacksTo(1)
          ));

  public static final DeferredHolder<Item, ? extends Item> WINTER_MILITARY_HELMET =
      deferredRegister.register("winter_military_helmet",
          () -> new HatItem((HatItem.Properties) hatArmor(2.0F, 1.0F)
              .setHeadshotReductionPercentage(0.2F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> YELLOW_HARD_HAT =
      deferredRegister.register("yellow_hard_hat",
          () -> new HatItem((HatItem.Properties) hatArmor(2.0F, 0.0F)
              .setHeadshotReductionPercentage(0.2F)
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> ZOMBIE_MASK = deferredRegister.register("zombie_mask",
      () -> new HatItem((HatItem.Properties) new HatItem.Properties()
          .stacksTo(1)
          ));

  // ================================================================================
  // Clothing
  // ================================================================================

  public static final DeferredHolder<Item, ? extends Item> ARMY_CLOTHING =
      deferredRegister.register("army_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.MILITARY));

  public static final DeferredHolder<Item, ? extends Item> SAS_CLOTHING =
      deferredRegister.register("sas_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.MILITARY));

  public static final DeferredHolder<Item, ? extends Item> SPETSNAZ_CLOTHING =
      deferredRegister.register("spetsnaz_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.MILITARY));

  public static final DeferredHolder<Item, ? extends Item> POLICE_CLOTHING =
      deferredRegister.register("police_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.MILITARY));

  public static final DeferredHolder<Item, ? extends Item> CAMO_CLOTHING =
      deferredRegister.register("camo_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.MILITARY));

  public static final DeferredHolder<Item, ? extends Item> COMBAT_BDU_CLOTHING =
      deferredRegister.register("combat_bdu_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.MILITARY));

  public static final DeferredHolder<Item, ? extends Item> WINTER_ARMY_CLOTHING =
      deferredRegister.register("winter_army_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.MILITARY));

  public static final DeferredHolder<Item, ? extends Item> ARMY_DESERT_CLOTHING =
      deferredRegister.register("army_desert_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.MILITARY));

  public static final DeferredHolder<Item, ? extends Item> PILOT_CLOTHING =
      deferredRegister.register("pilot_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.UTILITY));

  public static final DeferredHolder<Item, ? extends Item> HAZMAT_CLOTHING =
      deferredRegister.register("hazmat_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .fireImmunity()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.UTILITY));

  public static final DeferredHolder<Item, ? extends Item> TAC_GHILLIE_CLOTHING =
      deferredRegister.register("tac_ghillie_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.UTILITY));

  public static final DeferredHolder<Item, ? extends Item> SWAT_CLOTHING =
      deferredRegister.register("swat_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.MILITARY));

  public static final DeferredHolder<Item, ? extends Item> SPACE_SUIT_CLOTHING =
      deferredRegister.register("space_suit_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.UTILITY));

  public static final DeferredHolder<Item, ? extends Item> SHERIFF_CLOTHING =
      deferredRegister.register("sheriff_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.UTILITY));

  public static final DeferredHolder<Item, ? extends Item> JUGGERNAUT_CLOTHING =
      deferredRegister.register("juggernaut_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .fireImmunity()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.MILITARY));

  public static final DeferredHolder<Item, ? extends Item> FIREMAN_CLOTHING =
      deferredRegister.register("fireman_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .fireImmunity()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.UTILITY));

  public static final DeferredHolder<Item, ? extends Item> DOCTOR_CLOTHING =
      deferredRegister.register("doctor_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.UTILITY));

  public static final DeferredHolder<Item, ? extends Item> SMART_CLOTHING =
      deferredRegister.register("smart_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.CASUAL));

  public static final DeferredHolder<Item, ? extends Item> CASUAL_GREEN_CLOTHING =
      deferredRegister.register("casual_green_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.CASUAL));

  public static final DeferredHolder<Item, ? extends Item> BUILDER_CLOTHING =
      deferredRegister.register("builder_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.UTILITY));

  public static final DeferredHolder<Item, ? extends Item> BUSINESS_CLOTHING =
      deferredRegister.register("business_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.CASUAL));

  public static final DeferredHolder<Item, ? extends Item> SEC_GUARD_CLOTHING =
      deferredRegister.register("sec_guard_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.UTILITY));

  public static final DeferredHolder<Item, ? extends Item> MIL_HAZMAT_CLOTHING =
      deferredRegister.register("mil_hazmat_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .fireImmunity()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.MILITARY));

  public static final DeferredHolder<Item, ? extends Item> FULL_GHILLIE_CLOTHING =
      deferredRegister.register("full_ghillie_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.UTILITY));

  public static final DeferredHolder<Item, ? extends Item> RED_DUSK_CLOTHING =
      deferredRegister.register("red_dusk_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.UTILITY));

  public static final DeferredHolder<Item, ? extends Item> CLONE_CLOTHING =
      deferredRegister.register("clone_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.UTILITY));

  public static final DeferredHolder<Item, ? extends Item> COOKIE_CLOTHING =
      deferredRegister.register("cookie_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.CASUAL));

  public static final DeferredHolder<Item, ? extends Item> DEADPOOL_CLOTHING =
      deferredRegister.register("deadpool_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.CASUAL));

  public static final DeferredHolder<Item, ? extends Item> NINJA_CLOTHING =
      deferredRegister.register("ninja_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.CASUAL));

  public static final DeferredHolder<Item, ? extends Item> ARMY_MEDIC_CLOTHING =
      deferredRegister.register("army_medic_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.MILITARY));

  public static final DeferredHolder<Item, ? extends Item> BLUE_DUSK_CLOTHING =
      deferredRegister.register("blue_dusk_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.UTILITY));

  public static final DeferredHolder<Item, ? extends Item> PRESIDENT_CLOTHING =
      deferredRegister.register("president_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.UTILITY));

  public static final DeferredHolder<Item, ? extends Item> YELLOW_DUSK_CLOTHING =
      deferredRegister.register("yellow_dusk_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.UTILITY));

  public static final DeferredHolder<Item, ? extends Item> ORANGE_DUSK_CLOTHING =
      deferredRegister.register("orange_dusk_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.UTILITY));

  public static final DeferredHolder<Item, ? extends Item> GREEN_DUSK_CLOTHING =
      deferredRegister.register("green_dusk_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.UTILITY));

  public static final DeferredHolder<Item, ? extends Item> WHITE_DUSK_CLOTHING =
      deferredRegister.register("white_dusk_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.UTILITY));

  public static final DeferredHolder<Item, ? extends Item> PURPLE_DUSK_CLOTHING =
      deferredRegister.register("purple_dusk_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.UTILITY));

  public static final DeferredHolder<Item, ? extends Item> SCUBA_CLOTHING =
      deferredRegister.register("scuba_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .enhancesSwimming()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.UTILITY));

  public static final DeferredHolder<Item, ? extends Item> DDPAT_CLOTHING =
      deferredRegister.register("ddpat_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.MILITARY));

  public static final DeferredHolder<Item, ? extends Item> CONTRACTOR_CLOTHING =
      deferredRegister.register("contractor_clothing",
          () -> new ClothingItem((ClothingItem.Properties) new ClothingItem.Properties()
              .stacksTo(1)
              ,
              ActionTypes.SHRED_CLOTHING,
              ClothingType.UTILITY));

  // ================================================================================
  // Gun Parts
  // ================================================================================

  public static final DeferredHolder<Item, ? extends Item> SMALL_BARREL = deferredRegister.register("small_barrel",
      () -> new Item(new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> MEDIUM_BARREL =
      deferredRegister.register("medium_barrel",
          () -> new Item(new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> HEAVY_BARREL = deferredRegister.register("heavy_barrel",
      () -> new Item(new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> SMALL_BODY = deferredRegister.register("small_body",
      () -> new Item(new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> MEDIUM_BODY = deferredRegister.register("medium_body",
      () -> new Item(new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> HEAVY_BODY = deferredRegister.register("heavy_body",
      () -> new Item(new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> SMALL_HANDLE = deferredRegister.register("small_handle",
      () -> new Item(new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> MEDIUM_HANDLE =
      deferredRegister.register("medium_handle",
          () -> new Item(new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> HEAVY_HANDLE = deferredRegister.register("heavy_handle",
      () -> new Item(new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> SMALL_STOCK = deferredRegister.register("small_stock",
      () -> new Item(new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> MEDIUM_STOCK = deferredRegister.register("medium_stock",
      () -> new Item(new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> MEDIUM_BOLT = deferredRegister.register("medium_bolt",
      () -> new Item(new Item.Properties()));

  public static final DeferredHolder<Item, ? extends Item> HEAVY_BOLT = deferredRegister.register("heavy_bolt",
      () -> new Item(new Item.Properties()));

  // ================================================================================
  // Miscellaneous
  // ================================================================================

  public static final DeferredHolder<Item, ? extends Item> BINOCULARS =
      deferredRegister.register("binoculars",
          () -> new BinocularsItem(new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> PARACHUTE =
      deferredRegister.register("parachute",
          () -> new ParachuteItem(new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends ActionItem> HANDCUFFS =
      deferredRegister.register("handcuffs",
          () -> new ActionItem(ActionTypes.APPLY_HANDCUFFS, new Item.Properties()
              .durability(200)
              ));

  public static final DeferredHolder<Item, ? extends Item> HANDCUFFS_KEY =
      deferredRegister.register("handcuffs_key",
          () -> new HandcuffsKeyItem(new Item.Properties()
              .stacksTo(1)
              ));

  public static final DeferredHolder<Item, ? extends Item> ELECTRONIC_SIGNAL =
      deferredRegister.register("electronic_signal",
          () -> new Item(new Item.Properties()
              .stacksTo(1)
              ));

    public static void contributeProtectionMappings(ProtectionConfig.Builder builder) {
        builder.helmet(BLUE_HARD_HAT, "hard_hat");
        builder.helmet(GREEN_HARD_HAT, "hard_hat");
        builder.helmet(ORANGE_HARD_HAT, "hard_hat");
        builder.helmet(YELLOW_HARD_HAT, "hard_hat");

        builder.helmet(ARMY_HELMET, "military_helmet");
        builder.helmet(GREEN_ARMY_HELMET, "military_helmet");
        builder.helmet(CAMO_HELMET, "military_helmet");
        builder.helmet(COMBAT_BDU_HELMET, "military_helmet");
        builder.helmet(GREY_ARMY_HELMET, "military_helmet");
        builder.helmet(WINTER_MILITARY_HELMET, "military_helmet");

        builder.helmet(SPETSNAZ_HELMET, "ballistic_helmet");
        builder.helmet(BLACK_BALLISTIC_HAT, "ballistic_helmet");
        builder.helmet(GREEN_BALLISTIC_HELMET, "ballistic_helmet");

        builder.helmet(RIOT_HAT, "riot_helmet");
        builder.helmet(JUGGERNAUT_HELMET, "juggernaut_helmet");

        builder.vest(BLACK_TACTICAL_VEST, "tactical_vest");
        builder.vest(GREEN_TACTICAL_VEST, "tactical_vest");
        builder.vest(GREY_TACTICAL_VEST, "tactical_vest");
        builder.vest(TAN_TACTICAL_VEST, "tactical_vest");
        builder.vest(GHILLIE_TACTICAL_VEST, "tactical_vest");
        builder.vest(RIOT_VEST, "riot_vest");

        builder.cosmeticHelmet(BEANIE_HAT);
        builder.cosmeticHelmet(GAS_MASK);
        builder.cosmeticHelmet(PILOT_HELMET);
        builder.cosmeticHelmet(PAYDAY_MASK);
        builder.cosmeticHelmet(PAYDAY2_MASK);
        builder.cosmeticHelmet(SANTA_HAT);
        builder.cosmeticHelmet(HAZMAT_HAT);
        builder.cosmeticHelmet(MILITARY_HAZMAT_HAT);
        builder.cosmeticHelmet(NV_GOGGLES_HAT);
    }

  // ================================================================================
  // Medical
  // ================================================================================

  public static final DeferredHolder<Item, ? extends ActionItem> FIRST_AID_KIT =
      deferredRegister.register("first_aid_kit",
          () -> new ActionItem(ActionTypes.USE_FIRST_AID_KIT, new Item.Properties()
              .stacksTo(3)
              ));

  public static final DeferredHolder<Item, ? extends Item> DIRTY_RAG =
      deferredRegister.register("dirty_rag",
      () -> new ActionItem(ActionTypes.WASH_RAG, new Item.Properties()
          .stacksTo(3)
          ));

  public static final DeferredHolder<Item, ? extends Item> BLOODY_RAG =
      deferredRegister.register("bloody_rag",
      () -> new ActionItem(ActionTypes.WASH_RAG, new Item.Properties()
          .stacksTo(3)
          ));

  public static final DeferredHolder<Item, ? extends Item> CLEAN_RAG =
      deferredRegister.register("clean_rag",
      () -> new ActionItem(ActionTypes.USE_CLEAN_RAG, new Item.Properties()
          .stacksTo(3)
          ));

  public static final DeferredHolder<Item, ? extends ActionItem> ADRENALINE_SYRINGE =
      deferredRegister.register("adrenaline_syringe",
          () -> new ActionItem(ActionTypes.USE_ADRENALINE_SYRINGE, new Item.Properties()
              .stacksTo(3)
              ));

  public static final DeferredHolder<Item, ? extends ActionItem> SYRINGE =
      deferredRegister.register("syringe",
          () -> new ActionItem(ActionTypes.USE_SYRINGE, new Item.Properties()
              .stacksTo(3)
              ));

  public static final DeferredHolder<Item, ? extends ActionItem> BLOOD_SYRINGE =
      deferredRegister.register("blood_syringe",
          () -> new ActionItem(ActionTypes.USE_BLOOD_SYRINGE, new Item.Properties()
              .stacksTo(3)
              ));

  public static final DeferredHolder<Item, ? extends ActionItem> BANDAGE =
      deferredRegister.register("bandage",
          () -> new ActionItem(ActionTypes.USE_BANDAGE, new Item.Properties()
              .stacksTo(3)
              ));

  // ================================================================================
  // Backpacks
  // ================================================================================

  public static final DeferredHolder<Item, ? extends Item> SMALL_RED_BACKPACK = deferredRegister
      .register("small_red_backpack", ModItems::smallBackpack);

  public static final DeferredHolder<Item, ? extends Item> SMALL_ORANGE_BACKPACK = deferredRegister
      .register("small_orange_backpack", ModItems::smallBackpack);

  public static final DeferredHolder<Item, ? extends Item> SMALL_YELLOW_BACKPACK = deferredRegister
      .register("small_yellow_backpack", ModItems::smallBackpack);

  public static final DeferredHolder<Item, ? extends Item> SMALL_GREEN_BACKPACK = deferredRegister
      .register("small_green_backpack", ModItems::smallBackpack);

  public static final DeferredHolder<Item, ? extends Item> SMALL_BLUE_BACKPACK = deferredRegister
      .register("small_blue_backpack", ModItems::smallBackpack);

  public static final DeferredHolder<Item, ? extends Item> SMALL_PURPLE_BACKPACK = deferredRegister
      .register("small_purple_backpack", ModItems::smallBackpack);

  public static final DeferredHolder<Item, ? extends Item> MEDIUM_RED_BACKPACK = deferredRegister
      .register("medium_red_backpack", ModItems::mediumBackpack);

  public static final DeferredHolder<Item, ? extends Item> MEDIUM_ORANGE_BACKPACK = deferredRegister
      .register("medium_orange_backpack", ModItems::mediumBackpack);

  public static final DeferredHolder<Item, ? extends Item> MEDIUM_YELLOW_BACKPACK = deferredRegister
      .register("medium_yellow_backpack", ModItems::mediumBackpack);

  public static final DeferredHolder<Item, ? extends Item> MEDIUM_GREEN_BACKPACK = deferredRegister
      .register("medium_green_backpack", ModItems::mediumBackpack);

  public static final DeferredHolder<Item, ? extends Item> MEDIUM_BLUE_BACKPACK = deferredRegister
      .register("medium_blue_backpack", ModItems::mediumBackpack);

  public static final DeferredHolder<Item, ? extends Item> MEDIUM_PURPLE_BACKPACK = deferredRegister
      .register("medium_purple_backpack", ModItems::mediumBackpack);

  public static final DeferredHolder<Item, ? extends Item> MEDIUM_GREY_BACKPACK = deferredRegister
      .register("medium_grey_backpack", ModItems::mediumBackpack);

  public static final DeferredHolder<Item, ? extends Item> MEDIUM_BLACK_BACKPACK = deferredRegister
      .register("medium_black_backpack", ModItems::mediumBackpack);

  public static final DeferredHolder<Item, ? extends Item> MEDIUM_GHILLIE_BACKPACK = deferredRegister
      .register("medium_ghillie_backpack", ModItems::mediumBackpack);

  public static final DeferredHolder<Item, ? extends Item> MEDIUM_WHITE_BACKPACK = deferredRegister
      .register("medium_white_backpack", ModItems::mediumBackpack);

  public static final DeferredHolder<Item, ? extends Item> LARGE_GREY_BACKPACK = deferredRegister
      .register("large_grey_backpack", ModItems::largeBackpack);

  public static final DeferredHolder<Item, ? extends Item> LARGE_GREEN_BACKPACK = deferredRegister
      .register("large_green_backpack", ModItems::largeBackpack);

  public static final DeferredHolder<Item, ? extends Item> LARGE_TAN_BACKPACK = deferredRegister
      .register("large_tan_backpack", ModItems::largeBackpack);

  public static final DeferredHolder<Item, ? extends Item> LARGE_BLACK_BACKPACK = deferredRegister
      .register("large_black_backpack", ModItems::largeBackpack);

  public static final DeferredHolder<Item, ? extends Item> LARGE_GHILLIE_BACKPACK = deferredRegister
      .register("large_ghillie_backpack", ModItems::largeBackpack);

  public static final DeferredHolder<Item, ? extends Item> TAN_GUN_BAG = deferredRegister
      .register("tan_gun_bag", ModItems::gunBag);

  public static final DeferredHolder<Item, ? extends Item> GREY_GUN_BAG = deferredRegister
      .register("grey_gun_bag", ModItems::gunBag);

  private static HatItem.Properties hatArmor(float armor, float armorToughness) {
    return new HatItem.Properties()
        .attributeModifier(Attributes.ARMOR,
            new AttributeModifier(HatItem.ARMOR_MODIFIER_ID,
                armor, AttributeModifier.Operation.ADD_VALUE))
        .attributeModifier(Attributes.ARMOR_TOUGHNESS,
            new AttributeModifier(HatItem.ARMOR_MODIFIER_ID,
                armorToughness, AttributeModifier.Operation.ADD_VALUE));
  }

  private static StorageItem weakVest() {
    // NeoForge：物品在 RegisterEvent 注册时 config 尚未加载，不能调用 get()，只能用默认值
    return vest(
        CommonConfig.instance.weakVestArmor.getDefault().floatValue(),
        CommonConfig.instance.weakVestArmorToughness.getDefault().floatValue());
  }

  private static StorageItem strongVest() {
    return vest(
        CommonConfig.instance.strongVestArmor.getDefault().floatValue(),
        CommonConfig.instance.strongVestArmorToughness.getDefault().floatValue());
  }

  private static StorageItem vest(float armor, float armorToughness) {
    return new StorageItem((StorageItem.Properties) new StorageItem.Properties()
        .attributeModifier(Attributes.ARMOR,
            new AttributeModifier(StorageItem.ARMOR_MODIFIER_ID,
                armor, AttributeModifier.Operation.ADD_VALUE))
        .attributeModifier(Attributes.ARMOR_TOUGHNESS,
            new AttributeModifier(StorageItem.ARMOR_MODIFIER_ID,
                armorToughness, AttributeModifier.Operation.ADD_VALUE))
        .menuConstructor(GenericMenu::createVest)
        .slot(Equipment.Slot.VEST)
        .itemRows(2)
        .stacksTo(1)
        );
  }

  private static StorageItem smallBackpack() {
    return new StorageItem((StorageItem.Properties) new StorageItem.Properties()
        .slot(Equipment.Slot.BACKPACK)
        .itemRows(2)
        .menuConstructor(GenericMenu::createSmallBackpack)
        .toolTip(Component.translatable("small_backpack.information"))
        .stacksTo(1)
        );
  }

  private static StorageItem mediumBackpack() {
    return new StorageItem((StorageItem.Properties) new StorageItem.Properties()
        .slot(Equipment.Slot.BACKPACK)
        .itemRows(3)
        .menuConstructor(GenericMenu::createMediumBackpack)
        .toolTip(Component.translatable("medium_backpack.information"))
        .stacksTo(1)
        );
  }

  private static StorageItem largeBackpack() {
    return new StorageItem((StorageItem.Properties) new StorageItem.Properties()
        .slot(Equipment.Slot.BACKPACK)
        .itemRows(4)
        .menuConstructor(GenericMenu::createLargeBackpack)
        .toolTip(Component.translatable("large_backpack.information"))
        .stacksTo(1)
        );
  }

  private static StorageItem gunBag() {
    return new StorageItem((StorageItem.Properties) new StorageItem.Properties()
        .slot(Equipment.Slot.BACKPACK)
        .itemRows(2)
        .menuConstructor(GenericMenu::createGunBag)
        .toolTip(Component.translatable("gunbag.information"))
        .stacksTo(1)
        );
  }

  // ================================================================================
  // Creative Mode Tabs
  // ================================================================================

  public static final DeferredHolder<CreativeModeTab, ? extends CreativeModeTab> COSMETICS_TAB =
      CREATIVE_MODE_TABS.register("cosmetics",
          () -> CreativeModeTab.builder()
              .title(Component.translatable("craftingdead.cosmetics"))
              .icon(() -> new ItemStack(ModItems.getCosmeticsTabIcon()))
              .displayItems((params, output) -> ModItems.addCosmeticsTabItems(output))
              .build());

  public static final DeferredHolder<CreativeModeTab, ? extends CreativeModeTab> COMBAT_TAB =
      CREATIVE_MODE_TABS.register("combat",
          () -> CreativeModeTab.builder()
              .title(Component.translatable("craftingdead.combat"))
              .icon(() -> new ItemStack(ModItems.getCombatTabIcon()))
              .displayItems((params, output) -> ModItems.addCombatTabItems(output))
              .build());

  public static final DeferredHolder<CreativeModeTab, ? extends CreativeModeTab> MEDICAL_TAB =
      CREATIVE_MODE_TABS.register("medical",
          () -> CreativeModeTab.builder()
              .title(Component.translatable("craftingdead.medical"))
              .icon(() -> new ItemStack(ModItems.getMedicalTabIcon()))
              .displayItems((params, output) -> ModItems.addMedicalTabItems(output))
              .build());

  private static Item getCosmeticsTabIcon() {
    return BUILDER_CLOTHING.get();
  }

  private static Item getCombatTabIcon() {
    return AK47.get();
  }

  private static Item getMedicalTabIcon() {
    return FIRST_AID_KIT.get();
  }

  private static void addCombatTabItems(CreativeModeTab.Output output) {
    // Paints
    output.accept(new ItemStack(VULCAN_PAINT.get()));
    output.accept(new ItemStack(ASMO_PAINT.get()));
    output.accept(new ItemStack(CANDY_APPLE_PAINT.get()));
    output.accept(new ItemStack(CYREX_PAINT.get()));
    output.accept(new ItemStack(DIAMOND_PAINT.get()));
    output.accept(new ItemStack(DRAGON_PAINT.get()));
    output.accept(new ItemStack(FADE_PAINT.get()));
    output.accept(new ItemStack(FURY_PAINT.get()));
    output.accept(new ItemStack(GEM_PAINT.get()));
    output.accept(new ItemStack(INFERNO_PAINT.get()));
    output.accept(new ItemStack(RUBY_PAINT.get()));
    output.accept(new ItemStack(SCORCHED_PAINT.get()));
    output.accept(new ItemStack(SLAUGHTER_PAINT.get()));
    output.accept(new ItemStack(UV_PAINT.get()));
    output.accept(new ItemStack(HYPER_BEAST_PAINT.get()));
    output.accept(new ItemStack(EMPEROR_DRAGON_PAINT.get()));
    output.accept(new ItemStack(NUCLEAR_WINTER_PAINT.get()));
    output.accept(new ItemStack(MONARCH_PAINT.get()));
    output.accept(new ItemStack(LOVELACE_PAINT.get()));

    // Magazines
    output.accept(new ItemStack(STANAG_BOX_MAGAZINE.get()));
    output.accept(new ItemStack(STANAG_DRUM_MAGAZINE.get()));
    output.accept(new ItemStack(STANAG_30_ROUND_MAGAZINE.get()));
    output.accept(new ItemStack(STANAG_20_ROUND_MAGAZINE.get()));
    output.accept(new ItemStack(MPT55_MAGAZINE.get()));
    output.accept(new ItemStack(AK47_30_ROUND_MAGAZINE.get()));
    output.accept(new ItemStack(FNFAL_MAGAZINE.get()));
    output.accept(new ItemStack(ACR_MAGAZINE.get()));
    output.accept(new ItemStack(G36C_MAGAZINE.get()));
    output.accept(new ItemStack(HK417_MAGAZINE.get()));
    output.accept(new ItemStack(M1911_MAGAZINE.get()));
    output.accept(new ItemStack(G18_MAGAZINE.get()));
    output.accept(new ItemStack(M9_MAGAZINE.get()));
    output.accept(new ItemStack(DESERT_EAGLE_MAGAZINE.get()));
    output.accept(new ItemStack(P250_MAGAZINE.get()));
    output.accept(new ItemStack(MAGNUM_AMMUNITION.get()));
    output.accept(new ItemStack(FN57_MAGAZINE.get()));
    output.accept(new ItemStack(P90_MAGAZINE.get()));
    output.accept(new ItemStack(VECTOR_MAGAZINE.get()));
    output.accept(new ItemStack(MP5A5_35_ROUND_MAGAZINE.get()));
    output.accept(new ItemStack(MP5A5_21_ROUND_MAGAZINE.get()));
    output.accept(new ItemStack(MAC10_EXTENDED_MAGAZINE.get()));
    output.accept(new ItemStack(MAC10_MAGAZINE.get()));
    output.accept(new ItemStack(SPORTER22_MAGAZINE.get()));
    output.accept(new ItemStack(M107_MAGAZINE.get()));
    output.accept(new ItemStack(AS50_MAGAZINE.get()));
    output.accept(new ItemStack(M1GARAND_AMMUNITION.get()));
    output.accept(new ItemStack(AWP_MAGAZINE.get()));
    output.accept(new ItemStack(KAR98K_AMMUNITION.get()));
    output.accept(new ItemStack(TRENCH_GUN_SHELLS.get()));
    output.accept(new ItemStack(MOSSBERG_SHELLS.get()));
    output.accept(new ItemStack(DMR_MAGAZINE.get()));
    output.accept(new ItemStack(TASER_CARTRIDGE.get()));
    output.accept(new ItemStack(M240B_MAGAZINE.get()));
    output.accept(new ItemStack(RPK_DRUM_MAGAZINE.get()));
    output.accept(new ItemStack(RPK_MAGAZINE.get()));
    output.accept(new ItemStack(MINIGUN_MAGAZINE.get()));
    output.accept(new ItemStack(MK48MOD_MAGAZINE.get()));

    // Attachments
    output.accept(new ItemStack(RED_DOT_SIGHT.get()));
    output.accept(new ItemStack(ACOG_SIGHT.get()));
    output.accept(new ItemStack(LP_SCOPE.get()));
    output.accept(new ItemStack(HP_SCOPE.get()));
    output.accept(new ItemStack(SUPPRESSOR.get()));
    output.accept(new ItemStack(TACTICAL_GRIP.get()));
    output.accept(new ItemStack(BIPOD.get()));
    output.accept(new ItemStack(EOTECH_SIGHT.get()));
    output.accept(new ItemStack(KAR98K_SCOPE.get()));

    // 奖励 / 弹药物品
    output.accept(new ItemStack(CREATIVE_AMMO_BOX.get()));
    output.accept(new ItemStack(STARTER_REWARD_BOX.get()));
    output.accept(new ItemStack(FIRE_GRENADE.get()));
    output.accept(new ItemStack(SMOKE_GRENADE.get()));
    output.accept(new ItemStack(FLASH_GRENADE.get()));
    output.accept(new ItemStack(DECOY_GRENADE.get()));
    output.accept(new ItemStack(FRAG_GRENADE.get()));
    output.accept(new ItemStack(C4_EXPLOSIVE.get()));
    output.accept(new ItemStack(STICKY_C4_EXPLOSIVE.get()));
    output.accept(new ItemStack(REMOTE_DETONATOR.get()));

    // Melee Weapons
    output.accept(new ItemStack(CROWBAR.get()));
    output.accept(new ItemStack(BAT.get()));
    output.accept(new ItemStack(KATANA.get()));
    output.accept(new ItemStack(PIPE.get()));
    output.accept(new ItemStack(RUSTY_PIPE.get()));
    output.accept(new ItemStack(FIRE_AXE.get()));
    output.accept(new ItemStack(CHAINSAW.get()));
    output.accept(new ItemStack(BOWIE_KNIFE.get()));
    output.accept(new ItemStack(GOLF_CLUB.get()));
    output.accept(new ItemStack(NIGHT_STICK.get()));
    output.accept(new ItemStack(SLEDGEHAMMER.get()));
    output.accept(new ItemStack(NAIL_BAT.get()));
    output.accept(new ItemStack(SHOVEL.get()));
    output.accept(new ItemStack(HATCHET.get()));
    output.accept(new ItemStack(BROADSWORD.get()));
    output.accept(new ItemStack(MACHETE.get()));
    output.accept(new ItemStack(WEAPONIZED_SCYTHE.get()));
    output.accept(new ItemStack(SCYTHE.get()));
    output.accept(new ItemStack(PICKAXE.get()));
    output.accept(new ItemStack(BO_STAFF.get()));
    output.accept(new ItemStack(WRENCH.get()));
    output.accept(new ItemStack(FRYING_PAN.get()));
    output.accept(new ItemStack(BOLT_CUTTERS.get()));
    output.accept(new ItemStack(COMBAT_KNIFE.get()));
    output.accept(new ItemStack(STEEL_BAT.get()));
    output.accept(new ItemStack(CLEAVER.get()));
    output.accept(new ItemStack(BROKEN_BOTTLE.get()));
  }

  private static void addCosmeticsTabItems(CreativeModeTab.Output output) {
    // Hats, Helmets and Masks
    output.accept(new ItemStack(ARMY_HELMET.get()));
    output.accept(new ItemStack(BEANIE_HAT.get()));
    output.accept(new ItemStack(BLACK_BALLISTIC_HAT.get()));
    output.accept(new ItemStack(FIREMAN_CHIEF_HAT.get()));
    output.accept(new ItemStack(BLUE_HARD_HAT.get()));
    output.accept(new ItemStack(BUNNY_HAT.get()));
    output.accept(new ItemStack(CAMO_HELMET.get()));
    output.accept(new ItemStack(CLONE_HAT.get()));
    output.accept(new ItemStack(COMBAT_BDU_HELMET.get()));
    output.accept(new ItemStack(COOKIE_MASK.get()));
    output.accept(new ItemStack(COW_MASK.get()));
    output.accept(new ItemStack(CREEPER_MASK.get()));
    output.accept(new ItemStack(DEADPOOL_MASK.get()));
    output.accept(new ItemStack(DOCTOR_MASK.get()));
    output.accept(new ItemStack(FIREMAN_HAT.get()));
    output.accept(new ItemStack(GAS_MASK.get()));
    output.accept(new ItemStack(GHILLIE_HAT.get()));
    output.accept(new ItemStack(GREEN_ARMY_HELMET.get()));
    output.accept(new ItemStack(GREEN_BALLISTIC_HELMET.get()));
    output.accept(new ItemStack(GREEN_HARD_HAT.get()));
    output.accept(new ItemStack(GREY_ARMY_HELMET.get()));
    output.accept(new ItemStack(HACKER_MASK.get()));
    output.accept(new ItemStack(HAZMAT_HAT.get()));
    output.accept(new ItemStack(JUGGERNAUT_HELMET.get()));
    output.accept(new ItemStack(KNIGHT_HAT.get()));
    output.accept(new ItemStack(MILITARY_HAZMAT_HAT.get()));
    output.accept(new ItemStack(NINJA_HAT.get()));
    output.accept(new ItemStack(NV_GOGGLES_HAT.get()));
    output.accept(new ItemStack(ORANGE_HARD_HAT.get()));
    output.accept(new ItemStack(PAYDAY_MASK.get()));
    output.accept(new ItemStack(PAYDAY2_MASK.get()));
    output.accept(new ItemStack(PILOT_HELMET.get()));
    output.accept(new ItemStack(PUMPKIN_MASK.get()));
    output.accept(new ItemStack(RADAR_CAP.get()));
    output.accept(new ItemStack(RIOT_HAT.get()));
    output.accept(new ItemStack(SANTA_HAT.get()));
    output.accept(new ItemStack(SCUBA_MASK.get()));
    output.accept(new ItemStack(SHEEP_MASK.get()));
    output.accept(new ItemStack(SKI_MASK.get()));
    output.accept(new ItemStack(SPETSNAZ_HELMET.get()));
    output.accept(new ItemStack(TOP_HAT.get()));
    output.accept(new ItemStack(TRAPPER_HAT.get()));
    output.accept(new ItemStack(USHANKA_HAT.get()));
    output.accept(new ItemStack(WINTER_MILITARY_HELMET.get()));
    output.accept(new ItemStack(YELLOW_HARD_HAT.get()));
    output.accept(new ItemStack(ZOMBIE_MASK.get()));

    // Clothing
    output.accept(new ItemStack(ARMY_CLOTHING.get()));
    output.accept(new ItemStack(SAS_CLOTHING.get()));
    output.accept(new ItemStack(SPETSNAZ_CLOTHING.get()));
    output.accept(new ItemStack(POLICE_CLOTHING.get()));
    output.accept(new ItemStack(CAMO_CLOTHING.get()));
    output.accept(new ItemStack(COMBAT_BDU_CLOTHING.get()));
    output.accept(new ItemStack(WINTER_ARMY_CLOTHING.get()));
    output.accept(new ItemStack(ARMY_DESERT_CLOTHING.get()));
    output.accept(new ItemStack(PILOT_CLOTHING.get()));
    output.accept(new ItemStack(HAZMAT_CLOTHING.get()));
    output.accept(new ItemStack(TAC_GHILLIE_CLOTHING.get()));
    output.accept(new ItemStack(SWAT_CLOTHING.get()));
    output.accept(new ItemStack(SPACE_SUIT_CLOTHING.get()));
    output.accept(new ItemStack(SHERIFF_CLOTHING.get()));
    output.accept(new ItemStack(JUGGERNAUT_CLOTHING.get()));
    output.accept(new ItemStack(FIREMAN_CLOTHING.get()));
    output.accept(new ItemStack(DOCTOR_CLOTHING.get()));
    output.accept(new ItemStack(SMART_CLOTHING.get()));
    output.accept(new ItemStack(CASUAL_GREEN_CLOTHING.get()));
    output.accept(new ItemStack(BUILDER_CLOTHING.get()));
    output.accept(new ItemStack(BUSINESS_CLOTHING.get()));
    output.accept(new ItemStack(SEC_GUARD_CLOTHING.get()));
    output.accept(new ItemStack(MIL_HAZMAT_CLOTHING.get()));
    output.accept(new ItemStack(FULL_GHILLIE_CLOTHING.get()));
    output.accept(new ItemStack(RED_DUSK_CLOTHING.get()));
    output.accept(new ItemStack(CLONE_CLOTHING.get()));
    output.accept(new ItemStack(COOKIE_CLOTHING.get()));
    output.accept(new ItemStack(DEADPOOL_CLOTHING.get()));
    output.accept(new ItemStack(NINJA_CLOTHING.get()));
    output.accept(new ItemStack(ARMY_MEDIC_CLOTHING.get()));
    output.accept(new ItemStack(BLUE_DUSK_CLOTHING.get()));
    output.accept(new ItemStack(PRESIDENT_CLOTHING.get()));
    output.accept(new ItemStack(YELLOW_DUSK_CLOTHING.get()));
    output.accept(new ItemStack(ORANGE_DUSK_CLOTHING.get()));
    output.accept(new ItemStack(GREEN_DUSK_CLOTHING.get()));
    output.accept(new ItemStack(WHITE_DUSK_CLOTHING.get()));
    output.accept(new ItemStack(PURPLE_DUSK_CLOTHING.get()));
    output.accept(new ItemStack(SCUBA_CLOTHING.get()));
    output.accept(new ItemStack(DDPAT_CLOTHING.get()));
    output.accept(new ItemStack(CONTRACTOR_CLOTHING.get()));
    output.accept(new ItemStack(PARACHUTE.get()));
    output.accept(new ItemStack(HANDCUFFS.get()));

    // Vests
    output.accept(new ItemStack(BLACK_TACTICAL_VEST.get()));
    output.accept(new ItemStack(GHILLIE_TACTICAL_VEST.get()));
    output.accept(new ItemStack(GREEN_TACTICAL_VEST.get()));
    output.accept(new ItemStack(GREY_TACTICAL_VEST.get()));
    output.accept(new ItemStack(RIOT_VEST.get()));
    output.accept(new ItemStack(TAN_TACTICAL_VEST.get()));

    // Backpacks
    output.accept(new ItemStack(SMALL_RED_BACKPACK.get()));
    output.accept(new ItemStack(SMALL_ORANGE_BACKPACK.get()));
    output.accept(new ItemStack(SMALL_YELLOW_BACKPACK.get()));
    output.accept(new ItemStack(SMALL_GREEN_BACKPACK.get()));
    output.accept(new ItemStack(SMALL_BLUE_BACKPACK.get()));
    output.accept(new ItemStack(SMALL_PURPLE_BACKPACK.get()));
    output.accept(new ItemStack(MEDIUM_RED_BACKPACK.get()));
    output.accept(new ItemStack(MEDIUM_ORANGE_BACKPACK.get()));
    output.accept(new ItemStack(MEDIUM_YELLOW_BACKPACK.get()));
    output.accept(new ItemStack(MEDIUM_GREEN_BACKPACK.get()));
    output.accept(new ItemStack(MEDIUM_BLUE_BACKPACK.get()));
    output.accept(new ItemStack(MEDIUM_PURPLE_BACKPACK.get()));
    output.accept(new ItemStack(MEDIUM_GREY_BACKPACK.get()));
    output.accept(new ItemStack(MEDIUM_BLACK_BACKPACK.get()));
    output.accept(new ItemStack(MEDIUM_GHILLIE_BACKPACK.get()));
    output.accept(new ItemStack(MEDIUM_WHITE_BACKPACK.get()));
    output.accept(new ItemStack(LARGE_GREY_BACKPACK.get()));
    output.accept(new ItemStack(LARGE_GREEN_BACKPACK.get()));
    output.accept(new ItemStack(LARGE_TAN_BACKPACK.get()));
    output.accept(new ItemStack(LARGE_BLACK_BACKPACK.get()));
    output.accept(new ItemStack(LARGE_GHILLIE_BACKPACK.get()));
    output.accept(new ItemStack(TAN_GUN_BAG.get()));
    output.accept(new ItemStack(GREY_GUN_BAG.get()));
  }

  private static void addMedicalTabItems(CreativeModeTab.Output output) {
    output.accept(new ItemStack(FIRST_AID_KIT.get()));
    output.accept(new ItemStack(DIRTY_RAG.get()));
    output.accept(new ItemStack(BLOODY_RAG.get()));
    output.accept(new ItemStack(CLEAN_RAG.get()));
    output.accept(new ItemStack(ADRENALINE_SYRINGE.get()));
    output.accept(new ItemStack(SYRINGE.get()));
    output.accept(new ItemStack(BLOOD_SYRINGE.get()));
    output.accept(new ItemStack(BANDAGE.get()));
  }

  static {
    ArbitraryTooltips.registerTooltip(SCUBA_MASK,
        Component.translatable("clothing_item.water_breathing")
            .withStyle(ChatFormatting.GRAY));
    ArbitraryTooltips.registerTooltip(SCUBA_CLOTHING,
        Component.translatable("clothing_item.water_speed")
            .withStyle(ChatFormatting.GRAY));
    ArbitraryTooltips.registerTooltip(HANDCUFFS,
        Component.translatable("handcuffs.information")
            .withStyle(ChatFormatting.GRAY));
    ArbitraryTooltips.registerTooltip(FIRST_AID_KIT,
        Component.translatable("first_aid_kit.information")
            .withStyle(ChatFormatting.GRAY));
    ArbitraryTooltips.registerTooltip(ADRENALINE_SYRINGE,
        Component.translatable("adrenaline_syringe.information")
            .withStyle(ChatFormatting.GRAY));
    ArbitraryTooltips.registerTooltip(SYRINGE,
        Component.translatable("syringe.information")
            .withStyle(ChatFormatting.GRAY));
    ArbitraryTooltips.registerTooltip(BLOOD_SYRINGE,
        Component.translatable("blood_syringe.information")
            .withStyle(ChatFormatting.GRAY));
    ArbitraryTooltips.registerTooltip(BANDAGE,
        Component.translatable("bandage.information")
            .withStyle(ChatFormatting.GRAY));
    ArbitraryTooltips.registerTooltip(DIRTY_RAG,
        Component.translatable("dirty_rag.information")
            .withStyle(ChatFormatting.GRAY));
    ArbitraryTooltips.registerTooltip(BLOODY_RAG,
        Component.translatable("bloody_rag.information")
            .withStyle(ChatFormatting.GRAY));
    ArbitraryTooltips.registerTooltip(CLEAN_RAG,
        Component.translatable("clean_rag.information")
            .withStyle(ChatFormatting.GRAY));
    ArbitraryTooltips.registerTooltip(PARACHUTE,
        Component.translatable("parachute.information")
            .withStyle(ChatFormatting.GRAY));
  }

  public static void initAbilityProviders(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) {
    for (var item : net.minecraft.core.registries.BuiltInRegistries.ITEM) {
      if (item instanceof GunItem gunItem) {
        if (gunItem instanceof MinigunItem) {
          event.registerItem(Gun.CAPABILITY,
              (stack, ctx) -> new com.craftingdead.core.world.item.gun.minigun.Minigun(stack, gunItem),
              gunItem);
        } else {
          event.registerItem(Gun.CAPABILITY,
              (stack, ctx) -> new com.craftingdead.core.world.item.gun.aimable.AimableGun(stack, gunItem),
              gunItem);
        }
      } else if (item instanceof MagazineItem magazineItem) {
        event.registerItem(Magazine.CAPABILITY,
            (stack, ctx) -> new com.craftingdead.core.world.item.gun.magazine.MagazineImpl(magazineItem),
            magazineItem);
      } else if (item instanceof ClothingItem clothingItem) {
        event.registerItem(Equipment.CAPABILITY,
            (stack, ctx) -> SimpleClothing.of(clothingItem), clothingItem);
      } else if (item instanceof HatItem hatItem) {
        event.registerItem(Equipment.CAPABILITY, (stack, ctx) -> SimpleHat.of(hatItem), hatItem);
      } else if (item instanceof StorageItem storageItem) {
        event.registerItem(Equipment.CAPABILITY,
            (stack, ctx) -> storageItem.new Storage(), storageItem);
      } else if (item instanceof MeleeWeaponItem meleeWeaponItem) {
        event.registerItem(Equipment.CAPABILITY, (stack, ctx) -> Equipment.forSlot(Equipment.Slot.MELEE), meleeWeaponItem);
        event.registerItem(CombatSlotProvider.CAPABILITY, (stack, ctx) -> CombatSlot.MELEE, meleeWeaponItem);
      } else if (item instanceof GrenadeItem) {
        event.registerItem(CombatSlotProvider.CAPABILITY, (stack, ctx) -> CombatSlot.GRENADE, item);
      } else if (item instanceof BinocularsItem binocularsItem) {
        event.registerItem(Scope.CAPABILITY, (stack, ctx) -> Scope.of(binocularsItem, stack), binocularsItem);
      } else if (item instanceof PaintItem paintItem) {
        event.registerItem(Paint.CAPABILITY, (stack, ctx) -> {
          var skin = paintItem.getSkin();
          return skin == null ? null : Paint.of(skin);
        }, paintItem);
      }
    }
  }
}
