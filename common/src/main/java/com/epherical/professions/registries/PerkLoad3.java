package com.epherical.professions.registries;

import com.epherical.professions.PerkManager;
import com.epherical.professions.api.perks.Perk;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
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
                                          @NotNull PreparableReloadListener.PreparationBarrier barrier,
                                          @NotNull ResourceManager resourceManager,
                                          @NotNull ProfilerFiller prepProfiler,
                                          @NotNull ProfilerFiller applyProfiler,
                                          @NotNull Executor background,
                                          @NotNull Executor gameThread) {
        return CompletableFuture
                .supplyAsync(() -> decodeAll(resourceManager, registryLookup), background)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(perkManager::reloadPerks, gameThread);
    }

    private List<Perk> decodeAll(ResourceManager manager, HolderLookup.Provider registryLookup) {
        List<Perk> perks = new ArrayList<>();
        FileToIdConverter fileToIdConverter = FileToIdConverter.json(PATH);
        Map<ResourceLocation, List<Resource>> resources = fileToIdConverter.listMatchingResourceStacks(manager);
        var ops = registryLookup.createSerializationContext(JsonOps.INSTANCE);

        for (Map.Entry<ResourceLocation, List<Resource>> stackEntry : resources.entrySet()) {
            for (Resource entry : stackEntry.getValue()) {
                ResourceLocation fileId = stackEntry.getKey();
                ResourceLocation idFile = fileToIdConverter.fileToId(fileId);

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

