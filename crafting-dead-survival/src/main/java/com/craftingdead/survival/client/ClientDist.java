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

package com.craftingdead.survival.client;

import com.craftingdead.core.CraftingDead;
import com.craftingdead.core.client.ClientConfig;
import com.craftingdead.core.client.renderer.entity.grenade.GrenadeRenderer;
import com.craftingdead.core.client.util.RenderUtil;
import com.craftingdead.core.world.effect.ModMobEffects;
import com.craftingdead.survival.CraftingDeadSurvival;
import com.craftingdead.survival.ModDist;
import com.craftingdead.survival.client.gui.MoonHudOverlay;
import com.craftingdead.survival.client.model.PipeBombModel;
import com.craftingdead.survival.client.model.SupplyDropModel;
import com.craftingdead.survival.client.model.geom.SurvivalModelLayers;
import com.craftingdead.survival.client.renderer.entity.SupplyDropRenderer;
import com.craftingdead.survival.client.renderer.entity.HomingBigArrowRenderer;
import com.craftingdead.survival.client.renderer.entity.VanillaZombieGeoRenderer;
import com.craftingdead.survival.client.renderer.entity.ZombieGeoRenderer;
import com.craftingdead.survival.client.renderer.entity.layers.GeoParachuteLayer;
// import com.craftingdead.survival.client.sound.MovementSoundAmplifier; // TODO: Fix API compatibility
import com.craftingdead.survival.particles.SurvivalParticleTypes;
import com.craftingdead.survival.world.entity.SurvivalEntityTypes;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
public class ClientDist implements ModDist {

  public static final ClientConfig clientConfig;
  public static final ForgeConfigSpec clientConfigSpec;

  static {
    var clientConfigPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
    clientConfigSpec = clientConfigPair.getRight();
    clientConfig = clientConfigPair.getLeft();
  }

  private static final ResourceLocation BLOOD =
      new ResourceLocation(CraftingDeadSurvival.ID, "textures/gui/blood.png");
  private static final ResourceLocation BLOOD_2 =
      new ResourceLocation(CraftingDeadSurvival.ID, "textures/gui/blood_2.png");

  private final Minecraft minecraft;
  private final MoonHudOverlay moonHudOverlay;

  public ClientDist(FMLJavaModLoadingContext context) {
    this.minecraft = Minecraft.getInstance();
    this.moonHudOverlay = new MoonHudOverlay(this.minecraft);
    final IEventBus modEventBus = context.getModEventBus();
    modEventBus.addListener(this::handleEntityRenderers);
    modEventBus.addListener(this::handleEntityRenderersAddLayers);
    modEventBus.addListener(this::handleParticleFactoryRegisterEvent);
    modEventBus.addListener(this::handleEntityRenderersLayerDefinitions);

    context.registerConfig(ModConfig.Type.CLIENT, clientConfigSpec);

    // Register event handlers to Forge event bus
    MinecraftForge.EVENT_BUS.register(this);
    // Register item frame gun interaction handler for shop displays
    MinecraftForge.EVENT_BUS.register(new ItemFrameGunInteractionHandler());
    // TODO: Re-enable once MovementSoundAmplifier is fixed for Forge 1.18.2 API
    // Register CSGO-style movement sound amplifier for tactical awareness gameplay
    // MinecraftForge.EVENT_BUS.register(new MovementSoundAmplifier());
  }

