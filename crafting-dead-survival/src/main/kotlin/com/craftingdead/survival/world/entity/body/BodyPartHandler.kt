/*
 * Crafting Dead (Kotlin refactor)
 * Copyright (C) 2022  NexusNode LTD
 *
 * 部位伤害 / 断肢系统。
 *
 * 用枪命中敌对生物时按命中点判定部位，并产生对应效果：
 *  - 头部：爆头——每次造成高倍伤害，累计伤害达到阈值或触发幸运一击时打爆头部一击致命；
 *  - 腿部：断裂——累计伤害达到阈值后腿断，移动速度大幅降低（只能缓慢爬行）；
 *  - 手臂：断裂——累计伤害达到阈值后臂断，攻击力大幅下降（无法有效攻击）；
 *  - 腰部：断裂——累计伤害达到阈值后腰断，几乎无法移动（瘫痪）。
 *
 * 断裂状态与部位累计伤害都保存在生物 persistentData 中（存档持久化），
 * 命中同一部位会累积伤害，越打越容易断裂（更真实）。仅对僵尸 / 骷髅等人形
 * 敌对生物生效，不影响玩家与其他模组。
 */
package com.craftingdead.survival.world.entity.body

import com.craftingdead.core.event.GunEvent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeInstance
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.monster.AbstractSkeleton
import net.minecraft.world.entity.monster.Zombie
import net.minecraft.world.phys.Vec3
import net.minecraftforge.event.entity.living.LivingHurtEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import org.slf4j.LoggerFactory

object BodyPartHandler {

    /** 调试用日志器。 */
    private val LOGGER = LoggerFactory.getLogger("CraftingDeadBodyPart")

    /** persistentData 中部位状态所在根标签。 */
    private const val TAG_BODY = "craftingdead.body"
    private const val TAG_HEAD = "head_broken"
    private const val TAG_ARM = "arm_broken"
    private const val TAG_WAIST = "waist_broken"
    private const val TAG_LEG = "leg_broken"
    /** persistentData 中各部位累计伤害标签。 */
    private const val TAG_HEAD_DMG = "head_dmg"
    private const val TAG_ARM_DMG = "arm_dmg"
    private const val TAG_WAIST_DMG = "waist_dmg"
    private const val TAG_LEG_DMG = "leg_dmg"

    /** 腿断移动速度减速 modifier id。 */
    private val LEG_SPEED_MODIFIER =
        ResourceLocation.fromNamespaceAndPath("craftingdeadsurvival", "leg_broken_crawl")
    /** 腰断移动速度减速 modifier id。 */
    private val WAIST_SPEED_MODIFIER =
        ResourceLocation.fromNamespaceAndPath("craftingdeadsurvival", "waist_broken_cripple")
    /** 手臂断攻击力降低 modifier id。 */
    private val ARM_ATTACK_MODIFIER =
        ResourceLocation.fromNamespaceAndPath("craftingdeadsurvival", "arm_broken_weak")

    /** 部位断裂所需的累计伤害阈值（= 最大生命值 × 该比例）。 */
    private const val BREAK_THRESHOLD_RATIO = 0.5F
    /** 爆头基础伤害倍率（每次命中头部都生效）。 */
    private const val HEADSHOT_DAMAGE_MULTIPLIER = 2.5F
    /** 爆头幸运一击（直接打爆头部）概率。 */
    private const val HEADSHOT_INSTANT_KILL_CHANCE = 0.25F
    /** 腿断后移动速度降低比例（爬行）。 */
    private const val LEG_SPEED_REDUCTION = -0.60
    /** 腰断后移动速度降低比例（瘫痪）。 */
    private const val WAIST_SPEED_REDUCTION = -0.85
    /** 手臂断后攻击力降低比例。 */
    private const val ARM_ATTACK_REDUCTION = -0.50

    /**
     * TaCZ（Timeless and Classics Zero）动能子弹实体类名（软引用，无编译依赖；
     * TaCZ 未安装或类名变化时安全跳过）。
     */
    private const val TACZ_BULLET_CLASS_NAME = "com.tacz.guns.entity.EntityKineticBullet"

    /** 调试开关：是否打印每次 TaCZ 命中的判定日志（默认关闭）。 */
    @Volatile
    var debugLogEnabled: Boolean = false

