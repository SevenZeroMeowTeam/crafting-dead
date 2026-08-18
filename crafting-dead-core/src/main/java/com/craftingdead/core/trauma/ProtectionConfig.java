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

package com.craftingdead.core.trauma;

import com.craftingdead.core.CraftingDead;
import com.craftingdead.core.world.item.ModItems;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

/**
 * Loads and exposes configurable protection profiles for helmets, vests and trauma behaviour.
 */
public final class ProtectionConfig {

  private static final Logger LOGGER = LogUtils.getLogger();

  private static final Gson GSON = new GsonBuilder()
      .setPrettyPrinting()
      .disableHtmlEscaping()
      .create();

  private static final String CONFIG_RELATIVE_PATH = "craftingdead/trauma/protection_profiles.json";
  private static final String DEFAULT_RESOURCE_PATH = "/assets/" + CraftingDead.ID
      + "/trauma/protection_profiles.json";

  private static ProtectionConfig instance = ProtectionConfig.defaultConfig();
  private static String serializedConfigJson = "{}";

  public static ProtectionConfig get() {
    return instance;
  }

  public static String getSerializedConfig() {
    return serializedConfigJson;
  }

  public static void load() {
    Path configPath = FMLPaths.CONFIGDIR.get().resolve(CONFIG_RELATIVE_PATH);
    ensureDefaultExists(configPath);

    Builder builder = new Builder();
    ModItems.contributeProtectionMappings(builder);

    try (Reader reader = Files.newBufferedReader(configPath, StandardCharsets.UTF_8)) {
      ConfigData data = Optional.ofNullable(GSON.fromJson(reader, ConfigData.class))
          .orElseGet(ConfigData::new);
      applyConfigData(builder, data);
      serializedConfigJson = GSON.toJson(data);
      LOGGER.info("Loaded trauma protection config from {}", configPath);
    } catch (IOException | JsonSyntaxException e) {
      LOGGER.error("Failed to load trauma protection config. Using defaults.", e);
      useDefaultConfig();
    }
  }

  public static void applySerializedConfig(String json) {
    Builder builder = new Builder();
    ModItems.contributeProtectionMappings(builder);

    if (json == null || json.isBlank()) {
      useDefaultConfig();
      return;
    }

    try {
      ConfigData data = Optional.ofNullable(GSON.fromJson(json, ConfigData.class))
          .orElseGet(ConfigData::new);
      applyConfigData(builder, data);
      serializedConfigJson = GSON.toJson(data);
    } catch (JsonSyntaxException e) {
      LOGGER.error("Failed to apply synced trauma protection config; falling back to defaults.", e);
      useDefaultConfig();
    }
  }

  private static void ensureDefaultExists(Path configPath) {
    if (Files.exists(configPath)) {
      return;
    }
    try {
      Files.createDirectories(configPath.getParent());
      try (InputStream inputStream = ProtectionConfig.class.getResourceAsStream(DEFAULT_RESOURCE_PATH)) {
        if (inputStream == null) {
          LOGGER.warn("Default trauma configuration resource {} is missing", DEFAULT_RESOURCE_PATH);
          Files.writeString(configPath, "{}", StandardCharsets.UTF_8);
          return;
        }
        Files.copy(inputStream, configPath, StandardCopyOption.REPLACE_EXISTING);
      }
      LOGGER.info("Wrote default trauma configuration to {}", configPath);
    } catch (IOException e) {
      LOGGER.error("Unable to create default trauma configuration at {}", configPath, e);
    }
  }

  private static ProtectionConfig defaultConfig() {
    HeadTraumaSettings settings = new HeadTraumaSettings(true, 6.0F, 20.0F, 60.0F, 120.0F, 1.25F, 1.0F, 60);
    Map<TraumaSeverity, TraumaEffect> effects = new EnumMap<>(TraumaSeverity.class);
    effects.put(TraumaSeverity.NONE, TraumaEffect.EMPTY);
    effects.put(TraumaSeverity.MINOR,
        new TraumaEffect(12, 0, 0, 0, 10, 0.15F));
    effects.put(TraumaSeverity.MODERATE,
        new TraumaEffect(0, 100, 60, 0, 40, 0.40F));
    effects.put(TraumaSeverity.SEVERE,
        new TraumaEffect(0, 160, 80, 1, 60, 0.60F));
    return new ProtectionConfig(settings, effects, Collections.emptyMap(), Collections.emptyMap(),
        Set.of(), Set.of());
  }

    private static void applyConfigData(Builder builder, ConfigData data) {
      instance = builder.build(data);
    }

