/*
 * Crafting Dead (Kotlin refactor)
 * Copyright (C) 2022  NexusNode LTD
 */
package com.craftingdead.core.util

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator
import net.minecraft.util.Mth

/**
 * 缓动函数枚举（Kotlin 重构版）。每个常量是一个 FloatUnaryOperator 函数。
 */
enum class EasingFunction(private val function: FloatUnaryOperator) : FloatUnaryOperator {
    SINE_IN_OUT(FloatUnaryOperator { t -> -(Mth.cos(Math.PI.toFloat() * t) - 1.0f) / 2.0f }),
    SINE_IN(FloatUnaryOperator { t -> 1.0f - Mth.cos(((t * Math.PI) / 2.0f).toFloat()) }),
    SINE_OUT(FloatUnaryOperator { t -> Mth.sin(((t * Math.PI) / 2.0f).toFloat()) }),
    ELASTIC_OUT(FloatUnaryOperator { t ->
        val c4 = (2.0f * Math.PI.toFloat()) / 3.0f
        when {
            t == 0.0f -> 0.0f
            t == 1.0f -> 1.0f
            else -> Math.pow(2.0, -10.0 * t).toFloat() * Mth.sin((t * 10 - 0.75f) * c4) + 1.0f
        }
    }),
    EXPO_OUT(FloatUnaryOperator { t ->
        if (t == 1.0f) 1.0f else -(Math.pow(2.0, -10.0 * t).toFloat()) + 1.0f
    }),
    BOUNCE_OUT(FloatUnaryOperator { t ->
        val n1 = 7.5625f
        val d1 = 2.75f
        var tt = t
        when {
            tt < 1.0f / d1 -> n1 * tt * tt
            tt < 2.0f / d1 -> {
                tt -= 1.5f / d1
                n1 * tt * tt + 0.75f
            }
            tt < 2.5f / d1 -> {
                tt -= 2.25f / d1
                n1 * tt * tt + 0.9375f
            }
            else -> {
                tt -= 2.625f / d1
                n1 * tt * tt + 0.984375f
            }
        }
    }),
    BOUNCE_IN(FloatUnaryOperator { t -> 1.0f - BOUNCE_OUT.apply(1.0f - t) }),
    BOUNCE_IN_OUT(FloatUnaryOperator { t ->
        if (t < 0.5f) {
            (1.0f - BOUNCE_OUT.apply(1.0f - 2.0f * t)) / 2.0f
        } else {
            (1.0f + BOUNCE_OUT.apply(2.0f * t - 1.0f)) / 2.0f
        }
    });

    override fun apply(t: Float): Float = function.apply(t)

    fun andThen(after: FloatUnaryOperator): FloatUnaryOperator =
        FloatUnaryOperator { t -> after.apply(apply(t)) }
}
