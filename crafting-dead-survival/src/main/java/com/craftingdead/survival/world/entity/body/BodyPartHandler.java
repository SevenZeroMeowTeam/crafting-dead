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

package com.craftingdead.survival.world.entity.body;

import com.craftingdead.core.event.GunEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * 部位伤害 / 断肢系统。
 *
 * <p>用枪命中敌对生物时按命中点判定部位，并产生对应效果：
 * <ul>
 *   <li><b>头部</b>：爆头——每次造成高倍伤害，累计伤害达到阈值或触发幸运一击时打爆头部一击致命；</li>
 *   <li><b>腿部</b>：断裂——累计伤害达到阈值后腿断，移动速度大幅降低（只能缓慢爬行）；</li>
 *   <li><b>手臂</b>：断裂——累计伤害达到阈值后臂断，攻击力大幅下降（无法有效攻击）；</li>
 *   <li><b>腰部</b>：断裂——累计伤害达到阈值后腰断，几乎无法移动（瘫痪）。</li>
 * </ul>
 *
 * <p>断裂状态与部位累计伤害都保存在生物 persistentData 中（存档持久化），
 * 命中同一部位会累积伤害，越打越容易断裂（更真实）。仅对僵尸 / 骷髅等人形
 * 敌对生物生效，不影响玩家与其他模组。
 */
public final class BodyPartHandler {

  /** persistentData 中部位状态所在根标签。 */
  private static final String TAG_BODY = "craftingdead.body";
  private static final String TAG_HEAD = "head_broken";
  private static final String TAG_ARM = "arm_broken";
  private static final String TAG_WAIST = "waist_broken";
  private static final String TAG_LEG = "leg_broken";
  /** persistentData 中各部位累计伤害标签。 */
  private static final String TAG_HEAD_DMG = "head_dmg";
  private static final String TAG_ARM_DMG = "arm_dmg";
  private static final String TAG_WAIST_DMG = "waist_dmg";
  private static final String TAG_LEG_DMG = "leg_dmg";

  /**
   * TaCZ（Timeless and Classics Zero）动能子弹实体类名（软引用，无编译依赖；
   * TaCZ 未安装或类名变化时安全跳过）。
   */
  private static final String TACZ_BULLET_CLASS_NAME =
      "com.tacz.guns.entity.EntityKineticBullet";

  /** 腿断移动速度减速 modifier id。 */
  private static final ResourceLocation LEG_SPEED_MODIFIER =
      ResourceLocation.fromNamespaceAndPath("craftingdeadsurvival", "leg_broken_crawl");
  /** 腰断移动速度减速 modifier id。 */
  private static final ResourceLocation WAIST_SPEED_MODIFIER =
      ResourceLocation.fromNamespaceAndPath("craftingdeadsurvival", "waist_broken_cripple");
  /** 手臂断攻击力降低 modifier id。 */
  private static final ResourceLocation ARM_ATTACK_MODIFIER =
      ResourceLocation.fromNamespaceAndPath("craftingdeadsurvival", "arm_broken_weak");

  /** 部位断裂所需的累计伤害阈值（= 最大生命值 × 该比例）。 */
  private static final float BREAK_THRESHOLD_RATIO = 0.5F;
  /** 爆头基础伤害倍率（每次命中头部都生效）。 */
  private static final float HEADSHOT_DAMAGE_MULTIPLIER = 2.5F;
  /** 爆头幸运一击（直接打爆头部）概率。 */
  private static final float HEADSHOT_INSTANT_KILL_CHANCE = 0.25F;
  /** 腿断后移动速度降低比例（爬行）。 */
  private static final double LEG_SPEED_REDUCTION = -0.60D;
  /** 腰断后移动速度降低比例（瘫痪）。 */
  private static final double WAIST_SPEED_REDUCTION = -0.85D;
  /** 手臂断后攻击力降低比例。 */
  private static final double ARM_ATTACK_REDUCTION = -0.50D;

  private BodyPartHandler() {}

  /**
   * 判断某部位是否已断裂。
   */
  public static boolean isBroken(LivingEntity entity, BodyPart part) {
    var bodyTag = entity.getPersistentData().getCompound(TAG_BODY);
    return switch (part) {
      case HEAD -> bodyTag.getBoolean(TAG_HEAD);
      case ARM -> bodyTag.getBoolean(TAG_ARM);
      case WAIST -> bodyTag.getBoolean(TAG_WAIST);
      case LEG -> bodyTag.getBoolean(TAG_LEG);
    };
  }

  /**
   * 读取某部位已累计的伤害。
   */
  private static float getPartDamage(LivingEntity entity, BodyPart part) {
    var bodyTag = entity.getPersistentData().getCompound(TAG_BODY);
    return switch (part) {
      case HEAD -> bodyTag.getFloat(TAG_HEAD_DMG);
      case ARM -> bodyTag.getFloat(TAG_ARM_DMG);
      case WAIST -> bodyTag.getFloat(TAG_WAIST_DMG);
      case LEG -> bodyTag.getFloat(TAG_LEG_DMG);
    };
  }

  /**
   * 向某部位累计伤害，返回累计后的总量。
   */
  private static float addPartDamage(LivingEntity entity, BodyPart part, float damage) {
    var bodyTag = entity.getPersistentData().getCompound(TAG_BODY);
    float total = getPartDamage(entity, part) + damage;
    switch (part) {
      case HEAD -> bodyTag.putFloat(TAG_HEAD_DMG, total);
      case ARM -> bodyTag.putFloat(TAG_ARM_DMG, total);
      case WAIST -> bodyTag.putFloat(TAG_WAIST_DMG, total);
      case LEG -> bodyTag.putFloat(TAG_LEG_DMG, total);
    }
    entity.getPersistentData().put(TAG_BODY, bodyTag);
    return total;
  }

