/*
 * Crafting Dead (Kotlin refactor)
 * Copyright (C) 2022  NexusNode LTD
 *
 * 工具材质系统：合成的工具会随机获得一种材质，材质决定其挖掘速度与攻击力加成。
 */
package com.craftingdead.core.quality

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import java.util.Random

/**
 * 工具材质枚举。
 *
 * @param id 材质内部名称（用于 NBT 存储）
 * @param level 材质等级，用于判定可挖掘方块（与 Tier 对应）
 * @param digSpeed 挖掘速度（方块破坏速度倍率）
 * @param attackBonus 攻击力加成（在物品基础伤害之上额外增加的伤害）
 */
enum class ToolMaterialType(
    @get:JvmName("getName") val id: String,
    val level: Int,
    val digSpeed: Float,
    val attackBonus: Float
) {
    WOOD("wood", 1, 1.0F, 2.0F),
    STONE("stone", 2, 2.0F, 4.0F),
    IRON("iron", 3, 3.0F, 6.0F),
    GOLD("gold", 4, 4.0F, 2.0F),
    DIAMOND("diamond", 5, 5.0F, 8.0F),
    NETHERITE("netherite", 6, 6.0F, 10.0F);

    fun getDisplayName(): Component =
        Component.translatable("tool_material.craftingdead.$id")
            .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAAAA)))

    companion object {
        private val RANDOM = Random()

        /** 随机抽取一个工具材质（普通到高级概率递减）。 */
        @JvmStatic
        fun rollRandom(): ToolMaterialType {
            val roll = RANDOM.nextInt(100)
            return when {
                roll < 40 -> WOOD
                roll < 65 -> STONE
                roll < 82 -> IRON
                roll < 88 -> GOLD
                roll < 96 -> DIAMOND
                else -> NETHERITE
            }
        }

        /** 按名称解析材质，未知名称返回 null。 */
        @JvmStatic
        fun byName(name: String): ToolMaterialType? =
            entries.firstOrNull { it.id.equals(name, ignoreCase = true) }

        @JvmStatic
        fun all(): List<ToolMaterialType> = entries.toList()
    }
}
