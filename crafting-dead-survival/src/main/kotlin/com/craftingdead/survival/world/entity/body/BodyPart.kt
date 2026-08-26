/*
 * Crafting Dead (Kotlin refactor)
 * Copyright (C) 2022  NexusNode LTD
 *
 * 生物身体部位（用于枪械命中部位判定与断肢效果）。
 *
 * 判定基于命中点相对实体的高度比例（命中点高度 / 实体碰撞箱高度）：
 *  - >= 72%：头部（爆头）
 *  - >= 48%：手臂 / 上身
 *  - >= 30%：腰部
 *  - 其余：腿部
 */
package com.craftingdead.survival.world.entity.body

import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.phys.Vec3

enum class BodyPart {
    HEAD,
    ARM,
    WAIST,
    LEG;

    companion object {
        /** 头部高度比例阈值。 */
        private const val HEAD_RATIO = 0.72
        /** 手臂/上身高度比例阈值。 */
        private const val ARM_RATIO = 0.48
        /** 腰部高度比例阈值。 */
        private const val WAIST_RATIO = 0.30

        /**
         * 根据命中位置判定命中部位。
         *
         * @param entity 被击中的生物
         * @param hitPos 弹道命中点（世界坐标）
         * @return 命中的部位
         */
        @JvmStatic
        fun fromHitPosition(entity: LivingEntity, hitPos: Vec3): BodyPart {
            val ratio = (hitPos.y - entity.y) / entity.bbHeight
            return when {
                ratio >= HEAD_RATIO -> HEAD
                ratio >= ARM_RATIO -> ARM
                ratio >= WAIST_RATIO -> WAIST
                else -> LEG
            }
        }
    }
}
