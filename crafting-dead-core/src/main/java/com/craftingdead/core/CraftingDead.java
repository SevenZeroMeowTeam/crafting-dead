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

package com.craftingdead.core;

import com.craftingdead.core.data.tags.ModBlockTagsProvider;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;
import com.craftingdead.core.capability.CapabilityUtil;
import com.craftingdead.core.client.ClientDist;
import com.craftingdead.core.data.guns.GunDataProvider;
import com.craftingdead.core.data.recipes.ModRecipeProvider;
import com.craftingdead.core.data.tags.ModItemTagsProvider;
import com.craftingdead.core.event.CombatPickupEvent;
import com.craftingdead.core.network.NetworkChannel;
import com.craftingdead.core.network.message.play.SyncLivingMessage;
import com.craftingdead.core.network.message.play.SyncProtectionConfigMessage;
import com.craftingdead.core.particle.ModParticleTypes;
import com.craftingdead.core.server.ServerDist;
import com.craftingdead.core.sounds.ModSoundEvents;
import com.craftingdead.core.world.action.ActionTypes;
import com.craftingdead.core.world.effect.ModMobEffects;
import com.craftingdead.core.world.entity.ModEntityTypes;
import com.craftingdead.core.world.entity.extension.BasicLivingExtension;
import com.craftingdead.core.world.entity.extension.LivingExtension;
import com.craftingdead.core.world.entity.extension.PlayerExtension;
import com.craftingdead.core.world.inventory.ModMenuTypes;
import com.craftingdead.core.world.item.ModItems;
import com.craftingdead.core.world.item.combatslot.CombatSlot;
import com.craftingdead.core.world.item.combatslot.CombatSlotProvider;
import com.craftingdead.core.world.item.crafting.ModRecipeSerializers;
import com.craftingdead.core.world.item.equipment.Equipment;
import com.craftingdead.core.world.item.gun.Gun;
import com.craftingdead.core.world.item.gun.GunConfigurations;
import com.craftingdead.core.world.item.gun.GunTriggerPredicates;
import com.craftingdead.core.world.item.gun.ammoprovider.AmmoProviderTypes;
import com.craftingdead.core.world.item.gun.attachment.Attachments;
import com.craftingdead.core.world.item.gun.magazine.Magazine;
import com.craftingdead.core.world.item.gun.skin.Paint;
import com.craftingdead.core.world.item.scope.Scope;
import com.craftingdead.core.trauma.ProtectionConfig;
import com.craftingdead.core.event.LivingExtensionEvent;
import com.craftingdead.core.world.entity.extension.ClothingProtectionHandler;
import com.mojang.logging.LogUtils;
import io.netty.buffer.Unpooled;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(CraftingDead.ID)
public class CraftingDead {

  public static final String ID = "craftingdead";

  public static final String VERSION = net.neoforged.fml.ModList.get()
      .getModContainerById(ID)
      .map(container -> container.getModInfo().getVersion().toString())
      .orElse("[version]");

  /**
   * Logger.
   */
  private static final Logger logger = LogUtils.getLogger();

  /**
   * Singleton.
   */
  private static CraftingDead instance;

  /**
   * Mod distribution.
   */
  private final ModDist modDist;

  public CraftingDead(IEventBus modEventBus) {
    instance = this;

    if (FMLEnvironment.dist.isClient()) {
      this.modDist = new ClientDist(modEventBus);
    } else {
      this.modDist = new ServerDist();
    }

    modEventBus.addListener(this::handleCommonSetup);
    modEventBus.addListener(this::handleGatherData);
    modEventBus.addListener(this::handleRegisterCapabilities);
    modEventBus.addListener(this::handleConfigLoading);
    modEventBus.addListener(this::handleConfigReloading);
    modEventBus.addListener(this::handleRegisterPayloads);

    ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.COMMON, CommonConfig.configSpec);
    ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.SERVER, ServerConfig.configSpec);

    ModEntityTypes.deferredRegister.register(modEventBus);
    ModItems.deferredRegister.register(modEventBus);
    ModItems.CREATIVE_MODE_TABS.register(modEventBus);
    ModSoundEvents.deferredRegister.register(modEventBus);
    ModMenuTypes.deferredRegister.register(modEventBus);
    ModMobEffects.deferredRegister.register(modEventBus);
    ModParticleTypes.deferredRegister.register(modEventBus);
    ModRecipeSerializers.deferredRegister.register(modEventBus);

    // Custom registries
    ActionTypes.deferredRegister.register(modEventBus);
    AmmoProviderTypes.deferredRegister.register(modEventBus);
    Attachments.deferredRegister.register(modEventBus);
    GunConfigurations.deferredRegister.register(modEventBus);
    GunTriggerPredicates.deferredRegister.register(modEventBus);

    NeoForge.EVENT_BUS.register(this);
    NeoForge.EVENT_BUS.addListener(this::handleRegisterBrewingRecipes);
