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
import com.craftingdead.survival.world.entity.monster.ModZombie
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
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

    /**
     * TaCZ 子弹伤害类型的 message_id（data/tacz/damage_type/bullet.json 统一为 "tacz.bullet"）。
     * 用消息 id 识别 TaCZ 子弹比依赖 source.directEntity 更可靠，
     * 因为部分情况下 directEntity 为 null（导致旧逻辑误判为非子弹、跳过断肢/爆头）。
     */
    private const val TACZ_BULLET_MSG_ID = "tacz.bullet"

    /** 调试开关：是否打印每次 TaCZ 命中的判定日志（默认关闭）。 */
    @Volatile
    var debugLogEnabled: Boolean = true

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
        // 将断肢状态同步到客户端（SynchedEntityData），
        // 供客户端 Physics Mod 死亡布娃娃联动：对应部位不连接、直接掉落。
        if (broken && entity is ModZombie) {
            when (part) {
                BodyPart.HEAD -> entity.setHeadBroken(true)
                BodyPart.ARM -> entity.setArmBroken(true)
                BodyPart.WAIST -> entity.setWaistBroken(true)
                BodyPart.LEG -> entity.setLegBroken(true)
            }
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
     * TaCZ 枪械命中处理：TaCZ 的子弹命中由 [LivingHurtEvent] 触发。
     *
     * 识别 TaCZ 子弹的两种途径（任一命中即判定为 TaCZ 子弹）：
     * 1. 伤害来源的 message_id 为 "tacz.bullet"（TaCZ 所有子弹伤害类型统一该 id）；
     * 2. directEntity 是 com.tacz.guns.entity.EntityKineticBullet。
     *
     * 用 message_id 识别比只依赖 directEntity 更可靠——部分情况下（如左轮等手枪弹）
     * directEntity 为 null，旧逻辑会误判为普通伤害而跳过断肢/爆头，导致打不死。
     *
     * 命中点从射手视线方向与目标碰撞箱求交；无法求交（无射手 / 未命中）时回退到
     * 子弹位置，子弹也为 null 时回退到目标中心。
     */
    @JvmStatic
    @SubscribeEvent
    fun handleTaczGunHit(event: LivingHurtEvent) {
        val source = event.source ?: run {
            if (debugLogEnabled) LOGGER.info("[BodyPart] handleTaczGunHit: no source")
            return
        }
        val target = event.entity ?: run {
            if (debugLogEnabled) LOGGER.info("[BodyPart] handleTaczGunHit: no target entity")
            return
        }
        val msgId = source.getMsgId()
        val direct = source.directEntity
        val attacker = source.entity
        val isTaczBulletMsg = msgId == TACZ_BULLET_MSG_ID
        val isTaczBulletEntity =
            direct != null && TACZ_BULLET_CLASS_NAME.equals(direct.javaClass.name)

        // 无条件诊断：记录所有命中僵尸/骷髅的伤害事件（msgId 揭示是否 TaCZ 子弹 / 环境伤害）。
        if (debugLogEnabled && (target is Zombie || target is AbstractSkeleton)) {
            LOGGER.info(
                "[BodyPart] HURT target={} msgId={} direct={} attacker={} amount={}",
                target.javaClass.simpleName, msgId, direct?.javaClass?.name ?: "null",
                attacker?.javaClass?.name ?: "null", event.amount)
        }

        if (debugLogEnabled && (isTaczBulletMsg || isTaczBulletEntity)) {
            LOGGER.info(
                "[BodyPart] TACZ hurt msgId={} direct={} attacker={} target={} amount={}",
                msgId, direct?.javaClass?.name ?: "null",
                attacker?.javaClass?.name ?: "null", target.javaClass.simpleName, event.amount)
        }

        // 非 TaCZ 子弹（环境伤害 / 近战 / 箭等）：跳过，不增强。
        if (!isTaczBulletMsg && !isTaczBulletEntity) {
            if (debugLogEnabled && !msgId.startsWith("inFire") && !msgId.startsWith("onFire")
                && !msgId.startsWith("fall") && !msgId.startsWith("lava")
                && !msgId.startsWith("cramming") && !msgId.startsWith("flyIntoWall")
                && !msgId.startsWith("drown")) {
                LOGGER.info("[BodyPart] skip msgId={} direct={}",
                    msgId, direct?.javaClass?.name ?: "null")
            }
            return
        }

        if (target.level().isClientSide) {
            return
        }
        val hitPos = resolveTaczHitPoint(target, direct, attacker)
        event.amount = applyBodyPartHit(target, hitPos, event.amount)
    }

    /**
     * 计算 TaCZ 子弹的真实命中点：从射手眼睛沿视线方向与目标碰撞箱求交。
     * 无射手或射线未命中时回退到子弹位置；子弹为 null 时回退到目标中心。
     */
    private fun resolveTaczHitPoint(
        target: LivingEntity,
        direct: Entity?,
        attacker: Entity?
    ): Vec3 {
        // 优先用射手视线与目标碰撞箱求交（最接近真实命中点）。
        if (attacker is LivingEntity) {
            val eye = attacker.getEyePosition(1.0F)
            // 原射线长度(bbHeight*2+4≈8格)太短，远距射击求交会失败导致回退到子弹位置；
            // 改为覆盖目标距离 + 目标身高，确保命中较远处的目标。
            val distance = eye.distanceTo(target.getEyePosition(1.0F)) + target.bbHeight * 2.0
            val end = eye.add(attacker.lookAngle.scale(distance))
            val clipped = target.boundingBox.clip(eye, end)
            if (clipped.isPresent) {
                val hit = clipped.get()
                if (debugLogEnabled) {
                    LOGGER.info(
                        "[BodyPart] raycast hit={} clipY={} eyeY={} targetFeet={} ratio={}",
                        target.javaClass.simpleName, hit.y, eye.y, target.y,
                        (hit.y - target.y) / target.bbHeight
                    )
                }
                return clampHitPoint(target, hit)
            }
        }
        // 回退：子弹实时位置 / 目标中心，并夹取到目标碰撞箱内，保证 ratio 落在 [0,1]，
        // 避免命中点低于脚底/高于头顶导致爆头、断肢误判。
        val fallback = direct?.position()
            ?: target.position().add(0.0, target.bbHeight / 2.0, 0.0)
        return clampHitPoint(target, fallback)
    }

    /** 把命中点 Y 夹取到目标碰撞箱 [脚底, 头顶] 范围内，保证高度比例合法。 */
    private fun clampHitPoint(target: LivingEntity, hit: Vec3): Vec3 {
        val minY = target.y
        val maxY = target.y + target.bbHeight
        val y = hit.y.coerceIn(minY, maxY)
        return Vec3(hit.x, y, hit.z)
    }
}
