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

package com.craftingdead.core.client.tutorial;

import net.minecraft.client.tutorial.Tutorial;
import org.jetbrains.annotations.Nullable;

/**
 * NeoForge 1.21 makes {@link Tutorial#instance} private, so access it via reflection.
 */
public final class TutorialUtil {

  private static final String INSTANCE_FIELD = "instance";

  @Nullable
  public static Object getInstance(Tutorial tutorial) {
    try {
      var field = Tutorial.class.getDeclaredField(INSTANCE_FIELD);
      field.setAccessible(true);
      return field.get(tutorial);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      return null;
    }
  }

  public static void setInstance(Tutorial tutorial, Object instance) {
    try {
      var field = Tutorial.class.getDeclaredField(INSTANCE_FIELD);
      field.setAccessible(true);
      field.set(tutorial, instance);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new IllegalStateException("Unable to access Tutorial.instance", e);
    }
  }

  private TutorialUtil() {}
}
