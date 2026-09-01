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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.texture.TextureManager;

/**
 * NeoForge 1.21 makes several Minecraft internals private. This helper
 * accesses them via reflection so the rest of the codebase can use them.
 */
public final class ReflectionUtil {

  private ReflectionUtil() {}

  private static Field field(Class<?> type, String name) throws NoSuchFieldException {
    var f = type.getDeclaredField(name);
    f.setAccessible(true);
    return f;
  }

  private static Method method(Class<?> type, String name, Class<?>... params)
      throws NoSuchMethodException {
    var m = type.getDeclaredMethod(name, params);
    m.setAccessible(true);
    return m;
  }

  public static Object get(Object target, String fieldName) {
    try {
      return field(target.getClass(), fieldName).get(target);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("Unable to access field " + fieldName, e);
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T get(Object target, String fieldName, Class<T> type) {
    return (T) get(target, fieldName);
  }

  public static Object invoke(Object target, String methodName, Object... args) {
    try {
      Class<?>[] paramTypes = new Class<?>[args.length];
      for (int i = 0; i < args.length; i++) {
        paramTypes[i] = args[i] == null ? Object.class : args[i].getClass();
      }
      Method m = method(target.getClass(), methodName, paramTypes);
      return m.invoke(target, args);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("Unable to invoke " + methodName, e);
    }
  }

  /** Access {@code Minecraft.textureManager} (private in 1.21). */
  public static TextureManager textureManager() {
    return get(Minecraft.getInstance(), "textureManager", TextureManager.class);
  }

  /** Access {@code PostChain.passes} (private in 1.21). */
  public static Iterable<?> postChainPasses(PostChain postChain) {
    return get(postChain, "passes", Iterable.class);
  }

  /** Access {@code GameRenderer.getFov} (private in 1.21). */
  public static float gameRendererFov(GameRenderer renderer, Object camera, float partialTicks,
      boolean changingFov) {
    try {
      Method m = method(GameRenderer.class, "getFov",
          net.minecraft.client.Camera.class, float.class, boolean.class);
      return (float) m.invoke(renderer, camera, partialTicks, changingFov);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("Unable to access GameRenderer.getFov", e);
    }
  }
}