  private void handleEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerEntityRenderer(SurvivalEntityTypes.PIPE_BOMB.get(),
        context -> new GrenadeRenderer(context,
            context.bakeLayer(SurvivalModelLayers.PIPE_BOMB)));
    event.registerEntityRenderer(SurvivalEntityTypes.SUPPLY_DROP.get(),
        SupplyDropRenderer::new);
    event.registerEntityRenderer(SurvivalEntityTypes.HOMING_BIG_ARROW.get(),
        HomingBigArrowRenderer::new);
    // 所有僵尸使用 GeckoLib 人形模型渲染器（共享模型+动画，按类型用不同贴图）
    // 原版僵尸使用 GeoReplacedEntityRenderer 替换渲染
    event.registerEntityRenderer(EntityType.ZOMBIE,
        VanillaZombieGeoRenderer::new);
    event.registerEntityRenderer(SurvivalEntityTypes.FAST_ZOMBIE.get(),
        ZombieGeoRenderer::new);
    event.registerEntityRenderer(SurvivalEntityTypes.TANK_ZOMBIE.get(),
        ZombieGeoRenderer::new);
    event.registerEntityRenderer(SurvivalEntityTypes.WEAK_ZOMBIE.get(),
        ZombieGeoRenderer::new);
    event.registerEntityRenderer(SurvivalEntityTypes.POLICE_ZOMBIE.get(),
        ZombieGeoRenderer::new);
    event.registerEntityRenderer(SurvivalEntityTypes.DOCTOR_ZOMBIE.get(),
        ZombieGeoRenderer::new);
    event.registerEntityRenderer(SurvivalEntityTypes.GIANT_ZOMBIE.get(),
        context -> new ZombieGeoRenderer(context, 6.0F));
    event.registerEntityRenderer(SurvivalEntityTypes.SCOUT_ZOMBIE.get(),
        ZombieGeoRenderer::new);
    event.registerEntityRenderer(SurvivalEntityTypes.SNIPER_ZOMBIE.get(),
        ZombieGeoRenderer::new);
    event.registerEntityRenderer(SurvivalEntityTypes.PILOT_ZOMBIE.get(),
        ZombieGeoRenderer::new);
    event.registerEntityRenderer(SurvivalEntityTypes.SOLDIER_ZOMBIE.get(),
        ZombieGeoRenderer::new);
    event.registerEntityRenderer(SurvivalEntityTypes.NINJA_ZOMBIE.get(),
        ZombieGeoRenderer::new);
    event.registerEntityRenderer(SurvivalEntityTypes.ALFA_ZOMBIE.get(),
        ZombieGeoRenderer::new);
    event.registerEntityRenderer(SurvivalEntityTypes.BOUNTY_HUNTER_ZOMBIE.get(),
        ZombieGeoRenderer::new);
    event.registerEntityRenderer(SurvivalEntityTypes.DESERT_RAIDER_ZOMBIE.get(),
        ZombieGeoRenderer::new);
    event.registerEntityRenderer(SurvivalEntityTypes.FIREFIGHTER_ZOMBIE.get(),
        ZombieGeoRenderer::new);
    event.registerEntityRenderer(SurvivalEntityTypes.HAZMAT_ZOMBIE.get(),
        ZombieGeoRenderer::new);
    event.registerEntityRenderer(SurvivalEntityTypes.JUGGERNAUT_ZOMBIE.get(),
        ZombieGeoRenderer::new);
    event.registerEntityRenderer(SurvivalEntityTypes.MINER_ZOMBIE.get(),
        ZombieGeoRenderer::new);
    event.registerEntityRenderer(SurvivalEntityTypes.SWAT_ZOMBIE.get(),
        ZombieGeoRenderer::new);
  }

  private void handleEntityRenderersAddLayers(EntityRenderersEvent.AddLayers event) {
    // 僵尸使用 GeckoLib 渲染器(非 LivingEntityRenderer)，降落伞层已在 ZombieGeoRenderer 构造器中注册
  }

  private void handleEntityRenderersLayerDefinitions(
      EntityRenderersEvent.RegisterLayerDefinitions event) {
    event.registerLayerDefinition(SurvivalModelLayers.SUPPLY_DROP,
        SupplyDropModel::createBodyLayer);
    event.registerLayerDefinition(SurvivalModelLayers.PIPE_BOMB,
        PipeBombModel::createBodyLayer);
  }

  private void handleParticleFactoryRegisterEvent(RegisterParticleProvidersEvent event) {
    final var particleEngine = this.minecraft.particleEngine;
    particleEngine.register(SurvivalParticleTypes.MILITARY_LOOT_GEN.get(),
        SpellParticle.Provider::new);
    particleEngine.register(SurvivalParticleTypes.MEDIC_LOOT_GEN.get(),
        SpellParticle.Provider::new);
    particleEngine.register(SurvivalParticleTypes.CIVILIAN_LOOT_GEN.get(),
        SpellParticle.Provider::new);
    particleEngine.register(SurvivalParticleTypes.CIVILIAN_RARE_LOOT_GEN.get(),
        SpellParticle.Provider::new);
    particleEngine.register(SurvivalParticleTypes.POLICE_LOOT_GEN.get(),
        SpellParticle.Provider::new);
  }

  @SubscribeEvent
  public void handleRenderGameOverlayPre(RenderGuiOverlayEvent.Pre event) {
    var player = CraftingDead.getInstance().getClientDist().getCameraPlayer();
    if (player == null) {
      return;
    }

    // Only draw in survival
    if (this.minecraft.gameMode.canHurtPlayer() && !player.isCombatModeEnabled()) {
      float healthPercentage =
          player.entity().getHealth() / player.entity().getMaxHealth();
      if (clientConfig.displayBlood.get() && healthPercentage < 1.0F
          && player.entity().hasEffect(ModMobEffects.BLEEDING.get())) {
        renderBlood(event.getWindow().getGuiScaledWidth(),
            event.getWindow().getGuiScaledHeight(), healthPercentage);
      }
    }
  }

  private static void renderBlood(int width, int height, float healthPercentage) {
    RenderSystem.enableBlend();
    RenderSystem.setShaderTexture(0, healthPercentage <= 0.25F ? BLOOD_2 : BLOOD);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1 - healthPercentage);
    RenderUtil.blit(0, 0, width, height);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.disableBlend();
  }

  @SubscribeEvent
  public void handleRenderGuiPost(RenderGuiEvent.Post event) {
    this.moonHudOverlay.render(event.getGuiGraphics(),
        event.getWindow().getGuiScaledWidth(),
        event.getWindow().getGuiScaledHeight(),
        event.getPartialTick());
  }
}
