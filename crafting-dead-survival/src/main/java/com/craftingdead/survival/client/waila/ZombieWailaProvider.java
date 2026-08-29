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

import com.craftingdead.survival.CraftingDeadSurvival;
import com.craftingdead.survival.client.MoonDataHolder;
import com.craftingdead.survival.world.moon.ApocalypseManager;
import com.craftingdead.survival.world.moon.MoonEventType;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 为 Crafting Dead 的僵尸在 WTHIT 工具提示主体（BODY）中追加末世生存信息。
 *
 * <p>数据源为客户端已同步的 {@link MoonDataHolder}（由 {@code SyncMoonDataMessage} 从服务端
 * 同步），因此无需 WTHIT 服务端数据提供器即可显示准确数值。信息包括：</p>
 * <ul>
 *   <li>当前月相名称与强度（满月最强、新月最弱）</li>
 *   <li>激活中的月亮事件（血月 / 蓝月 / 黄月 / 超级…）</li>
 *   <li>僵尸进化等级</li>
 *   <li>尸潮波数</li>
 * </ul>
 *
 * <p>本文件适配 1.19.2：使用 {@link ForgeRegistries#ENTITY_TYPES} 取实体类型键，
 * 用 {@link Style#withColor(int)} 给组件上色，且 {@code MoonEventType} 无
 * {@code getDisplayComponent()}，改用 {@code getDisplayName()}。</p>
 */
@OnlyIn(Dist.CLIENT)
public class ZombieWailaProvider implements IEntityComponentProvider {

  private static final int SECONDARY_COLOR = 0xAAAAAA;

  @Override
  public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
    var entity = accessor.getEntity();
    if (!(entity instanceof Zombie)) {
      return;
    }
    // 仅处理本模组（craftingdeadsurvival 命名空间）注册的僵尸。
    var entityKey = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
    if (entityKey == null || !CraftingDeadSurvival.ID.equals(entityKey.getNamespace())) {
      return;
    }
    // 网络尚未同步月亮数据时先不显示。
    if (!MoonDataHolder.isSynced()) {
      return;
    }

    addMoonPhaseLine(tooltip);
    addMoonEventLine(tooltip);
    addEvolutionLine(tooltip);
    addHordeLine(tooltip);
  }

  /** 月相名称（按月相上色）+ 强度描述。 */
  private void addMoonPhaseLine(ITooltip tooltip) {
    int phase = MoonDataHolder.getMoonPhase();
    var phaseName = Component.literal(ApocalypseManager.getMoonPhaseName(phase))
        .withStyle(Style.EMPTY.withColor(ApocalypseManager.getMoonPhaseColor(phase)));
    var strength = Component.literal(ApocalypseManager.getMoonPhaseStrengthName(phase))
        .withStyle(Style.EMPTY.withColor(SECONDARY_COLOR));
    tooltip.addLine(Component.translatable(
        "waila.craftingdeadsurvival.moon_phase", phaseName, strength));
  }

  /** 夜晚激活的月亮事件（血月 / 蓝月 / 黄月等）。 */
  private void addMoonEventLine(ITooltip tooltip) {
    if (!MoonDataHolder.isActive()) {
      return;
    }
    MoonEventType event = MoonDataHolder.getEventType();
    if (event == MoonEventType.NONE) {
      return;
    }
    tooltip.addLine(Component.translatable(
        "waila.craftingdeadsurvival.moon_event",
        Component.literal(event.getDisplayName())
            .withStyle(Style.EMPTY.withColor(event.getColor()))));
  }

  /** 僵尸进化等级（大于 0 才显示）。 */
  private void addEvolutionLine(ITooltip tooltip) {
    int tier = MoonDataHolder.getEvolutionTier();
    if (tier <= 0) {
      return;
    }
    tooltip.addLine(Component.translatable(
        "waila.craftingdeadsurvival.evolution_tier", tier));
  }

  /** 尸潮波数（大于 0 才显示）。 */
  private void addHordeLine(ITooltip tooltip) {
    int wave = MoonDataHolder.getHordeWave();
    if (wave <= 0) {
      return;
    }
    tooltip.addLine(Component.translatable(
        "waila.craftingdeadsurvival.horde_wave", wave));
  }
}
