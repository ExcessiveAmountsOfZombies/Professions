package com.epherical.professions.registries;

import com.epherical.professions.PerkManager;
import com.epherical.professions.api.perks.Perk;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class PerkLoad3 {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String PATH = "professions/perks";

    private final PerkManager perkManager;

    public PerkLoad3(PerkManager perkManager) {
        this.perkManager = perkManager;
    }

    public CompletableFuture<Void> reload(HolderLookup.Provider registryLookup,
                                          @NotNull PreparableReloadListener.SharedState sharedState,
                                          @NotNull Executor taskExecutor,
                                          @NotNull PreparableReloadListener.PreparationBarrier preparationBarrier,
                                          @NotNull Executor reloadExecutor) {
        return CompletableFuture
                .supplyAsync(() -> decodeAll(sharedState.resourceManager(), registryLookup), taskExecutor)
                .thenCompose(preparationBarrier::wait)
                .thenAcceptAsync(perkManager::reloadPerks, reloadExecutor);
    }

    private List<Perk> decodeAll(ResourceManager manager, HolderLookup.Provider registryLookup) {
        List<Perk> perks = new ArrayList<>();
        FileToIdConverter fileToIdConverter = FileToIdConverter.json(PATH);
        Map<Identifier, List<Resource>> resources = fileToIdConverter.listMatchingResourceStacks(manager);
        var ops = registryLookup.createSerializationContext(JsonOps.INSTANCE);

        for (Map.Entry<Identifier, List<Resource>> stackEntry : resources.entrySet()) {
            for (Resource entry : stackEntry.getValue()) {
                Identifier fileId = stackEntry.getKey();
                Identifier idFile = fileToIdConverter.fileToId(fileId);

                try (Reader reader = entry.openAsReader()) {
                    JsonElement element = GsonHelper.fromJson(GSON, reader, JsonElement.class);
                    Perk perk = Perk.TYPED_CODEC.parse(ops, element)
                            .resultOrPartial(message -> LOGGER.error("Failed to decode perk from {}: {}", fileId, message))
                            .orElse(null);

                    if (perk != null) {
                        perk.setId(idFile);
                        LOGGER.debug("Successfully decoded perk for file: {}", fileId);
                        perks.add(perk);
                    }
                } catch (IllegalArgumentException | IOException e) {
                    LOGGER.error("Couldn't load resource {}", idFile, e);
                }
            }
        }
        return perks;
    }

    public PerkManager getPerkManager() {
        return perkManager;
    }
}

