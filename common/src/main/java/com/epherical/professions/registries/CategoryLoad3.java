package com.epherical.professions.registries;

import com.epherical.professions.ProfessionCategoryManager;
import com.epherical.professions.PlayerManager;
import com.epherical.professions.core.ProfessionCategory;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class CategoryLoad3 {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String PATH = "professions/categories";

    private final ProfessionCategoryManager categoryManager;
    private final PlayerManager playerManager;

    public CategoryLoad3(ProfessionCategoryManager categoryManager, PlayerManager playerManager) {
        this.categoryManager = categoryManager;
        this.playerManager = playerManager;
    }

    public CompletableFuture<Void> reload(HolderLookup.Provider registryLookup,
                                          @NotNull PreparableReloadListener.SharedState sharedState,
                                          @NotNull Executor taskExecutor,
                                          @NotNull PreparableReloadListener.PreparationBarrier preparationBarrier,
                                          @NotNull Executor reloadExecutor) {
        return CompletableFuture
                .supplyAsync(() -> decodeAll(sharedState.resourceManager()), taskExecutor)
                .thenCompose(preparationBarrier::wait)
                .thenAcceptAsync(categoryManager::reloadCategories, reloadExecutor);
    }

    public CompletableFuture<Void> reload(HolderLookup.Provider registryLookup,
                                          @NotNull PreparableReloadListener.PreparationBarrier barrier,
                                          @NotNull ResourceManager resourceManager,
                                          @NotNull ProfilerFiller prepProfiler,
                                          @NotNull ProfilerFiller applyProfiler,
                                          @NotNull Executor background,
                                          @NotNull Executor gameThread) {
        return CompletableFuture
                .supplyAsync(() -> decodeAll(resourceManager), background)
                .thenCompose(barrier::wait)
                .thenApplyAsync(categoryManager::reloadCategories, gameThread)
                .thenAcceptAsync(playerManager::refreshProfessionCategories, gameThread);
    }

    private Map<Identifier, ProfessionCategory> decodeAll(ResourceManager manager) {
        Map<Identifier, ProfessionCategory> categories = new LinkedHashMap<>();
        FileToIdConverter fileToIdConverter = FileToIdConverter.json(PATH);
        Map<Identifier, List<Resource>> resources = fileToIdConverter.listMatchingResourceStacks(manager);

        // todo; we need to write a system for merging the categories together now
        //  it can either merge the professions and the features
        //  or myabe just something else. not sure.
        //  either way, needs merging capability.
        //  priority/full replace functionality?


        for (Map.Entry<Identifier, List<Resource>> stackEntry : resources.entrySet()) {
            for (Resource entry : stackEntry.getValue()) {
                Identifier fileId = stackEntry.getKey();
                Identifier id = fileToIdConverter.fileToId(fileId);

                try (Reader reader = entry.openAsReader()) {
                    JsonElement element = GsonHelper.fromJson(GSON, reader, JsonElement.class);
                    ProfessionCategory category = ProfessionCategory.CODEC.parse(JsonOps.INSTANCE, element)
                            .resultOrPartial(message -> LOGGER.error("Failed to decode profession category from {}: {}", fileId, message))
                            .orElse(null);

                    if (category != null) {
                        category.setId(id);
                        if (categories.put(id, category) != null) {
                            LOGGER.debug("Overrode profession category {} from resource {}", id, fileId);
                        } else {
                            LOGGER.debug("Successfully decoded profession category for file: {}", fileId);
                        }
                    }
                } catch (IllegalArgumentException | IOException e) {
                    LOGGER.error("Couldn't load resource {}", id, e);
                }
            }
        }

        return categories;
    }

    public ProfessionCategoryManager getCategoryManager() {
        return categoryManager;
    }
}