  /**
   * 设置某部位断裂状态，并在断裂时应用对应效果。
   */
  public static void setBroken(LivingEntity entity, BodyPart part, boolean broken) {
    var bodyTag = entity.getPersistentData().getCompound(TAG_BODY);
    switch (part) {
      case HEAD -> bodyTag.putBoolean(TAG_HEAD, broken);
      case ARM -> bodyTag.putBoolean(TAG_ARM, broken);
      case WAIST -> bodyTag.putBoolean(TAG_WAIST, broken);
      case LEG -> bodyTag.putBoolean(TAG_LEG, broken);
    }
    entity.getPersistentData().put(TAG_BODY, bodyTag);
    if (broken) {
      applyBrokenEffect(entity, part);
    }
  }

  /**
   * 应用部位断裂后的效果（腿断爬行、腰断瘫痪、臂断攻击力下降；头部由伤害击杀）。
   */
  private static void applyBrokenEffect(LivingEntity entity, BodyPart part) {
    switch (part) {
      case LEG -> {
        AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
          speed.addTransientModifier(new AttributeModifier(LEG_SPEED_MODIFIER,
              LEG_SPEED_REDUCTION, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
      }
      case WAIST -> {
        AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
          speed.addTransientModifier(new AttributeModifier(WAIST_SPEED_MODIFIER,
              WAIST_SPEED_REDUCTION, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
      }
      case ARM -> {
        AttributeInstance attack = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attack != null) {
          attack.addTransientModifier(new AttributeModifier(ARM_ATTACK_MODIFIER,
              ARM_ATTACK_REDUCTION, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
      }
      case HEAD -> {
        // 头部打爆由伤害处理直接一击致命
      }
    }
  }

  /**
   * 是否需要部位判定的敌对生物（僵尸 / 骷髅等人形敌对生物）。
   */
  private static boolean isSupportedTarget(LivingEntity entity) {
    return entity instanceof Zombie || entity instanceof AbstractSkeleton;
  }

  /**
   * 通用部位命中入口：按命中点判定部位并应用爆头 / 断肢效果，返回修改后的伤害。
   * 同时服务于 Crafting Dead 自己的枪械（{@link GunEvent.EntityHit}）与 TaCZ 枪械。
   *
   * @param living 被击中的敌对生物（僵尸 / 骷髅）
   * @param hitPos 弹道命中点（世界坐标）
   * @param damage 原始伤害
   * @return 应用部位效果后的伤害
   */
  public static float applyBodyPartHit(LivingEntity living, Vec3 hitPos, float damage) {
    if (living.level().isClientSide() || !isSupportedTarget(living)) {
      return damage;
    }

    BodyPart part = BodyPart.fromHitPosition(living, hitPos);
    RandomSource random = living.getRandom();

    switch (part) {
      case HEAD -> {
        // 爆头：每次高倍伤害；累计打爆头部或幸运一击则一击致命
        if (isBroken(living, BodyPart.HEAD)) {
          damage *= HEADSHOT_DAMAGE_MULTIPLIER;
        } else {
          float total = addPartDamage(living, BodyPart.HEAD, damage);
          if (total >= living.getMaxHealth() * BREAK_THRESHOLD_RATIO
              || random.nextFloat() < HEADSHOT_INSTANT_KILL_CHANCE) {
            setBroken(living, BodyPart.HEAD, true);
            damage = living.getMaxHealth() + 20.0F;
          } else {
            damage *= HEADSHOT_DAMAGE_MULTIPLIER;
          }
        }
      }
      case LEG, ARM, WAIST -> {
        // 断肢：累计伤害达到阈值即断裂（越打越容易断）
        if (!isBroken(living, part)) {
          float total = addPartDamage(living, part, damage);
          if (total >= living.getMaxHealth() * BREAK_THRESHOLD_RATIO) {
            setBroken(living, part, true);
          }
        }
      }
    }
    return damage;
  }

  /**
   * Crafting Dead 枪械命中事件处理：判定部位并应用爆头 / 断肢效果。
   *
   * @param event 枪械命中实体事件（可修改伤害）
   */
  public static void handleGunHit(GunEvent.EntityHit event) {
    if (!(event.target() instanceof LivingEntity living)) {
      return;
    }
    event.damage(applyBodyPartHit(living, event.hitPos(), event.damage()));
  }

  /**
   * TaCZ 枪械命中处理：TaCZ 的子弹命中由 {@code LivingHurtEvent} 触发，
   * 通过子弹实体类名软引用判断伤害来源（无需编译依赖 TaCZ），命中点取子弹当前位置。
   *
   * <p>Crafting Dead 自己的枪械伤害的 directEntity 是射手本人，不会进入此分支，
   * 仍由 {@link #handleGunHit(GunEvent.EntityHit)} 处理，避免重复判定。
   */
  @SubscribeEvent
  public static void handleTaczGunHit(LivingHurtEvent event) {
    var source = event.getSource();
    if (source == null) {
      return;
    }
    var direct = source.getDirectEntity();
    if (direct == null || !TACZ_BULLET_CLASS_NAME.equals(direct.getClass().getName())) {
      return;
    }
    var target = event.getEntity();
    if (target == null || target.level().isClientSide()) {
      return;
    }
    // 子弹在造成伤害的瞬间仍位于命中点；TaCZ 在 hurt 之后才移除子弹
    float newDamage = applyBodyPartHit(target, direct.position(), event.getAmount());
    event.setAmount(newDamage);
  }
}
