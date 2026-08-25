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

package com.craftingdead.survival.network.message;

import com.craftingdead.survival.client.MoonDataHolder;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 服务端 → 客户端的击杀信息消息（玩家用什么武器击杀了什么）。
 *
 * <p>{@code weaponName} 用于 TaCZ 枪械：服务端按 GunId 构造真实枪名的翻译组件
 * （如 {@code tacz.gun.m1014.name}），避免 HUD 显示原始物品 id {@code item.tacz.modern_kinetic_gun}。
 */
public record SurvivalKillFeedMessage(Component killerName, Component victimName,
    ResourceLocation weaponId, int weaponCount, @Nullable Component weaponName) {

  public void encode(FriendlyByteBuf out) {
    out.writeComponent(this.killerName);
    out.writeComponent(this.victimName);
    out.writeBoolean(this.weaponId != null);
    if (this.weaponId != null) {
      out.writeResourceLocation(this.weaponId);
      out.writeVarInt(this.weaponCount);
    }
    out.writeBoolean(this.weaponName != null);
    if (this.weaponName != null) {
      out.writeComponent(this.weaponName);
    }
  }

  public static SurvivalKillFeedMessage decode(FriendlyByteBuf in) {
    Component killerName = in.readComponent();
    Component victimName = in.readComponent();
    ResourceLocation weaponId = null;
    int weaponCount = 0;
    if (in.readBoolean()) {
      weaponId = in.readResourceLocation();
      weaponCount = in.readVarInt();
    }
    Component weaponName = null;
    if (in.readBoolean()) {
      weaponName = in.readComponent();
    }
    return new SurvivalKillFeedMessage(killerName, victimName, weaponId, weaponCount,
        weaponName);
  }

  public boolean handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      if (FMLEnvironment.dist.isClient()) {
        ItemStack weapon = ItemStack.EMPTY;
        if (this.weaponId != null) {
          var item = ForgeRegistries.ITEMS.getValue(this.weaponId);
          if (item != null) {
            weapon = new ItemStack(item, this.weaponCount);
          }
        }
        MoonDataHolder.addKillFeed(this.killerName, this.victimName, weapon, this.weaponName);
      }
    });
    return true;
  }
}
