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
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * 服务端 → 客户端的击杀信息消息（玩家用什么武器击杀了什么）。
 *
 * <p>{@code weaponName} 用于 TaCZ 枪械：服务端按 GunId 构造真实枪名的翻译组件
 * （如 {@code tacz.gun.m1014.name}），避免 HUD 显示原始物品 id {@code item.tacz.modern_kinetic_gun}。
 */
public record SurvivalKillFeedMessage(Component killerName, Component victimName,
    ResourceLocation weaponId, int weaponCount, @Nullable Component weaponName) {

  public void encode(FriendlyByteBuf out) {
    ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.encode(out, this.killerName);
    ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.encode(out, this.victimName);
    out.writeBoolean(this.weaponId != null);
    if (this.weaponId != null) {
      out.writeResourceLocation(this.weaponId);
      out.writeVarInt(this.weaponCount);
    }
    out.writeBoolean(this.weaponName != null);
    if (this.weaponName != null) {
      ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.encode(out, this.weaponName);
    }
  }

  public static SurvivalKillFeedMessage decode(FriendlyByteBuf in) {
    Component killerName = ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.decode(in);
    Component victimName = ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.decode(in);
    ResourceLocation weaponId = null;
    int weaponCount = 0;
    if (in.readBoolean()) {
      weaponId = in.readResourceLocation();
      weaponCount = in.readVarInt();
    }
    Component weaponName = null;
    if (in.readBoolean()) {
      weaponName = ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.decode(in);
    }
    return new SurvivalKillFeedMessage(killerName, victimName, weaponId, weaponCount,
        weaponName);
  }

  public static void handle(SurvivalKillFeedMessage msg, CustomPayloadEvent.Context ctx) {
    ctx.enqueueWork(() -> {
      if (FMLEnvironment.dist.isClient()) {
        ItemStack weapon = ItemStack.EMPTY;
        if (msg.weaponId() != null) {
          var item = BuiltInRegistries.ITEM.get(msg.weaponId());
          weapon = new ItemStack(item, msg.weaponCount());
        }
        MoonDataHolder.addKillFeed(msg.killerName(), msg.victimName(), weapon,
            msg.weaponName());
      }
    });
  }
}
