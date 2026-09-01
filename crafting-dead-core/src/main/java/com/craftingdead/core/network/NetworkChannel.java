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

import com.craftingdead.core.network.message.play.SyncLivingMessage;
import com.craftingdead.core.network.message.play.OpenEquipmentMenuMessage;
import com.craftingdead.core.network.message.play.SecondaryActionMessage;
import com.craftingdead.core.network.message.play.SetFireModeMessage;
import com.craftingdead.core.network.message.play.TriggerPressedMessage;
import com.craftingdead.core.network.message.play.NPCTriggerPressedMessage;
import com.craftingdead.core.network.message.play.SyncGunContainerSlotMessage;
import com.craftingdead.core.network.message.play.OpenStorageMessage;
import com.craftingdead.core.network.message.play.PerformActionMessage;
import com.craftingdead.core.network.message.play.CancelActionMessage;
import com.craftingdead.core.network.message.play.ValidatePendingHitMessage;
import com.craftingdead.core.network.message.play.CrouchMessage;
import com.craftingdead.core.network.message.play.HitMessage;
import com.craftingdead.core.network.message.play.SyncGunEquipmentSlotMessage;
import com.craftingdead.core.network.message.play.EnableCombatModeMessage;
import com.craftingdead.core.network.message.play.DamageHandcuffsMessage;
import com.craftingdead.core.network.message.play.ParachuteSyncMessage;
import com.craftingdead.core.network.message.play.OpenCraftingMenuMessage;
import com.craftingdead.core.network.message.play.TraumaPacket;
import com.craftingdead.core.network.message.play.SyncProtectionConfigMessage;
import com.craftingdead.core.network.message.play.RightClickStateMessage;
import com.craftingdead.core.network.message.play.BlockDestroyParticleMessage;
import com.craftingdead.core.network.message.play.BlockDestroyActionMessage;
import com.craftingdead.core.network.message.play.AddKillFeedEntryMessage;
import com.craftingdead.core.network.message.play.SortInventoryMessage;
import com.craftingdead.core.CraftingDead;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class NetworkChannel {

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("play");
        registrar.playToClient(SyncLivingMessage.TYPE, SyncLivingMessage.STREAM_CODEC, SyncLivingMessage::handle);
        registrar.playToServer(OpenEquipmentMenuMessage.TYPE, OpenEquipmentMenuMessage.STREAM_CODEC, OpenEquipmentMenuMessage::handle);
        registrar.playBidirectional(SecondaryActionMessage.TYPE, SecondaryActionMessage.STREAM_CODEC, SecondaryActionMessage::handle);
        registrar.playBidirectional(SetFireModeMessage.TYPE, SetFireModeMessage.STREAM_CODEC, SetFireModeMessage::handle);
        registrar.playBidirectional(TriggerPressedMessage.TYPE, TriggerPressedMessage.STREAM_CODEC, TriggerPressedMessage::handle);
        registrar.playBidirectional(NPCTriggerPressedMessage.TYPE, NPCTriggerPressedMessage.STREAM_CODEC, NPCTriggerPressedMessage::handle);
        registrar.playToClient(SyncGunContainerSlotMessage.TYPE, SyncGunContainerSlotMessage.STREAM_CODEC, SyncGunContainerSlotMessage::handle);
        registrar.playToServer(OpenStorageMessage.TYPE, OpenStorageMessage.STREAM_CODEC, OpenStorageMessage::handle);
        registrar.playBidirectional(PerformActionMessage.TYPE, PerformActionMessage.STREAM_CODEC, PerformActionMessage::handle);
        registrar.playBidirectional(CancelActionMessage.TYPE, CancelActionMessage.STREAM_CODEC, CancelActionMessage::handle);
        registrar.playToServer(ValidatePendingHitMessage.TYPE, ValidatePendingHitMessage.STREAM_CODEC, ValidatePendingHitMessage::handle);
        registrar.playBidirectional(CrouchMessage.TYPE, CrouchMessage.STREAM_CODEC, CrouchMessage::handle);
        registrar.playToClient(HitMessage.TYPE, HitMessage.STREAM_CODEC, HitMessage::handle);
        registrar.playToClient(SyncGunEquipmentSlotMessage.TYPE, SyncGunEquipmentSlotMessage.STREAM_CODEC, SyncGunEquipmentSlotMessage::handle);
        registrar.playToServer(EnableCombatModeMessage.TYPE, EnableCombatModeMessage.STREAM_CODEC, EnableCombatModeMessage::handle);
        registrar.playToServer(DamageHandcuffsMessage.TYPE, DamageHandcuffsMessage.STREAM_CODEC, DamageHandcuffsMessage::handle);
        registrar.playToClient(ParachuteSyncMessage.TYPE, ParachuteSyncMessage.STREAM_CODEC, ParachuteSyncMessage::handle);
        registrar.playToServer(OpenCraftingMenuMessage.TYPE, OpenCraftingMenuMessage.STREAM_CODEC, OpenCraftingMenuMessage::handle);
        registrar.playToClient(TraumaPacket.TYPE, TraumaPacket.STREAM_CODEC, TraumaPacket::handle);
        registrar.playToClient(SyncProtectionConfigMessage.TYPE, SyncProtectionConfigMessage.STREAM_CODEC, SyncProtectionConfigMessage::handle);
        registrar.playToServer(RightClickStateMessage.TYPE, RightClickStateMessage.STREAM_CODEC, RightClickStateMessage::handle);
        registrar.playToClient(BlockDestroyParticleMessage.TYPE, BlockDestroyParticleMessage.STREAM_CODEC, BlockDestroyParticleMessage::handle);
        registrar.playToServer(BlockDestroyActionMessage.TYPE, BlockDestroyActionMessage.STREAM_CODEC, BlockDestroyActionMessage::handle);
        registrar.playToClient(AddKillFeedEntryMessage.TYPE, AddKillFeedEntryMessage.STREAM_CODEC, AddKillFeedEntryMessage::handle);
        registrar.playToServer(SortInventoryMessage.TYPE, SortInventoryMessage.STREAM_CODEC, SortInventoryMessage::handle);
    }
}
