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

package com.craftingdead.core.world.damagesource;

import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.core.registries.BuiltInRegistries;

public class KillFeedEntry {

  private final int killerEntityId;
  private final Component killerName;
  private final Component deadName;
  private final ItemStack weaponStack;
  private final Type type;

  public KillFeedEntry(int killerEntityId, Component killerName, Component deadName,
      ItemStack weaponStack, Type type) {
    this.killerEntityId = killerEntityId;
    this.killerName = killerName;
    this.deadName = deadName;
    this.weaponStack = weaponStack;
    this.type = type;
  }

  public int getKillerEntityId() {
    return this.killerEntityId;
  }

  public Component getKillerName() {
    return this.killerName;
  }

  public Component getDeadName() {
    return this.deadName;
  }

  public ItemStack getWeaponStack() {
    return this.weaponStack;
  }

  public Type getType() {
    return this.type;
  }

  public static enum Type {
    NONE, HEADSHOT, WALLBANG, WALLBANG_HEADSHOT;
  }

  public void encode(FriendlyByteBuf out) {
    out.writeVarInt(this.killerEntityId);
    ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.encode(out, this.killerName);
    ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.encode(out, this.deadName);
    // 击杀信息仅用于渲染武器图标，用注册表 id + 数量编码即可；
    // ItemStack.OPTIONAL_STREAM_CODEC 需要 RegistryFriendlyByteBuf（带注册表访问），
    // 而网络消息编解码拿到的是普通 FriendlyByteBuf，强行强转会导致崩溃。
    out.writeBoolean(!this.weaponStack.isEmpty());
    if (!this.weaponStack.isEmpty()) {
      out.writeResourceLocation(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(this.weaponStack.getItem()));
      out.writeVarInt(this.weaponStack.getCount());
    }
    out.writeEnum(this.type);
  }

  public static KillFeedEntry decode(FriendlyByteBuf in) {
    int killerEntityId = in.readVarInt();
    Component killerName = ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.decode(in);
    Component deadName = ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.decode(in);
    ItemStack weaponStack = ItemStack.EMPTY;
    if (in.readBoolean()) {
      var item = BuiltInRegistries.ITEM.get(in.readResourceLocation());
      int count = in.readVarInt();
      if (item != null) {
        weaponStack = new ItemStack(item, count);
      }
    }
    return new KillFeedEntry(killerEntityId, killerName, deadName, weaponStack,
        in.readEnum(KillFeedEntry.Type.class));
  }
}