    private static void useDefaultConfig() {
      instance = ProtectionConfig.defaultConfig();
      serializedConfigJson = "{}";
    }

  private final HeadTraumaSettings headTrauma;
  private final Map<TraumaSeverity, TraumaEffect> traumaEffects;
  private final Map<ResourceLocation, ProtectionProfile> helmetProfiles;
  private final Map<ResourceLocation, ProtectionProfile> vestProfiles;
  private final Set<ResourceLocation> cosmeticHelmets;
  private final Set<ResourceLocation> cosmeticVests;

  private ProtectionConfig(HeadTraumaSettings headTrauma,
      Map<TraumaSeverity, TraumaEffect> traumaEffects,
      Map<ResourceLocation, ProtectionProfile> helmetProfiles,
      Map<ResourceLocation, ProtectionProfile> vestProfiles,
      Set<ResourceLocation> cosmeticHelmets,
      Set<ResourceLocation> cosmeticVests) {
    this.headTrauma = headTrauma;
    this.traumaEffects = traumaEffects;
    this.helmetProfiles = helmetProfiles;
    this.vestProfiles = vestProfiles;
    this.cosmeticHelmets = cosmeticHelmets;
    this.cosmeticVests = cosmeticVests;
  }

  public HeadTraumaSettings headTrauma() {
    return this.headTrauma;
  }

  public TraumaEffect effectForSeverity(TraumaSeverity severity) {
    return this.traumaEffects.getOrDefault(severity, TraumaEffect.EMPTY);
  }

  public ProtectionProfile helmetProfile(ResourceLocation itemId) {
    return this.helmetProfiles.getOrDefault(itemId, ProtectionProfile.EMPTY);
  }

  public ProtectionProfile vestProfile(ResourceLocation itemId) {
    return this.vestProfiles.getOrDefault(itemId, ProtectionProfile.EMPTY);
  }

  public boolean isHelmetCosmetic(ResourceLocation itemId) {
    return this.cosmeticHelmets.contains(itemId);
  }

  public boolean isVestCosmetic(ResourceLocation itemId) {
    return this.cosmeticVests.contains(itemId);
  }

  public TraumaSeverity determineSeverity(float remainingEnergy, boolean forceSevere) {
    if (!this.headTrauma.enabled()) {
      return TraumaSeverity.NONE;
    }
    float energy = Math.max(0.0F, remainingEnergy);
    if (forceSevere || energy >= this.headTrauma.severeThreshold()) {
      return TraumaSeverity.SEVERE;
    }
    if (energy >= this.headTrauma.moderateThreshold()) {
      return TraumaSeverity.MODERATE;
    }
    if (energy >= this.headTrauma.minorThreshold()) {
      return TraumaSeverity.MINOR;
    }
    return TraumaSeverity.NONE;
  }

  public record HeadTraumaSettings(boolean enabled,
      float damageToEnergyScale,
      float minorThreshold,
      float moderateThreshold,
      float severeThreshold,
      float noHelmetMultiplier,
      float helmetAbsorptionFactor,
      int aimSwayMaxTicks) {
  }

  public record TraumaEffect(int blindnessTicks,
      int nauseaTicks,
      int slownessTicks,
      int slownessAmplifier,
      int aimSwayTicks,
      float aimSwayStrength) {

    public static final TraumaEffect EMPTY = new TraumaEffect(0, 0, 0, 0, 0, 0.0F);

    public boolean isEmpty() {
      return this.equals(EMPTY);
    }
  }

  public record ProtectionProfile(float absorption,
      float stoppingPower,
      float durabilityPerEnergy,
      float stunThreshold,
      boolean slowRecovery) {

    public static final ProtectionProfile EMPTY = new ProtectionProfile(0.0F, 0.0F, 0.0F, 0.0F, false);

    public boolean isEmpty() {
      return this.equals(EMPTY);
    }
  }

  public static final class Builder {

    private final Map<ResourceLocation, String> helmetAssignments = new HashMap<>();
    private final Map<ResourceLocation, String> vestAssignments = new HashMap<>();
    private final Set<ResourceLocation> helmetCosmetics = new HashSet<>();
    private final Set<ResourceLocation> vestCosmetics = new HashSet<>();

    public Builder helmet(net.minecraftforge.registries.RegistryObject<? extends Item> item,
        String profileId) {
      ResourceLocation id = Objects.requireNonNull(item.getId(), "Unregistered item");
      this.helmetAssignments.put(id, profileId);
      return this;
    }

