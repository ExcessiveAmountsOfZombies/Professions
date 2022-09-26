package com.epherical.professions;

import com.epherical.octoecon.api.Economy;
import com.epherical.octoecon.api.event.EconomyChangeEvent;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.capability.ChunkVisited;
import com.epherical.professions.capability.PlayerOwnable;
import com.epherical.professions.client.ProfessionsClientForge;
import com.epherical.professions.commands.ProfessionsCommands;
import com.epherical.professions.config.Config;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.data.FileStorage;
import com.epherical.professions.data.Storage;
import com.epherical.professions.datapack.ForgeProfLoader;
import com.epherical.professions.loot.UnlockCondition;
import com.epherical.professions.networking.NetworkHandler;
import com.epherical.professions.triggers.BlockTriggers;
import com.epherical.professions.triggers.EntityTriggers;
import com.epherical.professions.triggers.ProfessionListener;
import com.epherical.professions.util.ChunkVisitedProvider;
import com.epherical.professions.util.PlayerOwnableProvider;
import com.epherical.professions.util.ProfPermissions;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.resource.PathPackResources;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

@Mod("professions")
public class ProfessionsForge {

    private static ProfessionsForge mod;
    private PlayerManager playerManager;
    private ProfessionsCommands commands;
    private Config config;

    private Storage<ProfessionalPlayer, UUID> dataStorage;
    private Set<Class<?>> capHolders = new HashSet<>();

    private ForgeProfLoader professionLoader;
    private NetworkHandler handler;

    public static boolean isStopping = false;

    private MinecraftServer minecraftServer;
    private Economy economy;

    public ProfessionsForge() {
        mod = this;
        config = new Config();
        CommonPlatform.create(new ForgePlatform());
        handler = new NetworkHandler();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::sendMessages);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::readMessages);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ForgeRegConstants::createRegistries);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(config::initConfig);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::addPacks);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerEvent);

        MinecraftForge.EVENT_BUS.register(new ProfPermissions());
        MinecraftForge.EVENT_BUS.register(new ProfessionListener());
        MinecraftForge.EVENT_BUS.register(new BlockTriggers(this));
        MinecraftForge.EVENT_BUS.register(new EntityTriggers(this));
        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, config.getConfigSpec());


    }

    private void commonInit(FMLCommonSetupEvent event) {
        professionLoader = new ForgeProfLoader();
        MinecraftForge.EVENT_BUS.register(professionLoader);
    }

    private void clientInit(FMLClientSetupEvent event) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ProfessionsClientForge::registerKeyBindings);
            return ProfessionsClientForge::initClient;
        });
        MinecraftForge.EVENT_BUS.register(new ProfessionsClientForge());
    }

    private void sendMessages(InterModEnqueueEvent event) {
        // very specific, but also the fastest 'in code' that doesn't rely on if/else statements.
        // if there is a better way that is more general that'd be great, but I'm not sure of one.
        InterModComms.sendTo(Constants.MOD_ID, "regCap", () -> BrewingStandBlockEntity.class);
        InterModComms.sendTo(Constants.MOD_ID, "regCap", () -> BlastFurnaceBlockEntity.class);
        InterModComms.sendTo(Constants.MOD_ID, "regCap", () -> CampfireBlockEntity.class);
        InterModComms.sendTo(Constants.MOD_ID, "regCap", () -> FurnaceBlockEntity.class);

    }

    private void readMessages(InterModProcessEvent event) {
        event.getIMCStream(s -> s.equals("regCap")).forEach(imcMessage -> {
            Object supplier = imcMessage.messageSupplier().get();
            if (supplier instanceof Class<?> clazz) {
                capHolders.add(clazz);
            }
        });
    }


    public void registerEvent(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registry.LOOT_ITEM_REGISTRY)) {
            Constants.UNLOCK_CONDITION = registerLootCondition("unlock_condition", new UnlockCondition.Serializer());
        }
    }

    public static LootItemConditionType registerLootCondition(String id, Serializer<? extends LootItemCondition> serializer) {
        return Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(Constants.MOD_ID, id), new LootItemConditionType(serializer));
    }

    @SubscribeEvent
    public void onEconomyChange(EconomyChangeEvent event) {
        this.economy = event.getEconomy();
    }

    @SubscribeEvent
    public void serverStarting(ServerStartingEvent event) {
        MinecraftServer server = event.getServer();
        isStopping = false;
        dataStorage = new FileStorage(server.getWorldPath(LevelResource.ROOT).resolve("professions/playerdata"));
        //this.dataStorage = ProfessionUtilityEvents.STORAGE_CALLBACK.invoker().setStorage(dataStorage);
        //ProfessionUtilityEvents.STORAGE_FINALIZATION_EVENT.invoker().onFinalization(dataStorage);
        this.playerManager = new PlayerManager(this.dataStorage, server);
        this.minecraftServer = server;
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
        event.register(ChunkVisited.class);
    }

    @SubscribeEvent
    public void attachChunkCapability(AttachCapabilitiesEvent<LevelChunk> event) {
        ChunkVisitedProvider provider = new ChunkVisitedProvider();
        event.addCapability(ChunkVisitedProvider.ID, provider);
    }

    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        if (capHolders.contains(event.getObject().getClass())) {
            PlayerOwnableProvider provider = new PlayerOwnableProvider();
            event.addCapability(PlayerOwnableProvider.ID, provider);
        }
    }

    public void addPacks(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.SERVER_DATA) {
            if (ProfessionConfig.useBuiltinDatapack) { // since we are loading a server datapack, I think this is called after the configs have loaded.
                event.addRepositorySource((Consumer<Pack> packConsumer, Pack.PackConstructor packConstructor) -> {
                    packConsumer.accept(packConstructor.create("default_normal_professions", Component.nullToEmpty("Default Normal Professions"),
                            true, () -> new PathPackResources("Default Normal Professions",                                       /// ????
                                    ModList.get().getModFileById(Constants.MOD_ID).getFile().findResource("resourcepacks/forge/normal")),
                            new PackMetadataSection(Component.nullToEmpty("Default Normal Professions"), 10),
                            Pack.Position.BOTTOM,
                            PackSource.WORLD, false));
                });
                if (ProfessionConfig.useHardcoreDatapack) {
                    event.addRepositorySource((Consumer<Pack> packConsumer, Pack.PackConstructor packConstructor) -> {
                        packConsumer.accept(packConstructor.create("default_hardcore_professions", Component.nullToEmpty("Default Hardcore Professions"),
                                true, () -> new PathPackResources("Default Hardcore Professions",
                                        ModList.get().getModFileById(Constants.MOD_ID).getFile().findResource("resourcepacks/forge/hardcore")),
                                new PackMetadataSection(Component.nullToEmpty("Default Hardcore Professions"), 10),
                                Pack.Position.BOTTOM,
                                PackSource.WORLD, false));
                    });
                }
            }
        }
    }

    public static ProfessionsForge getInstance() {
        return mod;
    }

    public Economy getEconomy() {
        return economy;
    }

    public MinecraftServer getMinecraftServer() {
        return minecraftServer;
    }

    public ForgeProfLoader getProfessionLoader() {
        return professionLoader;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public Storage<ProfessionalPlayer, UUID> getDataStorage() {
        return dataStorage;
    }
}
