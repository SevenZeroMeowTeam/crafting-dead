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

package com.craftingdead.survival.data.models;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public class SurvivalModelProvider implements DataProvider {

  private static final Logger logger = LogManager.getLogger();
  private static final Gson gson =
      new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
  private final PackOutput output;

  public SurvivalModelProvider(PackOutput output) {
    this.output = output;
  }

  public CompletableFuture<?> run(CachedOutput cache) {
    Map<ResourceLocation, Supplier<JsonElement>> models = Maps.newHashMap();
    BiConsumer<ResourceLocation, Supplier<JsonElement>> modelConsumer = (model, json) -> {
      Supplier<JsonElement> existingJson = models.put(model, json);
      if (existingJson != null) {
        throw new IllegalStateException("Duplicate model definition for " + model);
      }
    };

    new SurvivalItemModelGenerators(modelConsumer).run();

    Path outputFolder = this.output.getOutputFolder();

    return this.saveCollection(cache, outputFolder, models,
        SurvivalModelProvider::createModelPath);
  }

  private <T> CompletableFuture<?> saveCollection(CachedOutput cache, Path outputFolder,
      Map<T, ? extends Supplier<JsonElement>> models, BiFunction<Path, T, Path> pathFunc) {
    return CompletableFuture.allOf(models.entrySet().stream().map(entry -> {
      Path path = pathFunc.apply(outputFolder, entry.getKey());
      return DataProvider.saveStable(cache, entry.getValue().get(), path);
    }).toArray(CompletableFuture[]::new));
  }

  private static Path createModelPath(Path parentDir, ResourceLocation modelLocation) {
    return parentDir.resolve(
        "assets/" + modelLocation.getNamespace() + "/models/" + modelLocation.getPath() + ".json");
  }

  @Override
  public String getName() {
    return "Railcraft Block State Definitions";
  }
}
