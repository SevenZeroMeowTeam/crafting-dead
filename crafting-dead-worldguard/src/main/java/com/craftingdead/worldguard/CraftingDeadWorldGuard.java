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

package com.craftingdead.worldguard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/**
 * NeoForge 模组入口：Crafting Dead WorldGuard 集成。
 *
 * <p>注意：本类不能直接引用任何 WorldGuard / Bukkit 类型，否则在纯 NeoForge 环境
 * （客户端，无 WorldGuard 类）下模组类无法通过 JVM 链接/校验，导致
 * {@code NoClassDefFoundError}。因此所有 WorldGuard 逻辑都放在
 * {@link WorldGuardIntegration}（仅在检测到 WorldGuard 时通过反射加载）。
 */
@Mod(CraftingDeadWorldGuard.ID)
public class CraftingDeadWorldGuard {

  public static final String ID = "craftingdeadworldguard";

  private static final Logger logger = LoggerFactory.getLogger(CraftingDeadWorldGuard.class);

  public CraftingDeadWorldGuard(IEventBus modEventBus) {
    if (!isHybridAvailable()) {
      logger.info("WorldGuard not found - Crafting Dead WorldGuard integration disabled.");
      return;
    }
    try {
      var integration = Class.forName("com.craftingdead.worldguard.WorldGuardIntegration");
      integration.getMethod("init", IEventBus.class).invoke(null, modEventBus);
      logger.info("Crafting Dead WorldGuard integration initialized.");
    } catch (Throwable t) {
      logger.error("Failed to initialize Crafting Dead WorldGuard integration", t);
    }
  }

  private static boolean isHybridAvailable() {
    try {
      Class.forName("com.sk89q.worldguard.WorldGuard");
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }
}
