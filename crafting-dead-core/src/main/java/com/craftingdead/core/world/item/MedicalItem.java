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

import com.craftingdead.core.ServerConfig;
import com.craftingdead.core.world.action.item.ItemActionType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Supplier;

public class MedicalItem extends ActionItem {

  private final MedicalItemType medicalType;

  public enum MedicalItemType {
    BANDAGE,
    FIRST_AID_KIT,
    ADRENALINE_SYRINGE,
    BLOOD_SYRINGE,
    CLEAN_RAG,
    SYRINGE
  }

  public MedicalItem(Supplier<? extends ItemActionType<?>> itemActionType, Properties properties,
      MedicalItemType medicalType) {
    super(itemActionType, properties);
    this.medicalType = medicalType;
  }

  @Override
  public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> lines,
      @Nonnull TooltipFlag tooltipFlag) {
    super.appendHoverText(stack, level, lines, tooltipFlag);

    switch (this.medicalType) {
      case BANDAGE -> {
        addDurationTooltip(lines, ServerConfig.instance.bandageDurationTicks.get());
        addHealingTooltip(lines, ServerConfig.instance.bandageHealAmount.get().floatValue());
        
        if (ServerConfig.instance.bandageRemovesBleeding.get()) {
          float chance = ServerConfig.instance.bandageBleedReductionChance.get().floatValue() * 100.0f;
          lines.add(new TextComponent("✚ ")
              .withStyle(ChatFormatting.DARK_RED)
              .append(new TranslatableComponent("medical.bleeding_stop_chance")
                  .withStyle(ChatFormatting.GRAY)
                  .append(new TextComponent(String.format(" %.0f%%", chance))
                      .withStyle(ChatFormatting.GREEN))));
        }
      }
      case FIRST_AID_KIT -> {
        addDurationTooltip(lines, ServerConfig.instance.firstAidKitDurationTicks.get());
        addHealingTooltip(lines, ServerConfig.instance.firstAidKitHealAmount.get().floatValue());
        
        if (ServerConfig.instance.firstAidKitRemovesBleeding.get()) {
          lines.add(new TextComponent("✚ ")
              .withStyle(ChatFormatting.DARK_RED)
              .append(new TranslatableComponent("medical.bleeding_stop")
                  .withStyle(ChatFormatting.GREEN)));
        }
        
        float infectionChance = ServerConfig.instance.firstAidKitInfectionReductionChance.get().floatValue() * 100.0f;
        if (infectionChance > 0) {
          lines.add(new TextComponent("✦ ")
              .withStyle(ChatFormatting.LIGHT_PURPLE)
              .append(new TranslatableComponent("medical.infection_reduction_chance")
                  .withStyle(ChatFormatting.GRAY)
                  .append(new TextComponent(String.format(" %.0f%%", infectionChance))
                      .withStyle(ChatFormatting.GREEN))));
        }
        
        int traumaReduction = ServerConfig.instance.firstAidKitTraumaSeverityReduction.get();
        if (traumaReduction > 0) {
          lines.add(new TextComponent("◈ ")
              .withStyle(ChatFormatting.GOLD)
              .append(new TranslatableComponent("medical.trauma_reduction")
                  .withStyle(ChatFormatting.GRAY)
                  .append(new TextComponent(" " + traumaReduction + " levels")
                      .withStyle(ChatFormatting.GREEN))));
        }
      }
      case ADRENALINE_SYRINGE -> {
        addDurationTooltip(lines, 16); // Fixed duration for adrenaline injection
        
        int effectDuration = ServerConfig.instance.adrenalineDurationTicks.get() / 20;
        lines.add(new TextComponent("⌛ ")
            .withStyle(ChatFormatting.YELLOW)
            .append(new TranslatableComponent("medical.effect_duration")
                .withStyle(ChatFormatting.GRAY)
                .append(new TextComponent(" " + effectDuration + "s")
                    .withStyle(ChatFormatting.YELLOW))));
        
        lines.add(new TextComponent("» ")
            .withStyle(ChatFormatting.YELLOW)
            .append(new TranslatableComponent("medical.adrenaline.speed_boost")
                .withStyle(ChatFormatting.GREEN)));
        lines.add(new TextComponent("▣ ")
            .withStyle(ChatFormatting.BLUE)
            .append(new TranslatableComponent("medical.adrenaline.absorption")
                .withStyle(ChatFormatting.GREEN)));
        
        float swayReduction = ServerConfig.instance.adrenalineAimSwayReductionFactor.get().floatValue() * 100.0f;
        if (swayReduction > 0) {
          lines.add(new TextComponent("◈ ")
              .withStyle(ChatFormatting.GOLD)
              .append(new TranslatableComponent("medical.adrenaline.aim_sway_reduction")
                  .withStyle(ChatFormatting.GRAY)
                  .append(new TextComponent(String.format(" %.0f%%", swayReduction))
                      .withStyle(ChatFormatting.GREEN))));
        }
        
        float slowReduction = ServerConfig.instance.adrenalineSlowReductionFactor.get().floatValue() * 100.0f;
        if (slowReduction > 0) {
          lines.add(new TextComponent("« ")
              .withStyle(ChatFormatting.DARK_GRAY)
              .append(new TranslatableComponent("medical.adrenaline.slow_reduction")
                  .withStyle(ChatFormatting.GRAY)
                  .append(new TextComponent(String.format(" %.0f%%", slowReduction))
                      .withStyle(ChatFormatting.GREEN))));
        }
        
        float bleedMultiplier = ServerConfig.instance.adrenalineBleedChanceMultiplier.get().floatValue();
        if (bleedMultiplier != 1.0f) {
          lines.add(new TextComponent("✚ ")
              .withStyle(ChatFormatting.DARK_RED)
              .append(new TranslatableComponent("medical.adrenaline.bleed_multiplier")
                  .withStyle(ChatFormatting.GRAY)
                  .append(new TextComponent(String.format(" %.1fx", bleedMultiplier))
                      .withStyle(bleedMultiplier > 1.0f ? ChatFormatting.GREEN : ChatFormatting.YELLOW))));
        }
      }
      case BLOOD_SYRINGE -> {
        addDurationTooltip(lines, ServerConfig.instance.bloodSyringeDurationTicks.get());
        addHealingTooltip(lines, ServerConfig.instance.bloodSyringeHealAmount.get().floatValue());
      }
      case CLEAN_RAG -> {
        addDurationTooltip(lines, ServerConfig.instance.cleanRagDurationTicks.get());
        
        if (ServerConfig.instance.cleanRagRemovesBleeding.get()) {
          lines.add(new TextComponent("✚ ")
              .withStyle(ChatFormatting.DARK_RED)
              .append(new TranslatableComponent("medical.bleeding_stop")
                  .withStyle(ChatFormatting.GREEN)));
        }
      }
      case SYRINGE -> {
        lines.add(new TextComponent("✦ ")
            .withStyle(ChatFormatting.LIGHT_PURPLE)
            .append(new TranslatableComponent("medical.syringe.empty")
                .withStyle(ChatFormatting.GRAY)));
      }
    }
  }

  private void addDurationTooltip(List<Component> lines, int durationTicks) {
    float durationSeconds = durationTicks / 20.0f;
    lines.add(new TextComponent("⌚ ")
        .withStyle(ChatFormatting.AQUA)
        .append(new TranslatableComponent("medical.use_duration")
            .withStyle(ChatFormatting.GRAY)
            .append(new TextComponent(String.format(" %.1fs", durationSeconds))
                .withStyle(ChatFormatting.YELLOW))));
  }

  private void addHealingTooltip(List<Component> lines, float healAmount) {
    if (healAmount > 0) {
      lines.add(new TextComponent("♥ ")
          .withStyle(ChatFormatting.RED)
          .append(new TranslatableComponent("medical.healing")
              .withStyle(ChatFormatting.GRAY)
              .append(new TextComponent(String.format(" %.1f", healAmount))
                  .withStyle(ChatFormatting.RED))));
    }
  }
}