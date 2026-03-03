package com.epherical.professions;


import com.epherical.professions.commands.ProfessionsStandardCommands;
import com.epherical.professions.core.Profession;
import com.epherical.professions.core.actions.ActionType;
import com.epherical.professions.core.conditions.ConditionType;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.epherical.professions.core.register.Actions;
import com.epherical.professions.core.register.PlatformBootstrap;
import com.epherical.professions.core.rewards.RewardType;
import com.epherical.professions.data.player.UuidOccupationDataLoader;
import com.epherical.professions.registries.ActionLoad3;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.io.File;
import java.nio.file.Path;


@Mod(Constants.MOD_ID)
public class NeoForgeProfessionsMod extends CommonClass {

    private static final NeoForgeRegistrarBackend NEO_FORGE_REGISTRAR_BACKEND = new NeoForgeRegistrarBackend();

    public static Registry<ActionType> ACTIONS;
    public static Registry<ConditionType> CONDITIONS;
    public static Registry<RewardType> REWARDS;

    public static final DeferredRegister<Profession> PROFESSION_REGISTER = DeferredRegister.create(PROFESSION_REGISTRY_KEY, Constants.MOD_ID);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS_REGISTER = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Constants.MOD_ID);

    public static RegistryAccess REGISTRY_ACCESS = null;

    private final ActionManager actionManager;
    private final PlayerManager playerManager;

    public static NeoForgeProfessionsMod mod;


    public NeoForgeProfessionsMod(IEventBus eventBus) {
        this.init();
        this.buildConfig();

        actionManager = new ActionManager(null);
        playerManager = new PlayerManager(null, actionManager, null);

        mod = this;
        PlatformBootstrap.init(NEO_FORGE_REGISTRAR_BACKEND);
        PROFESSION_REGISTER.register(eventBus);
        ATTACHMENTS_REGISTER.register(eventBus);

    }

    @Override
    public ActionLoad3 getActionLoader() {
        return ACTION_LOAD2;
    }

    @Override
    public File getModDir() {
        return FMLPaths.CONFIGDIR.get().toFile();
    }

    @Override
    public boolean isClientEnvironment() {
        return FMLEnvironment.dist.isClient();
    }


    @EventBusSubscriber(modid = Constants.MOD_ID)
    public static class EventHandler {

        @SubscribeEvent
        public static void serverStarting(ServerStartingEvent event) {
            Path resolve = event.getServer().getWorldPath(LevelResource.ROOT).resolve("professions/playerdata");
            mod.playerManager.setOccupationDataLoader(new UuidOccupationDataLoader(resolve, () -> REGISTRY_ACCESS));
            mod.playerManager.setServer(event.getServer());
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
            event.dataPackRegistry(
                    PROFESSION_REGISTRY_KEY,
                    Profession.CODEC,
                    Profession.CODEC
                    //professionRegistryBuilder -> professionRegistryBuilder.sync(true).create()
            );
        }



        @SubscribeEvent
        public static void onRegistryCreate(NewRegistryEvent event) {
            event.register(ACTIONS = new RegistryBuilder<>(ACTION_REGISTRY_KEY).sync(true).create());
            event.register(CONDITIONS = new RegistryBuilder<>(CONDITION_REGISTRY_KEY).sync(true).create());
            event.register(REWARDS = new RegistryBuilder<>(REWARD_REGISTRY_KEY).sync(true).create());
            CommonClass.register();
        }

        @SubscribeEvent
        public static void onRegisterEvent(RegisterEvent event) {
            NeoForgeProfessionsMod.NEO_FORGE_REGISTRAR_BACKEND.onRegister(event);
        }

        @SubscribeEvent
        public static void onDataReload(AddReloadListenerEvent event) {
            ActionLoad3 loader = new ActionLoad3(mod.actionManager);
            event.addListener(new NeoForgeActionReloadListener(loader));
            ACTION_LOAD2 = loader;
            REGISTRY_ACCESS = event.getRegistryAccess();
        }

        @SubscribeEvent
        public static void onCommandRegister(RegisterCommandsEvent event) {
            new ProfessionsStandardCommands(NeoForgeProfessionsMod.mod, event.getDispatcher(), event.getBuildContext(), mod.actionManager);
        }


        @SubscribeEvent
        public static void onBlockBreak(BlockEvent.BreakEvent event) {
            Holder<Block> blockHolder = event.getState().getBlockHolder();
            Player player = event.getPlayer();


            if (!player.isCreative() && player instanceof ServerPlayer) {
                ProfessionContext.Builder context = new ProfessionContext.Builder((ServerLevel) event.getLevel())
                        .addParameter(ProfessionParameter.ACTION_TYPE, Actions.BLOCK_BREAK)
                       // .addParameter(ProfessionParameter.THIS_PLAYER, iProfessionalPlayer)
                        .addParameter(ProfessionParameter.THIS_BLOCK, event.getState())
                        .addParameter(ProfessionParameter.BLOCKPOS, event.getPos())
                        .addParameter(ProfessionParameter.TOOL, player.getMainHandItem())
                        .addParameter(ProfessionParameter.ITEM_INVOLVED, event.getPlayer().getWeaponItem())
                        .addParameter(ProfessionParameter.THIS_HOLDER, blockHolder);

                mod.playerManager.processAction(player, Actions.BLOCK_BREAK, context);

                //iProfessionalPlayer.handleAction(context, blockHolder);
            }

        }
    }
}
