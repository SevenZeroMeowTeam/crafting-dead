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

import com.craftingdead.survival.network.message.SurvivalKillFeedMessage;
import com.craftingdead.survival.network.message.SyncMoonDataMessage;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * 生存模块的独立网络通道，用于同步月亮事件 / 天数数据与击杀信息。
 */
public class SurvivalNetworkChannel {

  public static void register(RegisterPayloadHandlersEvent event) {
    PayloadRegistrar registrar = event.registrar("survival_play");
    registrar.playToClient(SyncMoonDataMessage.TYPE, SyncMoonDataMessage.STREAM_CODEC,
        SyncMoonDataMessage::handle);
    registrar.playToClient(SurvivalKillFeedMessage.TYPE, SurvivalKillFeedMessage.STREAM_CODEC,
        SurvivalKillFeedMessage::handle);
  }
}