    public Builder vest(net.minecraftforge.registries.RegistryObject<? extends Item> item,
        String profileId) {
      ResourceLocation id = Objects.requireNonNull(item.getId(), "Unregistered item");
      this.vestAssignments.put(id, profileId);
      return this;
    }

    public Builder cosmeticHelmet(net.minecraftforge.registries.RegistryObject<? extends Item> item) {
      ResourceLocation id = Objects.requireNonNull(item.getId(), "Unregistered item");
      this.helmetCosmetics.add(id);
      return this;
    }

    public Builder cosmeticVest(net.minecraftforge.registries.RegistryObject<? extends Item> item) {
      ResourceLocation id = Objects.requireNonNull(item.getId(), "Unregistered item");
      this.vestCosmetics.add(id);
      return this;
    }

    private ProtectionConfig build(ConfigData data) {
      HeadTraumaSettings settings = data.headTrauma().toSettings();
      Map<TraumaSeverity, TraumaEffect> effects = data.headTrauma().toEffects();

      Map<String, ProtectionProfile> profileDefinitions = new HashMap<>();
      data.profiles().forEach((id, profileData) -> profileDefinitions.put(id,
          profileData.toProfile()));

      Map<ResourceLocation, ProtectionProfile> helmets = new HashMap<>();
      Map<ResourceLocation, ProtectionProfile> vests = new HashMap<>();

      Set<ResourceLocation> helmetCosmeticsAll = new HashSet<>(this.helmetCosmetics);
      Set<ResourceLocation> vestCosmeticsAll = new HashSet<>(this.vestCosmetics);

      data.cosmeticHelmets().forEach(entry ->
          parseLocation(entry).ifPresent(helmetCosmeticsAll::add));
      data.cosmeticVests().forEach(entry ->
          parseLocation(entry).ifPresent(vestCosmeticsAll::add));

      data.helmets().forEach((itemKey, profileKey) ->
          parseLocation(itemKey).ifPresent(itemId -> {
            ProtectionProfile profile = profileDefinitions.get(profileKey);
            if (profile == null) {
              LOGGER.warn("Unknown helmet profile '{}' for item '{}'.", profileKey, itemKey);
            } else {
              helmets.put(itemId, profile);
            }
          }));

      data.vests().forEach((itemKey, profileKey) ->
          parseLocation(itemKey).ifPresent(itemId -> {
            ProtectionProfile profile = profileDefinitions.get(profileKey);
            if (profile == null) {
              LOGGER.warn("Unknown vest profile '{}' for item '{}'.", profileKey, itemKey);
            } else {
              vests.put(itemId, profile);
            }
          }));

      this.helmetAssignments.forEach((itemId, profileId) -> {
        if (!helmets.containsKey(itemId) && !helmetCosmeticsAll.contains(itemId)) {
          ProtectionProfile profile = profileDefinitions.get(profileId);
          if (profile != null) {
            helmets.put(itemId, profile);
          } else {
            LOGGER.warn("No profile data for helmet '{}' (profile '{}').", itemId, profileId);
          }
        }
      });

      this.vestAssignments.forEach((itemId, profileId) -> {
        if (!vests.containsKey(itemId) && !vestCosmeticsAll.contains(itemId)) {
          ProtectionProfile profile = profileDefinitions.get(profileId);
          if (profile != null) {
            vests.put(itemId, profile);
          } else {
            LOGGER.warn("No profile data for vest '{}' (profile '{}').", itemId, profileId);
          }
        }
      });

      return new ProtectionConfig(settings,
          effects,
          Collections.unmodifiableMap(helmets),
          Collections.unmodifiableMap(vests),
          Collections.unmodifiableSet(helmetCosmeticsAll),
          Collections.unmodifiableSet(vestCosmeticsAll));
    }
  }

  private static Optional<ResourceLocation> parseLocation(String value) {
    try {
      return Optional.of(new ResourceLocation(Objects.requireNonNull(value, "value")));
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Invalid resource location '{}' in trauma config", value);
      return Optional.empty();
    }
  }

  private static final class ConfigData {

    private HeadTraumaData headTrauma = new HeadTraumaData();
    private Map<String, ProfileData> profiles = new HashMap<>();
    private Map<String, String> helmets = new HashMap<>();
    private Map<String, String> vests = new HashMap<>();
    private List<String> cosmeticHelmets = List.of();
    private List<String> cosmeticVests = List.of();

    public HeadTraumaData headTrauma() {
      return this.headTrauma;
    }

    public Map<String, ProfileData> profiles() {
      return this.profiles;
    }

