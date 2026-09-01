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

package com.craftingdead.core;

import com.craftingdead.core.telemetry.TelemetryEnvironment;
import net.neoforged.neoforge.common.ModConfigSpec;

public class CommonConfig {
  
  public static final CommonConfig instance;
  public static final ModConfigSpec configSpec;

  static {
    var pair = new ModConfigSpec.Builder().configure(CommonConfig::new);
    configSpec = pair.getRight();
    instance = pair.getLeft();
  }

  public final ModConfigSpec.EnumValue<TelemetryEnvironment> telemetryEnvironment;
  public final ModConfigSpec.ConfigValue<String> telemetryDsnExperimental;
  public final ModConfigSpec.ConfigValue<String> telemetryDsnProduction;
  public final ModConfigSpec.ConfigValue<String> telemetryPasswordSalt;
  public final ModConfigSpec.ConfigValue<String> telemetryPasswordHash;
  public final ModConfigSpec.BooleanValue telemetryClientEnabled;
  public final ModConfigSpec.BooleanValue telemetryServerEnabled;
  public final ModConfigSpec.ConfigValue<String> telemetryModpackId;
  public final ModConfigSpec.DoubleValue telemetryTracesSampleRate;

  public final ModConfigSpec.DoubleValue weakVestArmor;
  public final ModConfigSpec.DoubleValue weakVestArmorToughness;

  public final ModConfigSpec.DoubleValue strongVestArmor;
  public final ModConfigSpec.DoubleValue strongVestArmorToughness;

  private CommonConfig(ModConfigSpec.Builder builder) {
  builder.push("telemetry");
  this.telemetryEnvironment = builder.comment("Controls which DSN block is used.")
    .defineEnum("environment", TelemetryEnvironment.PRODUCTION);
  this.telemetryDsnExperimental = builder.comment("Experimental Sentry DSN.")
    .define("dsnExperimental",
      "https://31d8ac34b0c24ddf98223098d42fd526@o1128514.ingest.sentry.io/6174174");
  this.telemetryDsnProduction = builder.comment("Production Sentry DSN.")
    .define("dsnProduction",
      "https://31d8ac34b0c24ddf98223098d42fd526@o1128514.ingest.sentry.io/6174174");
  this.telemetryPasswordSalt = builder.comment(
    "Salt used when hashing the reporting password. Configure before enabling telemetry.")
  .define("passwordSalt", "");
  this.telemetryPasswordHash = builder.comment(
    "Hex encoded SHA-256 hash of the salt concatenated with the reporting password.")
  .define("passwordHash", "");
  this.telemetryClientEnabled = builder.comment(
    "Allow client installs to emit telemetry when credentials are valid.")
  .define("enableClient", true);
  this.telemetryServerEnabled = builder.comment(
    "Allow dedicated servers to emit telemetry when credentials are valid.")
  .define("enableServer", true);
  this.telemetryModpackId = builder.comment("Optional label applied to telemetry events.")
    .define("modpackId", "");
  this.telemetryTracesSampleRate = builder.comment(
    "Performance tracing sample rate between 0.0 and 1.0.")
  .defineInRange("tracesSampleRate", 1.0D, 0.0D, 1.0D);
  builder.pop();

  this.weakVestArmor =
    builder.defineInRange("weakVestArmor", 8.0D, 0.0D, Double.MAX_VALUE);
  this.weakVestArmorToughness =
    builder.defineInRange("weakVestArmorToughness", 1.0D, 0.0D, Double.MAX_VALUE);

  this.strongVestArmor =
    builder.defineInRange("strongVestArmor", 12.0D, 0.0D, Double.MAX_VALUE);
  this.strongVestArmorToughness =
    builder.defineInRange("strongVestArmorToughness", 2.0D, 0.0D, Double.MAX_VALUE);
  }
}
