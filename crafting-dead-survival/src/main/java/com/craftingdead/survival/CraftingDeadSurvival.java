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

package com.craftingdead.survival;

import com.craftingdead.survival.world.entity.extension.ALFAZombieHandler;
import com.craftingdead.survival.world.item.ConsumableConfigOverrides;
import com.craftingdead.survival.world.entity.extension.BountyHunterZombieHandler;
import com.craftingdead.survival.world.entity.extension.DesertRaiderZombieHandler;
import com.craftingdead.survival.world.entity.extension.FirefighterZombieHandler;
import com.craftingdead.survival.world.entity.extension.HazmatZombieHandler;
import com.craftingdead.survival.world.entity.extension.JuggernautZombieHandler;
import com.craftingdead.survival.world.entity.extension.MinerZombieHandler;
import com.craftingdead.survival.world.entity.extension.NinjaZombieHandler;
import com.craftingdead.survival.world.entity.extension.PilotZombieHandler;
import com.craftingdead.survival.world.entity.extension.ScoutZombieHandler;
import com.craftingdead.survival.world.entity.extension.SniperZombieHandler;
import com.craftingdead.survival.world.entity.extension.SoldierZombieHandler;
import com.craftingdead.survival.world.entity.extension.SwatZombieHandler;
import com.craftingdead.survival.world.entity.monster.NinjaZombieEntity;
import com.craftingdead.survival.world.entity.monster.PilotZombieEntity;
import com.craftingdead.survival.world.entity.monster.ScoutZombieEntity;
import com.craftingdead.survival.world.entity.monster.SniperZombieEntity;
import com.craftingdead.survival.world.entity.monster.SoldierZombieEntity;
import java.util.Optional;
import net.minecraft.util.RandomSource;

import org.slf4j.Logger;

import com.craftingdead.core.event.GunEvent;
import com.craftingdead.core.event.LivingExtensionEvent;
import com.craftingdead.core.world.action.ActionTypes;
import com.craftingdead.core.world.action.item.EntityItemAction;
import com.craftingdead.core.world.entity.extension.BasicLivingExtension;
import com.craftingdead.core.world.entity.extension.LivingExtension;
import com.craftingdead.core.world.entity.extension.PlayerExtension;
import com.craftingdead.core.world.item.ModItems;
import com.craftingdead.core.world.item.equipment.Equipment;
import com.craftingdead.survival.client.ClientDist;
import com.craftingdead.survival.data.SurvivalItemTagsProvider;
import com.craftingdead.survival.data.SurvivalRecipeProvider;
import com.craftingdead.survival.data.loot.SurvivalLootTableProvider;
import com.craftingdead.survival.data.models.SurvivalModelProvider;
import com.craftingdead.survival.particles.SurvivalParticleTypes;
import com.craftingdead.survival.server.ServerDist;
import com.craftingdead.survival.world.action.SurvivalActionTypes;
import com.craftingdead.survival.world.effect.SurvivalMobEffects;
import com.craftingdead.survival.world.entity.SurvivalEntityTypes;
import com.craftingdead.survival.world.entity.SurvivalPlayerHandler;
import com.craftingdead.survival.world.entity.body.BodyPartHandler;
import com.craftingdead.survival.world.entity.extension.DoctorZombieHandler;
import com.craftingdead.survival.world.entity.extension.GiantZombieHandler;
import com.craftingdead.survival.world.entity.extension.PoliceZombieHandler;
import com.craftingdead.survival.world.entity.extension.ZombieHandler;
import com.craftingdead.survival.world.entity.monster.DoctorZombieEntity;
import com.craftingdead.survival.world.entity.monster.FastZombie;
import com.craftingdead.survival.world.entity.monster.GiantZombie;
import com.craftingdead.survival.world.entity.monster.PoliceZombieEntity;
import com.craftingdead.survival.world.entity.monster.TankZombie;
import com.craftingdead.survival.world.entity.monster.WeakZombie;
import com.craftingdead.survival.world.item.SurvivalItems;
import com.craftingdead.survival.world.level.block.SurvivalBlocks;
import com.craftingdead.survival.network.SurvivalNetworkChannel;
import com.craftingdead.survival.world.MoonEventHandler;
import com.craftingdead.survival.world.moon.ApocalypseManager;
import com.craftingdead.survival.world.moon.MoonCommand;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.internal.NeoForgeBlockTagsProvider;
// RegistryEvent was removed in 1.19+
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
// NOTE: BiomeLoadingEvent was removed in 1.19.4+. Zombie spawn modification
// needs to be reimplemented via data-driven BiomeModifier JSON files.
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@Mod(CraftingDeadSurvival.ID)
public class CraftingDeadSurvival {

