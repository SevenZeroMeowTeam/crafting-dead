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

package com.craftingdead.core.network;

import com.craftingdead.core.CraftingDead;
import com.craftingdead.core.network.message.play.AddKillFeedEntryMessage;
import com.craftingdead.core.network.message.play.BlockDestroyParticleMessage;
import com.craftingdead.core.network.message.play.BlockDestroyActionMessage;
import com.craftingdead.core.network.message.play.CancelActionMessage;
import com.craftingdead.core.network.message.play.CrouchMessage;
import com.craftingdead.core.network.message.play.DamageHandcuffsMessage;
import com.craftingdead.core.network.message.play.EnableCombatModeMessage;
import com.craftingdead.core.network.message.play.HitMessage;
import com.craftingdead.core.network.message.play.NPCTriggerPressedMessage;
import com.craftingdead.core.network.message.play.OpenCraftingMenuMessage;
import com.craftingdead.core.network.message.play.OpenEquipmentMenuMessage;
import com.craftingdead.core.network.message.play.OpenStorageMessage;
import com.craftingdead.core.network.message.play.ParachuteSyncMessage;
import com.craftingdead.core.network.message.play.PerformActionMessage;
import com.craftingdead.core.network.message.play.RightClickStateMessage;
import com.craftingdead.core.network.message.play.SecondaryActionMessage;
import com.craftingdead.core.network.message.play.SetFireModeMessage;
import com.craftingdead.core.network.message.play.SyncGunContainerSlotMessage;
import com.craftingdead.core.network.message.play.SyncGunEquipmentSlotMessage;
import com.craftingdead.core.network.message.play.SyncLivingMessage;
import com.craftingdead.core.network.message.play.SyncProtectionConfigMessage;
import com.craftingdead.core.network.message.play.TriggerPressedMessage;
import com.craftingdead.core.network.message.play.TraumaPacket;
import com.craftingdead.core.network.message.play.ValidatePendingHitMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.SimpleChannel;

public enum NetworkChannel {

