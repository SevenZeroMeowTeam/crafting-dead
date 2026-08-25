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

package com.craftingdead.survival.client.renderer.entity.layers;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import com.craftingdead.core.world.entity.extension.LivingExtension;
import com.craftingdead.core.world.item.equipment.Equipment;
import com.craftingdead.survival.world.entity.monster.ModZombie;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/**
 * GeckoLib 版本 {@link com.craftingdead.core.client.renderer.entity.layers.EquipmentLayer}：
 * 将近战/背包/背心/帽子/枪械等装备物品渲染到 Geo 人形模型的对应骨骼上。
 */
public class GeoEquipmentLayer extends GeoRenderLayer<ModZombie> {

  private final Equipment.Slot slot;
  private final boolean useCrouchOrientation;
  private final boolean useHeadOrientation;
  @Nullable
  private final Consumer<PoseStack> transformation;

  private GeoEquipmentLayer(GeoRenderer<ModZombie> renderer, Equipment.Slot slot,
      boolean useCrouchOrientation, boolean useHeadOrientation,
      @Nullable Consumer<PoseStack> transformation) {
    super(renderer);
    this.slot = slot;
    this.useCrouchOrientation = useCrouchOrientation;
    this.useHeadOrientation = useHeadOrientation;
    this.transformation = transformation;
  }

  @Override
  public void render(PoseStack poseStack, ModZombie animatable, BakedGeoModel bakedModel,
      @Nullable RenderType renderType, MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
    animatable.getCapability(LivingExtension.CAPABILITY).ifPresent(living -> {
      ItemStack itemStack = living.getItemInSlot(this.slot);
      if (!itemStack.isEmpty()) {
        var minecraft = Minecraft.getInstance();
        var itemRenderer = minecraft.getItemRenderer();
        var bakedItemModel =
            itemRenderer.getModel(itemStack, animatable.level(), animatable, 0);

        poseStack.pushPose();

        if (this.useCrouchOrientation && animatable.isCrouching()) {
          com.craftingdead.core.client.util.RenderUtil.applyPlayerCrouchRotation(poseStack);
        }

        if (this.useHeadOrientation) {
          Optional<GeoBone> bone = bakedModel.getBone("head");
          bone.ifPresent(
              b -> software.bernie.geckolib.util.RenderUtils.prepMatrixForBone(poseStack, b));
        }

        if (this.transformation != null) {
          this.transformation.accept(poseStack);
        }

        itemRenderer.render(itemStack, ItemDisplayContext.HEAD, false,
            poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY, bakedItemModel);

        poseStack.popPose();
      }
    });
  }

  public static Builder builder(GeoRenderer<ModZombie> renderer) {
    return new Builder(renderer);
  }

  public static class Builder {

    private final GeoRenderer<ModZombie> renderer;
    private Equipment.Slot slot;
    private boolean useCrouchOrientation;
    private boolean useHeadOrientation;
    @Nullable
    private Consumer<PoseStack> transformation;

    private Builder(GeoRenderer<ModZombie> renderer) {
      this.renderer = renderer;
    }

    public Builder slot(Equipment.Slot slot) {
      this.slot = slot;
      return this;
    }

    public Builder useCrouchOrientation(boolean useCrouchOrientation) {
      this.useCrouchOrientation = useCrouchOrientation;
      return this;
    }

    public Builder useHeadOrientation(boolean useHeadOrientation) {
      this.useHeadOrientation = useHeadOrientation;
      return this;
    }

    public Builder transformation(Consumer<PoseStack> transformation) {
      this.transformation = transformation;
      return this;
    }

    public GeoEquipmentLayer build() {
      Objects.requireNonNull(this.slot, "The slot must not be null");
      return new GeoEquipmentLayer(this.renderer, this.slot, this.useCrouchOrientation,
          this.useHeadOrientation, this.transformation);
    }
  }
}
