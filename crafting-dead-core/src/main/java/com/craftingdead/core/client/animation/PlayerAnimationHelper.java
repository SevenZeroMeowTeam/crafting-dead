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

package com.craftingdead.core.client.animation;

import com.craftingdead.core.world.entity.extension.PlayerExtension;
import com.craftingdead.core.world.item.gun.Gun;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 第三人称玩家动画辅助类。
 *
 * <p>设计思路参考了开源模组 NotEnoughAnimations（把第一人称动画带入第三人称、修复第三人称
 * 动画硬切问题），但所有代码均为本项目原创实现，未复制其源码（其采用 tr7zw Protective
 * License，禁止直接分发/复制衍生源码）。
 *
 * <p>为每个玩家维护一组跨帧的平滑插值状态，让手铐/迷你枪/双手持枪/吃喝/瞄准等姿势平滑过渡，
 * 而不是瞬间硬切。
 */
public final class PlayerAnimationHelper {

  /** 自定义姿势（手铐/迷你枪）的平滑速度。 */
  private static final float POSE_SMOOTHING = 0.25F;
  /** 双手持枪姿势的平滑速度。 */
  private static final float GUN_SMOOTHING = 0.3F;
  /** 瞄准身体旋转的平滑速度。 */
  private static final float AIM_SMOOTHING = 0.4F;
  /** 吃喝手臂抬起的平滑速度。 */
  private static final float EAT_SMOOTHING = 0.35F;
  /** 状态缓存上限，超过时清理已离线的玩家，避免内存泄漏。 */
  private static final int MAX_ENTRIES = 64;

  private static final Map<UUID, PoseState> POSE_STATES = new HashMap<>();

  private PlayerAnimationHelper() {}

  /**
   * 获取指定玩家（跨帧持久）的平滑姿势状态。
   */
  public static PoseState getPoseState(Player player) {
    PoseState state = POSE_STATES.get(player.getUUID());
    if (state == null) {
      state = new PoseState();
      POSE_STATES.put(player.getUUID(), state);
      prune();
    }
    return state;
  }

  private static void prune() {
    if (POSE_STATES.size() <= MAX_ENTRIES) {
      return;
    }
    final var level = Minecraft.getInstance().level;
    POSE_STATES.entrySet()
        .removeIf(entry -> level == null || level.getPlayerByUUID(entry.getKey()) == null);
  }

  /**
   * 是否属于需要双手的武器/枪械（决定是否隐藏副手物品、是否使用双手持枪姿势）。
   *
   * <p>覆盖：Crafting Dead 自己的枪械（Gun 能力）、TaCZ（Timeless and Classics Zero）
   * 命名空间下的物品、以及原版弓/弩/三叉戟。
   */
  public static boolean isTwoHandedWeapon(ItemStack stack) {
    if (stack.isEmpty()) {
      return false;
    }
    // Crafting Dead 自己的枪械
    if (stack.getCapability(Gun.CAPABILITY).isPresent()) {
      return true;
    }
    // TaCZ（Timeless and Classics Zero）枪械：统一使用 tacz 命名空间下的物品
    final ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.getItem());
    if (key != null && "tacz".equals(key.getNamespace())) {
      return true;
    }
    // 原版双手武器
    return stack.getItem() instanceof BowItem
        || stack.getItem() instanceof CrossbowItem
        || stack.getItem() instanceof TridentItem;
  }

  /**
   * 玩家是否正在吃喝（使用中的物品 {@link UseAnim} 为 EAT/DRINK）。
   */
  public static boolean isEatingOrDrinking(Player player) {
    if (!player.isUsingItem()) {
      return false;
    }
    final UseAnim anim = player.getUseItem().getUseAnimation();
    return anim == UseAnim.EAT || anim == UseAnim.DRINK;
  }

  /**
   * 玩家是否正在用枪械瞄准（枪的次要动作被触发）。
   */
  public static boolean isAiming(Player player, @Nullable PlayerExtension<?> extension) {
    if (extension == null) {
      return false;
    }
    return extension.mainHandGun().map(Gun::isPerformingSecondaryAction).orElse(false);
  }

  /**
   * 是否处于第一人称视角下的本地玩家（用于排除第一人称手部渲染，避免影响第一人称持枪）。
   */
  public static boolean isFirstPersonView(Player player) {
    final var minecraft = Minecraft.getInstance();
    return player == minecraft.player && minecraft.options.getCameraType().isFirstPerson();
  }

  /**
   * 单个玩家的平滑姿势状态（跨帧插值，激活时向 1 靠近、否则向 0 回落）。
   */
  public static final class PoseState {
    private float poseBlend;
    private float gunBlend;
    private float aimBlend;
    private float eatBlend;

    public void updatePose(boolean active) {
      this.poseBlend = Mth.lerp(POSE_SMOOTHING, this.poseBlend, active ? 1.0F : 0.0F);
    }

    public void updateGun(boolean active) {
      this.gunBlend = Mth.lerp(GUN_SMOOTHING, this.gunBlend, active ? 1.0F : 0.0F);
    }

    public void updateAim(boolean active) {
      this.aimBlend = Mth.lerp(AIM_SMOOTHING, this.aimBlend, active ? 1.0F : 0.0F);
    }

    public void updateEat(boolean active) {
      this.eatBlend = Mth.lerp(EAT_SMOOTHING, this.eatBlend, active ? 1.0F : 0.0F);
    }

    public float getPoseBlend() {
      return this.poseBlend;
    }

    public float getGunBlend() {
      return this.gunBlend;
    }

    public float getAimBlend() {
      return this.aimBlend;
    }

    public float getEatBlend() {
      return this.eatBlend;
    }
  }
}
