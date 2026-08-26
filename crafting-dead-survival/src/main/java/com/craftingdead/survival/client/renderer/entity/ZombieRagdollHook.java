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

package com.craftingdead.survival.client.renderer.entity;

import com.craftingdead.survival.client.model.AdvancedZombieModel;
import com.craftingdead.survival.world.entity.monster.ModZombie;
import java.util.List;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.ragdoll.Ragdoll;
import net.diebuddies.physics.ragdoll.RagdollHook;
import net.diebuddies.physics.ragdoll.RagdollMapper;
import net.diebuddies.physics.ragdoll.RagdollMapper.Counter;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;

/**
 * Physics Mod（haubna/PhysicsMod）Ragdoll API 集成：
 * 为僵尸的死亡布娃娃（ragdoll）加入断肢联动。
 *
 * <p>原理：{@link RagdollMapper#map} 会先执行所有已注册的 {@link RagdollHook}；
 * 只要自定义 hook 建立了至少一个关节连接，Physics Mod 的 vanilla hook 就不会执行
 * （{@code joints.size() > 0} 时直接返回）。因此本 hook 完全接管人形僵尸的布娃娃映射，
 * 并跳过已断裂部位（头部 / 手臂 / 腿）的连接，使死亡布娃娃上对应部位直接掉落。
 *
 * <p>前提：僵尸必须使用原版 {@code ModelPart} 模型渲染（{@link AdvancedZombieRenderer}），
 * Physics Mod 的 mob 物理只作用于原版 {@code LivingEntityRenderer}。
 *
 * <p>仅在 Physics Mod 已安装（modid "physicsmod"）时注册，缺失时安全降级为无布娃娃。
 */
@OnlyIn(Dist.CLIENT)
public class ZombieRagdollHook implements RagdollHook {

  private ZombieRagdollHook() {}

  /**
   * Physics Mod 是否已安装。
   */
  public static boolean isPhysicsModLoaded() {
    return ModList.get() != null && ModList.get().isLoaded("physicsmod");
  }

  /**
   * 若 Physics Mod 已安装则注册本 hook（幂等，可重复调用）。
   */
  public static void registerIfPresent() {
    if (isPhysicsModLoaded()) {
      RagdollMapper.addHook(new ZombieRagdollHook());
    }
  }

  /**
   * 生成僵尸布娃娃的连接关系。
   *
   * <p>bodies 生成顺序与 Physics Mod vanilla hook 保持一致：
   * head(0), body(1), rightArm(2), leftArm(3), rightLeg(4), leftLeg(5), hat(6)；
   * {@link AdvancedZombieModel} 额外包含服装 overlay：leftSleeve(7), rightSleeve(8),
   * leftPants(9), rightPants(10), jacket(11)。
   */
  @Override
  public void map(Ragdoll ragdoll, Entity entity, EntityModel model) {
    if (!(entity instanceof Zombie) || !(model instanceof HumanoidModel<?> humanoid)) {
      return;
    }

    Counter counter = new Counter();
    RagdollMapper.getCuboids(ragdoll, humanoid.head, counter);
    RagdollMapper.getCuboids(ragdoll, humanoid.body, counter);
    RagdollMapper.getCuboids(ragdoll, humanoid.rightArm, counter);
    RagdollMapper.getCuboids(ragdoll, humanoid.leftArm, counter);
    RagdollMapper.getCuboids(ragdoll, humanoid.rightLeg, counter);
    RagdollMapper.getCuboids(ragdoll, humanoid.leftLeg, counter);
    RagdollMapper.getCuboids(ragdoll, humanoid.hat, counter);

    // 断肢状态（SynchedEntityData 已同步到客户端；非 ModZombie 保持完整连接）
    boolean headBroken = false;
    boolean armBroken = false;
    boolean legBroken = false;
    if (entity instanceof ModZombie modZombie) {
      headBroken = modZombie.isHeadBroken();
      armBroken = modZombie.isArmBroken();
      legBroken = modZombie.isLegBroken();
      // 腰断 = 瘫痪散架：四肢与头全部断开，仅剩躯干
      if (modZombie.isWaistBroken()) {
        headBroken = true;
        armBroken = true;
        legBroken = true;
      }
    }

    // 主连接（head→body, arms→body, legs→body, hat→head）
    if (!headBroken) {
      ragdoll.addConnection(0, 1);
    }
    if (!armBroken) {
      ragdoll.addConnection(2, 1); // rightArm → body
      ragdoll.addConnection(3, 1); // leftArm → body
    }
    if (!legBroken) {
      ragdoll.addConnection(4, 1); // rightLeg → body
      ragdoll.addConnection(5, 1); // leftLeg → body
    }
    if (!headBroken) {
      ragdoll.addConnection(6, 0, true, true); // hat → head
    }

    // 服装 overlay 部件连接到对应主部位
    if (model instanceof AdvancedZombieModel<?> advanced) {
      RagdollMapper.getCuboids(ragdoll, advanced.leftSleeve, counter);  // 7
      RagdollMapper.getCuboids(ragdoll, advanced.rightSleeve, counter); // 8
      RagdollMapper.getCuboids(ragdoll, advanced.leftPants, counter);   // 9
      RagdollMapper.getCuboids(ragdoll, advanced.rightPants, counter);  // 10
      RagdollMapper.getCuboids(ragdoll, advanced.jacket, counter);      // 11

      if (!armBroken) {
        ragdoll.addConnection(7, 3, true, true);  // leftSleeve → leftArm
        ragdoll.addConnection(8, 2, true, true);  // rightSleeve → rightArm
      }
      if (!legBroken) {
        ragdoll.addConnection(9, 5, true, true);  // leftPants → leftLeg
        ragdoll.addConnection(10, 4, true, true); // rightPants → rightLeg
      }
      ragdoll.addConnection(11, 1, true, true);    // jacket → body
    }

    // 只要建立了连接，vanilla hook 即被跳过（RagdollMapper.map 的 joints.size() > 0 检查）
  }

  /**
   * 过滤方块化实体（本次仅作保留，不做额外过滤）。
   */
  @Override
  public void filterCuboidsFromEntities(List<PhysicsEntity> blockifiedEntity, Entity entity,
      EntityModel model) {
    // 空实现：使用 Physics Mod 默认过滤逻辑（vanilla hook 仍会执行过滤）
  }
}