    public Map<String, String> helmets() {
      return this.helmets;
    }

    public Map<String, String> vests() {
      return this.vests;
    }

    public List<String> cosmeticHelmets() {
      return this.cosmeticHelmets;
    }

    public List<String> cosmeticVests() {
      return this.cosmeticVests;
    }
  }

  private static final class HeadTraumaData {

    private boolean enabled = true;
    private float damageToEnergyScale = 6.0F;
    private float minorThreshold = 20.0F;
    private float moderateThreshold = 60.0F;
    private float severeThreshold = 120.0F;
    private float noHelmetMultiplier = 1.25F;
    private float helmetAbsorptionFactor = 1.0F;
    private int aimSwayMaxTicks = 60;
    private EffectData minor = EffectData.minorDefaults();
    private EffectData moderate = EffectData.moderateDefaults();
    private EffectData severe = EffectData.severeDefaults();

    public HeadTraumaSettings toSettings() {
      float scale = this.damageToEnergyScale <= 0.0F ? 6.0F : this.damageToEnergyScale;
      float minorValue = this.minorThreshold <= 0.0F ? 20.0F : this.minorThreshold;
      float moderateValue = this.moderateThreshold <= 0.0F ? 60.0F : this.moderateThreshold;
      float severeValue = this.severeThreshold <= 0.0F ? 120.0F : this.severeThreshold;
      float multiplier = Mth.clamp(this.noHelmetMultiplier, 1.0F, 3.0F);
      float absorptionFactor = Math.max(0.1F, this.helmetAbsorptionFactor);
      int maxAimSway = Math.max(1, this.aimSwayMaxTicks);
      return new HeadTraumaSettings(this.enabled, scale, minorValue, moderateValue, severeValue,
          multiplier, absorptionFactor, maxAimSway);
    }

    public Map<TraumaSeverity, TraumaEffect> toEffects() {
      Map<TraumaSeverity, TraumaEffect> map = new EnumMap<>(TraumaSeverity.class);
      map.put(TraumaSeverity.NONE, TraumaEffect.EMPTY);
      map.put(TraumaSeverity.MINOR, this.minor.toEffect());
      map.put(TraumaSeverity.MODERATE, this.moderate.toEffect());
      map.put(TraumaSeverity.SEVERE, this.severe.toEffect());
      return map;
    }
  }

  private static final class EffectData {

    private int blindnessTicks;
    private int nauseaTicks;
    private int slownessTicks;
    private int slownessAmplifier;
    private int aimSwayTicks;
    private float aimSwayStrength;

    static EffectData minorDefaults() {
      EffectData data = new EffectData();
      data.blindnessTicks = 12;
      data.nauseaTicks = 0;
      data.slownessTicks = 0;
      data.slownessAmplifier = 0;
      data.aimSwayTicks = 10;
      data.aimSwayStrength = 0.15F;
      return data;
    }

    static EffectData moderateDefaults() {
      EffectData data = new EffectData();
      data.blindnessTicks = 0;
      data.nauseaTicks = 100;
      data.slownessTicks = 60;
      data.slownessAmplifier = 0;
      data.aimSwayTicks = 40;
      data.aimSwayStrength = 0.40F;
      return data;
    }

    static EffectData severeDefaults() {
      EffectData data = new EffectData();
      data.blindnessTicks = 0;
      data.nauseaTicks = 160;
      data.slownessTicks = 80;
      data.slownessAmplifier = 1;
      data.aimSwayTicks = 60;
      data.aimSwayStrength = 0.60F;
      return data;
    }

    TraumaEffect toEffect() {
      return new TraumaEffect(Math.max(0, this.blindnessTicks), Math.max(0, this.nauseaTicks),
          Math.max(0, this.slownessTicks), Math.max(0, this.slownessAmplifier),
          Math.max(0, this.aimSwayTicks), Math.max(0.0F, this.aimSwayStrength));
    }
  }

  private static final class ProfileData {

    private float absorption;
    private float stoppingPower;
    private float durabilityPerEnergy;
    private float stunThreshold;
    private boolean slowRecovery;

    ProtectionProfile toProfile() {
      float absorptionValue = Mth.clamp(this.absorption, 0.0F, 1.0F);
      float stopping = Math.max(0.0F, this.stoppingPower);
      float durability = Math.max(0.0F, this.durabilityPerEnergy);
      float stun = Math.max(0.0F, this.stunThreshold);
      return new ProtectionProfile(absorptionValue, stopping, durability, stun, this.slowRecovery);
    }
  }
}
