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

package com.craftingdead.core.client.util;

import java.util.List;
import java.util.Random;
import org.jetbrains.annotations.Nullable;
import com.craftingdead.core.CraftingDead;
import com.craftingdead.core.client.renderer.item.CombatSlotItemRenderer;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import com.mojang.math.Transformation;
import com.mojang.math.Axis;
import com.mojang.math.MatrixUtil;
import org.joml.Vector3f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.model.data.ModelData;

public class RenderUtil {

  public static final Codec<Vector3f> VECTOR3F_CODEC = RecordCodecBuilder.create(instance -> instance.group(
      Codec.FLOAT.fieldOf("x").forGetter(v -> v.x()),
      Codec.FLOAT.fieldOf("y").forGetter(v -> v.y()),
      Codec.FLOAT.fieldOf("z").forGetter(v -> v.z()))
      .apply(instance, Vector3f::new));

  public static final Codec<Quaternionf> QUATERNION_CODEC = VECTOR3F_CODEC
      .xmap(RenderUtil::fromXYZDegrees, RenderUtil::toXYZDegrees);

  private static Quaternionf fromXYZDegrees(Vector3f degrees) {
    return new Quaternionf().rotationYXZ(
        (float) Math.toRadians(degrees.y()),
        (float) Math.toRadians(degrees.x()),
        (float) Math.toRadians(degrees.z()));
  }

  private static Vector3f toXYZDegrees(Quaternionf q) {
    Vector3f radians = q.getEulerAnglesXYZ(new Vector3f());
    return new Vector3f(
        (float) Math.toDegrees(radians.x()),
        (float) Math.toDegrees(radians.y()),
        (float) Math.toDegrees(radians.z()));
  }

  public static final Codec<Transformation> TRANSFORMATION_MATRIX_CODEC =
      RecordCodecBuilder.create(instance -> instance
          .group(
              VECTOR3F_CODEC
                  .optionalFieldOf("translation", new Vector3f())
                  .xmap(vec -> {
                    var scaledVec = new Vector3f(vec);
                    scaledVec.mul(1.0F / 16.0F);
                    return scaledVec;
                  }, scaledVec -> {
                    var vec = new Vector3f(scaledVec);
                    vec.mul(16.0F);
                    return vec;
                  })
                  .forGetter(Transformation::getTranslation),
              QUATERNION_CODEC.optionalFieldOf("rotation", new Quaternionf())
                  .forGetter(Transformation::getLeftRotation),
              VECTOR3F_CODEC.optionalFieldOf("scale", new Vector3f(1.0F, 1.0F, 1.0F))
                  .forGetter(Transformation::getScale),
              QUATERNION_CODEC.optionalFieldOf("post-rotation", new Quaternionf())
                  .forGetter(Transformation::getRightRotation))
          .apply(instance, Transformation::new));

  public static final int FULL_LIGHT = 0xF000F0;

  private static final Minecraft minecraft = Minecraft.getInstance();

  public static void updateUniform(String name, float value, PostChain postChain) {
    for (var pass : postChain.passes) {
      pass.getEffect().safeGetUniform(name).set(value);
    }
  }

  public static void updateUniform(String name, float[] value, PostChain postChain) {
    for (var pass : postChain.passes) {
      pass.getEffect().safeGetUniform(name).set(value);
    }
  }

