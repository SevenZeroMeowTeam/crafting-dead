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
 *   <li><b>头部</b>：爆头——有概率一击致命（打爆头及死亡），否则造成高倍伤害；</li>
 *   <li><b>腿部</b>：有概率断裂——断裂后移动速度大幅降低（只能缓慢爬行）；</li>
 *   <li><b>手臂</b>：有概率断裂——断臂后仍可正常攻击（不影响战斗力）；</li>
 *   <li><b>腰部</b>：有概率断裂——断裂后几乎无法移动（瘫痪）。</li>
 * </ul>
 *
 * <p>断裂状态保存在生物 persistentData 中（存档持久化）。仅对僵尸 / 骷髅等人形
 * 敌对生物生效，不影响玩家与其他模组。
 */
public final class BodyPartHandler {

  /** persistentData 中部位状态所在根标签。 */
  private static final String TAG_BODY = "craftingdead.body";
  private static final String TAG_HEAD = "head_broken";
  private static final String TAG_ARM = "arm_broken";
  private static final String TAG_WAIST = "waist_broken";
  private static final String TAG_LEG = "leg_broken";

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

  /** 非头部部位断裂概率（每次命中）。 */
  private static final float BREAK_CHANCE = 0.30F;
  /** 爆头一击致命概率。 */
  private static final float HEADSHOT_LETHAL_CHANCE = 0.35F;
  /** 爆头（非致命）额外伤害倍率。 */
  private static final float HEADSHOT_BONUS_DAMAGE = 3.0F;
  /** 腿断后移动速度降低比例（爬行）。 */
  private static final double LEG_SPEED_REDUCTION = -0.60D;
  /** 腰断后移动速度降低比例（瘫痪）。 */
  private static final double WAIST_SPEED_REDUCTION = -0.85D;

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
   * 应用部位断裂后的效果（腿断爬行、腰断瘫痪；手臂断不影响）。
   */
  private static void applyBrokenEffect(LivingEntity entity, BodyPart part) {
    AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
    if (speed == null) {
      return;
    }
    switch (part) {
      case LEG -> speed.addTransientModifier(new AttributeModifier(LEG_SPEED_MODIFIER,
          LEG_SPEED_REDUCTION, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
      case WAIST -> speed.addTransientModifier(new AttributeModifier(WAIST_SPEED_MODIFIER,
          WAIST_SPEED_REDUCTION, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
      default -> {
        // 手臂断：仍可攻击，无速度影响
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
        // 爆头：概率一击致命（打爆头及死亡），否则高倍伤害
        if (random.nextFloat() < HEADSHOT_LETHAL_CHANCE) {
          damage = living.getMaxHealth() + 20.0F;
        } else {
          damage *= HEADSHOT_BONUS_DAMAGE;
        }
      }
      case LEG, ARM, WAIST -> {
        // 断肢：概率使对应部位断裂
        if (!isBroken(living, part) && random.nextFloat() < BREAK_CHANCE) {
          setBroken(living, part, true);
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
