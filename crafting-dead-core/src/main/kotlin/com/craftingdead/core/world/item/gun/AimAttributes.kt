/*
 * Crafting Dead (Kotlin refactor)
 * Copyright (C) 2022  NexusNode LTD
 *
 * 枪械瞄准属性。
 *
 * @property boltAction 是否栓动式枪械
 */
package com.craftingdead.core.world.item.gun

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

data class AimAttributes(
    @get:JvmName("boltAction") val boltAction: Boolean
) {
    companion object {
        @JvmField
        val CODEC: Codec<AimAttributes> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.BOOL
                    .optionalFieldOf("bolt_action", false)
                    .forGetter(AimAttributes::boltAction)
            ).apply(instance, ::AimAttributes)
        }
    }
}
