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

package com.craftingdead.survival.world.item;

import com.craftingdead.core.capability.CapabilityUtil;
import com.craftingdead.core.world.entity.extension.PlayerExtension;
import com.craftingdead.immerse.game.survival.SurvivalPlayerHandler;
import com.craftingdead.immerse.world.item.hydration.Hydration;
import com.craftingdead.survival.CraftingDeadSurvival;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;

public class ConsumableItem extends Item {

  public enum Type {ONLY_FOOD, ONLY_DRINK, FOOD_AND_DRINK}

  private final int water;
  private final Supplier<Item> emptyItem;
  private final FoodProperties foodProperties;
  private final Type type;

  public ConsumableItem(Properties properties, int nutrition, float saturation, int water,
      Supplier<Item> emptyItem, Type type) {
    super(properties);
    this.water = (type != Type.ONLY_FOOD) ? water : 0;
    this.emptyItem = emptyItem;
    this.foodProperties = (type == Type.ONLY_FOOD || type == Type.FOOD_AND_DRINK)
        ? new FoodProperties.Builder().nutrition(nutrition).saturationMod(saturation).build()
        : null;
    this.type = type;
  }

  @Override
  public ICapabilityProvider initCapabilities(ItemStack itemStack, @Nullable CompoundTag tag) {
    return (this.type != Type.ONLY_FOOD && CraftingDeadSurvival.instance().isImmerseLoaded())
        ? createHydrationProvider(this.water) : null;
  }

  private static ICapabilityProvider createHydrationProvider(int water) {
    return CapabilityUtil.provider(() -> Hydration.fixed(water), Hydration.CAPABILITY);
  }

  @Override
  public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level,
      @NotNull Player player, @NotNull InteractionHand hand) {
    boolean canConsume = (this.type == Type.ONLY_FOOD && this.canEat(player)) || (this.type == Type.FOOD_AND_DRINK && (
            this.canEat(player) || this.canDrink(player))) || (this.type == Type.ONLY_DRINK && this.canDrink(player)
            && CraftingDeadSurvival.instance().isImmerseLoaded());
    return canConsume ? ItemUtils.startUsingInstantly(level, player, hand)
        : InteractionResultHolder.fail(player.getItemInHand(hand));
  }

  /**
   * Prevents unnecessary consumption when the player no longer needs food or water
   */

  @SuppressWarnings("deprecation")
  @Override
  public void onUseTick(@NotNull Level level, @NotNull LivingEntity entity,
      @NotNull ItemStack stack, int remainingUseDuration) {
    if (!(entity instanceof Player player)) return;
    boolean needsFood = this.canEat(player);
    boolean needsWater = CraftingDeadSurvival.instance().isImmerseLoaded() && this.canDrink(player);
    boolean consumption = switch (this.type) {
      case ONLY_FOOD -> needsFood;
      case ONLY_DRINK -> needsWater;
      case FOOD_AND_DRINK -> needsFood || needsWater;
    };
    if (!consumption) {
      player.releaseUsingItem();
    }
  }

  @Override
  public @NotNull ItemStack finishUsingItem(@NotNull ItemStack itemStack, @NotNull Level level,
      @NotNull LivingEntity entity) {

    if (entity instanceof ServerPlayer serverPlayer) {
      CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, itemStack);
    }

    if (entity instanceof Player player) {
      this.applyEffects(player);

      if (!player.getAbilities().instabuild) {
        itemStack.shrink(1);

        if (this.emptyItem != null) {
          var emptyStack = new ItemStack(this.emptyItem.get());
          if (!player.getInventory().add(emptyStack)) {
            player.spawnAtLocation(emptyStack);
          }
        }
      }
    }

    this.triggerConsumptionEvent(level, entity);
    return itemStack;
  }


  private void applyEffects(Player player) {
    player.awardStat(Stats.ITEM_USED.get(this));
    if (this.foodProperties != null) {
      player.getFoodData()
          .eat(this.foodProperties.getNutrition(), this.foodProperties.getSaturationModifier());
    }
  }

  private void triggerConsumptionEvent(Level level, LivingEntity entity) {
    var event = (this.type == Type.ONLY_DRINK) ? GameEvent.DRINKING_FINISH : GameEvent.EAT;
    level.gameEvent(entity, event, entity.eyeBlockPosition());
  }

  @Override
  public int getUseDuration(@NotNull ItemStack itemStack) {
    return 32;
  }

  @Override
  public @NotNull UseAnim getUseAnimation(@NotNull ItemStack itemStack) {
    return (this.type == Type.ONLY_DRINK) ? UseAnim.DRINK : UseAnim.EAT;
  }

  private boolean canEat(Player player) {
    return player.getFoodData().getFoodLevel() < 20;
  }

  private boolean canDrink(Player player) {
    var handler = PlayerExtension.getOrThrow(player).getHandlerOrThrow(SurvivalPlayerHandler.TYPE);
    return handler.getWater() < handler.getMaxWater();
  }

  @Override
  public void appendHoverText(@NotNull ItemStack itemStack, @Nullable Level level,
      @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
    if (this.foodProperties != null) {
      tooltip.add(new TranslatableComponent("item.craftingdeadsurvival.consumable.food_info").withStyle(
              ChatFormatting.GRAY).append(new TextComponent(" " + this.foodProperties.getNutrition()).withStyle(ChatFormatting.RED)));
    }
    if (this.water > 0) {
      tooltip.add(new TranslatableComponent("item.craftingdeadsurvival.consumable.water_info").withStyle(
                  ChatFormatting.GRAY).append(new TextComponent(" " + this.water).withStyle(ChatFormatting.RED)));
    }
  }
}
