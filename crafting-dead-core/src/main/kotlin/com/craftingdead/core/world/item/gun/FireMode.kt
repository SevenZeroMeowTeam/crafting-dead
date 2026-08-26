/*
 * Crafting Dead (Kotlin refactor)
 * Copyright (C) 2022  NexusNode LTD
 */
package com.craftingdead.core.world.item.gun

import com.craftingdead.core.ServerConfig
import com.mojang.serialization.Codec
import net.minecraft.util.StringRepresentable
import java.util.Optional
import java.util.function.IntSupplier

enum class FireMode(
    private val modeName: String,
    private val maxShots: IntSupplier? = null
) : StringRepresentable {
    AUTO("auto"),
    BURST("burst", ServerConfig.instance.burstfireShotsPerBurst::get),
    SEMI("semi", IntSupplier { 1 });

    /**
     * 该开火模式允许连续射击的最大发数。
     */
    fun getMaxShots(): Optional<Int> =
        if (maxShots == null) Optional.empty() else Optional.of(maxShots.getAsInt())

    fun getTranslationKey(): String = "fire_mode.$modeName"

    override fun getSerializedName(): String = modeName

    companion object {
        @JvmField
        val CODEC: Codec<FireMode> =
            StringRepresentable.fromEnum { entries.toTypedArray() }
        private val BY_NAME = entries.associateBy { it.modeName }

        @JvmStatic
        fun byName(name: String): FireMode? = BY_NAME[name]
    }
}