  public static final String ID = "craftingdeadsurvival";
  public static final String VERSION = ModList.get()
      .getModContainerById(ID)
      .map(container -> container.getModInfo().getVersion().toString())
      .orElse("[version]");

  private static final String H_CD_SERVER_CORE_ID = "hcdservercore";

  private static final Logger logger = LogUtils.getLogger();

  public static final ServerConfig serverConfig;
  public static final ModConfigSpec serverConfigSpec;

  static {
    var pair = new ModConfigSpec.Builder().configure(ServerConfig::new);
    serverConfigSpec = pair.getRight();
    serverConfig = pair.getLeft();
  }

  private static CraftingDeadSurvival instance;

  private final ModDist modDist;

  private final boolean immerseLoaded = ModList.get().isLoaded("craftingdeadimmerse");

  public CraftingDeadSurvival(IEventBus modEventBus) {
    instance = this;

    if (FMLEnvironment.dist.isClient()) {
      this.modDist = new ClientDist(modEventBus);
    } else {
      this.modDist = new ServerDist();
    }

    modEventBus.addListener(this::handleCommonSetup);
    modEventBus.addListener(this::handleEntityAttributeCreation);
    modEventBus.addListener(this::handleGatherData);
    modEventBus.addListener(this::handleSpawnPlacementRegister);
    modEventBus.addListener(this::handleRegisterPayloads);

    ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.SERVER, serverConfigSpec);

    NeoForge.EVENT_BUS.register(this);
    NeoForge.EVENT_BUS.register(new MoonEventHandler());
    // /moon 手动切换月相 / 事件命令
    NeoForge.EVENT_BUS.register(MoonCommand.class);
    // 部位伤害/断肢系统：注册静态 @SubscribeEvent（TaCZ 枪命中处理）
    NeoForge.EVENT_BUS.register(BodyPartHandler.class);

    SurvivalActionTypes.deferredRegister.register(modEventBus);
    SurvivalItems.deferredRegister.register(modEventBus);

