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

package com.craftingdead.survival.client.waila;

import mcp.mobius.waila.api.IClientRegistrar;
import mcp.mobius.waila.api.IWailaClientPlugin;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * WTHIT（What The Hell Is That?）客户端插件。
 *
 * <p>为本模组僵尸在 WTHIT 工具提示中追加末日生存相关信息（月相 / 月亮事件 / 进化等级 / 尸潮波数）。
 * 该插件是 <b>可选</b> 依赖：只有安装了 WTHIT 时才会由 {@code wthit_plugins.json} 加载，
 * 未安装时此类永远不会被加载，模组照常运行。</p>
 */
@OnlyIn(Dist.CLIENT)
public class CraftingDeadWailaPlugin implements IWailaClientPlugin {

  @Override
  public void register(IClientRegistrar registrar) {
    // 注册实体 provider：作用于所有僵尸（含原版），provider 内部按命名空间过滤为本模组僵尸。
    registrar.body(new ZombieWailaProvider(), Zombie.class);
  }
}
