package com.epherical.professions;

import com.epherical.octoecon.api.Economy;
import com.epherical.octoecon.api.event.EconomyEvents;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.commands.ProfessionsCommands;
import com.epherical.professions.data.FileStorage;
import com.epherical.professions.data.Storage;
import com.epherical.professions.datapack.ProfessionLoader;
import com.epherical.professions.profession.ProfessionSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ProfessionsMod implements ModInitializer {
    public static final String MOD_ID = "professions";

    private static ProfessionsMod mod;
    private ProfessionListener listener;
    private PlayerManager playerManager;
    private ProfessionsCommands commands;

    private static @Nullable Economy economy;
    private Storage<ProfessionalPlayer, UUID> dataStorage;

    private final ProfessionLoader professionLoader = new ProfessionLoader();

    @Override
    public void onInitialize() {
        mod = this;
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
            this.playerManager = new PlayerManager(this.dataStorage, server);
            this.listener = new ProfessionListener(this.playerManager);
            ServerPlayConnectionEvents.JOIN.register(this.listener::onPlayerJoin);
            ServerPlayConnectionEvents.DISCONNECT.register(this.listener::onPlayerLeave);
        });
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
}
