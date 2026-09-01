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

package com.craftingdead.core.trauma;

import com.craftingdead.core.event.GunEvent;
import com.craftingdead.core.network.NetworkChannel;
import com.craftingdead.core.network.message.play.TraumaPacket;
import com.craftingdead.core.world.entity.extension.LivingExtension;
import com.craftingdead.core.world.item.equipment.Equipment;
import com.mojang.logging.LogUtils;
import java.util.Locale;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.core.registries.BuiltInRegistries;
import org.slf4j.Logger;
import net.minecraft.core.registries.Registries;

public final class TraumaHandler {

  public static final TraumaHandler INSTANCE = new TraumaHandler();

  private static final Logger LOGGER = LogUtils.getLogger();

  private TraumaHandler() {
  }

//  @SubscribeEvent
//  public void handleGunEntityDamaged(GunEvent.EntityDamaged event) {
//    if (!event.headshot() || !(event.target() instanceof LivingEntity living)) {
//      return;
//    }
//    if (living.getLevel().isClientSide()) {
//      return;
//    }
//
//    var target = LivingExtension.get(living);
//    if (target == null) {
//      return;
//    }
//
//    var config = ProtectionConfig.get();
//    var settings = config.headTrauma();
//    if (!settings.enabled()) {
//      return;
//    }
//
//    float incomingEnergy = Math.max(0.0F, event.damage() * settings.damageToEnergyScale());
//    var result = ProtectionLookup.INSTANCE.resolveHeadshot(target, incomingEnergy);
//
//    if (LOGGER.isDebugEnabled()) {
//      logHeadshotDebug(event, living, target, incomingEnergy, result);
//    }
//
//    var effect = result.effect();
//    if (result.severity() == TraumaSeverity.NONE || effect.isEmpty()) {
//      return;
//    }
//
//    applyMobEffects(living, effect);
//
//    if (living instanceof ServerPlayer player) {
//      PacketDistributor.__SEND__(
//          PacketDistributor.PLAYER.with(() -> player),
//          new TraumaPacket(result.severity(), effect.aimSwayTicks(), effect.aimSwayStrength()));
//    }
//  }

  private void applyMobEffects(LivingEntity living, ProtectionConfig.TraumaEffect effect) {
    if (effect.blindnessTicks() > 0) {
      living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, effect.blindnessTicks()));
    }
    if (effect.nauseaTicks() > 0) {
      living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, effect.nauseaTicks()));
    }
    if (effect.slownessTicks() > 0) {
      living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
          effect.slownessTicks(), Math.max(0, effect.slownessAmplifier())));
    }
  }

  private void logHeadshotDebug(GunEvent.EntityDamaged event, LivingEntity living,
      LivingExtension<?, ?> target, float incomingEnergy, ProtectionLookup.HeadshotResult result) {
    var helmetStack = target.getItemInSlot(Equipment.Slot.HAT);
    var vestStack = target.getItemInSlot(Equipment.Slot.VEST);
    var clothingStack = target.getItemInSlot(Equipment.Slot.CLOTHING);

    LOGGER.debug(
        "Headshot trauma debug: target={}, weapon={}, rawDamage={}, incomingEnergy={}, severity={}, helmet={} absorbed={} durabilityLoss={}, vest={} absorbed={} durabilityLoss={}, clothing={} mitigated={}, remainingEnergy={}, helmetEquipped={}",
        living.getName().getString(),
        describeItem(event.getItemStack()),
        formatFloat(event.damage()),
        formatFloat(incomingEnergy),
        result.severity(),
        describeItem(helmetStack),
        formatFloat(result.helmetAbsorbed()),
        result.helmetDurabilityLoss(),
        describeItem(vestStack),
        formatFloat(result.vestAbsorbed()),
        result.vestDurabilityLoss(),
        describeItem(clothingStack),
        formatFloat(result.clothingMitigated()),
        formatFloat(result.remainingEnergy()),
        result.helmetEquipped());
  }

  private static String describeItem(ItemStack stack) {
    if (stack.isEmpty()) {
      return "empty";
    }
    var itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
    String idText = itemId != null ? itemId.toString() : "unknown";
    return stack.getCount() == 1
        ? String.format(Locale.ROOT, "%s(dmg=%d)", idText, stack.getDamageValue())
        : String.format(Locale.ROOT, "%s x%d (dmg=%d)", idText, stack.getCount(),
            stack.getDamageValue());
  }

  private static String formatFloat(float value) {
    return String.format(Locale.ROOT, "%.2f", value);
  }
}
