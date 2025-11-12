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

package com.craftingdead.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Provides a resilient way to assign translation keys on
 * {@link ForgeConfigSpec.Builder}
 * instances without depending on stable MCP or official field names.
 */
public final class ConfigTranslationHelper {

    private static final Method builderTranslationMethod = locateTranslationMethod();
    private static final Field builderContextField = locateBuilderContextField();
    private static final Method builderContextTranslationMethod = locateBuilderContextTranslationMethod(
            builderContextField);
    private static final Field builderTranslationField = locateTranslationField();

    private ConfigTranslationHelper() {
    }

    public static ForgeConfigSpec.Builder translation(ForgeConfigSpec.Builder builder, String key) {
        applyTranslation(builder, key);
        return builder;
    }

    public static void applyTranslation(ForgeConfigSpec.Builder builder, String key) {
        if (builderTranslationMethod != null && builderTranslationField != null) {
            try {
                builderTranslationMethod.invoke(builder, key);
                return;
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("Unable to invoke translation on ForgeConfigSpec.Builder", e);
            }
        }

        if (builderContextField != null && builderContextTranslationMethod != null) {
            try {
                Object context = builderContextField.get(builder);
                if (context != null) {
                    builderContextTranslationMethod.invoke(context, key);
                    return;
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("Unable to set translation via BuilderContext", e);
            }
        }

        if (builderTranslationField != null) {
            try {
                builderTranslationField.set(builder, key);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Unable to set translation on ForgeConfigSpec.Builder", e);
            }
        }
    }

    private static Method locateTranslationMethod() {
        try {
            Method method = ForgeConfigSpec.Builder.class.getMethod("translation", String.class);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException ignored) {
            for (Method candidate : ForgeConfigSpec.Builder.class.getDeclaredMethods()) {
                if (candidate.getParameterCount() == 1
                        && candidate.getParameterTypes()[0] == String.class
                        && candidate.getReturnType() == ForgeConfigSpec.Builder.class) {
                    candidate.setAccessible(true);
                    return candidate;
                }
            }
            return null;
        }
    }

    private static Field locateBuilderContextField() {
        for (Field field : ForgeConfigSpec.Builder.class.getDeclaredFields()) {
            if (field.getType().getName().contains("BuilderContext")) {
                field.setAccessible(true);
                return field;
            }
        }
        return null;
    }

    private static Method locateBuilderContextTranslationMethod(Field contextField) {
        if (contextField == null) {
            return null;
        }

        Class<?> contextType = contextField.getType();
        for (Method method : contextType.getDeclaredMethods()) {
            if (method.getParameterCount() == 1 && method.getParameterTypes()[0] == String.class) {
                method.setAccessible(true);
                return method;
            }
        }
        return null;
    }

    private static Field locateTranslationField() {
        try {
            Field field = ForgeConfigSpec.Builder.class.getDeclaredField("translation");
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException firstFailure) {
            try {
                Field field = ForgeConfigSpec.Builder.class.getDeclaredField("f_12516_");
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ignored) {
                return null;
            }
        }
    }
}
