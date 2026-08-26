/*
 * Crafting Dead (Kotlin refactor)
 * Copyright (C) 2022  NexusNode LTD
 */
package com.craftingdead.core.world.item.gun

import com.craftingdead.core.world.entity.extension.EntitySnapshot

data class PendingHit(
    @get:JvmName("tickOffset") val tickOffset: Byte,
    @get:JvmName("playerSnapshot") val playerSnapshot: EntitySnapshot,
    @get:JvmName("hitSnapshot") val hitSnapshot: EntitySnapshot,
    @get:JvmName("randomSeed") val randomSeed: Long,
    @get:JvmName("shotCount") val shotCount: Int
)
