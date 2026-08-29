package com.qlm.zombie.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * QLM（qlmzombie）兼容配置入口。
 *
 * <p>为 KubeJS 服务端脚本（{@code server_scripts/*.js} 里的
 * {@code com.qlm.zombie.config.QLMConfig}）提供配置项。当前仅暴露
 * {@link #PEACEFUL_DAYS}：安全天数，在此之前不触发血月 / 幸运月 / 丰收月。</p>
 *
 * <p>该类位于本模组（craftingdeadsurvival）中，但沿用 QLM 的包名与字段名，
 * 以便依赖 {@code com.qlm.zombie.*} 的整合包脚本无需改动即可加载。</p>
 */
public class QLMConfig {

  /** 由 {@link #PEACEFUL_DAYS} 等配置项构建出的配置规格，供注册使用。 */
  public static final ForgeConfigSpec SPEC;

  /** 安全天数：在此天数之前不会触发血月 / 幸运月 / 丰收月。 */
  public static final ForgeConfigSpec.IntValue PEACEFUL_DAYS;

  static {
    var builder = new ForgeConfigSpec.Builder();
    PEACEFUL_DAYS = builder
        .comment("安全天数：在此天数之前不会触发血月 / 幸运月 / 丰收月。")
        .defineInRange("peacefulDays", 25, 0, Integer.MAX_VALUE);
    SPEC = builder.build();
  }
}