  PLAY(ResourceLocation.fromNamespaceAndPath(CraftingDead.ID, "play")) {
    @Override
    public void registerMessages(SimpleChannel simpleChannel) {
      simpleChannel
          .messageBuilder(SyncLivingMessage.class, 0x00, NetworkDirection.PLAY_TO_CLIENT)
          .encoder(SyncLivingMessage::encode)
          .decoder(SyncLivingMessage::decode)
          .consumerMainThread(SyncLivingMessage::handle)
          .add();

      simpleChannel
          .messageBuilder(OpenEquipmentMenuMessage.class, 0x01, NetworkDirection.PLAY_TO_SERVER)
          .encoder(OpenEquipmentMenuMessage::encode)
          .decoder(OpenEquipmentMenuMessage::decode)
          .consumerMainThread(OpenEquipmentMenuMessage::handle)
          .add();

      simpleChannel
          .messageBuilder(SecondaryActionMessage.class, 0x02)
          .encoder(SecondaryActionMessage::encode)
          .decoder(SecondaryActionMessage::decode)
          .consumerMainThread(SecondaryActionMessage::handle)
          .add();

      simpleChannel
          .messageBuilder(SetFireModeMessage.class, 0x03)
          .encoder(SetFireModeMessage::encode)
          .decoder(SetFireModeMessage::decode)
          .consumerMainThread(SetFireModeMessage::handle)
          .add();

      simpleChannel
          .messageBuilder(TriggerPressedMessage.class, 0x04)
          .encoder(TriggerPressedMessage::encode)
          .decoder(TriggerPressedMessage::decode)
          .consumerMainThread(TriggerPressedMessage::handle)
          .add();

      simpleChannel
          .messageBuilder(NPCTriggerPressedMessage.class, 0x05)
          .encoder(NPCTriggerPressedMessage::encode)
          .decoder(NPCTriggerPressedMessage::decode)
          .consumerMainThread(NPCTriggerPressedMessage::handle)
          .add();

      simpleChannel
          .messageBuilder(SyncGunContainerSlotMessage.class, 0x06, NetworkDirection.PLAY_TO_CLIENT)
          .encoder(SyncGunContainerSlotMessage::encode)
          .decoder(SyncGunContainerSlotMessage::decode)
          .consumerMainThread(SyncGunContainerSlotMessage::handle)
          .add();

      simpleChannel
          .messageBuilder(OpenStorageMessage.class, 0x07, NetworkDirection.PLAY_TO_SERVER)
          .encoder(OpenStorageMessage::encode)
          .decoder(OpenStorageMessage::decode)
          .consumerMainThread(OpenStorageMessage::handle)
          .add();

      simpleChannel
          .messageBuilder(PerformActionMessage.class, 0x08)
          .encoder(PerformActionMessage::encode)
          .decoder(PerformActionMessage::decode)
          .consumerMainThread(PerformActionMessage::handle)
          .add();

      simpleChannel
          .messageBuilder(CancelActionMessage.class, 0x09)
          .encoder(CancelActionMessage::encode)
          .decoder(CancelActionMessage::decode)
          .consumerMainThread(CancelActionMessage::handle)
          .add();

      simpleChannel
          .messageBuilder(ValidatePendingHitMessage.class, 0x0A, NetworkDirection.PLAY_TO_SERVER)
          .encoder(ValidatePendingHitMessage::encode)
          .decoder(ValidatePendingHitMessage::decode)
          .consumerMainThread(ValidatePendingHitMessage::handle)
          .add();

      simpleChannel
          .messageBuilder(CrouchMessage.class, 0x0B)
          .encoder(CrouchMessage::encode)
          .decoder(CrouchMessage::decode)
          .consumerMainThread(CrouchMessage::handle)
          .add();

      simpleChannel
          .messageBuilder(HitMessage.class, 0x0C, NetworkDirection.PLAY_TO_CLIENT)
          .encoder(HitMessage::encode)
          .decoder(HitMessage::decode)
          .consumerMainThread(HitMessage::handle)
          .add();

      simpleChannel
          .messageBuilder(SyncGunEquipmentSlotMessage.class, 0x0D, NetworkDirection.PLAY_TO_CLIENT)
          .encoder(SyncGunEquipmentSlotMessage::encode)
          .decoder(SyncGunEquipmentSlotMessage::decode)
          .consumerMainThread(SyncGunEquipmentSlotMessage::handle)
          .add();

      simpleChannel
          .messageBuilder(EnableCombatModeMessage.class, 0x0E, NetworkDirection.PLAY_TO_SERVER)
          .encoder(EnableCombatModeMessage::encode)
          .decoder(EnableCombatModeMessage::decode)
          .consumerMainThread(EnableCombatModeMessage::handle)
          .add();

      simpleChannel
          .messageBuilder(DamageHandcuffsMessage.class, 0x0F, NetworkDirection.PLAY_TO_SERVER)
          .encoder(DamageHandcuffsMessage::encode)
          .decoder(DamageHandcuffsMessage::decode)
          .consumerMainThread(DamageHandcuffsMessage::handle)
          .add();

      simpleChannel
          .messageBuilder(ParachuteSyncMessage.class, 0x11, NetworkDirection.PLAY_TO_CLIENT)
          .encoder(ParachuteSyncMessage::encode)
          .decoder(ParachuteSyncMessage::decode)
          .consumerMainThread(ParachuteSyncMessage::handle)
          .add();

      simpleChannel
          .messageBuilder(OpenCraftingMenuMessage.class, 0x12, NetworkDirection.PLAY_TO_SERVER)
          .encoder(OpenCraftingMenuMessage::encode)
          .decoder(OpenCraftingMenuMessage::decode)
          .consumerMainThread(OpenCraftingMenuMessage::handle)
          .add();

      simpleChannel
          .messageBuilder(TraumaPacket.class, 0x13, NetworkDirection.PLAY_TO_CLIENT)
          .encoder(TraumaPacket::encode)
          .decoder(TraumaPacket::decode)
          .consumerMainThread(TraumaPacket::handle)
          .add();

      simpleChannel
          .messageBuilder(SyncProtectionConfigMessage.class, 0x14, NetworkDirection.PLAY_TO_CLIENT)
          .encoder(SyncProtectionConfigMessage::encode)
          .decoder(SyncProtectionConfigMessage::decode)
          .consumerMainThread(SyncProtectionConfigMessage::handle)
          .add();

      simpleChannel
          .messageBuilder(RightClickStateMessage.class, 0x15, NetworkDirection.PLAY_TO_SERVER)
          .encoder(RightClickStateMessage::encode)
          .decoder(RightClickStateMessage::decode)
          .consumerMainThread(RightClickStateMessage::handle)
          .add();

      simpleChannel
          .messageBuilder(BlockDestroyParticleMessage.class, 0x16, NetworkDirection.PLAY_TO_CLIENT)
          .encoder(BlockDestroyParticleMessage::encode)
          .decoder(BlockDestroyParticleMessage::decode)
          .consumerMainThread(BlockDestroyParticleMessage::handle)
          .add();

      simpleChannel
          .messageBuilder(BlockDestroyActionMessage.class, 0x17, NetworkDirection.PLAY_TO_SERVER)
          .encoder(BlockDestroyActionMessage::encode)
          .decoder(BlockDestroyActionMessage::decode)
          .consumerMainThread(BlockDestroyActionMessage::handle)
          .add();

      simpleChannel
          .messageBuilder(AddKillFeedEntryMessage.class, 0x18, NetworkDirection.PLAY_TO_CLIENT)
          .encoder(AddKillFeedEntryMessage::encode)
          .decoder(AddKillFeedEntryMessage::decode)
          .consumerMainThread(AddKillFeedEntryMessage::handle)
          .add();
    }
  };

  /**
   * Network protocol version.
   */
    private static final int NETWORK_VERSION = 1;
  /**
   * Prevents re-registering messages.
   */
  private static boolean loaded;
  /**
   * Simple channel.
   */
  private final SimpleChannel simpleChannel;

  NetworkChannel(ResourceLocation channelName) {
    this.simpleChannel = ChannelBuilder
        .named(channelName)
        .clientAcceptedVersions(Channel.VersionTest.exact(NETWORK_VERSION))
        .serverAcceptedVersions(Channel.VersionTest.exact(NETWORK_VERSION))
        .networkProtocolVersion(NETWORK_VERSION)
        .simpleChannel();
  }

  protected abstract void registerMessages(SimpleChannel simpleChannel);

  public SimpleChannel getSimpleChannel() {
    return this.simpleChannel;
  }

  public static void loadChannels() {
    if (!loaded) {
      for (NetworkChannel channel : NetworkChannel.values()) {
        channel.registerMessages(channel.simpleChannel);
      }
      loaded = true;
    }
  }
}