  @Nullable
  public static Vec2 projectToPlayerView(double x, double y, double z, float partialTicks) {
    final var activeRenderInfo = minecraft.gameRenderer.getMainCamera();
    final var cameraPos = activeRenderInfo.getPosition();
    final var cameraRotation = new Quaternionf(activeRenderInfo.rotation());
    cameraRotation.conjugate();

    final var result = new Vector3f(
        (float) (cameraPos.x - x),
        (float) (cameraPos.y - y),
        (float) (cameraPos.z - z));

    result.rotate(cameraRotation);

    if (minecraft.options.bobView().get()) {
      final var renderViewEntity = minecraft.getCameraEntity();
      if (renderViewEntity instanceof Player player) {
        final var distanceWalkedModified = player.walkDist;

        final var changeInDistance = distanceWalkedModified - player.walkDistO;
        final var lerpDistance = -(distanceWalkedModified + changeInDistance * partialTicks);
        final var lerpYaw = Mth.lerp(partialTicks, player.oBob, player.bob);
        final var q2 = Axis.XP.rotationDegrees(
            Math.abs(Mth.cos(lerpDistance * (float) Math.PI - 0.2F) * lerpYaw) * 5.0F);
        q2.conjugate();
        result.rotate(q2);

        final var q1 = Axis.ZP.rotationDegrees(
            Mth.sin(lerpDistance * (float) Math.PI) * lerpYaw * 3.0F);
        q1.conjugate();
        result.rotate(q1);

        var bobTranslation = new Vector3f(
            Mth.sin(lerpDistance * (float) Math.PI) * lerpYaw * 0.5F,
            -Math.abs(Mth.cos(lerpDistance * (float) Math.PI) * lerpYaw),
            0.0F);
        bobTranslation.y = -bobTranslation.y; // this is weird but hey, if it works
        result.add(bobTranslation);
      }
    }

    final var fov = minecraft.gameRenderer.getFov(activeRenderInfo, partialTicks, true);
    final var halfHeight = minecraft.getWindow().getGuiScaledHeight() / 2.0F;
    final var scale = halfHeight / (result.z() * (float) Math.tan(Math.toRadians(fov / 2.0D)));
    return result.z() > 0.0D
        ? null
        : new Vec2(-result.x() * scale, result.y() * scale);
  }

  /**
   * Checks whether the entity is inside the viewing frustum. The size of the game window is also
   * considered. Blocks in front of player's view are not considered.
   *
   * @param target - the entity to test for
   * @return <code>true</code> if the entity is visible, <code>false</code> otherwise.
   */
  public static boolean isInsideFrustum(Entity target, boolean firstPerson) {
    final var camera = minecraft.gameRenderer.getMainCamera();
    final var projectedViewVec3d = firstPerson
        ? target.position().add(0, target.getEyeHeight(), 0)
        : camera.getPosition();
    final var viewerX = projectedViewVec3d.x();
    final var viewerY = projectedViewVec3d.y();
    final var viewerZ = projectedViewVec3d.z();

    if (!target.shouldRender(viewerX, viewerY, viewerZ)) {
      return false;
    }

    var renderBoundingBox = target.getBoundingBoxForCulling();

    // Validation from Vanilla.
    // Generates a render bounding box if it is needed.
    if (renderBoundingBox.hasNaN() || renderBoundingBox.getSize() == 0.0D) {
      renderBoundingBox = new AABB(
          target.getX() - 2.0D,
          target.getY() - 2.0D,
          target.getZ() - 2.0D,
          target.getX() + 2.0D,
          target.getY() + 2.0D,
          target.getZ() + 2.0D);
    }

    return RenderUtil.createFrustum(1.0F, firstPerson).isVisible(renderBoundingBox);
  }

  public static Frustum createFrustum(float partialTicks, boolean firstPerson) {
    final var gameRenderer = minecraft.gameRenderer;
    final var player = minecraft.player;
    final var camera = gameRenderer.getMainCamera();
    final var projectedViewVec3d = firstPerson
        ? player.position().add(0, player.getEyeHeight(), 0)
        : camera.getPosition();
    final var viewerX = projectedViewVec3d.x();
    final var viewerY = projectedViewVec3d.y();
    final var viewerZ = projectedViewVec3d.z();
    final var pitch = firstPerson ? player.getXRot() : camera.getXRot();
    final var yaw = firstPerson ? player.getYRot() : camera.getYRot();

    final var poseStack = new PoseStack();
    // As of writing this, Camera does not contain a roll
    // cameraRotationStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(rollHere));
    poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
    poseStack.mulPose(Axis.YP.rotationDegrees(yaw + 180.0F));

    final var projectionMatrix = new Matrix4f();
    projectionMatrix.identity();

    projectionMatrix.mul(
        gameRenderer.getProjectionMatrix(gameRenderer.getFov(camera, partialTicks, true)));

    final var frustum = new Frustum(poseStack.last().pose(), projectionMatrix);
    frustum.prepare(viewerX, viewerY, viewerZ);

    return frustum;
  }

