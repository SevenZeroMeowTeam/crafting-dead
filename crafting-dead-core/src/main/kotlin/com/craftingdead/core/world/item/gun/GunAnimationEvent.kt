/*
 * Crafting Dead (Kotlin refactor)
 * Copyright (C) 2022  NexusNode LTD
 */
package com.craftingdead.core.world.item.gun

import com.mojang.serialization.Codec
import net.minecraft.util.StringRepresentable

enum class GunAnimationEvent(private val eventName: String) : StringRepresentable {
    SHOOT("shoot"),
    RELOAD("reload"),
    INSPECT("inspect");

    override fun getSerializedName(): String = eventName

    companion object {
        @JvmField
        val CODEC: Codec<GunAnimationEvent> =
            StringRepresentable.fromEnum { entries.toTypedArray() }
        private val BY_NAME = entries.associateBy { it.eventName }

        @JvmStatic
        fun byName(name: String): GunAnimationEvent? = BY_NAME[name]
    }
}
