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

package com.craftingdead.core.mixin;

import com.mojang.logging.LogUtils;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fix for TaCZ (Timeless &amp; Classics Guns) gun sounds being silent on Minecraft 1.21.1.
 *
 * <p><b>Symptom:</b> fires / reloads play no sound, while other sounds (footsteps, ambient)
 * play normally.
 *
 * <p><b>Root cause:</b> Minecraft's {@link SoundManager} builds a dedicated
 * {@link net.minecraft.server.packs.resources.ResourceProvider} from its {@code registry}
 * map (a {@code Map<ResourceLocation, Resource>} populated only from {@code sounds.json}
 * files). This sound resource provider is the ONLY one {@code SoundBufferLibrary} uses to
 * open sound buffers.
 *
 * <p>TaCZ's gun sounds {@code tacz:tacz_sounds/*.ogg} are NOT referenced by any
 * {@code sounds.json}; they are resolved at runtime by {@code GunSoundInstance} via
 * {@link FileToIdConverter#idToFile}. Because they are absent from {@code registry},
 * {@code SoundBufferLibrary.open(...)} cannot find them. On-demand loading fails, the OpenAL
 * channel receives no buffer, and the gun plays silence.
 *
 * <p>TaCZ ships its own {@code SoundManagerPreparationsMixin} to inject these resources into
 * {@code registry}, but that mixin is <b>not registered</b> in the shipped
 * {@code tacz.mixins.json}, so it is dead code. This mixin replicates that behaviour at the
 * correct injection point (end of {@link SoundManager#apply}), merging every
 * {@code tacz:tacz_sounds/*.ogg} resource into the live {@code registry} map. Since
 * {@code SoundBufferLibrary} holds a reference to the same map, the buffers now load and the
 * gun sounds become audible.
 *
 * <p>Safe to apply when TaCZ is absent: {@code listMatchingResources} simply returns nothing,
 * so the mixin is a no-op.
 */
@Mixin(SoundManager.class)
public abstract class SoundManagerTaczSoundMixin {

  private static final Logger LOGGER = LogUtils.getLogger();

  /**
   * The live sound <b>registry</b> that backs the {@link net.minecraft.server.packs.resources.ResourceProvider}
   * handed to the {@code SoundEngine} ({@code ResourceProvider.fromMap(this.registry)}). This is the map
   * {@code SoundBufferLibrary} resolves sound buffers from, so TaCZ's gun sounds must be injected here.
   */
  @Shadow
  @Final
  private Map<ResourceLocation, Resource> registry;

  /**
   * After the SoundManager finishes applying its reload, add all TaCZ gun sound resources so
   * {@code SoundBufferLibrary} can open them.
   *
   * <p>Note: the handler only takes a {@link CallbackInfo} on purpose. The first parameter of the
   * target {@code apply(...)} is the {@code protected} {@code SoundManager$Preparations} type, which
   * is not referencable from this package. Mixin therefore matches the target with an empty parameter
   * prefix, and the resource manager is obtained from {@link Minecraft#getResourceManager()}, which is
   * the same instance the reload passes to {@code apply}.
   */
  @Inject(
      method = "apply(Lnet/minecraft/client/sounds/SoundManager$Preparations;"
          + "Lnet/minecraft/server/packs/resources/ResourceManager;"
          + "Lnet/minecraft/util/profiling/ProfilerFiller;)V",
      at = @At("RETURN"))
  private void cd$injectTaczGunSounds(CallbackInfo ci) {
    Minecraft minecraft = Minecraft.getInstance();
    if (minecraft == null) {
      return;
    }
    ResourceManager resourceManager = minecraft.getResourceManager();
    if (resourceManager == null) {
      return;
    }
    FileToIdConverter converter = new FileToIdConverter("tacz_sounds", ".ogg");
    Map<ResourceLocation, Resource> found = converter.listMatchingResources(resourceManager);
    int before = this.registry.size();
    this.registry.putAll(found);
    if (found.size() > 0) {
      LOGGER.info("[CD-SND] registry size {} -> {}, found {} tacz_sounds",
          before, this.registry.size(), found.size());
    }
  }
}
