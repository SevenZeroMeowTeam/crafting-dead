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

package com.craftingdead.core.world.action;

import com.craftingdead.core.CraftingDead;
import com.craftingdead.core.ServerConfig;
import com.craftingdead.core.tags.ModItemTags;
import com.craftingdead.core.world.action.item.BlockItemActionType;
import com.craftingdead.core.world.action.item.EntityItemActionType;
import com.craftingdead.core.world.action.reload.MagazineReloadAction;
import com.craftingdead.core.world.action.reload.RefillableReloadAction;
import com.craftingdead.core.world.effect.ModMobEffects;
import com.craftingdead.core.world.entity.extension.PlayerExtension;
import com.craftingdead.core.world.item.ModItems;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ActionTypes {

  public static final ResourceKey<Registry<ActionType<?>>> REGISTRY_KEY =
      ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(CraftingDead.ID, "action_type"));

  public static final DeferredRegister<ActionType<?>> deferredRegister =
      DeferredRegister.create(REGISTRY_KEY, CraftingDead.ID);

  public static final Registry<ActionType<?>> REGISTRY =
      deferredRegister.makeRegistry(builder -> builder.sync(true));

  public static final DeferredHolder<ActionType<?>, ActionType<?>> MAGAZINE_RELOAD =
      deferredRegister.register("magazine_reload",
          () -> new SimpleActionType<>(MagazineReloadAction::new, true));

  public static final DeferredHolder<ActionType<?>, ActionType<?>> REFILLABLE_RELOAD =
      deferredRegister.register("refillable_reload",
          () -> new SimpleActionType<>(RefillableReloadAction::new, true));

  public static final DeferredHolder<ActionType<?>, ActionType<?>> REMOVE_MAGAZINE =
      deferredRegister.register("remove_magazine",
          () -> new SimpleActionType<>(RemoveMagazineAction::new, true));

  public static final DeferredHolder<ActionType<?>, EntityItemActionType<?>> SHRED_CLOTHING =
      deferredRegister.register("shred_clothing",
          () -> EntityItemActionType.builder(TargetSelector.SELF_ONLY)
              .forItem(itemStack -> itemStack.is(ModItemTags.CLOTHING))
              .customAction((performer, target) -> {
                var random = target.random();
                int randomRagAmount = random.nextInt(3) + 3;

                for (int i = 0; i < randomRagAmount; i++) {
                  if (random.nextBoolean()) {
                    target.entity().spawnAtLocation(
                        new ItemStack(ModItems.CLEAN_RAG::get));
                  } else {
                    target.entity().spawnAtLocation(
                        new ItemStack(ModItems.DIRTY_RAG::get));
                  }
                }
              }, 1.0F)
              .build());

  public static final DeferredHolder<ActionType<?>, EntityItemActionType<?>> USE_SYRINGE =
      deferredRegister.register("use_syringe",
          () -> EntityItemActionType
              .builder((performer, target) -> {
                if (target == null
                    || performer == target
                    || target.entity() instanceof Skeleton) {
                  return Optional.empty();
                }

                var targetEntity = target.entity();
                if (targetEntity.getHealth() > 4) {
                  return Optional.of(target);
                }

                if (performer.entity() instanceof Player player) {
                  player.displayClientMessage(
                      Component.translatable("message.low_health",
                          targetEntity.getDisplayName()).withStyle(ChatFormatting.RED),
                      true);
                }

                return Optional.empty();
              })
              .forItem(ModItems.SYRINGE)
              .duration(16)
              .customAction((performer, target) -> target.entity().hurt(
                  target.entity().damageSources().mobAttack(target.entity()), 2.0F), 1.0F)
              .resultItem(ModItems.BLOOD_SYRINGE)
              .build());

  public static final DeferredHolder<ActionType<?>, EntityItemActionType<?>> USE_FIRST_AID_KIT =
      deferredRegister.register("use_first_aid_kit",
          () -> EntityItemActionType.builder(TargetSelector.SELF_OR_OTHERS)
              .forItem(ModItems.FIRST_AID_KIT)
              .duration(ServerConfig.instance.firstAidKitDurationTicks.get())
              .effect(() -> new MobEffectInstance(MobEffects.HEAL, 1, 
                  (int) Math.max(0, ServerConfig.instance.firstAidKitHealAmount.get().floatValue() - 1)))
              .build());

  public static final DeferredHolder<ActionType<?>, EntityItemActionType<?>> USE_ADRENALINE_SYRINGE =
      deferredRegister.register("use_adrenaline_syringe",
          () -> EntityItemActionType.builder(TargetSelector.SELF_OR_OTHERS)
              .forItem(ModItems.ADRENALINE_SYRINGE)
              .duration(16)
              .resultItem(ModItems.SYRINGE)
              .useResultItemInCreative(false)
              .effect(() -> new MobEffectInstance(ModMobEffects.ADRENALINE, 
                  ServerConfig.instance.adrenalineDurationTicks.get(), 1))
              .build());

  public static final DeferredHolder<ActionType<?>, EntityItemActionType<?>> USE_BLOOD_SYRINGE =
      deferredRegister.register("use_blood_syringe",
          () -> EntityItemActionType.builder(TargetSelector.SELF_OR_OTHERS)
              .forItem(ModItems.BLOOD_SYRINGE)
              .duration(ServerConfig.instance.bloodSyringeDurationTicks.get())
              .resultItem(ModItems.SYRINGE)
              .useResultItemInCreative(false)
              .effect(() -> new MobEffectInstance(MobEffects.HEAL, 1, 
                  (int) Math.max(0, ServerConfig.instance.bloodSyringeHealAmount.get().floatValue() - 1)))
              .build());

  public static final DeferredHolder<ActionType<?>, EntityItemActionType<?>> USE_BANDAGE =
      deferredRegister.register("use_bandage",
          () -> EntityItemActionType.builder(TargetSelector.SELF_OR_OTHERS)
              .forItem(ModItems.BANDAGE)
              .duration(ServerConfig.instance.bandageDurationTicks.get())
              .effect(() -> new MobEffectInstance(MobEffects.HEAL, 1, 
                  (int) Math.max(0, ServerConfig.instance.bandageHealAmount.get().floatValue() - 1)))
              .build());

  public static final DeferredHolder<ActionType<?>, EntityItemActionType<?>> USE_CLEAN_RAG =
      deferredRegister.register("use_clean_rag",
          () -> EntityItemActionType
              .builder(TargetSelector.SELF_OR_OTHERS.hasEffect(ModMobEffects.BLEEDING))
              .forItem(ModItems.CLEAN_RAG)
              .duration(ServerConfig.instance.cleanRagDurationTicks.get())
              .resultItem(ModItems.BLOODY_RAG)
              .build());

  public static final DeferredHolder<ActionType<?>, BlockItemActionType> WASH_RAG =
      deferredRegister.register("wash_rag",
          () -> BlockItemActionType.builder()
              .forItem(itemStack -> itemStack.is(ModItems.DIRTY_RAG.get())
                  || itemStack.is(ModItems.BLOODY_RAG.get()))
              .resultItem(ModItems.CLEAN_RAG)
              .consumeItemInCreative(true)
              .finishSound(SoundEvents.BUCKET_FILL)
              .forFluid(FluidTags.WATER)
              .build());

  public static final DeferredHolder<ActionType<?>, EntityItemActionType<?>> APPLY_HANDCUFFS =
      deferredRegister.register("apply_handcuffs",
          () -> EntityItemActionType.builder(TargetSelector.OTHERS_ONLY
              .players()
              .filter(((Predicate<PlayerExtension<?>>) PlayerExtension::isHandcuffed).negate()))
              .forItem(ModItems.HANDCUFFS)
              .customAction((performer, target) -> {
                target.setHandcuffs(performer.mainHandItem().copy());
                target.entity().displayClientMessage(
                    Component.translatable("handcuffs.handcuffed",
                        performer.entity().getDisplayName())
                            .withStyle(ChatFormatting.RED, ChatFormatting.BOLD),
                    true);
              }, 1.0F)
              .build());
}