    SurvivalItems.CREATIVE_MODE_TABS.register(modEventBus);
    SurvivalMobEffects.deferredRegister.register(modEventBus);
    SurvivalEntityTypes.deferredRegister.register(modEventBus);
    SurvivalParticleTypes.deferredRegister.register(modEventBus);
    SurvivalBlocks.deferredRegister.register(modEventBus);
  }

  public ModDist getModDist() {
    return this.modDist;
  }

  public boolean isImmerseLoaded() {
    return this.immerseLoaded;
  }

  public static CraftingDeadSurvival instance() {
    return instance;
  }

  // ================================================================================
  // Mod Events
  // ================================================================================

  private void handleCommonSetup(FMLCommonSetupEvent event) {
    // TelemetryManager.initialize(ID, VERSION, Optional::empty, scope -> {
    //   scope.setTag("survival.version", VERSION);
    //   scope.setTag("survival.immerseLoaded", String.valueOf(this.isImmerseLoaded()));
    // });
    // Sentry telemetry disabled - dependency not bundled
  }

  private void handleRegisterPayloads(RegisterPayloadHandlersEvent event) {
    SurvivalNetworkChannel.register(event);
  }

  @SubscribeEvent
  public void handleBrewingRecipeRegister(RegisterBrewingRecipesEvent event) {
    final ItemStack syringe = ModItems.SYRINGE.get().getDefaultInstance();
    final ItemStack cure = SurvivalItems.CURE_SYRINGE.get().getDefaultInstance();
    final Ingredient goldenApple = Ingredient.of(Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE);
    event.getBuilder().addRecipe(new net.neoforged.neoforge.common.brewing.IBrewingRecipe() {
      @Override
      public boolean isInput(ItemStack input) {
        return ItemStack.isSameItemSameComponents(input, syringe);
      }

      @Override
      public boolean isIngredient(ItemStack ingredient) {
        return goldenApple.test(ingredient);
      }

      @Override
      public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        return isInput(input) && isIngredient(ingredient) ? cure.copy() : ItemStack.EMPTY;
      }
    });
  }

  private void handleSpawnPlacementRegister(RegisterSpawnPlacementsEvent event) {
    var operation = RegisterSpawnPlacementsEvent.Operation.REPLACE;
    event.register(SurvivalEntityTypes.FAST_ZOMBIE.get(),
        net.minecraft.world.entity.SpawnPlacementTypes.ON_GROUND,
        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
        CraftingDeadSurvival::checkZombieSpawnRules, operation);

    event.register(SurvivalEntityTypes.TANK_ZOMBIE.get(),
        net.minecraft.world.entity.SpawnPlacementTypes.ON_GROUND,
        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
        CraftingDeadSurvival::checkZombieSpawnRules, operation);

    event.register(SurvivalEntityTypes.WEAK_ZOMBIE.get(),
        net.minecraft.world.entity.SpawnPlacementTypes.ON_GROUND,
        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
        CraftingDeadSurvival::checkZombieSpawnRules, operation);
  }

  private void handleEntityAttributeCreation(EntityAttributeCreationEvent event) {
    event.put(SurvivalEntityTypes.DOCTOR_ZOMBIE.get(),
        DoctorZombieEntity.createAttributes().build());
    event.put(SurvivalEntityTypes.FAST_ZOMBIE.get(),
        FastZombie.createAttributes().build());
    event.put(SurvivalEntityTypes.GIANT_ZOMBIE.get(),
        GiantZombie.createAttributes().build());
    event.put(SurvivalEntityTypes.SCOUT_ZOMBIE.get(),
        ScoutZombieEntity.createAttributes().build());
    event.put(SurvivalEntityTypes.SNIPER_ZOMBIE.get(),
        SniperZombieEntity.createAttributes().build());
    event.put(SurvivalEntityTypes.PILOT_ZOMBIE.get(),
        PilotZombieEntity.createAttributes().build());
    event.put(SurvivalEntityTypes.SOLDIER_ZOMBIE.get(),
        SoldierZombieEntity.createAttributes().build());
    event.put(SurvivalEntityTypes.NINJA_ZOMBIE.get(),
        NinjaZombieEntity.createAttributes().build());
    event.put(SurvivalEntityTypes.ALFA_ZOMBIE.get(),
        WeakZombie.createAttributes().build());
    event.put(SurvivalEntityTypes.BOUNTY_HUNTER_ZOMBIE.get(),
        WeakZombie.createAttributes().build());
    event.put(SurvivalEntityTypes.DESERT_RAIDER_ZOMBIE.get(),
        WeakZombie.createAttributes().build());
    event.put(SurvivalEntityTypes.FIREFIGHTER_ZOMBIE.get(),
        WeakZombie.createAttributes().build());
    event.put(SurvivalEntityTypes.HAZMAT_ZOMBIE.get(),
        WeakZombie.createAttributes().build());
    event.put(SurvivalEntityTypes.JUGGERNAUT_ZOMBIE.get(),
        WeakZombie.createAttributes().build());
    event.put(SurvivalEntityTypes.MINER_ZOMBIE.get(),
        WeakZombie.createAttributes().build());
    event.put(SurvivalEntityTypes.SWAT_ZOMBIE.get(),
        WeakZombie.createAttributes().build());
    event.put(SurvivalEntityTypes.POLICE_ZOMBIE.get(),
        PoliceZombieEntity.createAttributes().build());
    event.put(SurvivalEntityTypes.TANK_ZOMBIE.get(),
        TankZombie.createAttributes().build());
    event.put(SurvivalEntityTypes.WEAK_ZOMBIE.get(),
        WeakZombie.createAttributes().build());
  }

  private void handleGatherData(GatherDataEvent event) {
    var generator = event.getGenerator();
    var packOutput = generator.getPackOutput();
    var lookupProvider = event.getLookupProvider();
    var existingFileHelper = event.getExistingFileHelper();
    if (event.includeServer()) {
      var blockTagsProvider = new NeoForgeBlockTagsProvider(packOutput, lookupProvider,
          existingFileHelper);
      generator.addProvider(true, new SurvivalItemTagsProvider(packOutput, lookupProvider,
          blockTagsProvider.contentsGetter(), existingFileHelper));
      generator.addProvider(true, new SurvivalRecipeProvider(packOutput, lookupProvider));
      generator.addProvider(true, new SurvivalLootTableProvider(packOutput, lookupProvider));
    }

    if (event.includeClient()) {
      generator.addProvider(true, new SurvivalModelProvider(packOutput));
    }
  }

  // ================================================================================
  // Common Forge Events
  // ================================================================================

  @SubscribeEvent
  public void handleServerAboutToStart(net.neoforged.neoforge.event.server.ServerAboutToStartEvent event) {
    // Load consumable config overrides on server start
    ConsumableConfigOverrides.loadOverrides();
    logger.info("Loaded consumable configuration overrides");
  }

  @SubscribeEvent
  public void handleSpecialSpawn(FinalizeSpawnEvent event) {
    var level = event.getEntity().level();
    if (!level.isClientSide() && event.getEntity() instanceof Zombie zombie) {

      zombie.getAttribute(Attributes.ATTACK_KNOCKBACK)
          .setBaseValue(serverConfig.zombieAttackKnockback.get());

      if (zombie.getType() == EntityType.ZOMBIE) {
        zombie.getAttribute(Attributes.MAX_HEALTH)
            .setBaseValue(serverConfig.advancedZombieMaxHealth.get());
        zombie.getAttribute(Attributes.ATTACK_DAMAGE)
            .setBaseValue(serverConfig.advancedZombieAttackDamage.get());
      }

      zombie.getAttribute(Attributes.ARMOR)
          .addPermanentModifier(new AttributeModifier(
              ResourceLocation.fromNamespaceAndPath(ID, "armor_bonus"),
              2, AttributeModifier.Operation.ADD_VALUE));

      var extension = LivingExtension.getOrThrow(zombie);

      extension.getHandler(ZombieHandler.TYPE)
          .ifPresentOrElse(ZombieHandler::applyEquipmentDropChances, () -> {
            extension.setEquipmentDropChance(Equipment.Slot.CLOTHING,
                serverConfig.zombieClothingDropChance.get().floatValue());
            extension.setEquipmentDropChance(Equipment.Slot.HAT,
                serverConfig.zombieHatDropChance.get().floatValue());
            extension.setEquipmentDropChance(Equipment.Slot.VEST,
                serverConfig.zombieVestDropChance.get().floatValue());
            extension.setEquipmentDropChance(Equipment.Slot.BACKPACK,
                serverConfig.zombieBackpackDropChance.get().floatValue());
            zombie.setDropChance(EquipmentSlot.MAINHAND,
                serverConfig.zombieHandDropChance.get().floatValue());
            zombie.setDropChance(EquipmentSlot.OFFHAND,
                serverConfig.zombieHandDropChance.get().floatValue());
              }
          );

      // 末日进化：随天数提升僵尸血量 / 攻击 / 速度
      if (serverConfig.zombieEvolutionEnabled.get()) {
        ApocalypseManager.applyZombieEvolution(zombie, level);
      }
      // 血月：僵尸有概率额外进化
      if (serverConfig.moonEventsEnabled.get()
          && ApocalypseManager.isBloodMoon(level)) {
        ApocalypseManager.applyBloodMoonEvolution(zombie, level);
      }
      // 月相强度：月相决定僵尸强弱（满月更强、新月更弱）
      if (serverConfig.moonEventsEnabled.get()) {
        ApocalypseManager.applyMoonPhaseEffects(zombie, level);
      }
      // 进化僵尸：有概率手持物品（进化等级越高、月相越强越容易手持）
      if (serverConfig.zombieEvolutionEnabled.get()) {
        ApocalypseManager.equipEvolvedHeldItem(zombie, level);
      }
    }
  }

  @SubscribeEvent
  public void handlePerformAction(LivingExtensionEvent.PerformAction<EntityItemAction<?>> event) {
    var action = event.getAction();
    var target = action.getSelectedTarget();
    if (!event.getLiving().level().isClientSide()
        && action.type() == ActionTypes.USE_SYRINGE.get()) {
      SurvivalActionTypes.USE_SYRINGE_ON_ZOMBIE.get()
          .createEntityAction(event.getLiving(), target, action.getHand())
          .ifPresent(newAction -> {
            event.setCanceled(true);
            event.getLiving().performAction(newAction, true);
          });
    }
  }

  // TODO: Reimplement missing mappings handling using 1.20.1 API
  // RegistryEvent was removed in 1.19+. Missing mappings are now handled differently.

  @SubscribeEvent
  public void handleAttachLivingExtensions(LivingExtensionEvent.Load event) {
    if (event.getLiving() instanceof PlayerExtension<?> player) {
      player.registerHandler(SurvivalPlayerHandler.TYPE, new SurvivalPlayerHandler(player));
    } else if (event.getLiving().entity() instanceof Zombie zombie) {
      @SuppressWarnings("unchecked")
      var extension = (BasicLivingExtension<Zombie>) event.getLiving();
      ZombieHandler handler;
      if (zombie.getType() == SurvivalEntityTypes.DOCTOR_ZOMBIE.get()) {
        handler = new DoctorZombieHandler(extension);
      } else if (zombie.getType() == SurvivalEntityTypes.GIANT_ZOMBIE.get()) {
        handler = new GiantZombieHandler(extension);
      } else if (zombie.getType() == SurvivalEntityTypes.POLICE_ZOMBIE.get()) {
        handler = new PoliceZombieHandler(extension);
      } else if (zombie.getType() == SurvivalEntityTypes.SCOUT_ZOMBIE.get()) {
        handler = new ScoutZombieHandler(extension);
      } else if (zombie.getType() == SurvivalEntityTypes.SNIPER_ZOMBIE.get()) {
        handler = new SniperZombieHandler(extension);
      } else if (zombie.getType() == SurvivalEntityTypes.PILOT_ZOMBIE.get()) {
        handler = new PilotZombieHandler(extension);
      } else if (zombie.getType() == SurvivalEntityTypes.SOLDIER_ZOMBIE.get()) {
        handler = new SoldierZombieHandler(extension);
      } else if (zombie.getType() == SurvivalEntityTypes.NINJA_ZOMBIE.get()) {
        handler = new NinjaZombieHandler(extension);
      } else if (zombie.getType() == SurvivalEntityTypes.ALFA_ZOMBIE.get()) {
        handler = new ALFAZombieHandler(extension);
      } else if (zombie.getType() == SurvivalEntityTypes.BOUNTY_HUNTER_ZOMBIE.get()) {
        handler = new BountyHunterZombieHandler(extension);
      } else if (zombie.getType() == SurvivalEntityTypes.DESERT_RAIDER_ZOMBIE.get()) {
        handler = new DesertRaiderZombieHandler(extension);
      } else if (zombie.getType() == SurvivalEntityTypes.FIREFIGHTER_ZOMBIE.get()) {
        handler = new FirefighterZombieHandler(extension);
      } else if (zombie.getType() == SurvivalEntityTypes.HAZMAT_ZOMBIE.get()) {
        handler = new HazmatZombieHandler(extension);
      } else if (zombie.getType() == SurvivalEntityTypes.JUGGERNAUT_ZOMBIE.get()) {
        handler = new JuggernautZombieHandler(extension);
      } else if (zombie.getType() == SurvivalEntityTypes.MINER_ZOMBIE.get()) {
        handler = new MinerZombieHandler(extension);
      } else if (zombie.getType() == SurvivalEntityTypes.SWAT_ZOMBIE.get()) {
        handler = new SwatZombieHandler(extension);
      } else {
        handler = new ZombieHandler(extension);
      }

      extension.registerHandler(ZombieHandler.TYPE, handler);
    }
  }

  @SubscribeEvent
  public void handleGunHitEntity(GunEvent.EntityHit event) {
    var living = event.target().getCapability(LivingExtension.CAPABILITY);
    if (living != null) {
      living.getHandler(SurvivalPlayerHandler.TYPE)
          .ifPresent(playerHandler -> playerHandler.infect(0.5F));
    }
  }

  @SubscribeEvent
  public void handleBodyPartHit(GunEvent.EntityHit event) {
    // 部位伤害/断肢系统：爆头死亡、腿断爬行、手臂断仍攻击、腰断瘫痪
    BodyPartHandler.handleGunHit(event);
  }

  // TODO: Reimplement zombie spawn modification via data-driven BiomeModifier
  // JSON files (data/craftingdeadsurvival/forge/biome_modifier/). The old
  // BiomeLoadingEvent was removed in 1.19.4+. The original logic removed vanilla
  // zombie spawns and added FAST_ZOMBIE/TANK_ZOMBIE/WEAK_ZOMBIE with configurable
  // weights from serverConfig.

  public static boolean checkZombieSpawnRules(EntityType<? extends Monster> type,
      ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
    return level.getBrightness(LightLayer.BLOCK, pos) <= 8
        && Monster.checkAnyLightMonsterSpawnRules(type, level, spawnType, pos, random);
  }

}