    /** 判断某部位是否已断裂。 */
    @JvmStatic
    fun isBroken(entity: LivingEntity, part: BodyPart): Boolean {
        val bodyTag = entity.persistentData.getCompound(TAG_BODY)
        return when (part) {
            BodyPart.HEAD -> bodyTag.getBoolean(TAG_HEAD)
            BodyPart.ARM -> bodyTag.getBoolean(TAG_ARM)
            BodyPart.WAIST -> bodyTag.getBoolean(TAG_WAIST)
            BodyPart.LEG -> bodyTag.getBoolean(TAG_LEG)
        }
    }

    /** 读取某部位已累计的伤害。 */
    private fun getPartDamage(entity: LivingEntity, part: BodyPart): Float {
        val bodyTag = entity.persistentData.getCompound(TAG_BODY)
        return when (part) {
            BodyPart.HEAD -> bodyTag.getFloat(TAG_HEAD_DMG)
            BodyPart.ARM -> bodyTag.getFloat(TAG_ARM_DMG)
            BodyPart.WAIST -> bodyTag.getFloat(TAG_WAIST_DMG)
            BodyPart.LEG -> bodyTag.getFloat(TAG_LEG_DMG)
        }
    }

    /** 向某部位累计伤害，返回累计后的总量。 */
    private fun addPartDamage(entity: LivingEntity, part: BodyPart, damage: Float): Float {
        val bodyTag = entity.persistentData.getCompound(TAG_BODY)
        val total = getPartDamage(entity, part) + damage
        when (part) {
            BodyPart.HEAD -> bodyTag.putFloat(TAG_HEAD_DMG, total)
            BodyPart.ARM -> bodyTag.putFloat(TAG_ARM_DMG, total)
            BodyPart.WAIST -> bodyTag.putFloat(TAG_WAIST_DMG, total)
            BodyPart.LEG -> bodyTag.putFloat(TAG_LEG_DMG, total)
        }
        entity.persistentData.put(TAG_BODY, bodyTag)
        return total
    }

    /** 设置某部位断裂状态，并在断裂时应用对应效果。 */
    @JvmStatic
    fun setBroken(entity: LivingEntity, part: BodyPart, broken: Boolean) {
        val bodyTag = entity.persistentData.getCompound(TAG_BODY)
        when (part) {
            BodyPart.HEAD -> bodyTag.putBoolean(TAG_HEAD, broken)
            BodyPart.ARM -> bodyTag.putBoolean(TAG_ARM, broken)
            BodyPart.WAIST -> bodyTag.putBoolean(TAG_WAIST, broken)
            BodyPart.LEG -> bodyTag.putBoolean(TAG_LEG, broken)
        }
        entity.persistentData.put(TAG_BODY, bodyTag)
        if (broken) {
            applyBrokenEffect(entity, part)
        }
    }

