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

package com.craftingdead.core.client;

import com.craftingdead.core.client.crosshair.CrosshairManager;
import com.craftingdead.core.client.tutorial.ModTutorialSteps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {

  public final ForgeConfigSpec.BooleanValue displayBlood;

  public final ForgeConfigSpec.BooleanValue displayTargetInfo;

  public final ForgeConfigSpec.BooleanValue displayPlantGrowth;

  public final ForgeConfigSpec.BooleanValue moonPhaseZombieTintEnabled;

  public final ForgeConfigSpec.ConfigValue<String> killSound;

  public final ForgeConfigSpec.ConfigValue<String> crosshair;

  public final ForgeConfigSpec.EnumValue<ModTutorialSteps> tutorialStep;

  public ClientConfig(ForgeConfigSpec.Builder builder) {
    builder.push("client");
    {
      this.displayBlood = builder
          .translation("options.craftingdead.client.display_blood")
          .define("displayBlood", true);
      this.displayTargetInfo = builder
          .translation("options.craftingdead.client.display_target_info")
          .comment("Display Jade-like target info overlay (block/entity name, mod and health)")
          .define("displayTargetInfo", true);
      this.displayPlantGrowth = builder
          .translation("options.craftingdead.client.display_plant_growth")
          .comment("瞄准农作物时显示其生长阶段 / 进度")
          .define("displayPlantGrowth", true);
      this.moonPhaseZombieTintEnabled = builder
          .translation("options.craftingdead.client.moon_phase_zombie_tint_enabled")
          .comment("根据当前月相给僵尸模型染上对应的颜色（满月暖金、新月石板灰等）")
          .define("moonPhaseZombieTintEnabled", true);
      this.killSound = builder
          .translation("options.craftingdead.client.kill_sound")
          .define("killSound", SoundEvents.TRIDENT_RETURN.getLocation().toString(),
              v -> v instanceof String s && net.minecraft.resources.ResourceLocation.tryParse(s) != null);
      this.tutorialStep = builder
          .comment("Internal")
          .defineEnum("tutorialStep", ModTutorialSteps.OPEN_EQUIPMENT_MENU);
      this.crosshair = builder
          .translation("options.craftingdead.client.crosshair")
          .define("crosshair", CrosshairManager.DEFAULT_CROSSHAIR.toString(),
              o -> o instanceof String s && net.minecraft.resources.ResourceLocation.tryParse(s) != null);
    }
    builder.pop();
  }
}