  public static void fill(PoseStack poseStack, int x, int y, int width, int height, int colour) {
    int x2 = x + width;
    int y2 = y + height;
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    RenderSystem.setShader(GameRenderer::getPositionColorShader);

    var alpha = (colour >> 24 & 255) / 255.0F;
    var red = (colour >> 16 & 255) / 255.0F;
    var green = (colour >> 8 & 255) / 255.0F;
    var blue = (colour & 255) / 255.0F;

    var pose = poseStack.last().pose();
    var tessellator = Tesselator.getInstance();
    var builder = tessellator.getBuilder();
    builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
    builder.vertex(pose, x, y, 0.0F).color(red, green, blue, alpha).endVertex();
    builder.vertex(pose, x, y2, 0.0F).color(red, green, blue, alpha).endVertex();
    builder.vertex(pose, x2, y2, 0.0F).color(red, green, blue, alpha).endVertex();
    builder.vertex(pose, x2, y, 0.0F).color(red, green, blue, alpha).endVertex();
    tessellator.end();

    RenderSystem.disableBlend();
  }

  public static void fillGradient(PoseStack poseStack, float x, float y, float x2, float y2,
      int startColor, int endColor) {
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    RenderSystem.setShader(GameRenderer::getPositionColorShader);

    var startAlpha = (startColor >> 24 & 255) / 255.0F;
    var startRed = (startColor >> 16 & 255) / 255.0F;
    var startGreen = (startColor >> 8 & 255) / 255.0F;
    var startBlue = (startColor & 255) / 255.0F;

    var endAlpha = (endColor >> 24 & 255) / 255.0F;
    var endRed = (endColor >> 16 & 255) / 255.0F;
    var endGreen = (endColor >> 8 & 255) / 255.0F;
    var endBlue = (endColor & 255) / 255.0F;

    var pose = poseStack.last().pose();

    var tessellator = Tesselator.getInstance();
    var builder = tessellator.getBuilder();
    builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
    builder
        .vertex(pose, x, y2, 0.0F)
        .color(startRed, startGreen, startBlue, startAlpha)
        .endVertex();
    builder
        .vertex(pose, x2, y2, 0.0F)
        .color(endRed, endGreen, endBlue, endAlpha)
        .endVertex();
    builder
        .vertex(pose, x2, y, 0.0F)
        .color(endRed, endGreen, endBlue, endAlpha)
        .endVertex();
    builder
        .vertex(pose, x, y, 0.0F)
        .color(startRed, startGreen, startBlue, startAlpha)
        .endVertex();
    tessellator.end();

    RenderSystem.disableBlend();
  }

  /**
   * Applies the same body rotation of a vanilla crouching player.
   */
  public static void applyPlayerCrouchRotation(PoseStack matrix) {
    // Fix X rotation
    matrix.mulPose(Axis.XP.rotation(0.5F));

    // Fix XYZ position
    matrix.translate(0F, 0.2F, -0.1F);
  }

  public static void blit(float x, float y, float width, float height,
      float textureX, float textureY) {
    blit(x, y, x + width, y + height, textureX, textureY, textureX + width,
        textureY + height, 256, 256);
  }

  public static void blit(float x, float y, float x2, float y2, float textureX,
      float textureY, float textureX2, float textureY2, float width, float height) {
    float u = textureX / width;
    float u2 = textureX2 / width;
    float v = textureY / height;
    float v2 = textureY2 / height;
    blit(x, y, x2, y2, u, v, u2, v2);
  }

  public static void blit(float x, float y, float width, float height) {
    blit(x, y, x + width, y + height, 0.0F, 1.0F, 1.0F, 0.0F);
  }

