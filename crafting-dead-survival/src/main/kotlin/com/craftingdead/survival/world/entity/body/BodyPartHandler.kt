/*
 * Crafting Dead (Kotlin refactor)
 * Copyright (C) 2022  NexusNode LTD
 *
 * 部位伤害 / 断肢系统。
 *
 * 用枪命中敌对生物时按命中点判定部位，并产生对应效果：
 *  - 头部：爆头——有概率一击致命（打爆头即死亡），否则造成高倍伤害；
 *  - 腿部：有概率断裂——断裂后移动速度大幅降低（只能缓慢爬行）；
 *  - 手臂：有概率断裂——断臂后仍可正常攻击（不影响战斗力）；
 *  - 腰部：有概率断裂——断裂后几乎无法移动（瘫痪）。
 *
 * 断裂状态保存在生物 persistentData 中（存档持久化）。仅对僵尸/骷髅等人形
 * 敌对生物生效，不影响玩家与其他模组。
 */
package com.craftingdead.survival.world.entity.body

import com.craftingdead.core.event.GunEvent
import com.craftingdead.survival.world.entity.monster.ModZombie
import java.util.UUID
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

object BodyPartHandler {

    /** persistentData 中部位状态所在根标签。 */
    private const val TAG_BODY = "craftingdead.body"
    private const val TAG_HEAD = "head_broken"
    private const val TAG_ARM = "arm_broken"
    private const val TAG_WAIST = "waist_broken"
    private const val TAG_LEG = "leg_broken"

    /** 腿断移动速度减速 modifier UUID。 */
    private val LEG_SPEED_MODIFIER: UUID =
        UUID.fromString("1b9e3a44-8c7e-4f10-9d3a-6f2c1a8e4b5c")
    /** 腰断移动速度减速 modifier UUID。 */
    private val WAIST_SPEED_MODIFIER: UUID =
        UUID.fromString("3d7f2b90-51ae-4c86-b4e8-90a2c17d6f31")

    /** 非头部部位断裂概率（每次命中）。 */
    private const val BREAK_CHANCE = 0.30F
    /** 爆头一击致命概率。 */
    private const val HEADSHOT_LETHAL_CHANCE = 0.35F
    /** 爆头（非致命）额外伤害倍率。 */
    private const val HEADSHOT_BONUS_DAMAGE = 3.0F
    /** 腿断后移动速度降低比例（爬行）。 */
    private const val LEG_SPEED_REDUCTION = -0.60
    /** 腰断后移动速度降低比例（瘫痪）。 */
    private const val WAIST_SPEED_REDUCTION = -0.85

    /**
     * TaCZ（Timeless and Classics Zero）动能子弹实体类名（软引用，无编译依赖；
     * TaCZ 未安装或类名变化时安全跳过）。
     */
    private const val TACZ_BULLET_CLASS_NAME = "com.tacz.guns.entity.EntityKineticBullet"

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
        // 将断肢状态同步到客户端（SynchedEntityData），供 Physics Mod 死亡布娃娃联动
        if (broken && entity is ModZombie) {
            when (part) {
                BodyPart.HEAD -> entity.setHeadBroken(true)
                BodyPart.ARM -> entity.setArmBroken(true)
                BodyPart.WAIST -> entity.setWaistBroken(true)
                BodyPart.LEG -> entity.setLegBroken(true)
            }
        }
    }

    /** 应用部位断裂后的效果（腿断爬行、腰断瘫痪；手臂断不影响）。 */
    private fun applyBrokenEffect(entity: LivingEntity, part: BodyPart) {
        val speed: AttributeInstance = entity.getAttribute(Attributes.MOVEMENT_SPEED) ?: return
        when (part) {
            BodyPart.LEG -> speed.addTransientModifier(
                AttributeModifier(
                    LEG_SPEED_MODIFIER,
                    "craftingdead leg broken crawl",
                    LEG_SPEED_REDUCTION,
                    AttributeModifier.Operation.MULTIPLY_TOTAL
                )
            )
            BodyPart.WAIST -> speed.addTransientModifier(
                AttributeModifier(
                    WAIST_SPEED_MODIFIER,
                    "craftingdead waist broken cripple",
                    WAIST_SPEED_REDUCTION,
                    AttributeModifier.Operation.MULTIPLY_TOTAL
                )
            )
            else -> {
                // 手臂断：仍可攻击，无速度影响
            }
        }
    }

    /** 是否需要部位判定的敌对生物（僵尸/骷髅等人形敌对生物）。 */
    private fun isSupportedTarget(entity: LivingEntity): Boolean =
        entity is Zombie || entity is AbstractSkeleton

    /**
     * 通用部位命中入口：按命中点判定部位并应用爆头 / 断肢效果，返回修改后的伤害。
     * 同时服务于 Crafting Dead 自己的枪械（[GunEvent.EntityHit]）与 TaCZ 枪械。
     */
    @JvmStatic
    fun applyBodyPartHit(living: LivingEntity, hitPos: Vec3, damage: Float): Float {
        if (living.level().isClientSide || !isSupportedTarget(living)) {
            return damage
        }

        val part = BodyPart.fromHitPosition(living, hitPos)
        val random = living.random
        return when (part) {
            BodyPart.HEAD -> {
                // 爆头：概率一击致命（打爆头即死亡），否则高倍伤害
                if (random.nextFloat() < HEADSHOT_LETHAL_CHANCE) {
                    living.maxHealth + 20.0F
                } else {
                    damage * HEADSHOT_BONUS_DAMAGE
                }
            }
            BodyPart.LEG, BodyPart.ARM, BodyPart.WAIST -> {
                // 断肢：概率使对应部位断裂
                if (!isBroken(living, part) && random.nextFloat() < BREAK_CHANCE) {
                    setBroken(living, part, true)
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
     * 通过子弹实体类名软引用判断伤害来源（无需编译依赖 TaCZ），命中点取 子弹当前位置。
     */
    @JvmStatic
    @SubscribeEvent
    fun handleTaczGunHit(event: LivingHurtEvent) {
        val source = event.source ?: return
        val direct = source.directEntity ?: return
        if (!TACZ_BULLET_CLASS_NAME.equals(direct.javaClass.name)) {
            return
        }
        val target = event.entity ?: return
        if (target.level().isClientSide) {
            return
        }
        // 注意：TaCZ 子弹实体的 position() 是子弹飞行高度（≈射手眼睛高度），并非命中点。
        // 射手与目标地面高度不同时会误判部位（例如打腿被判为头部）。
        // 因此用射手视线方向与目标碰撞箱求交，得到真实命中高度。
        val hitPos = resolveTaczHitPoint(target, source, direct)
        event.amount = applyBodyPartHit(target, hitPos, event.amount)
    }

    /**
     * 计算 TaCZ 子弹的真实命中点：从射手眼睛沿视线方向与目标碰撞箱求交。
     * 无射手或射线未命中时回退到子弹当前位置。
     */
    private fun resolveTaczHitPoint(
        target: LivingEntity,
        source: DamageSource,
        direct: Entity
    ): Vec3 {
        val attacker = source.entity
        if (attacker is LivingEntity) {
            val eye = attacker.getEyePosition(1.0F)
            val end = eye.add(attacker.lookAngle.scale(target.bbHeight * 2 + 4.0))
            val clipped = target.boundingBox.clip(eye, end)
            if (clipped.isPresent) {
                return clipped.get()
            }
        }
        return direct.position()
    }
}
