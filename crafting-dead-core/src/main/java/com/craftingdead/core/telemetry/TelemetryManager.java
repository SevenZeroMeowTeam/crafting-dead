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

package com.craftingdead.core.telemetry;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.craftingdead.core.CommonConfig;
import com.craftingdead.core.CraftingDead;
import com.mojang.logging.LogUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.sentry.Scope;
import io.sentry.Sentry;
import io.sentry.SentryOptions;
import net.minecraft.SharedConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.fml.ModList;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public final class TelemetryManager {

  private static final Logger LOGGER = LogUtils.getLogger();

  private static final AtomicBoolean INITIALIZATION_ATTEMPTED = new AtomicBoolean();
  private static final Set<String> REGISTERED_MODULES = ConcurrentHashMap.newKeySet();
  private static final AtomicBoolean SAMPLER_STARTED = new AtomicBoolean();
  private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

  private TelemetryManager() {
  }

  public static boolean initialize(String moduleId, String moduleVersion,
      Supplier<Optional<String>> fallbackDsnSupplier,
      Consumer<SentryOptions> optionsCustomizer,
      Consumer<Scope> scopeCustomizer) {
    Objects.requireNonNull(moduleId, "moduleId");
    Objects.requireNonNull(moduleVersion, "moduleVersion");

    if (!Sentry.isEnabled() && INITIALIZATION_ATTEMPTED.compareAndSet(false, true)) {
      attemptInitialization(fallbackDsnSupplier, optionsCustomizer);
    }

    if (!Sentry.isEnabled()) {
      return false;
    }

    registerModule(moduleId, moduleVersion, scopeCustomizer);
    return true;
  }

  public static boolean initialize(String moduleId, String moduleVersion,
      Supplier<Optional<String>> fallbackDsnSupplier,
      Consumer<Scope> scopeCustomizer) {
    return initialize(moduleId, moduleVersion, fallbackDsnSupplier, options -> {
    }, scopeCustomizer);
  }

  private static void attemptInitialization(Supplier<Optional<String>> fallbackDsnSupplier,
      Consumer<SentryOptions> optionsCustomizer) {
    CommonConfig config = CommonConfig.instance;

    LOGGER.info("Telemetry initialization requested for {} runtime", FMLEnvironment.dist);
    if (!hasRuntimeConsent(config)) {
      LOGGER.info("Telemetry disabled for {} runtime", FMLEnvironment.dist);
      return;
    }

    boolean passwordValid = validatePassword(config);
    if (!passwordValid) {
      LOGGER.debug("Telemetry password validation failed or bypassed; continuing with initialization");
    }

    String dsn = resolveDsn(config, fallbackDsnSupplier);
    if (dsn.isBlank()) {
      LOGGER.warn("Telemetry blocked because no DSN was configured");
      return;
    }

    TelemetryEnvironment environment = config.telemetryEnvironment.get();
    double tracesSampleRate = config.telemetryTracesSampleRate.get();
    String release = resolveBuildId();

    LOGGER.info("Telemetry initializing Sentry with environment {} release {} and trace sample rate {}",
        environment, release, tracesSampleRate);

    try {
      Sentry.init(options -> {
        options.setDsn(dsn);
        options.setEnvironment(environment.getWireValue());
        options.setRelease(release);
        options.setTracesSampleRate(tracesSampleRate);
        options.setEnableUncaughtExceptionHandler(true);
        options.setDebug(false);
        options.setAttachStacktrace(true);
        options.setSendDefaultPii(true);
        options.setMaxBreadcrumbs(200);
        options.setEnableAutoSessionTracking(true);
        options.setSessionTrackingIntervalMillis((int) Duration.ofMinutes(1).toMillis());
        options.setMaxQueueSize(100);
        options.setServerName(resolveHostName());
        if (optionsCustomizer != null) {
          optionsCustomizer.accept(options);
        }
      });
    } catch (RuntimeException e) {
      LOGGER.error("Failed to initialize telemetry", e);
      return;
    }

    if (!Sentry.isEnabled()) {
      LOGGER.warn("Telemetry initialization completed but Sentry is not enabled");
      return;
    }

    Sentry.configureScope(scope -> decorateBaseScope(scope, environment, release));
    LOGGER.info("Telemetry enabled for environment {} with release {}", environment,
        release);
  ensureSamplerStarted();
  }

  private static void registerModule(String moduleId, String moduleVersion,
      Consumer<Scope> scopeCustomizer) {
    Sentry.configureScope(scope -> {
      if (REGISTERED_MODULES.add(moduleId)) {
        scope.setTag(moduleId + ".version", moduleVersion);
        scope.setExtra(moduleId + ".version", moduleVersion);
      }
      scope.setTag("modules", String.join(",", REGISTERED_MODULES));
      if (scopeCustomizer != null) {
        scopeCustomizer.accept(scope);
      }
    });
  }

  private static boolean hasRuntimeConsent(CommonConfig config) {
    Dist dist = FMLEnvironment.dist;
    if (dist == Dist.DEDICATED_SERVER) {
      return config.telemetryServerEnabled.get();
    }
    return config.telemetryClientEnabled.get();
  }

  private static boolean validatePassword(CommonConfig config) {
    String expectedHash = StringUtils.defaultString(config.telemetryPasswordHash.get());
    if (expectedHash.isBlank()) {
      LOGGER.debug("Telemetry password hash not configured; telemetry will initialize without password gate");
      return true;
    }

    Optional<String> passwordSecret = resolveSecret();
    if (passwordSecret.isEmpty()) {
      LOGGER.debug(
          "Telemetry password secret not provided. Supply craftingdead.sentryPassword system property or CD_SENTRY_PASSWORD environment variable");
      return false;
    }

    String salt = StringUtils.defaultString(config.telemetryPasswordSalt.get());
    String computedHash = hashWithSalt(salt, passwordSecret.get());
    if (!expectedHash.equalsIgnoreCase(computedHash)) {
      LOGGER.warn("Telemetry password secret does not match configured hash");
      return false;
    }
    return true;
  }

  private static Optional<String> resolveSecret() {
    String propertySecret = System.getProperty("craftingdead.sentryPassword");
    if (StringUtils.isNotBlank(propertySecret)) {
      return Optional.of(propertySecret);
    }
    String envSecret = System.getenv("CD_SENTRY_PASSWORD");
    if (StringUtils.isNotBlank(envSecret)) {
      return Optional.of(envSecret);
    }
    String envSecretAlt = System.getenv("CRAFTING_DEAD_SENTRY_PASSWORD");
    if (StringUtils.isNotBlank(envSecretAlt)) {
      return Optional.of(envSecretAlt);
    }
    return Optional.empty();
  }

  private static String hashWithSalt(String salt, String secret) {
    String payload = salt.isEmpty() ? secret : salt + ':' + secret;
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(payload.getBytes(StandardCharsets.UTF_8));
      return toHex(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 digest not available", e);
    }
  }

  private static String toHex(byte[] data) {
    StringBuilder builder = new StringBuilder(data.length * 2);
    for (byte value : data) {
      builder.append(String.format(Locale.ROOT, "%02x", value));
    }
    return builder.toString();
  }

  private static String resolveDsn(CommonConfig config,
      Supplier<Optional<String>> fallbackDsnSupplier) {
    TelemetryEnvironment environment = config.telemetryEnvironment.get();
    String configured = environment == TelemetryEnvironment.PRODUCTION
        ? config.telemetryDsnProduction.get()
        : config.telemetryDsnExperimental.get();

    if (StringUtils.isBlank(configured)) {
      configured = System.getProperty("craftingdead.sentryDsn", "");
    }
    if (StringUtils.isBlank(configured)) {
      configured = System.getenv("CRAFTING_DEAD_SENTRY_DSN");
    }
    if (StringUtils.isBlank(configured)) {
      configured = System.getenv("SENTRY_DSN");
    }

    if (StringUtils.isBlank(configured) && fallbackDsnSupplier != null) {
      configured = fallbackDsnSupplier.get().orElse("");
    }

    return StringUtils.defaultString(configured);
  }

  private static void decorateBaseScope(Scope scope, TelemetryEnvironment environment,
      String release) {
    scope.setTag("environment", environment.getWireValue());
    scope.setTag("runtime", resolveRuntimeTag());
    scope.setTag("dist", FMLEnvironment.dist.name());
    scope.setTag("forgeVersion", ForgeVersion.getVersion());
    scope.setTag("mcVersion", SharedConstants.getCurrentVersion().getName());
    scope.setTag("buildId", release);
    scope.setTag("os.arch", System.getProperty("os.arch"));
    scope.setTag("os.version", System.getProperty("os.version"));
    scope.setTag("user.language", System.getProperty("user.language"));
    scope.setTag("jvm.version", System.getProperty("java.runtime.version"));
    scope.setTag("jvm.vendor", System.getProperty("java.vendor"));

    String commit = resolveCommitId();
    if (StringUtils.isNotBlank(commit)) {
      scope.setTag("commit", commit);
    }

    String modpackId = StringUtils.defaultString(CommonConfig.instance.telemetryModpackId.get());
    if (StringUtils.isNotBlank(modpackId)) {
      scope.setTag("modpackId", modpackId);
    }

    Runtime runtime = Runtime.getRuntime();
  scope.setExtra("runtime.memory", encodeToJson(mapOf(
    "freeBytes", runtime.freeMemory(),
    "totalBytes", runtime.totalMemory(),
    "maxBytes", runtime.maxMemory())));

  scope.setExtra("runtime.threads", Integer.toString(Thread.activeCount()));

  scope.setExtra("system.properties", encodeToJson(collectSystemProperties()));
  scope.setExtra("system.user", encodeToJson(mapOf(
    "name", System.getProperty("user.name"),
    "home", System.getProperty("user.home"),
    "dir", System.getProperty("user.dir"))));
  scope.setExtra("mods.loaded", encodeToJson(collectModSnapshot()));
  }

  private static String resolveRuntimeTag() {
    return FMLEnvironment.dist == Dist.DEDICATED_SERVER ? "server" : "client";
  }

  private static String resolveBuildId() {
    String buildId = firstNonBlank(System.getenv("CRAFTING_DEAD_BUILD_ID"),
        System.getProperty("craftingdead.buildId"), CraftingDead.VERSION);
    return StringUtils.defaultIfBlank(buildId, CraftingDead.VERSION);
  }

  private static String resolveCommitId() {
    return firstNonBlank(System.getenv("GITHUB_SHA"),
        System.getenv("CRAFTING_DEAD_COMMIT"),
        System.getProperty("craftingdead.commit"));
  }

  private static String firstNonBlank(String... values) {
    for (String value : values) {
      if (StringUtils.isNotBlank(value)) {
        return value;
      }
    }
    return "";
  }

  private static void ensureSamplerStarted() {
    if (SAMPLER_STARTED.compareAndSet(false, true)) {
      TelemetryRuntimeSampler.ensureStarted();
    }
  }

  private static String resolveHostName() {
    try {
      return InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      LOGGER.debug("Unable to resolve hostname", e);
      return "unknown-host";
    }
  }

  private static Map<String, String> collectSystemProperties() {
    Map<String, String> properties = new LinkedHashMap<>();
    System.getProperties().forEach((key, value) -> {
      if (key != null && value != null) {
        properties.put(String.valueOf(key), String.valueOf(value));
      }
    });
    return properties;
  }

  private static List<Map<String, String>> collectModSnapshot() {
    List<Map<String, String>> mods = new ArrayList<>();
    ModList.get().getMods().forEach(mod -> {
      Map<String, String> entry = new LinkedHashMap<>();
      entry.put("id", mod.getModId());
      entry.put("displayName", mod.getDisplayName());
      entry.put("version", mod.getVersion().toString());
      mods.add(entry);
    });
    return mods;
  }


  static String encodeToJson(Object value) {
    try {
      return GSON.toJson(value);
    } catch (RuntimeException e) {
      LOGGER.warn("Failed to encode telemetry payload to JSON", e);
      return String.valueOf(value);
    }
  }

  private static Map<String, Object> mapOf(Object... entries) {
    Map<String, Object> map = new LinkedHashMap<>();
    for (int i = 0; i + 1 < entries.length; i += 2) {
      map.put(String.valueOf(entries[i]), entries[i + 1]);
    }
    return map;
  }
}
