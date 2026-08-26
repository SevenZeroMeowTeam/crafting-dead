/*
 * Crafting Dead (Kotlin refactor)
 * Copyright (C) 2022  NexusNode LTD
 *
 * 物品品质系统。
 *
 * 品质从高到低：
 * 神话（紫粉）> 传说（橙色）> 英雄（红色）> 史诗（金色）> 稀有（紫色）
 *   > 优秀（蓝色）> 普通（绿色）> 劣质（黑色）
 *
 * 品质越高，武器 / 工具造成的伤害越高。
 */
package com.craftingdead.core.quality

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import java.util.Random

/**
 * 装备品质枚举。
 *
 * @param id 品质内部名称（用于语言键 / NBT 存储）
 * @param color 品质颜色（RGB）
 * @param damageMultiplier 伤害倍率，品质越高倍率越高
 * @param weight 随机权重
 */
enum class ItemQuality(
    @get:JvmName("getName") val id: String,
    val color: Int,
    val damageMultiplier: Float,
    val weight: Int
) {
    /** 神话 - 紫粉（最高品质，无视游戏规则，由下界合金+钻石合成） */
    MYTHIC("mythic", 0xFF00FF, 5.0F, 1),
    /** 传说 - 橙色 */
    LEGENDARY("legendary", 0xFFB000, 3.0F, 1),
    /** 英雄 - 红色 */
    HERO("hero", 0xFF2A2A, 2.5F, 2),
    /** 史诗 - 金色 */
    EPIC("epic", 0xFFAA00, 2.0F, 4),
    /** 稀有 - 紫色 */
    RARE("rare", 0xAA00AA, 1.6F, 8),
    /** 优秀 - 蓝色 */
    EXCELLENT("excellent", 0x5555FF, 1.3F, 12),
    /** 普通 - 绿色 */
    COMMON("common", 0x00AA00, 1.0F, 24),
    /** 劣质 - 黑色 */
    POOR("poor", 0x404040, 0.6F, 8);

    fun getDisplayName(): Component =
        Component.translatable("quality.craftingdead.$id")
            .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)))

    companion object {
        private val RANDOM = Random()

        /** 根据权重随机抽取一个品质。 */
        @JvmStatic
        fun rollRandom(): ItemQuality {
            val totalWeight = entries.sumOf { it.weight }
            var roll = RANDOM.nextInt(totalWeight)
            for (quality in entries) {
                roll -= quality.weight
                if (roll < 0) {
                    return quality
                }
            }
            return COMMON
        }

        /** 按名称解析品质，未知名称返回 null。 */
        @JvmStatic
        fun byName(name: String): ItemQuality? =
            entries.firstOrNull { it.id.equals(name, ignoreCase = true) }
    }
}
