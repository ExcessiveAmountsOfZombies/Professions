package com.epherical.professions;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.client.ProfessionsClientForge;
import com.epherical.professions.commands.ProfessionsCommands;
import com.epherical.professions.config.Config;
import com.epherical.professions.data.FileStorage;
import com.epherical.professions.data.Storage;
import com.epherical.professions.datapack.ProfessionLoader;
import com.epherical.professions.networking.NetworkHandler;
import com.epherical.professions.triggers.ProfessionListener;
import com.epherical.professions.util.PlayerOwnableProvider;
import com.epherical.professions.util.mixins.PlayerOwnable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.UUID;

@Mod("professions")
public class ProfessionsForge {

    private static ProfessionsForge mod;
    //private ProfessionListener listener;
    private PlayerManager playerManager;
    private ProfessionsCommands commands;
    private Config config;

    private Storage<ProfessionalPlayer, UUID> dataStorage;

    private ProfessionLoader professionLoader;
    private NetworkHandler handler;

    public static boolean isStopping = false;

    public ProfessionsForge() {
        mod = this;
        config = new Config();
        handler = new NetworkHandler();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(RegistryConstants::createRegistries);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(config::initConfig);

        MinecraftForge.EVENT_BUS.register(new ProfPermissions());
        MinecraftForge.EVENT_BUS.register(new ProfessionListener());
        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, config.getConfigSpec());



    }

    private void commonInit(FMLCommonSetupEvent event) {
        professionLoader = new ProfessionLoader();
        MinecraftForge.EVENT_BUS.register(professionLoader);
    }

    private void clientInit(FMLClientSetupEvent event) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ProfessionsClientForge::initClient);
        MinecraftForge.EVENT_BUS.register(new ProfessionsClientForge());
    }

    @SubscribeEvent
    public void serverStarting(ServerStartingEvent event) {
        MinecraftServer server = event.getServer();
        isStopping = false;
        dataStorage = new FileStorage(server.getWorldPath(LevelResource.ROOT).resolve("professions/playerdata"));
        //this.dataStorage = ProfessionUtilityEvents.STORAGE_CALLBACK.invoker().setStorage(dataStorage);
        //ProfessionUtilityEvents.STORAGE_FINALIZATION_EVENT.invoker().onFinalization(dataStorage);
        this.playerManager = new PlayerManager(this.dataStorage, server);
    }

    @SubscribeEvent
    public void serverStopping(ServerStoppingEvent event) {
        isStopping = true;
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        commands = new ProfessionsCommands(this, event.getDispatcher());
    }

    @SubscribeEvent
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerOwnable.class);
    }

    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        PlayerOwnableProvider provider = new PlayerOwnableProvider();
        event.addCapability(PlayerOwnableProvider.ID, provider);
        System.out.println(event.getObject());
    }


    public static ProfessionsForge getInstance() {
        return mod;
    }

    public ProfessionLoader getProfessionLoader() {
        return professionLoader;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public Storage<ProfessionalPlayer, UUID> getDataStorage() {
        return dataStorage;
    }
}
