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

package com.craftingdead.core;

import com.craftingdead.core.client.gui.HitMarker;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {

  public static final ServerConfig instance;
  public static final ForgeConfigSpec configSpec;

  static {
    var pair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
    configSpec = pair.getRight();
    instance = pair.getLeft();
  }

  public final ForgeConfigSpec.EnumValue<HitMarker.Mode> hitMarkerMode;
  public final ForgeConfigSpec.BooleanValue killSoundEnabled;

  // ================================================================================
  // Game-Settings Values
  // ================================================================================

  public final ForgeConfigSpec.DoubleValue handcuffDamageChance;

  // ================================================================================
  // Clothing Values
  // ================================================================================

  public final ForgeConfigSpec.DoubleValue casualClothingDamageReduction;
  public final ForgeConfigSpec.DoubleValue casualClothingBleedAndInfectionReduction;
  public final ForgeConfigSpec.DoubleValue utilityClothingDamageReduction;
  public final ForgeConfigSpec.DoubleValue utilityClothingBleedAndInfectionReduction;
  public final ForgeConfigSpec.DoubleValue militaryClothingDamageReduction;
  public final ForgeConfigSpec.DoubleValue militaryClothingBleedAndInfectionReduction;

  // Passive Protection Values
  public final ForgeConfigSpec.DoubleValue casualClothingBiteProtection;
  public final ForgeConfigSpec.DoubleValue casualClothingStabResistance;
  public final ForgeConfigSpec.DoubleValue casualClothingBluntResistance;
  public final ForgeConfigSpec.DoubleValue casualClothingWeightModifier;

  public final ForgeConfigSpec.DoubleValue utilityClothingBiteProtection;
  public final ForgeConfigSpec.DoubleValue utilityClothingStabResistance;
  public final ForgeConfigSpec.DoubleValue utilityClothingBluntResistance;
  public final ForgeConfigSpec.DoubleValue utilityClothingWeightModifier;

  public final ForgeConfigSpec.DoubleValue militaryClothingBiteProtection;
  public final ForgeConfigSpec.DoubleValue militaryClothingStabResistance;
  public final ForgeConfigSpec.DoubleValue militaryClothingBluntResistance;
  public final ForgeConfigSpec.DoubleValue militaryClothingWeightModifier;

  public final ForgeConfigSpec.DoubleValue heavyClothingBiteProtection;
  public final ForgeConfigSpec.DoubleValue heavyClothingStabResistance;
  public final ForgeConfigSpec.DoubleValue heavyClothingBluntResistance;
  public final ForgeConfigSpec.DoubleValue heavyClothingWeightModifier;

  // ================================================================================
  // Medical Consumables Values
  // ================================================================================

  // Bandage
  public final ForgeConfigSpec.DoubleValue bandageHealAmount;
  public final ForgeConfigSpec.IntValue bandageDurationTicks;
  public final ForgeConfigSpec.BooleanValue bandageRemovesBleeding;
  public final ForgeConfigSpec.DoubleValue bandageBleedReductionChance;

  // First Aid Kit
  public final ForgeConfigSpec.DoubleValue firstAidKitHealAmount;
  public final ForgeConfigSpec.IntValue firstAidKitDurationTicks;
  public final ForgeConfigSpec.BooleanValue firstAidKitRemovesBleeding;
  public final ForgeConfigSpec.DoubleValue firstAidKitInfectionReductionChance;
  public final ForgeConfigSpec.IntValue firstAidKitTraumaSeverityReduction;

  // Adrenaline Syringe
  public final ForgeConfigSpec.IntValue adrenalineDurationTicks;
  public final ForgeConfigSpec.IntValue adrenalineCooldownTicks;
  public final ForgeConfigSpec.DoubleValue adrenalineAimSwayReductionFactor;
  public final ForgeConfigSpec.DoubleValue adrenalineSlowReductionFactor;
  public final ForgeConfigSpec.DoubleValue adrenalineBleedChanceMultiplier;

  // Blood Syringe
  public final ForgeConfigSpec.DoubleValue bloodSyringeHealAmount;
  public final ForgeConfigSpec.IntValue bloodSyringeDurationTicks;

  // Clean Rag
  public final ForgeConfigSpec.IntValue cleanRagDurationTicks;
  public final ForgeConfigSpec.BooleanValue cleanRagRemovesBleeding;

  // ================================================================================
  // Burst-fire Values
  // ================================================================================

  public final ForgeConfigSpec.BooleanValue burstfireEnabled;
  public final ForgeConfigSpec.IntValue burstfireShotsPerBurst;

  // ================================================================================
  // Reload Values
  // ================================================================================

  // TODO: Implement those once individual ammo/bullets are added - juanmuscaria
  // public final ForgeConfigSpec.BooleanValue reloadReloadBulletsIndividually;
  // public final ForgeConfigSpec.BooleanValue reloadTakeAmmoOnReload;
  // public final ForgeConfigSpec.BooleanValue reloadTakeAmmoAsMagazine;
  public final ForgeConfigSpec.BooleanValue reloadGunComeEmptyMag;
  public final ForgeConfigSpec.BooleanValue reloadGunComeNoMag;
  public final ForgeConfigSpec.IntValue reloadDuration;
  public final ForgeConfigSpec.BooleanValue reloadDestroyMagWhenEmpty;
  // Sub Category Dual Wield
  // TODO : Implement those once dual wielding is added - juanmuscaria
  // public final ForgeConfigSpec.IntValue reloadDualWieldSingleReloadDuration;
  // public final ForgeConfigSpec.BooleanValue reloadDualWieldSoundsSingleReload;
  // public final ForgeConfigSpec.BooleanValue reloadDualWieldSoundsShootWithNoAmmo;

  // ================================================================================
  // Recoil Values
  // ================================================================================

  public final ForgeConfigSpec.DoubleValue crouchRecoilMultiplier;
  public final ForgeConfigSpec.DoubleValue proneRecoilMultiplier;
  public final ForgeConfigSpec.DoubleValue tacticalGripRecoilMultiplier;
  public final ForgeConfigSpec.DoubleValue bipodRecoilMultiplier;

  // ================================================================================
  // Scope Values
  // ================================================================================

  public final ForgeConfigSpec.BooleanValue scopeAttachmentsAllowed;
  // TODO: Implement when pre define attachments are added - juanmuscaria
  // public final ForgeConfigSpec.BooleanValue scopeAttachmentOverride;
  // TODO: Implement those when Night/Thermal vision scopes are added - juanmuscaria
  // public final ForgeConfigSpec.BooleanValue scopeNightVision;
  // public final ForgeConfigSpec.BooleanValue scopeThermalVision;
  public final ForgeConfigSpec.DoubleValue scopeZoomMultiplier;
  // public final ForgeConfigSpec.BooleanValue scopeZoomBeforeShooting;

  // ================================================================================
  // Riot Shield Values
  // ================================================================================
  // TODO: Implement those when riot shield is added - juanmuscaria
  // public final ForgeConfigSpec.BooleanValue riotShieldEnable;
  // public final ForgeConfigSpec.BooleanValue riotShieldDoNotBlockProjectiles;
  // public final ForgeConfigSpec.BooleanValue riotShieldDoNotBlockMeleeAttacks;
  // public final ForgeConfigSpec.BooleanValue riotShieldDurabilityBasedOnDamage;
  // public final ForgeConfigSpec.IntValue riotShieldDurabilityLossPerHit;
  // public final ForgeConfigSpec.BooleanValue riotShieldForcefieldMode;
  // public final ForgeConfigSpec.BooleanValue riotShieldOnlyWorksWhileBlocking;

  // ================================================================================
  // Headshot Values
  // ================================================================================

  public final ForgeConfigSpec.BooleanValue headshotEnabled;
  public final ForgeConfigSpec.DoubleValue headshotBonusDamage;

  // ================================================================================
  // Backstab Values
  // ================================================================================

  public final ForgeConfigSpec.BooleanValue backstabEnabled;
  public final ForgeConfigSpec.DoubleValue backstabBonusDamage;

  // ================================================================================
  // Critical Hits Values
  // ================================================================================

  public final ForgeConfigSpec.BooleanValue criticalHitEnable;
  public final ForgeConfigSpec.DoubleValue criticalHitBonusDamage;
  public final ForgeConfigSpec.DoubleValue criticalHitChance;

  // ================================================================================
  // Damage Drop Off Values
  // ================================================================================

  public final ForgeConfigSpec.BooleanValue damageDropOffEnable;
  public final ForgeConfigSpec.DoubleValue damageDropOffLoss;
  public final ForgeConfigSpec.DoubleValue damageDropOffMinimumDamage;

  // ================================================================================
  // Explosives Values
  // ================================================================================

  public final ForgeConfigSpec.BooleanValue explosivesRemoteDetonatorEnabled;
  public final ForgeConfigSpec.BooleanValue explosivesC4Enabled;
  public final ForgeConfigSpec.BooleanValue explosivesDecoyGrenadeEnabled;
  public final ForgeConfigSpec.BooleanValue explosivesFireGrenadeEnabled;
  public final ForgeConfigSpec.BooleanValue explosivesFlashGrenadeEnabled;
  public final ForgeConfigSpec.BooleanValue explosivesFragGrenadeEnabled;
  public final ForgeConfigSpec.BooleanValue explosivesSmokeGrenadeEnabled;
  public final ForgeConfigSpec.EnumValue<Level.ExplosionInteraction> explosivesC4ExplosionMode;
  public final ForgeConfigSpec.EnumValue<Level.ExplosionInteraction> explosivesFireGrenadeExplosionMode;
  public final ForgeConfigSpec.EnumValue<Level.ExplosionInteraction> explosivesFragGrenadeExplosionMode;
  public final ForgeConfigSpec.DoubleValue explosivesRemoteDetonatorRange;
  public final ForgeConfigSpec.DoubleValue explosivesC4Radius;
  public final ForgeConfigSpec.DoubleValue explosivesFireGrenadeRadius;
  public final ForgeConfigSpec.DoubleValue explosivesFlashRadius;
  public final ForgeConfigSpec.DoubleValue explosivesFragGrenadeRadius;
  public final ForgeConfigSpec.DoubleValue explosivesSmokeGrenadeRadius;
  public final ForgeConfigSpec.DoubleValue explosivesC4KnockbackMultiplier;
  public final ForgeConfigSpec.DoubleValue explosivesFireGrenadeKnockbackMultiplier;
  public final ForgeConfigSpec.DoubleValue explosivesFragGrenadeKnockbackMultiplier;
  public final ForgeConfigSpec.DoubleValue explosivesC4DamageMultiplier;
  public final ForgeConfigSpec.DoubleValue explosivesFireGrenadeDamageMultiplier;
  public final ForgeConfigSpec.DoubleValue explosivesFragGrenadeDamageMultiplier;
  public final ForgeConfigSpec.IntValue explosivesFlashGrenadeTicksBeforeActivation;
  public final ForgeConfigSpec.IntValue explosivesFragGrenadeTicksBeforeActivation;
  public final ForgeConfigSpec.IntValue explosivesDecoyGrenadeTicksBeforeDeactivation;
  public final ForgeConfigSpec.IntValue explosivesFlashGrenadeTicksBeforeDeactivation;
  public final ForgeConfigSpec.IntValue explosivesSmokeGrenadeTicksBeforeDeactivation;
  public final ForgeConfigSpec.BooleanValue explosivesDispenseGrenades;
  public final ForgeConfigSpec.BooleanValue enableFireGrenadeSecondaryExplosion;

  private ServerConfig(ForgeConfigSpec.Builder builder) {
    this.hitMarkerMode = builder
        .translation("options.craftingdead.server.hit_marker_mode")
        .defineEnum("hitMarkerMode", HitMarker.Mode.HIT_AND_KILL);
    this.killSoundEnabled = builder
        .translation("options.craftingdead.server.kill_sound_enabled")
        .define("killSoundEnabled", true);

    // Game-Settings configuration
    builder
        .comment("General Game-Settings")
        .push("game-settings");
    {
      this.handcuffDamageChance = builder
          .translation("options.craftingdead.server.handcuff_damage_chance")
          .comment("The Chance that the player damages the handcuff")
          .defineInRange("handcuffDamageChance", 0.4D, 0.1D, 1.0D);
    }
    builder.pop();

    // Clothing Values
    builder
        .comment("Values regarding the clothing a player wears")
        .push("clothing-values");
    {
      this.casualClothingDamageReduction = builder
          .translation("options.craftingdead.server.casualClothingDamageReduction")
          .comment("Sets the percentage of damage reduction. Default is 2% (0.02)")
          .defineInRange("casualDamageReduction", 0.02D, 0.00D, 1.00D);

      this.casualClothingBleedAndInfectionReduction = builder
          .translation("options.craftingdead.server.casualClothingBleedAndInfectionReduction")
          .comment("Sets the percentage of bleed and infection chance reduction. Default is 10% (0.10)")
          .defineInRange("casualBleedAndInfectionReduction", 0.10D, 0.00D, 1.00D);

      this.utilityClothingDamageReduction = builder
          .translation("options.craftingdead.server.utilityClothingDamageReduction")
          .comment("Sets the percentage of damage reduction. Default is 5% (0.05)")
          .defineInRange("utilityDamageReduction", 0.05D, 0.00D, 1.00D);

      this.utilityClothingBleedAndInfectionReduction = builder
          .translation("options.craftingdead.server.utilityClothingBleedAndInfectionReduction")
          .comment("Sets the percentage of bleed and infection chance reduction. Default is 20% (0.20)")
          .defineInRange("utilityBleedAndInfectionReduction", 0.20D, 0.00D, 1.00D);

      this.militaryClothingDamageReduction = builder
          .translation("options.craftingdead.server.militaryClothingDamageReduction")
          .comment("Sets the percentage of damage reduction. Default is 8% (0.08)")
          .defineInRange("militaryDamageReduction", 0.08D, 0.00D, 1.00D);

      this.militaryClothingBleedAndInfectionReduction = builder
          .translation("options.craftingdead.server.militaryClothingBleedAndInfectionReduction")
          .comment("Sets the percentage of bleed and infection chance reduction. Default is 30% (0.30)")
          .defineInRange("militaryBleedAndInfectionReduction", 0.30D, 0.00D, 1.00D);

      // Passive Protection - Casual Tier
      this.casualClothingBiteProtection = builder
          .translation("options.craftingdead.server.casualClothingBiteProtection")
          .comment("Bite protection for casual clothing (reduces infection chance). Default is 10% (0.10)")
          .defineInRange("casualBiteProtection", 0.10D, 0.00D, 1.00D);

      this.casualClothingStabResistance = builder
          .translation("options.craftingdead.server.casualClothingStabResistance")
          .comment("Stab resistance for casual clothing (reduces bleeding chance). Default is 5% (0.05)")
          .defineInRange("casualStabResistance", 0.05D, 0.00D, 1.00D);

      this.casualClothingBluntResistance = builder
          .translation("options.craftingdead.server.casualClothingBluntResistance")
          .comment("Blunt resistance for casual clothing (reduces blunt damage). Default is 2% (0.02)")
          .defineInRange("casualBluntResistance", 0.02D, 0.00D, 1.00D);

      this.casualClothingWeightModifier = builder
          .translation("options.craftingdead.server.casualClothingWeightModifier")
          .comment("Weight modifier for casual clothing (affects movement speed). Default is 0% (0.00)")
          .defineInRange("casualWeightModifier", 0.00D, -1.00D, 1.00D);

      // Passive Protection - Utility Tier
      this.utilityClothingBiteProtection = builder
          .translation("options.craftingdead.server.utilityClothingBiteProtection")
          .comment("Bite protection for utility clothing. Default is 50% (0.50)")
          .defineInRange("utilityBiteProtection", 0.50D, 0.00D, 1.00D);

      this.utilityClothingStabResistance = builder
          .translation("options.craftingdead.server.utilityClothingStabResistance")
          .comment("Stab resistance for utility clothing. Default is 15% (0.15)")
          .defineInRange("utilityStabResistance", 0.15D, 0.00D, 1.00D);

      this.utilityClothingBluntResistance = builder
          .translation("options.craftingdead.server.utilityClothingBluntResistance")
          .comment("Blunt resistance for utility clothing. Default is 10% (0.10)")
          .defineInRange("utilityBluntResistance", 0.10D, 0.00D, 1.00D);

      this.utilityClothingWeightModifier = builder
          .translation("options.craftingdead.server.utilityClothingWeightModifier")
          .comment("Weight modifier for utility clothing. Default is -2.5% (-0.025)")
          .defineInRange("utilityWeightModifier", -0.025D, -1.00D, 1.00D);

      // Passive Protection - Military Tier
      this.militaryClothingBiteProtection = builder
          .translation("options.craftingdead.server.militaryClothingBiteProtection")
          .comment("Bite protection for military clothing. Default is 40% (0.40)")
          .defineInRange("militaryBiteProtection", 0.40D, 0.00D, 1.00D);

      this.militaryClothingStabResistance = builder
          .translation("options.craftingdead.server.militaryClothingStabResistance")
          .comment("Stab resistance for military clothing. Default is 30% (0.30)")
          .defineInRange("militaryStabResistance", 0.30D, 0.00D, 1.00D);

      this.militaryClothingBluntResistance = builder
          .translation("options.craftingdead.server.militaryClothingBluntResistance")
          .comment("Blunt resistance for military clothing. Default is 15% (0.15)")
          .defineInRange("militaryBluntResistance", 0.15D, 0.00D, 1.00D);

      this.militaryClothingWeightModifier = builder
          .translation("options.craftingdead.server.militaryClothingWeightModifier")
          .comment("Weight modifier for military clothing. Default is -2% (-0.02)")
          .defineInRange("militaryWeightModifier", -0.02D, -1.00D, 1.00D);

      // Passive Protection - Heavy Tier (Juggernaut, etc.)
      this.heavyClothingBiteProtection = builder
          .translation("options.craftingdead.server.heavyClothingBiteProtection")
          .comment("Bite protection for heavy clothing. Default is 60% (0.60)")
          .defineInRange("heavyBiteProtection", 0.60D, 0.00D, 1.00D);

      this.heavyClothingStabResistance = builder
          .translation("options.craftingdead.server.heavyClothingStabResistance")
          .comment("Stab resistance for heavy clothing. Default is 40% (0.40)")
          .defineInRange("heavyStabResistance", 0.40D, 0.00D, 1.00D);

      this.heavyClothingBluntResistance = builder
          .translation("options.craftingdead.server.heavyClothingBluntResistance")
          .comment("Blunt resistance for heavy clothing. Default is 30% (0.30)")
          .defineInRange("heavyBluntResistance", 0.30D, 0.00D, 1.00D);

      this.heavyClothingWeightModifier = builder
          .translation("options.craftingdead.server.heavyClothingWeightModifier")
          .comment("Weight modifier for heavy clothing. Default is -6% (-0.06)")
          .defineInRange("heavyWeightModifier", -0.06D, -1.00D, 1.00D);
    }
    builder.pop();

    // Medical Consumables Values
    builder
        .comment("Values regarding medical consumables behavior")
        .push("medical-consumables");
    {
      // Bandage configuration
      this.bandageHealAmount = builder
          .translation("options.craftingdead.server.medical.bandage.heal_amount")
          .comment("Amount of health restored by bandages")
          .defineInRange("bandageHealAmount", 2.0D, 0.0D, 20.0D);

      this.bandageDurationTicks = builder
          .translation("options.craftingdead.server.medical.bandage.duration_ticks")
          .comment("Duration in ticks to use a bandage")
          .defineInRange("bandageDurationTicks", 16, 1, 200);

      this.bandageRemovesBleeding = builder
          .translation("options.craftingdead.server.medical.bandage.removes_bleeding")
          .comment("Whether bandages remove bleeding effect")
          .define("bandageRemovesBleeding", false);

      this.bandageBleedReductionChance = builder
          .translation("options.craftingdead.server.medical.bandage.bleed_reduction_chance")
          .comment("Chance to reduce bleeding when using bandage")
          .defineInRange("bandageBleedReductionChance", 0.50D, 0.0D, 1.0D);

      // First Aid Kit configuration
      this.firstAidKitHealAmount = builder
          .translation("options.craftingdead.server.medical.first_aid_kit.heal_amount")
          .comment("Amount of health restored by first aid kit")
          .defineInRange("firstAidKitHealAmount", 8.0D, 0.0D, 20.0D);

      this.firstAidKitDurationTicks = builder
          .translation("options.craftingdead.server.medical.first_aid_kit.duration_ticks")
          .comment("Duration in ticks to use a first aid kit")
          .defineInRange("firstAidKitDurationTicks", 40, 1, 400);

      this.firstAidKitRemovesBleeding = builder
          .translation("options.craftingdead.server.medical.first_aid_kit.removes_bleeding")
          .comment("Whether first aid kits remove bleeding effect")
          .define("firstAidKitRemovesBleeding", true);

      this.firstAidKitInfectionReductionChance = builder
          .translation("options.craftingdead.server.medical.first_aid_kit.infection_reduction_chance")
          .comment("Chance to reduce infection when using first aid kit")
          .defineInRange("firstAidKitInfectionReductionChance", 0.65D, 0.0D, 1.0D);

      this.firstAidKitTraumaSeverityReduction = builder
          .translation("options.craftingdead.server.medical.first_aid_kit.trauma_severity_reduction")
          .comment("Number of trauma severity tiers reduced by first aid kit")
          .defineInRange("firstAidKitTraumaSeverityReduction", 1, 0, 3);

      // Adrenaline Syringe configuration
      this.adrenalineDurationTicks = builder
          .translation("options.craftingdead.server.medical.adrenaline.duration_ticks")
          .comment("Duration in ticks for adrenaline effect")
          .defineInRange("adrenalineDurationTicks", 20 * 20, 1, 20 * 300);

      this.adrenalineCooldownTicks = builder
          .translation("options.craftingdead.server.medical.adrenaline.cooldown_ticks")
          .comment("Cooldown in ticks before adrenaline can be used again")
          .defineInRange("adrenalineCooldownTicks", 20 * 120, 0, 20 * 600);

      this.adrenalineAimSwayReductionFactor = builder
          .translation("options.craftingdead.server.medical.adrenaline.aim_sway_reduction_factor")
          .comment("Factor by which adrenaline reduces aim sway (1.0 = complete reduction)")
          .defineInRange("adrenalineAimSwayReductionFactor", 1.0D, 0.0D, 1.0D);

      this.adrenalineSlowReductionFactor = builder
          .translation("options.craftingdead.server.medical.adrenaline.slow_reduction_factor")
          .comment("Factor by which adrenaline reduces slowness effect")
          .defineInRange("adrenalineSlowReductionFactor", 0.5D, 0.0D, 1.0D);

      this.adrenalineBleedChanceMultiplier = builder
          .translation("options.craftingdead.server.medical.adrenaline.bleed_chance_multiplier")
          .comment("Multiplier for bleeding chance while under adrenaline effect")
          .defineInRange("adrenalineBleedChanceMultiplier", 1.10D, 0.5D, 2.0D);

      // Blood Syringe configuration
      this.bloodSyringeHealAmount = builder
          .translation("options.craftingdead.server.medical.blood_syringe.heal_amount")
          .comment("Amount of health restored by blood syringe")
          .defineInRange("bloodSyringeHealAmount", 4.0D, 0.0D, 20.0D);

      this.bloodSyringeDurationTicks = builder
          .translation("options.craftingdead.server.medical.blood_syringe.duration_ticks")
          .comment("Duration in ticks to use a blood syringe")
          .defineInRange("bloodSyringeDurationTicks", 16, 1, 200);

      // Clean Rag configuration
      this.cleanRagDurationTicks = builder
          .translation("options.craftingdead.server.medical.clean_rag.duration_ticks")
          .comment("Duration in ticks to use a clean rag")
          .defineInRange("cleanRagDurationTicks", 16, 1, 200);

      this.cleanRagRemovesBleeding = builder
          .translation("options.craftingdead.server.medical.clean_rag.removes_bleeding")
          .comment("Whether clean rags remove bleeding effect")
          .define("cleanRagRemovesBleeding", true);
    }
    builder.pop();

    // Burst-fire configuration
    builder
        .comment("Some guns allow 'burstfire', where it can fire multiple shots at the same time",
            "Here you can tweak all settings related to burstfire")
        .push("burstfire");
    {
      this.burstfireEnabled = builder
          .translation("options.craftingdead.server.burstfire.enable")
          .comment("Enable (some) guns to fire a burst of shots")
          .define("enable", true);
      this.burstfireShotsPerBurst = builder
          .translation("options.craftingdead.server.burstfire.shots_per_burst")
          .comment("The amount of shots per fired burst")
          .defineInRange("shotsPerBurst", 3, 1, Integer.MAX_VALUE);
    }
    builder.pop();

    // Reload configuration
    builder
        .comment("Tweak multiple behaviours on how guns should be reloaded as well it's ammo")
        .push("reload");
    {
      // this.reloadReloadBulletsIndividually = builder
      // .translation("")
      // .define("Reload Bullets Individually", true);
      // this.reloadTakeAmmoOnReload = builder
      // .translation("")
      // .define("Take Ammo On Reload", true);
      // this.reloadTakeAmmoAsMagazine = builder
      // .translation("")
      // .define("Take Ammo As Magazine", true);
      this.reloadGunComeEmptyMag = builder
          .translation("options.craftingdead.server.reload.gun_comes_empty_mag")
          .comment("Defines whenever a gun should come with an empty magazine when crafted")
          .define("gunComeEmptyMag", true);
      this.reloadGunComeNoMag = builder
          .translation("options.craftingdead.server.reload.gun_comes_no_mag")
          .comment("Defines whenever a gun should come with no magazine when crafted")
          .define("gunComeNoMag", false);
      this.reloadDuration = builder
          .translation("options.craftingdead.server.reload.extra_reload_duration")
          .comment("Additional reload time applied to the standard gun reload time (Ticks)")
          .defineInRange("duration", 0, 0, 20 * 10);
      this.reloadDestroyMagWhenEmpty = builder
          .translation("options.craftingdead.server.destroy_mag_when_empty")
          .comment(
              "When empty, magazines will be destroyed instead of being given back to the player when reloading")
          .define("destroyMagWhenEmpty", false);

      // Sub category Dual Wield
      // builder
      // .push("Dual Wield");
      // {
      // this.reloadDualWieldSingleReloadDuration = builder
      // .translation("")
      // .defineInRange("Single Reload Duration", 0, 0, 0);
      // this.reloadDualWieldSoundsSingleReload = builder
      // .translation("")
      // .define("Sounds Single Reload", true);
      // this.reloadDualWieldSoundsShootWithNoAmmo = builder
      // .translation("")
      // .define("Sounds Shoot With No Ammo", true);
      // }
      // builder.pop();
    }
    builder.pop();

    // Recoil Configuration
    builder
        .comment("Configure recoil multipliers")
        .push("recoil");
    {
      this.crouchRecoilMultiplier = builder
          .translation("options.craftingdead.server.recoil.crouch_recoil_multiplier")
          .comment("Recoil multiplier applied while crouching. (0.2F = 20% less recoil)")
          .defineInRange("crouchRecoilMultiplier", 0.1D, 0.01D, 1.0D);
      this.proneRecoilMultiplier = builder
          .translation("options.craftingdead.server.recoil.prone_recoil_multiplier")
          .comment("Recoil multiplier applied while prone. (0.2F = 20% less recoil)")
          .defineInRange("proneRecoilMultiplier", 0.2D, 0.01D, 1.0D);
      this.tacticalGripRecoilMultiplier = builder
          .translation("options.craftingdead.server.recoil.tactical_grip_recoil_multiplier")
          .comment("Recoil multiplier applied when a tactical grip is attached. (0.2F = 20% less recoil)")
          .defineInRange("tacticalGripRecoilMultiplier", 0.6D, 0.01D, 1.0D);
      this.bipodRecoilMultiplier = builder
          .translation("options.craftingdead.server.recoil.bipod_recoil_multiplier")
          .comment("Recoil multiplier applied when a bipod is attached. (0.2F = 20% less recoil)")
          .defineInRange("bipodRecoilMultiplier", 0.6D, 0.01D, 1.0D);
    }
    builder.pop();

    // Scope configuration
    builder
        .comment("Tweak multiple scope and attachment behaviors")
        .push("scope");
    {
      this.scopeAttachmentsAllowed = builder
          .translation("options.craftingdead.server.scope.allow_attachments")
          .comment("Defines if attachments can be added by the player",
              "Guns with pre existing attachments or that where added before this option was toggled will remain unchanged")
          .define("attachmentsAllowed", true);
      // this.scopeAttachmentOverride = builder
      // .translation("")
      // .define("AttatchmentOverride", false);
      // this.scopeNightVision = builder
      // .translation("")
      // .define("Night Vision", true);
      // this.scopeThermalVision = builder
      // .translation("")
      // .define("Thermal Vision", true);
      this.scopeZoomMultiplier = builder
          .translation("options.craftingdead.server.zoom_amount_multiplier")
          .comment("Additional zoom given to the base scope zoom (Multiplier)")
          .defineInRange("zoomMultiplier", 1D, 0.1D, 3D);
      // this.scopeZoomBeforeShooting = builder
      // .translation("")
      // .define("Zoom Before Shooting", true);
    }
    builder.pop();

    // Riot Shield configuration
    // builder
    // .push("Riot Shield");
    // {
    // this.riotShieldEnable = builder
    // .translation("")
    // .define("Enable", true);
    // this.riotShieldDoNotBlockProjectiles = builder
    // .translation("")
    // .define("Do Not Block Projectiles", true);
    // this.riotShieldDoNotBlockMeleeAttacks = builder
    // .translation("")
    // .define("Do Not Block Melee Attacks", true);
    // this.riotShieldDurabilityBasedOnDamage = builder
    // .translation("")
    // .define("Durability Based On Damage", true);
    // this.riotShieldDurabilityLossPerHit = builder
    // .translation("")
    // .defineInRange("Durability Loss Per Hit", 0, 0, 0);
    // this.riotShieldForcefieldMode = builder
    // .translation("")
    // .define("Forcefield Mode", true);
    // this.riotShieldOnlyWorksWhileBlocking = builder
    // .translation("")
    // .define("Only Works While Blocking", true);
    // }
    // builder.pop();

    // Headshot configuration
    builder
        .comment("Tweak and toggle headshots and it's properties")
        .push("headshot");
    {
      this.headshotEnabled = builder
          .translation("options.craftingdead.server.headshot.enable")
          .comment("Enables the ability to headshot")
          .define("enabled", true);
      this.headshotBonusDamage = builder
          .translation("options.craftingdead.server.headshot.bonus_damage")
          .comment("Additional bonus damage when a headshot is hit (Multiplier)")
          .defineInRange("bonusDamage", 4.0D, 1.0D, 10D);
    }
    builder.pop();

    // Backstab configuration
    builder
        .comment("Tweak and toggle backstab and it's properties")
        .push("backstab");
    {
      this.backstabEnabled = builder
          .translation("options.craftingdead.server.backstab.enable")
          .comment("Enables the ability to backstab with melee weapons")
          .define("enabled", false);
      this.backstabBonusDamage = builder
          .translation("options.craftingdead.server.backstab.bonus_damage")
          .comment("Additional bonus damage multiplier when backstabbing (Multiplier)")
          .defineInRange("bonusDamage", 1.2D, 1D, 10D);
    }
    builder.pop();

    // Critical Hit configuration
    builder
        .comment("Tweak and toggle critical hits and it's properties")
        .push("criticalHit");
    {
      this.criticalHitEnable = builder
          .translation("options.craftingdead.server.critical_hit.enable")
          .comment("Enables critical hits")
          .define("enabled", false);
      this.criticalHitBonusDamage = builder
          .translation("options.craftingdead.server.critical_hit.bonus_damage")
          .comment("Additional bonus damage multiplier when a critical hit (Multiplier)")
          .defineInRange("bonusDamage", 0.5D, 1D, 10D);
      this.criticalHitChance = builder
          .translation("options.craftingdead.server.critical_hit.chance")
          .comment("How likely the player is to hit a critical hit (Percentage)")
          .defineInRange("chance", 0.005D, 0D, 1D);
    }
    builder.pop();

    // Damage Based On Flight Time configuration
    builder
        .comment("Distance based damage reduction applied to gun shots")
        .push("damageDropOff");
    {
      this.damageDropOffEnable = builder
          .translation("options.craftingdead.server.damage_drop_off.enable")
          .comment("Enables damage dropoff")
          .define("enable", false);
      this.damageDropOffLoss = builder
          .translation("options.craftingdead.server.damage_drop_off.loss")
          .comment("How much damage is lost per block (Percentage)")
          .defineInRange("loss", 0.1D, 0.01D, 100D);
      this.damageDropOffMinimumDamage = builder
          .translation("options.craftingdead.server.damage_drop_off.min_damage")
          .comment("The absolute minimum damage after all the loss is applied")
          .defineInRange("minimumDamage", 2D, 0D, 20D);
    }
    builder.pop();

    // Explosives configuration
    builder
        .comment("Change every aspect of how grenades interact with the world")
        .push("explosives");
    {
      this.explosivesRemoteDetonatorEnabled = builder
          .translation("options.craftingdead.server.explosives.remote_detonator.enable")
          .comment("Enables the usage of Remote Detonator",
              "It wont prevent the ability to get Remote Detonators, only the ability to use it")
          .define("remoteDetonatorEnabled", true);
      this.explosivesC4Enabled = builder
          .translation("options.craftingdead.server.explosives.c4.enable")
          .comment("Enables the usage of C4 Explosive",
              "It wont prevent the ability to get C4 Explosives, only the ability to use it")
          .define("C4Enabled", true);
      this.explosivesDecoyGrenadeEnabled = builder
          .translation("options.craftingdead.server.explosives.decoy_grenade.enable")
          .comment("Enables the usage of Decoy Grenade",
              "It wont prevent the ability to get Decoy Grenades, only the ability to use it")
          .define("decoyGrenadeEnabled", true);
      this.explosivesFireGrenadeEnabled = builder
          .translation("options.craftingdead.server.explosives.fire_grenade.enable")
          .comment("Enables the usage of Fire Grenade",
              "It wont prevent the ability to get Fire Grenades, only the ability to use it")
          .define("fireGrenadeEnabled", true);
      this.explosivesFlashGrenadeEnabled = builder
          .translation("options.craftingdead.server.explosives.flash_grenade.enable")
          .comment("Enables the usage of Flash Grenade",
              "It wont prevent the ability to get Flash Grenades, only the ability to use it")
          .define("flashGrenadeEnabled", true);
      this.explosivesFragGrenadeEnabled = builder
          .translation("options.craftingdead.server.explosives.frag_grenade.enable")
          .comment("Enables the usage of Frag Grenade",
              "It wont prevent the ability to get Frag Grenades, only the ability to use it")
          .define("fragGrenadeEnabled", true);
      this.explosivesSmokeGrenadeEnabled = builder
          .translation("options.craftingdead.server.explosives.smoke_grenade.enable")
          .comment("Enables the usage of Smoke Grenade",
              "It wont prevent the ability to get Smoke Grenade, only the ability to use it")
          .define("smokeGrenadeEnabled", true);
      this.explosivesC4ExplosionMode = builder
          .translation("options.craftingdead.server.explosives.c4.mode")
          .comment("Defines how the explosion should interact with blocks",
              "NONE: No block interaction, blocks will remain unchanged",
              "BREAK: Blocks are broken, they will be dropped when exploded",
              "DESTROY: Blocks are destroyed, nothing will be dropped and only a crater will be left")
          .defineEnum("c4ExplosionMode", Level.ExplosionInteraction.NONE);
      this.explosivesFireGrenadeExplosionMode = builder
          .translation("options.craftingdead.server.explosives.fire_grenade.mode")
          .comment("Defines how the explosion should interact with blocks",
              "NONE: No block interaction, blocks will remain unchanged",
              "BREAK: Blocks are broken, they will be dropped when exploded",
              "DESTROY: Blocks are destroyed, nothing will be dropped and only a crater will be left")
          .defineEnum("fireGrenadeExplosionMode", Level.ExplosionInteraction.NONE);
      this.explosivesFragGrenadeExplosionMode = builder
          .translation("options.craftingdead.server.explosives.frag_grenade.mode")
          .comment("Defines how the explosion should interact with blocks",
              "NONE: No block interaction, blocks will remain unchanged",
              "BREAK: Blocks are broken, they will be dropped when exploded",
              "DESTROY: Blocks are destroyed, nothing will be dropped and only a crater will be left")
          .defineEnum("fragGrenadeExplosionMode", Level.ExplosionInteraction.NONE);
      this.explosivesRemoteDetonatorRange = builder
          .translation("options.craftingdead.server.explosives.remote_detonator.range")
          .comment("Activation range (in blocks) of the Remote Detonator")
          .defineInRange("remoteDetonatorRange", 50D, 1D, 500D);
      this.explosivesC4Radius = builder
          .translation("options.craftingdead.server.explosives.c4.radius")
          .comment("The explosion radius (in blocks), it tells how big the explosion should be")
          .defineInRange("c4Radius", 4D, 0.1D, 50D);
      this.explosivesFireGrenadeRadius = builder
          .translation("options.craftingdead.server.explosives.fire_grenade.radius")
          .comment("The explosion radius (in blocks), it tells how big the explosion should be")
          .defineInRange("fireGrenadeRadius", 2D, 0.1D, 50D);
      this.explosivesFlashRadius = builder
          .translation("options.craftingdead.server.explosives.flash_grenade.radius")
          .comment("The flash radius (in blocks), it tells how big the flash should be")
          .defineInRange("flashGrenadeRadius", 50D, 0.1D, 200D);
      this.explosivesFragGrenadeRadius = builder
          .translation("options.craftingdead.server.explosives.frag_grenade.radius")
          .comment("The explosion radius (in blocks), it tells how big the explosion should be")
          .defineInRange("fragGrenadeRadius", 4D, 0.1D, 50D);
      this.explosivesSmokeGrenadeRadius = builder
          .translation("options.craftingdead.server.explosives.smoke_grenade.radius")
          .comment("The smoke cloud radius (in blocks), it tells how big the smoke cloud should be")
          .defineInRange("smokeGrenadeRadius", 1D, 0.1D, 5D);
      this.explosivesC4KnockbackMultiplier = builder
          .translation("options.craftingdead.server.explosives.c4.knockback")
          .comment("Defines how strong the explosion knockback should be (Multiplier)")
          .defineInRange("c4KnockbackMultiplier", 1D, 0D, 30D);
      this.explosivesFireGrenadeKnockbackMultiplier = builder
          .translation("options.craftingdead.server.explosives.fire_grenade.knockback")
          .comment("Defines how strong the explosion knockback should be (Multiplier)")
          .defineInRange("fireGrenadeKnockbackMultiplier", 1D, 0D, 30D);
      this.explosivesFragGrenadeKnockbackMultiplier = builder
          .translation("options.craftingdead.server.explosives.frag_grenade.knockback")
          .comment("Defines how strong the explosion knockback should be (Multiplier)")
          .defineInRange("fragGrenadeKnockbackMultiplier", 1D, 0D, 30D);
      this.explosivesC4DamageMultiplier = builder
          .translation("options.craftingdead.server.explosives.c4.damage")
          .comment("Multiplies the base damage given by the explosion (Multiplier)")
          .defineInRange("c4DamageMultiplier", 1D, 0D, 30D);
      this.explosivesFireGrenadeDamageMultiplier = builder
          .translation("options.craftingdead.server.explosives.fire_grenade.damage")
          .comment("Multiplies the base damage given by the explosion (Multiplier)")
          .defineInRange("fireGrenadeDamageMultiplier", 1D, 0D, 30D);
      this.explosivesFragGrenadeDamageMultiplier = builder
          .translation("options.craftingdead.server.explosives.frag_grenade.damage")
          .comment("Multiplies the base damage given by the explosion (Multiplier)")
          .defineInRange("fragGrenadeDamageMultiplier", 1D, 0D, 30D);
      this.explosivesFlashGrenadeTicksBeforeActivation = builder
          .translation("options.craftingdead.server.explosives.flash_grenade.activation_tick")
          .comment("How long before the grenade activates automatically (Ticks)")
          .defineInRange("flashGrenadeTicksBeforeActivation", 30, 0, 18000);
      this.explosivesFragGrenadeTicksBeforeActivation = builder
          .translation("options.craftingdead.server.explosives.frag_grenade.activation_tick")
          .comment("How long before the grenade activates automatically (Ticks)")
          .defineInRange("fragGrenadeTicksBeforeActivation", 35, 0, 18000);
      this.explosivesDecoyGrenadeTicksBeforeDeactivation = builder
          .translation("options.craftingdead.server.explosives.decoy_grenade.deactivation_tick")
          .comment("How long before the grenade deactivates automatically (Ticks)")
          .defineInRange("decoyGrenadeTicksBeforeDeactivation", 400, 0, 18000);
      this.explosivesFlashGrenadeTicksBeforeDeactivation = builder
          .translation("options.craftingdead.server.explosives.flash_grenade.deactivation_tick")
          .comment("How long before the grenade deactivates automatically (Ticks)")
          .defineInRange("flashGrenadeTicksBeforeDeactivation", 5, 0, 18000);
      this.explosivesSmokeGrenadeTicksBeforeDeactivation = builder
          .translation("options.craftingdead.server.explosives.smoke_grenade.deactivation_tick")
          .comment("How long before the grenade deactivates automatically (Ticks)")
          .defineInRange("smokeGrenadeTicksBeforeDeactivation", 500, 0, 18000);
      this.explosivesDispenseGrenades =
          builder.translation("options.craftingdead.server.explosives.dispense_grenades")
              .comment("Enables dispensers being able to throw grenades")
              .define("dispenseGrenades", true);
      this.enableFireGrenadeSecondaryExplosion =
          builder.translation("options.craftingdead.server.explosives.fire_grenade_secondary_explosion")
              .comment("Enables an additional explosion with a radius equal to the fire spread")
              .define("fireGrenadeSecondaryExplosion", true);
    }
    builder.pop();
  }
}