    /** 应用部位断裂后的效果（腿断爬行、腰断瘫痪、臂断攻击力下降；头部由伤害击杀）。 */
    private fun applyBrokenEffect(entity: LivingEntity, part: BodyPart) {
        when (part) {
            BodyPart.LEG -> entity.getAttribute(Attributes.MOVEMENT_SPEED)
                ?.addTransientModifier(
                    AttributeModifier(
                        LEG_SPEED_MODIFIER,
                        LEG_SPEED_REDUCTION,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
                )
            BodyPart.WAIST -> entity.getAttribute(Attributes.MOVEMENT_SPEED)
                ?.addTransientModifier(
                    AttributeModifier(
                        WAIST_SPEED_MODIFIER,
                        WAIST_SPEED_REDUCTION,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
                )
            BodyPart.ARM -> entity.getAttribute(Attributes.ATTACK_DAMAGE)
                ?.addTransientModifier(
                    AttributeModifier(
                        ARM_ATTACK_MODIFIER,
                        ARM_ATTACK_REDUCTION,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
                )
            BodyPart.HEAD -> {
                // 头部打爆由伤害处理直接一击致命
            }
        }
    }

    /** 是否需要部位判定的敌对生物（僵尸 / 骷髅等人形敌对生物）。 */
    private fun isSupportedTarget(entity: LivingEntity): Boolean =
        entity is Zombie || entity is AbstractSkeleton

    /**
     * 通用部位命中入口：按命中点判定部位并应用爆头 / 断肢效果，返回修改后的伤害。
     * 同时服务于 Crafting Dead 自己的枪械（[GunEvent.EntityHit]）与 TaCZ 枪械。
     */
    @JvmStatic
    fun applyBodyPartHit(living: LivingEntity, hitPos: Vec3, damage: Float): Float {
        if (debugLogEnabled) {
            LOGGER.info("[BodyPart] applyBodyPartHit target={} client={} supported={} hitY={} feetY={} h={} dmg={}",
                living.javaClass.simpleName, living.level().isClientSide,
                isSupportedTarget(living), hitPos.y, living.y, living.bbHeight, damage)
        }
        if (living.level().isClientSide || !isSupportedTarget(living)) {
            return damage
        }

        val part = BodyPart.fromHitPosition(living, hitPos)
        if (debugLogEnabled) {
            LOGGER.info("[BodyPart] classified part={} ratio={}", part,
                (hitPos.y - living.y) / living.bbHeight)
        }
        val random = living.random
        return when (part) {
            BodyPart.HEAD -> {
                // 爆头：每次高倍伤害；累计打爆头部或幸运一击则一击致命
                if (isBroken(living, BodyPart.HEAD)) {
                    damage * HEADSHOT_DAMAGE_MULTIPLIER
                } else {
                    val total = addPartDamage(living, BodyPart.HEAD, damage)
                    if (total >= living.maxHealth * BREAK_THRESHOLD_RATIO
                        || random.nextFloat() < HEADSHOT_INSTANT_KILL_CHANCE
                    ) {
                        setBroken(living, BodyPart.HEAD, true)
                        if (debugLogEnabled) {
                            LOGGER.info("[BodyPart] HEAD destroyed! accumulated={} threshold={}",
                                total, living.maxHealth * BREAK_THRESHOLD_RATIO)
                        }
                        living.maxHealth + 20.0F
                    } else {
                        damage * HEADSHOT_DAMAGE_MULTIPLIER
                    }
                }
            }
            BodyPart.LEG, BodyPart.ARM, BodyPart.WAIST -> {
                // 断肢：累计伤害达到阈值即断裂（越打越容易断）
                if (!isBroken(living, part)) {
                    val total = addPartDamage(living, part, damage)
                    if (total >= living.maxHealth * BREAK_THRESHOLD_RATIO) {
                        setBroken(living, part, true)
                        if (debugLogEnabled) {
                            LOGGER.info("[BodyPart] {} broken! accumulated={} threshold={}",
                                part, total, living.maxHealth * BREAK_THRESHOLD_RATIO)
                        }
                    }
                }
                damage
            }
        }
    }

    /** Crafting Dead 枪械命中事件处理：判定部位并应用爆头 / 断肢效果。 */
    @JvmStatic
    fun handleGunHit(event: GunEvent.EntityHit) {
        val living = event.target() as? LivingEntity ?: return
        event.damage(applyBodyPartHit(living, event.hitPos(), event.damage()))
    }

    /**
     * TaCZ 枪械命中处理：TaCZ 的子弹命中由 [LivingHurtEvent] 触发，
     * 通过子弹实体类名软引用判断伤害来源（无需编译依赖 TaCZ），命中点取子弹当前位置。
     */
    @JvmStatic
    @SubscribeEvent
    fun handleTaczGunHit(event: LivingHurtEvent) {
        val source = event.source ?: run {
            if (debugLogEnabled) LOGGER.info("[BodyPart] handleTaczGunHit: no source")
            return
        }
        val direct = source.directEntity ?: run {
            if (debugLogEnabled) LOGGER.info("[BodyPart] handleTaczGunHit: no directEntity")
            return
        }
        if (!TACZ_BULLET_CLASS_NAME.equals(direct.javaClass.name)) {
            if (debugLogEnabled) {
                LOGGER.info("[BodyPart] handleTaczGunHit: direct={} NOT bullet, skipped", direct.javaClass.name)
            }
            return
        }
        val target = event.entity ?: run {
            if (debugLogEnabled) LOGGER.info("[BodyPart] handleTaczGunHit: no target entity")
            return
        }
        if (target.level().isClientSide) {
            return
        }
        // 子弹在造成伤害的瞬间仍位于命中点；TaCZ 在 hurt 之后才移除子弹
        event.amount = applyBodyPartHit(target, direct.position(), event.amount)
    }
}
