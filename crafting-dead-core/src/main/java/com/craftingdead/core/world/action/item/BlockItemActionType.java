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

package com.craftingdead.core.world.action.item;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import com.craftingdead.core.world.action.Action;
import com.craftingdead.core.world.entity.extension.LivingExtension;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public class BlockItemActionType extends ItemActionType<BlockItemAction> {

  private final Predicate<BlockState> predicate;
  private final Predicate<BlockState> destroyPredicate;
  private final double maxDistanceSquared;

  protected BlockItemActionType(Builder builder) {
    super(builder);
    this.predicate = builder.predicate;
    this.destroyPredicate = builder.destroyPredicate;
    this.maxDistanceSquared = builder.maxDistanceSquared;
  }

  public Predicate<BlockState> getPredicate() {
    return this.predicate;
  }

  public Predicate<BlockState> getDestroyPredicate() {
    return this.destroyPredicate;
  }

  public double getMaxDistanceSquared() {
    return this.maxDistanceSquared;
  }

  @Override
  public void encode(BlockItemAction action, FriendlyByteBuf out) {
    out.writeEnum(action.getHand());
    out.writeBlockHitResult(action.getHitResult());
  }

  @Override
  public BlockItemAction decode(LivingExtension<?, ?> performer, FriendlyByteBuf in) {
    return new BlockItemAction(in.readEnum(InteractionHand.class), this, performer,
        in.readBlockHitResult());
  }

  @Override
  public Optional<Action> createBlockAction(LivingExtension<?, ?> performer, UseOnContext context) {
    return Optional.of(new BlockItemAction(this, performer, context));
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder extends ItemActionType.Builder<Builder> {

    private Predicate<BlockState> predicate;
    private Predicate<BlockState> destroyPredicate;
    private double maxDistanceSquared = 4.0D;

    public Builder forBlock(Predicate<BlockState> predicate) {
      this.predicate = predicate;
      return this;
    }

    public Builder forFluid(Predicate<FluidState> predicate) {
      return this.forBlock(blockState -> predicate.test(blockState.getFluidState()));
    }

    public Builder forFluid(Supplier<Fluid> fluid) {
      return this.forFluid(fluidState -> fluidState.is(fluid.get()));
    }

    public Builder forFluid(Fluid fluid) {
      return this.forFluid(fluidState -> fluidState.is(fluid));
    }

    public Builder forFluid(TagKey<Fluid> fluidTag) {
      return this.forFluid(fluidState -> fluidState.is(fluidTag));
    }

    public Builder maxDistance(double maxDistance) {
      this.maxDistanceSquared = maxDistance * maxDistance;
      return this;
    }

    public Builder destroyBlock(Block... blocks) {
      Predicate<BlockState> blockPredicate = state -> {
        for (Block block : blocks) {
          if (state.is(block)) {
            return true;
          }
        }
        return false;
      };
      if (this.destroyPredicate != null) {
        this.destroyPredicate = this.destroyPredicate.and(blockPredicate);
      } else {
        this.destroyPredicate = blockPredicate;
      }
      if (this.predicate == null) {
        this.predicate = this.destroyPredicate;
      }
      return this;
    }

    @Override
    public BlockItemActionType build() {
      if (this.predicate == null) {
        this.predicate = state -> false;
      }
      if (this.destroyPredicate == null) {
        this.destroyPredicate = state -> false;
      }
      return new BlockItemActionType(this);
    }
  }
}
