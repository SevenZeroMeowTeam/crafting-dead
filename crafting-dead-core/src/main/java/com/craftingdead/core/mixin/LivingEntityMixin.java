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
import com.craftingdead.core.network.message.play.SyncGunEquipmentSlotMessage;
import com.craftingdead.core.world.entity.extension.LivingExtension;
import com.craftingdead.core.world.item.gun.Gun;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

  @Inject(at = @At("RETURN"), method = "isImmobile", cancellable = true)
  private void isImmobile(CallbackInfoReturnable<Boolean> callbackInfo) {
    var self = (LivingEntity) (Object) this;
    var living = self.getCapability(LivingExtension.CAPABILITY);
    if (living != null && !callbackInfo.getReturnValue() && living.isMovementBlocked()) {
      callbackInfo.setReturnValue(true);
    }
  }

  // TODO - temp until https://github.com/MinecraftForge/MinecraftForge/pull/7630 gets merged
  // 服务端每 tick 都会走 collectEquipmentChanges 检查各装备槽（主手/副手/盔甲）堆栈是否变化。
  // 枪械弹药/弹匣存在 ItemStack 的能力对象里而非物品数据组件中，堆栈本身不变（matches 为真），
  // 原版 equipmentHasChanged 返回 false 不广播任何东西 → 客户端主手枪的弹药永远不会刷新。
  // 因此当堆栈未变、但其枪械能力 requiresSync() 时，补发 SyncGunEquipmentSlotMessage 同步弹药状态
  // （此钩子与 AbstractContainerMenuMixin 配套：容器侧对装备槽枪械故意跳过，交给这里处理）。
  @Redirect(method = "collectEquipmentChanges",
      at = @At(value = "INVOKE",
          target = "Lnet/minecraft/world/entity/LivingEntity;equipmentHasChanged(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
  private boolean craftingdead$equipmentHasChanged(ItemStack lastStack, ItemStack currentStack) {
    if (!ItemStack.matches(currentStack, lastStack)) {
      // 堆栈本身变化：交给原版正常处理装备变更 / 广播
      return true;
    }

    // 堆栈相同：仅枪械能力（弹药）可能变化，找到对应装备槽并补发同步
    var self = (LivingEntity) (Object) this;
    for (EquipmentSlot slotType : EquipmentSlot.values()) {
      if (currentStack == self.getItemBySlot(slotType)) {
        var gun = currentStack.getCapability(Gun.CAPABILITY);
        if (gun != null && gun.requiresSync()) {
          PacketDistributor.sendToPlayersTrackingEntityAndSelf(self,
              new SyncGunEquipmentSlotMessage(self.getId(), slotType, gun, false,
                  self.level().registryAccess()));
        }
      }
    }
    return false;
  }
}
