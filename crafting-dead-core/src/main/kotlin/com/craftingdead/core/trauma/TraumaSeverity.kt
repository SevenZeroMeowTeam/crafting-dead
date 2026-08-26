/*
 * Crafting Dead (Kotlin refactor)
 * Copyright (C) 2022  NexusNode LTD
 */
package com.craftingdead.core.trauma

enum class TraumaSeverity {
    NONE, MINOR, MODERATE, SEVERE;

    companion object {
        @JvmStatic
        fun fromOrdinal(ordinal: Int): TraumaSeverity {
            val values = entries
            return if (ordinal < 0 || ordinal >= values.size) NONE else values[ordinal]
        }
    }
}
