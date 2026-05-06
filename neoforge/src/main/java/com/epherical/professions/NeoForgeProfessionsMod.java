package com.epherical.professions;


import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.bootstrap.Actions;
import com.epherical.professions.core.Profession;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.epherical.professions.core.register.PlatformBootstrap;
import com.epherical.professions.data.player.UuidOccupationDataLoader;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.model.actions.ActionType;
import com.epherical.professions.model.actions.conditions.ConditionType;
import com.epherical.professions.model.actions.rewards.RewardType;
import com.epherical.professions.networking.NetworkPayloadDispatcher;
import com.epherical.professions.networking.client.C2SCategorySelectionPayload;
import com.epherical.professions.networking.client.C2SOccupationExperienceTrackingPayload;
import com.epherical.professions.networking.client.ExperienceOccupationSyncHandler;
import com.epherical.professions.networking.client.ExperienceNotificationHandler;
import com.epherical.professions.networking.client.PlayerDataSyncPayloadHandler;
import com.epherical.professions.networking.client.ProfessionCategorySyncPayloadHandler;
import com.epherical.professions.networking.server.CategorySelectionPayloadHandler;
import com.epherical.professions.networking.server.OccupationExperienceTrackingPayloadHandler;
import com.epherical.professions.networking.server.S2CCategorySyncPayload;
import com.epherical.professions.networking.server.S2CExperienceGainPayload;
import com.epherical.professions.networking.server.S2CPlayerDataSyncPayload;
import com.epherical.professions.presentation.commands.ProfessionsStandardCommands;
import com.epherical.professions.registries.ActionLoad3;
import com.epherical.professions.registries.CategoryLoad3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.commands.ReloadCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.brewing.PlayerBrewedPotionEvent;
import net.neoforged.neoforge.event.entity.living.AnimalTameEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEnchantItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.TradeWithVillagerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;


@Mod(ProfessionsCommon.MOD_ID)
public class NeoForgeProfessionsMod extends ProfessionsCommon {

    private static final NeoForgeRegistrarBackend NEO_FORGE_REGISTRAR_BACKEND = new NeoForgeRegistrarBackend();

    public static Registry<ActionType> ACTIONS;
    public static Registry<ConditionType> CONDITIONS;
    public static Registry<RewardType> REWARDS;