//    NeoForge.EVENT_BUS.register(TraumaHandler.INSTANCE);

    // 注册质量事件处理器（每 50 tick / server 端）
    NeoForge.EVENT_BUS.register(com.craftingdead.core.quality.QualityEventHandler.INSTANCE);

    ProtectionConfig.load();
  }

  public ModDist getModDist() {
    return this.modDist;
  }

  public ClientDist getClientDist() {
    if (this.modDist instanceof ClientDist clientDist) {
      return clientDist;
    }
    throw new IllegalStateException("Accessing client dist on wrong side");
  }

  public static CraftingDead getInstance() {
    return instance;
  }

  // ================================================================================
  // Mod Events
  // ================================================================================

  private void handleCommonSetup(FMLCommonSetupEvent event) {
    logger.info("Starting Crafting Dead, version {}", VERSION);
    // TelemetryManager.initialize(ID, VERSION, Optional::empty, null,
    //     scope -> scope.setTag("craftingdead.version", VERSION));
    // Sentry telemetry disabled - dependency not bundled
  }

  private void handleRegisterBrewingRecipes(RegisterBrewingRecipesEvent event) {
    final ItemStack syringe = ModItems.SYRINGE.get().getDefaultInstance();
    final ItemStack adrenaline = ModItems.ADRENALINE_SYRINGE.get().getDefaultInstance();
    final Ingredient redstone = Ingredient.of(Items.REDSTONE);
    event.getBuilder().addRecipe(new net.neoforged.neoforge.common.brewing.IBrewingRecipe() {
      @Override
      public boolean isInput(ItemStack input) {
        return ItemStack.isSameItemSameComponents(input, syringe);
      }

      @Override
      public boolean isIngredient(ItemStack ingredient) {
        return redstone.test(ingredient);
      }

      @Override
      public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        return isInput(input) && isIngredient(ingredient) ? adrenaline.copy() : ItemStack.EMPTY;
      }
    });
  }

  private void handleGatherData(GatherDataEvent event) {
    DataGenerator dataGenerator = event.getGenerator();
    PackOutput packOutput = dataGenerator.getPackOutput();
    CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
    if (event.includeServer()) {
      var blockTagsProvider = new ModBlockTagsProvider(packOutput, lookupProvider,
          event.getExistingFileHelper());
      dataGenerator.addProvider(true, blockTagsProvider);
      dataGenerator.addProvider(true, new ModItemTagsProvider(packOutput, lookupProvider,
          blockTagsProvider.contentsGetter(), event.getExistingFileHelper()));
      dataGenerator.addProvider(true, new ModRecipeProvider(packOutput, lookupProvider));
      dataGenerator.addProvider(true, new GunDataProvider(packOutput));
    }
  }

  private void handleRegisterCapabilities(RegisterCapabilitiesEvent event) {
    // LivingExtension 实体能力：所有活体生物附加能力（玩家用 PlayerExtension，其他用
    // BasicLivingExtension），与原 Forge 版 AttachCapabilitiesEvent 一致。
    // NeoForge 1.21 迁移时曾丢失 registerEntity 注册，导致 getOrThrow 报
    // "Expecting capability: craftingdead:living_extension"
    for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
      event.registerEntity(LivingExtension.CAPABILITY, entityType, (entity, ctx) -> {
        if (entity instanceof Player player) {
          return PlayerExtension.create(player);
        } else if (entity instanceof LivingEntity livingEntity) {
          return BasicLivingExtension.create(livingEntity);
        }
        return null;
      });
    }
    ModItems.initAbilityProviders(event);
  }

  private void handleRegisterPayloads(RegisterPayloadHandlersEvent event) {
    NetworkChannel.register(event);
  }

  private void handleConfigLoading(ModConfigEvent.Loading event) {
    if (event.getConfig().getModId().equals(ID)) {
      ProtectionConfig.load();
      if (event.getConfig().getType() == ModConfig.Type.SERVER) {
        this.syncProtectionConfigToAllPlayers();
      }
    }
  }

  private void handleConfigReloading(ModConfigEvent.Reloading event) {
    if (event.getConfig().getModId().equals(ID)) {
      ProtectionConfig.load();
      if (event.getConfig().getType() == ModConfig.Type.SERVER) {
        this.syncProtectionConfigToAllPlayers();
      }
    }
  }

  // ================================================================================
  // Common Forge Events
  // ================================================================================

  @SubscribeEvent
  public void handleAttack(AttackEntityEvent event) {
    event.setCanceled(PlayerExtension.getOrThrow((Player) event.getEntity())
        .handleAttack(event.getTarget()));
  }

  @SubscribeEvent
  public void handleInteract(PlayerInteractEvent.EntityInteract event) {
    event.setCanceled(PlayerExtension.getOrThrow((Player) event.getEntity())
        .handleInteract(event.getHand(), event.getTarget()));
  }

  @SubscribeEvent
  public void handlePlayerLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
    event.setCanceled(PlayerExtension.getOrThrow((Player) event.getEntity())
        .handleLeftClickBlock(event.getPos(), event.getFace(), event::setUseBlock,
            event::setUseItem));
  }

  @SubscribeEvent
  public void handlePlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
    event.setCanceled(PlayerExtension.getOrThrow((Player) event.getEntity())
        .handleRightClickBlock(event.getHand(), event.getPos(), event.getFace()));
  }

  @SubscribeEvent
  public void handlePlayerRightClickItem(PlayerInteractEvent.RightClickItem event) {
    event.setCanceled(PlayerExtension.getOrThrow((Player) event.getEntity())
        .handleRightClickItem(event.getHand()));
  }

  @SubscribeEvent
  public void handleEntityItemPickup(ItemEntityPickupEvent.Pre event) {
    var living = event.getPlayer().getCapability(LivingExtension.CAPABILITY);
    if (living instanceof PlayerExtension<?> playerExtension && playerExtension.isCombatModeEnabled()) {
      final ItemStack itemStack = event.getItemEntity().getItem();
      CombatSlot combatSlot = CombatSlot.getSlotType(itemStack).orElse(null);
      CombatPickupEvent combatPickupEvent = new CombatPickupEvent(itemStack, combatSlot);
      NeoForge.EVENT_BUS.post(combatPickupEvent);
      if (combatPickupEvent.isCanceled()) {
        event.setCanPickup(TriState.FALSE);
      } else if (combatSlot != null) {
        if (combatSlot.addToInventory(itemStack, event.getPlayer().getInventory(), false)) {
          // Allows normal processing of item pickup but prevents item being added to inventory
          // because we've already added it.
          event.setCanPickup(TriState.TRUE);
        } else {
          event.setCanPickup(TriState.FALSE);
        }
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void handleLivingSetTarget(LivingChangeTargetEvent event) {
    if (event.getNewAboutToBeSetTarget() != null && event.getEntity() instanceof Mob mob) {
      if (mob.hasEffect(ModMobEffects.FLASH_BLINDNESS)) {
        event.setNewAboutToBeSetTarget(null);
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void handleLivingDeath(LivingDeathEvent event) {
    var deadLiving = event.getEntity().getCapability(LivingExtension.CAPABILITY);
    var sourceEntity = event.getSource().getEntity();
    var killerLiving = sourceEntity != null ? sourceEntity.getCapability(LivingExtension.CAPABILITY) : null;
    if ((deadLiving != null && deadLiving.handleDeath(event.getSource()))
        || (killerLiving != null && killerLiving.handleKill(event.getEntity()))) {
      event.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void handleLivingDrops(LivingDropsEvent event) {
    var living = event.getEntity().getCapability(LivingExtension.CAPABILITY);
    boolean canceled = living != null
        && living.handleDeathLoot(event.getSource(), event.getDrops(), 0);
    event.setCanceled(canceled);
    if (!canceled) {
      scatterDrops(event.getEntity(), event.getDrops());
    }
  }

  /**
   * 将死亡掉落物向四周散开，让掉落物看起来更真实。
   */
  private static void scatterDrops(LivingEntity entity, Collection<ItemEntity> drops) {
    var random = entity.getRandom();
    for (var itemEntity : drops) {
      itemEntity.setDeltaMovement(new Vec3(
          (random.nextDouble() - 0.5D) * 0.7D,
          random.nextDouble() * 0.35D + 0.15D,
          (random.nextDouble() - 0.5D) * 0.7D));
      itemEntity.setDefaultPickUpDelay();
      itemEntity.setYRot(random.nextFloat() * 360.0F);
    }
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void handleLivingAttack(LivingIncomingDamageEvent event) {
    var living = event.getEntity().getCapability(LivingExtension.CAPABILITY);
    if (living != null) {
      event.setCanceled(living.handleHurt(event.getSource(), event.getAmount()));
    }
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void handleLivingDamage(LivingDamageEvent.Pre event) {
    var living = event.getEntity().getCapability(LivingExtension.CAPABILITY);
    if (living != null) {
      event.setNewDamage(living.handleDamaged(event.getSource(), event.getNewDamage()));
    }
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void handleEntityBlockPlace(BlockEvent.EntityPlaceEvent event) {
    if (event.getEntity() != null) {
      var living = event.getEntity().getCapability(LivingExtension.CAPABILITY);
      if (living != null) {
        event.setCanceled(living.handleBlockPlace(event.getBlockSnapshot(), event.getPlacedBlock(),
            event.getPlacedAgainst()));
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void handleEntityBlockMultiPlace(BlockEvent.EntityMultiPlaceEvent event) {
    if (event.getEntity() != null) {
      var living = event.getEntity().getCapability(LivingExtension.CAPABILITY);
      if (living != null) {
        event.setCanceled(living.handleMultiBlockPlace(event.getReplacedBlockSnapshots(),
            event.getPlacedBlock(), event.getPlacedAgainst()));
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void handleEntityBlockBreakEvent(BlockEvent.BreakEvent event) {
    var xp = new MutableInt();
    event.setCanceled(PlayerExtension.getOrThrow(event.getPlayer()).handleBlockBreak(event.getPos(),
        event.getState(), xp));
  }

  @SubscribeEvent
  public void handlePlayerClone(PlayerEvent.Clone event) {
    PlayerExtension.getOrThrow(event.getEntity()).copyFrom(
        PlayerExtension.getOrThrow((ServerPlayer) event.getOriginal()), event.isWasDeath());
  }

  @SubscribeEvent
  public void handleLivingUpdate(EntityTickEvent.Post event) {
    if (!(event.getEntity() instanceof LivingEntity livingEntity)) {
      return;
    }
    var living = livingEntity.getCapability(LivingExtension.CAPABILITY);
    if (living != null) {
      living.tick();
      if (!living.level().isClientSide() && living.requiresSync()) {
        RegistryFriendlyByteBuf data = new RegistryFriendlyByteBuf(
            new FriendlyByteBuf(Unpooled.buffer()), living.level().registryAccess());
        living.encode(data, false);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(living.entity(), new SyncLivingMessage(living.entity().getId(), data));
      }
    }
  }

  @SubscribeEvent
  public void handlePlayerTick(PlayerTickEvent.Post event) {
    var living = event.getEntity().getCapability(LivingExtension.CAPABILITY);
    if (living instanceof PlayerExtension<?> playerExtension) {
      playerExtension.playerTick();
    }
  }

  @SubscribeEvent
  public void handleLivingExtensionLoad(LivingExtensionEvent.Load event) {
    // Register clothing protection handler for all living entities
    @SuppressWarnings({"unchecked", "rawtypes"})
    var living = (LivingExtension) event.getLiving();
    living.registerHandler(
        ClothingProtectionHandler.TYPE,
        new ClothingProtectionHandler(event.getLiving())
    );
  }

  @SubscribeEvent
  public void handlePlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
    RegistryFriendlyByteBuf data = new RegistryFriendlyByteBuf(
        new FriendlyByteBuf(Unpooled.buffer()), event.getEntity().level().registryAccess());
    PlayerExtension.getOrThrow(event.getEntity()).encode(data, true);
    PacketDistributor.sendToPlayersTrackingEntityAndSelf(event.getEntity(), new SyncLivingMessage(event.getEntity().getId(), data));
  }

  @SubscribeEvent
  public void handlePlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
    startTracking(event.getEntity(), (ServerPlayer) event.getEntity());
    this.syncProtectionConfig((ServerPlayer) event.getEntity());
  }

  @SubscribeEvent
  public void handlePlayerStartTracking(PlayerEvent.StartTracking event) {
    startTracking(event.getTarget(), (ServerPlayer) event.getEntity());
  }

  private static void startTracking(Entity targetEntity, ServerPlayer playerEntity) {
    var trackedLiving = targetEntity.getCapability(LivingExtension.CAPABILITY);
    if (trackedLiving != null) {
      trackedLiving.handleStartTracking(playerEntity);
      RegistryFriendlyByteBuf data = new RegistryFriendlyByteBuf(
          new FriendlyByteBuf(Unpooled.buffer()), targetEntity.level().registryAccess());
      trackedLiving.encode(data, true);
      PacketDistributor.sendToPlayer(playerEntity, new SyncLivingMessage(trackedLiving.entity().getId(), data));
    }
  }

  private void syncProtectionConfig(ServerPlayer player) {
    PacketDistributor.sendToPlayer(player, new SyncProtectionConfigMessage(ProtectionConfig.getSerializedConfig()));
  }

  private void syncProtectionConfigToAllPlayers() {
    var server = ServerLifecycleHooks.getCurrentServer();
    if (server == null) {
      return;
    }
    String serializedConfig = ProtectionConfig.getSerializedConfig();
    server.getPlayerList().getPlayers().forEach(player -> PacketDistributor.sendToPlayer(player, new SyncProtectionConfigMessage(serializedConfig)));
  }
}
