package com.epherical.professions.registries;

import com.epherical.professions.ActionManager;
import com.epherical.professions.core.actions.Action;
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

public class ActionLoad3 {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String PATH = "professions/actions";

    private final ActionManager actionManager;

    public ActionLoad3(ActionManager actionManager) {
        this.actionManager = actionManager;
    }

    public CompletableFuture<Void> reload(HolderLookup.Provider registryLookup,
                                          @NotNull PreparableReloadListener.PreparationBarrier barrier,
                                          @NotNull ResourceManager resourceManager,
                                          @NotNull ProfilerFiller prepProfiler,
                                          @NotNull ProfilerFiller applyProfiler,
                                          @NotNull Executor background,
                                          @NotNull Executor gameThread) {
        actionManager.setRegistryLookup(registryLookup);
        return CompletableFuture
                .supplyAsync(() -> decodeAll(resourceManager, registryLookup), background)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(actionManager::reloadActions, gameThread);
    }

    private List<Action<?>> decodeAll(ResourceManager manager, HolderLookup.Provider registryLookup) {
        List<Action<?>> actions = new ArrayList<>();
        FileToIdConverter fileToIdConverter = FileToIdConverter.json(PATH);
        Map<ResourceLocation, Resource> resources = fileToIdConverter.listMatchingResources(manager);
        var ops = registryLookup.createSerializationContext(JsonOps.INSTANCE);

        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            ResourceLocation fileId = entry.getKey();
            ResourceLocation idFile = fileToIdConverter.fileToId(fileId);

            try (Reader reader = entry.getValue().openAsReader()) {
                JsonElement element = GsonHelper.fromJson(GSON, reader, JsonElement.class);
                Action<?> action = Action.TYPED_CODEC.parse(ops, element)
                        .resultOrPartial(message -> LOGGER.error("Failed to decode action from {}: {}", fileId, message))
                        .orElse(null);

                if (action != null) {
                    LOGGER.debug("Successfully decoded action for file: {}", fileId);
                    actions.add(action);
                }
            } catch (IllegalArgumentException | IOException e) {
                LOGGER.error("Couldn't load resource {}", idFile, e);
            }
        }
        return actions;
    }

    public ActionManager getActionManager() {
        return actionManager;
    }
}