  public static void blit(float x, float y, float x2, float y2, float u,
      float v, float u2, float v2) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    final var tessellator = Tesselator.getInstance();
    final var builder = tessellator.getBuilder();
    builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
    builder.vertex(x, y2, 0.0D).uv(u, v).endVertex();
    builder.vertex(x2, y2, 0.0D).uv(u2, v).endVertex();
    builder.vertex(x2, y, 0.0D).uv(u2, v2).endVertex();
    builder.vertex(x, y, 0.0D).uv(u, v2).endVertex();
    tessellator.end();
  }

  public static float getFitScale(final float imageWidth, final float imageHeight) {
    final var window = minecraft.getWindow();
    final var widthScale = window.getScreenWidth() / imageWidth;
    final var heightScale = window.getScreenHeight() / imageHeight;
    final var scale = imageHeight * widthScale < window.getScreenHeight()
        ? heightScale
        : widthScale;
    return scale / (float) minecraft.getWindow().getGuiScale();
  }

  public static void renderItemInCombatSlot(ItemStack itemStack, int x, int y, PoseStack poseStack,
      float partialTick) {
    poseStack.pushPose();
    {
      var halfItemWidth = 8.0F;
      poseStack.translate(x - halfItemWidth, y + halfItemWidth, 0);

      var renderer = CraftingDead.getInstance().getClientDist().getItemRendererManager()
          .getItemRenderer(itemStack.getItem());
      if (renderer instanceof CombatSlotItemRenderer combatSlotRenderer) {
        RenderUtil.setupItemRendering(poseStack);
        Lighting.setupForFlatItems();
        var bufferSource = minecraft.renderBuffers().bufferSource();
        combatSlotRenderer.renderInCombatSlot(itemStack, poseStack, partialTick,
            bufferSource, RenderUtil.FULL_LIGHT,
            OverlayTexture.NO_OVERLAY);
        bufferSource.endBatch();
        Lighting.setupFor3DItems();
      } else {
        poseStack.translate(-8, -4, 0);
        RenderUtil.renderGuiItem(poseStack, itemStack, 1.0F,
            ItemDisplayContext.GUI);
      }
    }
    poseStack.popPose();
  }

  public static void renderGuiItem(PoseStack poseStack, ItemStack itemStack, float alpha) {
    renderGuiItem(poseStack, itemStack, alpha,
        minecraft.getItemRenderer().getModel(itemStack, null, null, 0),
        ItemDisplayContext.GUI);
  }

  public static void renderGuiItem(PoseStack poseStack, ItemStack itemStack,
      float alpha, ItemDisplayContext transformType) {
    renderGuiItem(poseStack, itemStack, alpha,
        minecraft.getItemRenderer().getModel(itemStack, null, null, 0), transformType);
  }

  @SuppressWarnings("deprecation")
  public static void setupItemRendering(PoseStack poseStack) {
    minecraft.textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
    RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
    RenderSystem.enableBlend();
    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
        GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

    poseStack.translate(0, 0, 100.0F);
    poseStack.scale(1.0F, -1.0F, 1.0F);
    poseStack.scale(16.0F, 16.0F, 16.0F);
  }

  /**
   * Copied from {@link ItemRenderer#renderGuiItem} with the ability to customise the color.
   */
  public static void renderGuiItem(PoseStack poseStack, ItemStack itemStack,
      float alpha, BakedModel bakedmodel, ItemDisplayContext transformType) {
    poseStack.pushPose();
    {
      poseStack.translate(8.0D, 8.0D, 0.0D);
      setupItemRendering(poseStack);

      var flatLighting = !bakedmodel.usesBlockLight();
      if (flatLighting) {
        Lighting.setupForFlatItems();
      }

      var bufferSource = minecraft.renderBuffers().bufferSource();
      if (alpha < 1.0F) {
        render(itemStack, transformType, false, poseStack,
            bufferSource, alpha, FULL_LIGHT, OverlayTexture.NO_OVERLAY, bakedmodel);
      } else {
        minecraft.getItemRenderer().render(itemStack, transformType, false, poseStack,
            bufferSource, FULL_LIGHT, OverlayTexture.NO_OVERLAY, bakedmodel);
      }
      bufferSource.endBatch();

      RenderSystem.enableDepthTest();
      if (flatLighting) {
        Lighting.setupFor3DItems();
      }
    }
    poseStack.popPose();
  }

  /**
   * Copied from
   * {@link ItemRenderer#render(ItemStack, net.minecraft.client.renderer.block.model.ItemDisplayContext, boolean, PoseStack, MultiBufferSource, int, int, BakedModel)
   * with the ability to customise the alpha.
   */
  public static void render(ItemStack itemStack, ItemDisplayContext transformType,
      boolean leftHanded, PoseStack poseStack, MultiBufferSource bufferSource, float alpha,
      int packedLight, int packedOverlay, BakedModel bakedModel) {
    if (itemStack.isEmpty()) {
      return;
    }

    if (itemStack.is(Items.TRIDENT)) {
      bakedModel = minecraft.getItemRenderer().getItemModelShaper().getModelManager()
          .getModel(new ModelResourceLocation(ResourceLocation.parse("minecraft:trident"), "inventory"));
    } else if (itemStack.is(Items.SPYGLASS)) {
      bakedModel = minecraft.getItemRenderer().getItemModelShaper().getModelManager()
          .getModel(new ModelResourceLocation(ResourceLocation.parse("minecraft:spyglass"), "inventory"));
    }

    poseStack.pushPose();
    {
      bakedModel =
          ForgeHooksClient.handleCameraTransforms(poseStack, bakedModel, transformType, leftHanded);
      poseStack.translate(-0.5D, -0.5D, -0.5D);
      if (!bakedModel.isCustomRenderer()) {
          var renderType = ItemBlockRenderTypes.getRenderType(itemStack, true);
          VertexConsumer vertexConsumer;
          if (itemStack.is(Items.COMPASS) && itemStack.hasFoil()) {
            poseStack.pushPose();
            {
              var pose = poseStack.last();
              MatrixUtil.mulComponentWise(pose.pose(), 0.5F);
              vertexConsumer =
                  ItemRenderer.getCompassFoilBufferDirect(bufferSource, renderType, pose);
            }
            poseStack.popPose();
          } else {
            vertexConsumer =
                ItemRenderer.getFoilBufferDirect(bufferSource, renderType, true,
                    itemStack.hasFoil());
          }

          renderModelLists(bakedModel, itemStack, poseStack,
              vertexConsumer, packedLight, packedOverlay, alpha);
      } else {
        IClientItemExtensions.of(itemStack).getCustomRenderer().renderByItem(
            itemStack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
      }
    }
    poseStack.popPose();
  }

  public static void renderModelLists(BakedModel bakedModel, ItemStack itemStack,
      PoseStack poseStack, VertexConsumer vertexConsumer,
      int packedLight, int packedOverlay, float alpha) {
    var seed = 42L;
    for (var direction : Direction.values()) {
      renderQuadList(bakedModel.getQuads(null, direction, RandomSource.create(seed)),
          itemStack, poseStack, vertexConsumer, packedLight, packedOverlay, alpha);
    }

    renderQuadList(bakedModel.getQuads(null, null, RandomSource.create(seed)),
        itemStack, poseStack, vertexConsumer, packedLight, packedOverlay, alpha);
  }


  public static void renderQuadList(List<BakedQuad> bakedQuads, ItemStack itemStack,
      PoseStack poseStack, VertexConsumer vertexConsumer,
      int packedLight, int packedOverlay, float alpha) {
    var notEmpty = !itemStack.isEmpty();
    var pose = poseStack.last();

    for (var bakedQuad : bakedQuads) {
      var color = notEmpty && bakedQuad.isTinted()
          ? minecraft.getItemColors().getColor(itemStack, bakedQuad.getTintIndex())
          : 0xFFFFFF;

      var red = (float) (color >> 16 & 255) / 255.0F;
      var green = (float) (color >> 8 & 255) / 255.0F;
      var blue = (float) (color & 255) / 255.0F;
      vertexConsumer.putBulkData(pose, bakedQuad, red, green, blue, alpha, packedLight,
          packedOverlay, true);
    }
  }
}
