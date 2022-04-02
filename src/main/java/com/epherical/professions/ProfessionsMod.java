package com.epherical.professions;

import com.epherical.octoecon.api.Economy;
import com.epherical.octoecon.api.event.EconomyEvents;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.commands.ProfessionsCommands;
import com.epherical.professions.config.CommonConfig;
import com.epherical.professions.data.FileStorage;
import com.epherical.professions.data.Storage;
import com.epherical.professions.datapack.ProfessionLoader;
import com.epherical.professions.events.ProfessionUtilityEvents;
import com.epherical.professions.integration.ftb.FTBIntegration;
import com.epherical.professions.networking.ServerHandler;
import com.epherical.professions.profession.ProfessionSerializer;
import com.epherical.professions.trigger.BlockTriggers;
import com.epherical.professions.trigger.EntityTriggers;
import com.epherical.professions.trigger.UtilityListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ProfessionsMod implements ModInitializer {
    public static final String MOD_ID = "professions";
    public static final ResourceLocation MOD_CHANNEL = new ResourceLocation(MOD_ID, "data");

    private static ProfessionsMod mod;
    private ProfessionListener listener;
    private PlayerManager playerManager;
    private ProfessionsCommands commands;
    private CommonConfig config;

    private static @Nullable Economy economy;
    private Storage<ProfessionalPlayer, UUID> dataStorage;

    private final ProfessionLoader professionLoader = new ProfessionLoader();

    private boolean startup;

    @Override
    public void onInitialize() {
        startup = true;
        mod = this;
        this.config = new CommonConfig(false, "professions.yml");
        this.config.loadConfig();
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            this.commands = new ProfessionsCommands(this, dispatcher);
        });
        init();
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(professionLoader);
        EconomyEvents.ECONOMY_CHANGE_EVENT.register(economy -> {
            this.economy = economy;
        });
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            dataStorage = new FileStorage(server.getWorldPath(LevelResource.ROOT).resolve("professions/playerdata"));
            this.dataStorage = ProfessionUtilityEvents.STORAGE_CALLBACK.invoker().setStorage(dataStorage);
            ProfessionUtilityEvents.STORAGE_FINALIZATION_EVENT.invoker().onFinalization(dataStorage);
            this.playerManager = new PlayerManager(this.dataStorage, server);
        });
        this.listener = new ProfessionListener();
        ServerPlayConnectionEvents.JOIN.register(this.listener::onPlayerJoin);
        ServerPlayConnectionEvents.DISCONNECT.register(this.listener::onPlayerLeave);
        BlockTriggers.init(this);
        EntityTriggers.init(this);
        UtilityListener.init(this);

        ServerPlayNetworking.registerGlobalReceiver(MOD_CHANNEL, ServerHandler::receivePacket);

        if (FabricLoader.getInstance().isModLoaded("ftbquests")) {
            FTBIntegration.init();
        }


    }

    public static ResourceLocation modID(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    private static ProfessionSerializer<?> init() {
        return ProfessionSerializer.DEFAULT_PROFESSION;
    }

    public static Economy getEconomy() {
        return economy;
    }

    public Storage<ProfessionalPlayer, UUID> getDataStorage() {
        return dataStorage;
    }

    public static ProfessionsMod getInstance() {
        return mod;
    }

    public ProfessionLoader getProfessionLoader() {
        return professionLoader;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public CommonConfig getConfig() {
        return config;
    }
}
