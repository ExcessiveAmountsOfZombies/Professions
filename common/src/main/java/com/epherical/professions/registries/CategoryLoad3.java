package com.epherical.professions.registries;

import com.epherical.professions.ProfessionCategoryManager;
import com.epherical.professions.core.ProfessionCategory;
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

    public CategoryLoad3(ProfessionCategoryManager categoryManager) {
        this.categoryManager = categoryManager;
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
                .thenAcceptAsync(categoryManager::reloadCategories, gameThread);
    }

    private Map<ResourceLocation, ProfessionCategory> decodeAll(ResourceManager manager) {
        Map<ResourceLocation, ProfessionCategory> categories = new LinkedHashMap<>();
        FileToIdConverter fileToIdConverter = FileToIdConverter.json(PATH);
        Map<ResourceLocation, List<Resource>> resources = fileToIdConverter.listMatchingResourceStacks(manager);

        for (Map.Entry<ResourceLocation, List<Resource>> stackEntry : resources.entrySet()) {
            for (Resource entry : stackEntry.getValue()) {
                ResourceLocation fileId = stackEntry.getKey();
                ResourceLocation id = fileToIdConverter.fileToId(fileId);

                try (Reader reader = entry.openAsReader()) {
                    JsonElement element = GsonHelper.fromJson(GSON, reader, JsonElement.class);
                    ProfessionCategory category = ProfessionCategory.CODEC.parse(JsonOps.INSTANCE, element)
                            .resultOrPartial(message -> LOGGER.error("Failed to decode profession category from {}: {}", fileId, message))
                            .orElse(null);

                    if (category != null) {
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
