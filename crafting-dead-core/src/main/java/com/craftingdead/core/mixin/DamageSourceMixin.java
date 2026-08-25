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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * 修复 TaCZ（Timeless and Classics Zero）枪械击杀死亡消息显示原始物品 id 的问题。
 *
 * <p>症状：聊天栏死亡消息显示 "… 用 {@code item.tacz.modern_kinetic_gun} 击倒了 …"，
 * 而不是枪械的实际名称（如 "M1014 战斗霰弹枪"）。
 *
 * <p>根因：TaCZ 用统一的 {@code tacz:modern_kinetic_gun} 物品 + {@code GunId} 区分枪械，
 * 其 {@code getName} 通过<b>客户端</b>枪械索引（{@code ClientGunIndex}）解析显示名；
 * 而死亡消息在<b>服务端</b>生成，服务端没有客户端索引，因此名称回退为原始物品 key。
 *
 * <p>修复：服务端无法解析枪名时，读取物品 NBT 中的 {@code GunId}，用 TaCZ 的语言键
 * （{@code {namespace}.gun.{path}.name}，如 {@code tacz.gun.m1014.name}）构造翻译组件，
 * 由各客户端按本地语言正确渲染。仅当显示的确实是原始 id 时才替换，玩家自定义改名的枪不受影响。
 */
@Mixin(DamageSource.class)
public abstract class DamageSourceMixin {

  /** TaCZ 统一枪械物品在服务端回退时显示的名称。 */
  private static final String TACZ_RAW_GUN_NAME = "item.tacz.modern_kinetic_gun";

  @Inject(method = "getLocalizedDeathMessage", at = @At("RETURN"), cancellable = true)
  private void craftingdead$fixTaczGunDeathMessage(LivingEntity victim,
      CallbackInfoReturnable<Component> callbackInfo) {
    final DamageSource source = (DamageSource) (Object) this;
    if (!(source.getEntity() instanceof LivingEntity killer)) {
      return;
    }
    final ItemStack held = killer.getMainHandItem();
    if (held.isEmpty() || !isTaczGun(held)) {
      return;
    }
    // 仅当显示的确实是原始物品 id 时才修复；玩家自定义改名的枪不处理
    if (!TACZ_RAW_GUN_NAME.equals(held.getDisplayName().getString())) {
      return;
    }
    final Component gunName = getTaczGunDisplayName(held);
    if (gunName == null) {
      return;
    }
    final Component victimName = victim.getDisplayName();
    final Component killerName = killer.getDisplayName();
    final String key = "death.attack." + source.getMsgId();
    // 与原版一致：物品存在自定义名（TaCZ 枪通常带 CustomName）时用 .item 变体，
    // 否则也用 .item 变体补上枪名，让击杀信息显示实际枪械名称
    callbackInfo.setReturnValue(
        Component.translatable(key + ".item", victimName, killerName, gunName));
  }

  /**
   * 是否属于 TaCZ 命名空间下的物品（TaCZ 枪械统一为 {@code tacz:modern_kinetic_gun}）。
   */
  private static boolean isTaczGun(ItemStack stack) {
    final ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());
    return key != null && "tacz".equals(key.getNamespace());
  }

  /**
   * 从物品 NBT 中的 {@code GunId} 构造 TaCZ 枪械的显示名翻译组件。
   *
   * <p>TaCZ 语言键格式：{@code {namespace}.gun.{path}.name}（如 {@code tacz.gun.m1014.name}），
   * 由客户端按本地语言渲染。无法解析 GunId 时返回 {@code null}。
   */
  @javax.annotation.Nullable
  private static Component getTaczGunDisplayName(ItemStack stack) {
    final CompoundTag tag =
        stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).getUnsafe();
    if (tag == null || !tag.contains("GunId")) {
      return null;
    }
    final ResourceLocation gunId = ResourceLocation.tryParse(tag.getString("GunId"));
    if (gunId == null) {
      return null;
    }
    return Component.translatable(gunId.getNamespace() + ".gun." + gunId.getPath() + ".name");
  }
}
