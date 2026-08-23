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

package com.craftingdead.survival.network;

import com.craftingdead.survival.CraftingDeadSurvival;
import com.craftingdead.survival.network.message.SurvivalKillFeedMessage;
import com.craftingdead.survival.network.message.SyncMoonDataMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * 生存模块的独立网络通道，用于同步月亮事件 / 天数数据与击杀信息。
 */
public enum SurvivalNetworkChannel {

  PLAY(new ResourceLocation(CraftingDeadSurvival.ID, "survival_play")) {
    @Override
    protected void registerMessages(SimpleChannel simpleChannel) {
      simpleChannel
          .messageBuilder(SyncMoonDataMessage.class, 0x00, NetworkDirection.PLAY_TO_CLIENT)
          .encoder(SyncMoonDataMessage::encode)
          .decoder(SyncMoonDataMessage::decode)
          .consumerMainThread(SyncMoonDataMessage::handle)
          .add();

      simpleChannel
          .messageBuilder(SurvivalKillFeedMessage.class, 0x01, NetworkDirection.PLAY_TO_CLIENT)
          .encoder(SurvivalKillFeedMessage::encode)
          .decoder(SurvivalKillFeedMessage::decode)
          .consumerMainThread(SurvivalKillFeedMessage::handle)
          .add();
    }
  };

  /**
   * 网络协议版本。
   */
  private static final String NETWORK_VERSION = "0.0.1.2";

  /**
   * 防止重复注册消息。
   */
  private static boolean loaded;

  /**
   * 简单网络通道。
   */
  private final SimpleChannel simpleChannel;

  SurvivalNetworkChannel(ResourceLocation channelName) {
    this.simpleChannel = NetworkRegistry.ChannelBuilder
        .named(channelName)
        .clientAcceptedVersions(NETWORK_VERSION::equals)
        .serverAcceptedVersions(NETWORK_VERSION::equals)
        .networkProtocolVersion(() -> NETWORK_VERSION)
        .simpleChannel();
  }

  protected abstract void registerMessages(SimpleChannel simpleChannel);

  public SimpleChannel getSimpleChannel() {
    return this.simpleChannel;
  }

  public static void loadChannels() {
    if (!loaded) {
      for (SurvivalNetworkChannel channel : SurvivalNetworkChannel.values()) {
        channel.registerMessages(channel.simpleChannel);
      }
      loaded = true;
    }
  }
}
