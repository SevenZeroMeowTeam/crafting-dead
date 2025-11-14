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

package com.craftingdead.immerse.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.jetbrains.annotations.Nullable;
import net.minecraft.CrashReport;

/**
 * Utility to safely access crash {@link Throwable} instances across mapped
 * environments.
 */
public final class CrashReportUtil {

    private static final Method GET_EXCEPTION_METHOD = locateGetExceptionMethod();
    private static final Field THROWABLE_FIELD = locateThrowableField();

    private CrashReportUtil() {
    }

    @Nullable
    public static Throwable extractThrowable(CrashReport crashReport) {
        if (GET_EXCEPTION_METHOD != null) {
            try {
                return (Throwable) GET_EXCEPTION_METHOD.invoke(crashReport);
            } catch (ReflectiveOperationException ignored) {
                // Fall back to field lookup when the accessor method is not available.
            }
        }

        if (THROWABLE_FIELD != null) {
            try {
                return (Throwable) THROWABLE_FIELD.get(crashReport);
            } catch (IllegalAccessException ignored) {
                // Give up if even the declared field is unreachable.
            }
        }

        return null;
    }

    @Nullable
    private static Method locateGetExceptionMethod() {
        try {
            var method = CrashReport.class.getMethod("getException");
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException | SecurityException ignored) {
            return null;
        }
    }

    @Nullable
    private static Field locateThrowableField() {
        for (var field : CrashReport.class.getDeclaredFields()) {
            if (Throwable.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                return field;
            }
        }
        return null;
    }
}
