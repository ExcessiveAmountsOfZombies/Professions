package com.epherical.professions;

import com.epherical.octoecon.api.Economy;
import com.epherical.octoecon.api.event.EconomyEvents;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.commands.ProfessionsFabricCommands;
import com.epherical.professions.config.CommonConfig;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.data.FileStorage;
import com.epherical.professions.data.Storage;
import com.epherical.professions.datapack.FabricOperationLoader;
import com.epherical.professions.datapack.FabricProfLoader;
import com.epherical.professions.datapack.FabricProfLoaderV2;
import com.epherical.professions.events.ProfessionUtilityEvents;
import com.epherical.professions.events.trigger.TriggerEvents;
import com.epherical.professions.integration.ftb.FTBIntegration;
import com.epherical.professions.loot.UnlockCondition;
import com.epherical.professions.mixin.LootTableBuilderAccessor;
import com.epherical.professions.networking.ServerHandler;
import com.epherical.professions.profession.operation.AbstractOperation;
import com.epherical.professions.trigger.BlockTriggers;
import com.epherical.professions.trigger.EntityTriggers;
import com.epherical.professions.trigger.UtilityListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.loot.v2.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ProfessionsFabric extends ProfessionMod implements ModInitializer {

    private static ProfessionsFabric mod;
    private ProfessionListener listener;
    private PlayerManager playerManager;
    private ProfessionsFabricCommands commands;
    private CommonConfig config;

    private static @Nullable Economy economy;
    private Storage<ProfessionalPlayer, UUID> dataStorage;

    private final FabricProfLoader professionLoader = new FabricProfLoader();
    private final FabricProfLoaderV2 fabricProfLoaderV2 = new FabricProfLoaderV2();

    private boolean startup;

    public static boolean isStopping = false;

    private MinecraftServer minecraftServer;

    @Override
    public void onInitialize() {
        ProfessionPlatform.create(new FabricPlatform());
        startup = true;
        mod = this;
        this.config = new CommonConfig(false, "professions.conf");
        this.config.loadConfig();
        if (ProfessionConfig.useBuiltinDatapack) {
            ResourceManagerHelper.registerBuiltinResourcePack(new ResourceLocation("professions", "fabric/normal"), FabricLoader.getInstance().getModContainer("professions").get(), ResourcePackActivationType.DEFAULT_ENABLED);
            if (ProfessionConfig.useHardcoreDatapack) {
                ResourceManagerHelper.registerBuiltinResourcePack(new ResourceLocation("professions", "fabric/hardcore"), FabricLoader.getInstance().getModContainer("professions").get(), ResourcePackActivationType.DEFAULT_ENABLED);
            }
        }
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            this.commands = new ProfessionsFabricCommands(this, dispatcher);
        });

        RegistryConstants.init();
        Constants.UNLOCK_CONDITION = registerLootCondition("unlock_condition", new UnlockCondition.Serializer());
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(professionLoader);
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(fabricProfLoaderV2);
        // TODO: add a page to display future rewards


        // TODO; we can abstract this better
        FabricOperationLoader<AbstractOperation<Item>, Item> ITEM_ACTIONS = new FabricOperationLoader("professions/operations/actionables/item", Registries.ITEM);
        FabricOperationLoader<AbstractOperation<Block>, Block> BLOCK_ACTIONS = new FabricOperationLoader("professions/operations/actionables/block", Registries.BLOCK);
        FabricOperationLoader<AbstractOperation<EntityType<?>>, EntityType<?>> ENTITY_ACTIONS = new FabricOperationLoader("professions/operations/actionables/entity_type", Registries.ENTITY_TYPE);
        FabricOperationLoader<AbstractOperation<Biome>, Biome> BIOME_ACTIONS = new FabricOperationLoader("professions/operations/actionables/worldgen/biome", Registries.BIOME);

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(ITEM_ACTIONS);
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(BLOCK_ACTIONS);
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(ENTITY_ACTIONS);
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(BIOME_ACTIONS);


        // TODO; delete temporary data afterwards to reduce memory consumption?
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            if (success) {
                ITEM_ACTIONS.finishLoading(server, fabricProfLoaderV2.getBuilderMap());
                BLOCK_ACTIONS.finishLoading(server, fabricProfLoaderV2.getBuilderMap());
                fabricProfLoaderV2.finishLoading();
            }
            System.out.println("FINISHED RELOADING DATAPACK");
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ITEM_ACTIONS.finishLoading(server, fabricProfLoaderV2.getBuilderMap());
            BLOCK_ACTIONS.finishLoading(server, fabricProfLoaderV2.getBuilderMap());
            fabricProfLoaderV2.finishLoading();
        });

        EconomyEvents.ECONOMY_CHANGE_EVENT.register(economy -> {
            this.economy = economy;
        });
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            isStopping = false;
            dataStorage = new FileStorage(server.getWorldPath(LevelResource.ROOT).resolve("professions/playerdata"));
            this.dataStorage = ProfessionUtilityEvents.STORAGE_CALLBACK.invoker().setStorage(dataStorage);
            ProfessionUtilityEvents.STORAGE_FINALIZATION_EVENT.invoker().onFinalization(dataStorage);
            this.playerManager = new PlayerManager(this.dataStorage, server);
            this.minecraftServer = server;
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            isStopping = true;
        });
        LootTableEvents.MODIFY.register((resourceManager, manager, id, supplier, setter) -> {
            List<LootPool> pools = ((LootTableBuilderAccessor) supplier).getPools();
            // this is probably really awful to do, adding our condition check to EVERY SINGLE lootpool in the game.
            if (!pools.isEmpty()) {
                for (int i = 0; i < pools.size(); i++) {
                    LootPool.Builder builder = FabricLootPoolBuilder.copyOf(pools.get(i));
                    builder.when(UnlockCondition::new);
                    pools.set(i, builder.build());
                }
            }
        });
        this.listener = new ProfessionListener();
        ServerPlayConnectionEvents.JOIN.register(this.listener::onPlayerJoin);
        ServerPlayConnectionEvents.DISCONNECT.register(this.listener::onPlayerLeave);
        TriggerEvents.PLAYER_LOCATION_EVENT.register(player -> {
            this.listener.onPlayerTick(player, this);
        });
        BlockTriggers.init(this);
        EntityTriggers.init(this);
        UtilityListener.init(this);

        ServerPlayNetworking.registerGlobalReceiver(Constants.MOD_CHANNEL, ServerHandler::receivePacket);

        if (FabricLoader.getInstance().isModLoaded("ftbquests")) {
            FTBIntegration.init();
        }
        // just create a playerManager, if it's on the client we don't need the two parameters. Otherwise, it'll be overridden when the server starts.
        playerManager = new PlayerManager(null, null);
    }

    public static LootItemConditionType registerLootCondition(String id, Serializer<? extends LootItemCondition> serializer) {
        return Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, new ResourceLocation(Constants.MOD_ID, id), new LootItemConditionType(serializer));
    }

    public static Economy getEconomy() {
        return economy;
    }

    public Storage<ProfessionalPlayer, UUID> getDataStorage() {
        return dataStorage;
    }

    public static ProfessionsFabric getInstance() {
        return mod;
    }

    public MinecraftServer getMinecraftServer() {
        return minecraftServer;
    }

    public FabricProfLoader getProfessionLoader() {
        return professionLoader;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public CommonConfig getConfig() {
        return config;
    }
}
