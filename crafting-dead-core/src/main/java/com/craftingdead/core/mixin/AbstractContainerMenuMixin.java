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

import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.craftingdead.core.network.NetworkChannel;
import com.craftingdead.core.network.Synched;
import com.craftingdead.core.network.message.play.SyncGunContainerSlotMessage;
import com.craftingdead.core.world.item.gun.Gun;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerSynchronizer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.network.PacketDistributor;

//TODO - temp until https://github.com/MinecraftForge/MinecraftForge/pull/8224 gets merged
@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {

  private static final Logger logger = LogUtils.getLogger();

  @Nullable
  @SuppressWarnings("unchecked")
  private ContainerSynchronizer getSynchronizer() {
    try {
      return (ContainerSynchronizer) (Object) ObfuscationReflectionHelper.getPrivateValue(
          AbstractContainerMenu.class, (AbstractContainerMenu) (Object) this, "f_150397_");
    } catch (ObfuscationReflectionHelper.UnableToAccessFieldException e) {
      // f_150397_ is the SRG name of AbstractContainerMenu.synchronizer. If we can't reach it
      // (e.g. mismatched mappings), fall back to the vanilla behaviour.
      logger.error("Failed to access AbstractContainerMenu.synchronizer", e);
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  @Redirect(at = @At(value = "INVOKE",
      target = "Lnet/minecraft/world/item/ItemStack;matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"),
      method = "synchronizeSlotToRemote")
  private boolean matches(ItemStack lastStack, ItemStack currentStack, int slotIndex,
      ItemStack __, Supplier<ItemStack> coppiedStack) {

    ContainerSynchronizer synchronizer = this.getSynchronizer();
    if (synchronizer == null) {
      return ItemStack.matches(lastStack, currentStack);
    }

    var clazz = synchronizer.getClass();
    if (clazz.isAnonymousClass() && clazz.getEnclosingClass() == ServerPlayer.class) {
      Object parent;
      try {
        // this$0
        parent = ObfuscationReflectionHelper.getPrivateValue(
            (Class<ContainerSynchronizer>) synchronizer.getClass(), synchronizer,
            "f_143433_");
      } catch (ObfuscationReflectionHelper.UnableToAccessFieldException e) {
        logger.error("Failed to reflect", e);
        return ItemStack.matches(lastStack, currentStack);
      }

      if (parent instanceof ServerPlayer player) {
        if (!currentStack.equals(lastStack, true)) {
          return false;
        }

        var container = (AbstractContainerMenu) (Object) this;

        currentStack.getCapability(Gun.CAPABILITY)
            .filter(Synched::requiresSync)
            .ifPresent(gun -> {
              if (container == player.inventoryMenu) {
                for (ItemStack equipmentStack : player.getAllSlots()) {
                  // If the item is equipment we don't need to sync it as Minecraft does
                  // that in a separate method (and if we sync it twice the capability wont think
                  // it's dirty anymore on the second call).
                  if (equipmentStack == currentStack) {
                    return;
                  }
                }
              }
              NetworkChannel.PLAY.getSimpleChannel().send(
                  PacketDistributor.PLAYER.with(() -> player),
                  new SyncGunContainerSlotMessage(
                      player.getId(), slotIndex, gun, false));
            });
        return true;
      }
    }
    return ItemStack.matches(lastStack, currentStack);
  }
}
