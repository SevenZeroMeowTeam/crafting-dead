/*
 * Crafting Dead
 * Copyright (C) 2022  NexusNode LTD
 *
 * This Non-Commercial Software License Agreement (the "Agreement") is made between
 * you (the "Licensee") and NEXUSNODE (BRAD HUNTER). (the "Licensor").
 * By installing or otherwise using Crafting Dead (the "Software"), you agree to be
 * bound by the terms and conditions of this Agreement as may be revised from time
 * to time at Licensor's sole discretion.
 *
 * If you do not agree to the terms and conditions of this Agreement do not download,
 * copy, reproduce or otherwise use any of the source code available online at any time.
 *
 * https://github.com/nexusnode/crafting-dead/blob/1.18.x/LICENSE.txt
 *
 * https://craftingdead.net/terms.php
 */

package com.craftingdead.core.client.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;

public final class MinecraftUtil {

    private static final Supplier<Minecraft> minecraftSupplier = locateMinecraftSupplier();

    private MinecraftUtil() {
    }

    public static Minecraft getInstance() {
        return minecraftSupplier.get();
    }

    private static Supplier<Minecraft> locateMinecraftSupplier() {
        try {
            Method method = Minecraft.class.getMethod("getInstance");
            return () -> invokeFactory(method);
        } catch (NoSuchMethodException fallback) {
            try {
                Method method = Minecraft.class.getDeclaredMethod("m_91087_");
                method.setAccessible(true);
                return () -> invokeFactory(method);
            } catch (NoSuchMethodException missing) {
                throw new IllegalStateException("Unable to locate Minecraft singleton accessor", missing);
            }
        }
    }

    private static Minecraft invokeFactory(Method method) {
        try {
            return (Minecraft) method.invoke(null);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to acquire Minecraft instance", e);
        }
    }
}
