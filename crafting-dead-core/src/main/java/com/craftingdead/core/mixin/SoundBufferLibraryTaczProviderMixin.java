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
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Root-cause fix for TaCZ (Timeless &amp; Classics Guns) gun sounds being silent on 1.21.1.
 *
 * <p><b>Diagnosis (proven by log):</b> {@code SoundBufferLibrary.getCompleteBuffer(...)} IS called
 * for TaCZ sounds (e.g. {@code tacz:tacz_sounds/hk_mp5a5/hk_mp5a5_shoot.ogg}), but it completes
 * {code exceptionally} with {@link java.io.FileNotFoundException}. The provider backing
 * {@code SoundBufferLibrary} only sees resources referenced by {@code sounds.json} — TaCZ's gun
 * sounds are resolved dynamically (not via {@code sounds.json}), so the provider's underlying map
 * does not contain them, and {@code ResourceProvider.getResourceOrThrow} throws.
 *
 * <p><b>Fix:</b> Wrap the {@code SoundBufferLibrary} {@link ResourceProvider} at construction time so
 * that, when the original provider cannot resolve a {@code tacz:} resource, it falls back to the
 * global {@link ResourceManager}. The global resource manager does contain every
 * {@code tacz:tacz_sounds/*.ogg} (they ship inside the TaCZ jar's {@code assets/tacz/} pack), which
 * is why the earlier {@code listMatchingResources} found 1349 of them. This makes the buffer load in
 * the same way the vanilla {@code SoundBufferLibrary} would for any sounds.json-referenced sound.
 *
 * <p>Safe when TaCZ is absent: the fallback simply returns empty for unrecognised keys, so behaviour
 * is unchanged.
 */
@Mixin(SoundBufferLibrary.class)
public abstract class SoundBufferLibraryTaczProviderMixin {

  private static final Logger LOGGER = LogUtils.getLogger();

  /**
   * The {@link ResourceProvider} field backing this {@code SoundBufferLibrary}
   * (SRG {@code f_120189_}, official name {@code resourceManager}).
   */
  @Shadow
  @Final
  @Mutable
  private ResourceProvider resourceManager;

  /**
   * At the end of {@code SoundBufferLibrary(ResourceProvider)}, replace the provider with one that
   * falls back to the global {@link ResourceManager} for {@code tacz:} resources.
   */
  @Inject(method = "<init>(Lnet/minecraft/server/packs/resources/ResourceProvider;)V", at = @At("RETURN"))
  private void craftingdead$wrapResourceManager(ResourceProvider provider, CallbackInfo ci) {
    ResourceProvider original = this.resourceManager;
    this.resourceManager = new ResourceProvider() {
      @Override
      public Optional<Resource> getResource(ResourceLocation location) {
        Optional<Resource> direct = original.getResource(location);
        if (direct.isPresent()) {
          return direct;
        }
        if (location.getNamespace().equals("tacz")) {
          ResourceManager global = Minecraft.getInstance().getResourceManager();
          if (global != null) {
            Optional<Resource> fallback = global.getResource(location);
            if (fallback.isPresent()) {
              LOGGER.debug("[CD-PROV] fallback -> global RM for {}", location);
              return fallback;
            }
            LOGGER.warn("[CD-PROV] fallback MISS for {}", location);
          }
        }
        return Optional.empty();
      }
    };
  }
}
