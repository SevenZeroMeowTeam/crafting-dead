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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.craftingdead.core.network.NetworkChannel;
import com.craftingdead.core.network.message.play.SyncGunEquipmentSlotMessage;
import com.craftingdead.core.world.entity.extension.LivingExtension;
import com.craftingdead.core.world.item.gun.Gun;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

  @Inject(at = @At("RETURN"), method = "isImmobile", cancellable = true)
  private void isImmobile(CallbackInfoReturnable<Boolean> callbackInfo) {
    var self = (LivingEntity) (Object) this;
    self.getCapability(LivingExtension.CAPABILITY).ifPresent(living -> {
      if (!callbackInfo.getReturnValue() && living.isMovementBlocked()) {
        callbackInfo.setReturnValue(true);
      }
    });
  }

  /**
   * 同步装备槽（主手/副手/盔甲）中枪械的内部数据到客户端。
   *
   * <p>枪械的弹药、弹匣、配件等都保存在 ItemStack 的能力（{@code Gun}）中，而不是物品的
   * NBT 里，所以原版的装备变化检测（比较物品 NBT）无法发现它们的变化，必须由本模组主动
   * 发送 {@link SyncGunEquipmentSlotMessage}。
   *
   * <p>1.18.x 的注入点是 {@code collectEquipmentChanges}，但从 1.19.4/1.20.1 起
   * {@code ItemStack.matches} 的调用被移到了 {@code equipmentHasChanged}，旧注入点在
   * 1.20.1 失效并因此被移除（提交 39543033 “Fix runtime mixin errors for Minecraft
   * 1.20.1”），导致手持枪械的弹药/弹匣数据不再同步到客户端：换弹完成后客户端仍持有旧的
   * （空）弹匣，HUD 就会一直显示“空弹药”/弹药数不更新，枪械模型也不会显示新弹匣。
   * 这里按 1.20.1 的方法结构重新接上该同步。
   */
  // TODO - temp until https://github.com/MinecraftForge/MinecraftForge/pull/7630 gets merged
  @Redirect(at = @At(value = "INVOKE",
      target = "Lnet/minecraft/world/item/ItemStack;matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"),
      method = "equipmentHasChanged(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z")
  private boolean matches(ItemStack currentStack, ItemStack lastStack) {
    LivingEntity livingEntity = (LivingEntity) (Object) this;

    // 只有“物品本身没变（同一实例/NBT 相同）、但枪械数据需要同步”时才需要特殊处理，
    // 其余情况直接回退原版逻辑（返回 false 即视为装备发生变化，由原版发送装备包）。
    if (!livingEntity.level().isClientSide() && currentStack.equals(lastStack, true)) {
      var gun = currentStack.getCapability(Gun.CAPABILITY).orElse(null);
      if (gun != null && gun.requiresSync()) {
        for (EquipmentSlot slotType : EquipmentSlot.values()) {
          if (currentStack == livingEntity.getItemBySlot(slotType)) {
            NetworkChannel.PLAY.getSimpleChannel().send(
                PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingEntity),
                new SyncGunEquipmentSlotMessage(livingEntity.getId(), slotType, gun, false));
            break;
          }
        }
      }
    }

    return ItemStack.matches(currentStack, lastStack);
  }
}
