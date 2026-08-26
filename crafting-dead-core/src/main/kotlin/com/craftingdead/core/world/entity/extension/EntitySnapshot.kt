/*
 * Crafting Dead (Kotlin refactor)
 * Copyright (C) 2022  NexusNode LTD
 */
package com.craftingdead.core.world.entity.extension

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.util.Mth
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

/**
 * 实体快照（命中验证用）。使用 @get:JvmName 保持 Java record 访问器风格（position() 等）。
 */
data class EntitySnapshot(
    @get:JvmName("position") val position: Vec3,
    @get:JvmName("boundingBox") val boundingBox: AABB,
    @get:JvmName("rotation") val rotation: Vec2,
    @get:JvmName("velocity") val velocity: Vec3,
    @get:JvmName("crouching") val crouching: Boolean,
    @get:JvmName("eyeHeight") val eyeHeight: Float,
    @get:JvmName("complete") val complete: Boolean
) {
    constructor(
        position: Vec3,
        boundingBox: AABB,
        rotation: Vec2,
        velocity: Vec3,
        crouching: Boolean
    ) : this(position, boundingBox, rotation, velocity, crouching, -1f, false)

    fun combineUntrustedSnapshot(snapshot: EntitySnapshot): EntitySnapshot {
        require(complete) { "Snapshot not complete" }

        var pos = snapshot.position
        if (position.distanceTo(snapshot.position) > 1.0) {
            pos = position
        }

        var bb = snapshot.boundingBox
        if (Math.abs(boundingBox.getSize() - snapshot.boundingBox.getSize()) > 1.0E-10) {
            bb = boundingBox
        }

        var rot = snapshot.rotation
        if (Mth.degreesDifferenceAbs(rotation.x, snapshot.rotation.x) > 10.0 ||
            Mth.degreesDifferenceAbs(rotation.y, snapshot.rotation.y) > 10.0
        ) {
            rot = rotation
        }

        var delta = snapshot.velocity
        if (velocity.distanceTo(snapshot.velocity) > 0.1) {
            delta = velocity
        }

        return EntitySnapshot(pos, bb, rot, delta, snapshot.crouching, eyeHeight, true)
    }

    fun encode(out: FriendlyByteBuf) {
        out.writeDouble(position.x)
        out.writeDouble(position.y)
        out.writeDouble(position.z)
        out.writeDouble(boundingBox.minX)
        out.writeDouble(boundingBox.minY)
        out.writeDouble(boundingBox.minZ)
        out.writeDouble(boundingBox.maxX)
        out.writeDouble(boundingBox.maxY)
        out.writeDouble(boundingBox.maxZ)
        out.writeFloat(rotation.x)
        out.writeFloat(rotation.y)
        out.writeDouble(velocity.x)
        out.writeDouble(velocity.y)
        out.writeDouble(velocity.z)
        out.writeBoolean(crouching)
    }

    companion object {
        @JvmStatic
        fun decode(buf: FriendlyByteBuf): EntitySnapshot {
            val position = Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble())
            val minX = buf.readDouble()
            val minY = buf.readDouble()
            val minZ = buf.readDouble()
            val maxX = buf.readDouble()
            val maxY = buf.readDouble()
            val maxZ = buf.readDouble()
            val rotation = Vec2(buf.readFloat(), buf.readFloat())
            val velocity = Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble())
            return EntitySnapshot(
                position,
                AABB(minX, minY, minZ, maxX, maxY, maxZ),
                rotation,
                velocity,
                buf.readBoolean()
            )
        }
    }
}