    public static final DeferredRegister<Profession> PROFESSION_REGISTER = DeferredRegister.create(PROFESSION_REGISTRY_KEY, ProfessionsCommon.MOD_ID);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS_REGISTER = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ProfessionsCommon.MOD_ID);

    public static RegistryAccess REGISTRY_ACCESS = null;

    private final ActionManager actionManager;
    private final PlayerManager playerManager;

    public static NeoForgeProfessionsMod mod;


    public NeoForgeProfessionsMod(IEventBus eventBus) {
        super();

        NetworkPayloadDispatcher.setPayloadSender(PacketDistributor::sendToPlayer);
        NetworkPayloadDispatcher.setServerboundPayloadSender(PacketDistributor::sendToServer);

        actionManager = new ActionManager(null);
        playerManager = new PlayerManager(actionManager, null, getEventBus(), getCategoryManager());

        mod = this;
        PlatformBootstrap.init(NEO_FORGE_REGISTRAR_BACKEND);
        PROFESSION_REGISTER.register(eventBus);
        ATTACHMENTS_REGISTER.register(eventBus);

    }

    @Override
    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    @Override
    public File getModDir() {
        return FMLPaths.CONFIGDIR.get().toFile();
    }

    @Override
    public ActionManager getActionManager() {
        return actionManager;
    }


    @EventBusSubscriber(modid = ProfessionsCommon.MOD_ID)
    public static class EventHandler {

        @SubscribeEvent
        public static void registerNetworkPayloads(final RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar registrar = event.registrar("1");
            registrar.playToClient(S2CExperienceGainPayload.TYPE, S2CExperienceGainPayload.STREAM_CODEC,
                    (payload, context) -> {
                        ExperienceOccupationSyncHandler.handle(payload);
                        ExperienceNotificationHandler.handle(payload);
                    });
            registrar.playToClient(S2CCategorySyncPayload.TYPE, S2CCategorySyncPayload.STREAM_CODEC,
                    (payload, context) -> ProfessionCategorySyncPayloadHandler.handle(payload));
            registrar.playToClient(S2CPlayerDataSyncPayload.TYPE, S2CPlayerDataSyncPayload.STREAM_CODEC,
                    (payload, context) -> PlayerDataSyncPayloadHandler.handle(payload));
            registrar.playToServer(C2SCategorySelectionPayload.TYPE, C2SCategorySelectionPayload.STREAM_CODEC, (payload, context) -> {
                if (context.player() instanceof ServerPlayer serverPlayer) {
                    context.enqueueWork(() -> CategorySelectionPayloadHandler.handle(serverPlayer, payload));
                }
            });
            registrar.playToServer(C2SOccupationExperienceTrackingPayload.TYPE, C2SOccupationExperienceTrackingPayload.STREAM_CODEC, (payload, context) -> {
                if (context.player() instanceof ServerPlayer serverPlayer) {
                    context.enqueueWork(() -> OccupationExperienceTrackingPayloadHandler.handle(serverPlayer, payload));
                }
            });
        }


        @SubscribeEvent
        public static void serverStarting(ServerStartingEvent event) {
            Path resolve = event.getServer().getWorldPath(LevelResource.ROOT).resolve("professions/playerdata");
            mod.playerManager.setOccupationDataLoader(new UuidOccupationDataLoader(resolve, () -> REGISTRY_ACCESS));
            mod.playerManager.setServer(event.getServer());
            mod.playerManager.startExecutor();
        }


        @SubscribeEvent
        public static void serverStarted(ServerStartedEvent event) {
            mod.playerManager.loadAll();
        }

        @SubscribeEvent
        public static void serverStopping(ServerStoppingEvent event) {
            mod.playerManager.shutdown();
        }

        @SubscribeEvent
        public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {

                mod.playerManager.playerJoined(serverPlayer);
            }
        }

        @SubscribeEvent
        public static void playerQuit(PlayerEvent.PlayerLoggedOutEvent event) {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                mod.playerManager.playerQuit(serverPlayer);
            }
        }


        @SubscribeEvent
        public static void registerDatapackRegistry(DataPackRegistryEvent.NewRegistry event) {
            // TODO: this isn't able to be hot reloaded
            event.dataPackRegistry(
                    PROFESSION_REGISTRY_KEY,
                    Profession.CODEC,
                    Profession.CODEC // todo; set to null. nmaybe
                    //professionRegistryBuilder -> professionRegistryBuilder.sync(true).create()
            );
        }

        @SubscribeEvent
        public static void onDataPackSync(OnDatapackSyncEvent event) {
            event.getRelevantPlayers().forEach(player -> {
                IProfessionalPlayer professionalPlayer = mod.playerManager.getPlayer(player.getUUID());
                if (professionalPlayer == null) {
                    mod.playerManager.playerJoined(player);
                    professionalPlayer = mod.playerManager.getPlayer(player.getUUID());
                }

                if (professionalPlayer == null) {
                    return;
                }

                NetworkPayloadDispatcher.sendToPlayer(player, new S2CCategorySyncPayload(mod.getCategoryManager().getCategoryMap()));

                S2CPlayerDataSyncPayload payload = new S2CPlayerDataSyncPayload(
                        player.getUUID(),
                        professionalPlayer.getAllOccupations(),
                        Optional.ofNullable(mod.playerManager.getCategoryIdFor(professionalPlayer)),
                        mod.playerManager.getRelevantActionsForCategory(professionalPlayer.getCategory())
                );
                NetworkPayloadDispatcher.sendToPlayer(player, payload);
            });
        }



        @SubscribeEvent
        public static void onRegistryCreate(NewRegistryEvent event) {
            event.register(ACTIONS = new RegistryBuilder<>(ACTION_REGISTRY_KEY).sync(true).create());
            event.register(CONDITIONS = new RegistryBuilder<>(CONDITION_REGISTRY_KEY).sync(true).create());
            event.register(REWARDS = new RegistryBuilder<>(REWARD_REGISTRY_KEY).sync(true).create());
            ProfessionsCommon.register();
        }

        @SubscribeEvent
        public static void onRegisterEvent(RegisterEvent event) {
            NeoForgeProfessionsMod.NEO_FORGE_REGISTRAR_BACKEND.onRegister(event);
        }

        @SubscribeEvent
        public static void onDataReload(AddReloadListenerEvent event) {
            ActionLoad3 loader = new ActionLoad3(mod.actionManager);
            event.addListener(new NeoForgeActionReloadListener(loader));
            CategoryLoad3 categoryLoader = new CategoryLoad3(mod.getCategoryManager());
            event.addListener(new NeoForgeCategoryReloadListener(categoryLoader));

            // MVP for NF release
            // todo; build a better notification system (chat, pop up, toast, announcements)
            // todo; we need a separate playerManager for the client code... yuck it works but I'll improve it.

            // todo; we should improve the config next
            // todo; back buttons in the UI
            // todo; add commands back

            mod.setActionLoader(loader);
            mod.setCategoryLoader(categoryLoader);
            REGISTRY_ACCESS = event.getRegistryAccess();
        }

        @SubscribeEvent
        public static void onCommandRegister(RegisterCommandsEvent event) {
            new ProfessionsStandardCommands(NeoForgeProfessionsMod.mod, event.getDispatcher(), event.getBuildContext(), mod.actionManager);
        }


        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onBlockBreak(BlockEvent.BreakEvent event) {
            Holder<Block> blockHolder = event.getState().getBlockHolder();
            Player player = event.getPlayer();

            if (event.isCanceled()) {
                return;
            }

            if (!player.isCreative() && player instanceof ServerPlayer) {
                ProfessionContext.Builder builder = ProfessionContext.builder((ServerLevel) event.getLevel(),
                        Actions.BLOCK_BREAK, mod.playerManager.getPlayer(player.getUUID()))
                        .addParameter(ProfessionParameter.THIS_BLOCK, event.getState())
                        .addParameter(ProfessionParameter.BLOCKPOS, event.getPos())
                        .addParameter(ProfessionParameter.TOOL, player.getMainHandItem())
                        .addParameter(ProfessionParameter.ITEM_INVOLVED, event.getPlayer().getWeaponItem())
                        .addParameter(ProfessionParameter.THIS_HOLDER, blockHolder);

                mod.playerManager.processAction(player, builder.build());
            }
        }

        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
            Holder<Block> blockHolder = event.getState().getBlockHolder();
            Entity entity = event.getEntity();

            if (event.isCanceled()) {
                return;
            }

            // todo; re-add the cache for preventing gaming the system with xp gains.

            if (entity instanceof ServerPlayer serverPlayer && !serverPlayer.isCreative()) {
                ProfessionContext.Builder builder = ProfessionContext.builder((ServerLevel) event.getLevel(),
                                Actions.BLOCK_PLACE, mod.playerManager.getPlayer(serverPlayer.getUUID()))
                        .addParameter(ProfessionParameter.THIS_BLOCK, event.getState())
                        .addParameter(ProfessionParameter.BLOCKPOS, event.getPos())
                        .addParameter(ProfessionParameter.THIS_HOLDER, blockHolder);

                mod.playerManager.processAction(serverPlayer, builder.build());
            }
        }

        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onBlockExplode(ExplosionEvent.Detonate event) {
            if (event.getLevel() instanceof ServerLevel level && event.getExplosion().getIndirectSourceEntity() instanceof ServerPlayer player) {
                // This could possibly be slow, in fabric we just use a mixin to directly get the blockstate in the final explosion.
                for (BlockPos affectedBlock : event.getAffectedBlocks()) {
                    BlockState blockState = level.getBlockState(affectedBlock);
                    ProfessionContext.Builder builder = ProfessionContext.builder(level,
                                    Actions.BLOCK_EXPLODE, mod.playerManager.getPlayer(player.getUUID()))
                            .addParameter(ProfessionParameter.BLOCKPOS, affectedBlock)
                            .addParameter(ProfessionParameter.THIS_BLOCK, blockState)
                            .addParameter(ProfessionParameter.THIS_HOLDER, blockState.getBlockHolder());
                    mod.playerManager.processAction(player, builder.build());
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onEntityDeath(LivingDeathEvent event) {
            if (event.isCanceled() || event.getEntity().level().isClientSide) {
                return;
            }

            ServerLevel level = (ServerLevel) event.getEntity().level();
            Entity source = event.getSource().getEntity();
            LivingEntity killedEntity = event.getEntity();

            ProfessionContext.Builder builder = ProfessionContext.builder(level,
                    Actions.SLAY_ACTION, mod.playerManager.getPlayer(killedEntity.getUUID()));

            if (source instanceof ServerPlayer serverPlayer) {
                if (killedEntity instanceof AgeableMob mob) {
                    if (!mob.isBaby()) {
                        builder.addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(source.getUUID()))
                                .addParameter(ProfessionParameter.ENTITY, killedEntity);
                    }
                } else {
                    builder.addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(source.getUUID()))
                            .addParameter(ProfessionParameter.ENTITY, killedEntity);
                }
                mod.playerManager.processAction(serverPlayer, builder.build());
            }
        }

        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onFishedItem(ItemFishedEvent event) {
            Player player = event.getEntity();
            if (event.isCanceled() || player.level().isClientSide) {
                return;
            }
            ServerLevel level = (ServerLevel) event.getEntity().level();

            ProfessionContext.Builder builder = ProfessionContext.builder(level,
                    Actions.FISHING_ACTION, mod.playerManager.getPlayer(player.getUUID()));
            for (ItemStack drop : event.getDrops()) {
                builder.addParameter(ProfessionParameter.ITEM_INVOLVED, drop);
                mod.playerManager.processAction(player, builder.build());
            }
        }

        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
            Player player = event.getEntity();
            if (player.level().isClientSide) {
                return;
            }

            ServerLevel level = (ServerLevel) player.level();

            ProfessionContext.Builder builder = ProfessionContext.builder(level,
                            Actions.CRAFTING_ACTION, mod.playerManager.getPlayer(player.getUUID()))
                    .addParameter(ProfessionParameter.ITEM_INVOLVED, event.getCrafting());
            mod.playerManager.processAction(player, builder.build());
        }

        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onItemSmelted(PlayerEvent.ItemSmeltedEvent event) {
            Player player = event.getEntity();
            if (player.level().isClientSide) {
                return;
            }
            ServerLevel level = (ServerLevel) player.level();

            ProfessionContext.Builder builder = ProfessionContext.builder(level,
                            Actions.SMELT_TAKE_ACTION, mod.playerManager.getPlayer(player.getUUID()))
                    .addParameter(ProfessionParameter.ITEM_INVOLVED, event.getSmelting());
            mod.playerManager.processAction(player, builder.build());
        }

        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onBreedAnimal(BabyEntitySpawnEvent event) {
            Player player = event.getCausedByPlayer();
            if (event.isCanceled() || player == null || player.level().isClientSide) {
                return;
            }
            ServerLevel level = (ServerLevel) player.level();
            ProfessionContext.Builder builder = ProfessionContext.builder(level,
                            Actions.BREED_ACTION, mod.playerManager.getPlayer(player.getUUID()))
                    .addParameter(ProfessionParameter.ENTITY, event.getChild());
            mod.playerManager.processAction(player, builder.build());
        }

        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onTameAnimal(AnimalTameEvent event) {
            Player player = event.getTamer();
            if (event.isCanceled() || player.level().isClientSide) {
                return;
            }
            ServerLevel level = (ServerLevel) player.level();
            ProfessionContext.Builder builder = ProfessionContext.builder(level,
                            Actions.TAME_ACTION, mod.playerManager.getPlayer(player.getUUID()))
                    .addParameter(ProfessionParameter.ENTITY, event.getAnimal());
            mod.playerManager.processAction(player, builder.build());
        }

        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onTradeWithVillager(TradeWithVillagerEvent event) {
            // todo; implement trading action
        }

        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onPlayerEnchant(PlayerEnchantItemEvent event) {
            Level level = event.getEntity().level();

            if (level instanceof ServerLevel serverLevel) {
                ServerPlayer player = (ServerPlayer) event.getEntity();
                ItemStack itemEnchanted = event.getEnchantedItem();
                List<EnchantmentInstance> enchantments = event.getEnchantments();

                for (EnchantmentInstance enchantment : enchantments) {
                    ProfessionContext.Builder builder = ProfessionContext.builder(serverLevel,
                                    Actions.ENCHANT_ACTION, mod.playerManager.getPlayer(player.getUUID()))
                            .addParameter(ProfessionParameter.ENCHANTMENT_INSTANCE, enchantment);
                    mod.playerManager.processAction(player, builder.build());
                }

                ProfessionContext.Builder builder = ProfessionContext.builder(serverLevel,
                                Actions.ENCHANT_ACTION, mod.playerManager.getPlayer(player.getUUID()))
                        .addParameter(ProfessionParameter.ITEM_INVOLVED, itemEnchanted);

                mod.playerManager.processAction(player, builder.build());
            }
        }

        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onPlayerBrew(PlayerBrewedPotionEvent event) {
            // todo; can't do this one cause we have to know what ingredient was used.
        }

    }
}
