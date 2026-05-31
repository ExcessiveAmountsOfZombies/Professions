package com.epherical.professions.registries;

import com.epherical.professions.ActionManager;
import com.epherical.professions.api.actions.Action;
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
        Map<ResourceLocation, List<Resource>> resources = fileToIdConverter.listMatchingResourceStacks(manager);
        var ops = registryLookup.createSerializationContext(JsonOps.INSTANCE);

        for (Map.Entry<ResourceLocation, List<Resource>> stackEntry : resources.entrySet()) {
            for (Resource entry : stackEntry.getValue()) {
                ResourceLocation fileId = stackEntry.getKey();
                ResourceLocation idFile = fileToIdConverter.fileToId(fileId);
                // todo; this would be a map with all the actions that are tied to that particular file
                //  that means we can build a way to merge content together. not right now cause im not sure how i want to do it.
                //  at least now i think content can be overridden.

                try (Reader reader = entry.openAsReader()) {
                    JsonElement element = GsonHelper.fromJson(GSON, reader, JsonElement.class);
                    Action<?> action = Action.TYPED_CODEC.parse(ops, element)
                            .resultOrPartial(message -> LOGGER.error("Failed to decode action from {}: {}", fileId, message))
                            .orElse(null);

                    if (action != null) {
                        action.setId(idFile);
                        LOGGER.debug("Successfully decoded action for file: {}", fileId);
                        actions.add(action);
                    }
                } catch (IllegalArgumentException | IOException e) {
                    LOGGER.error("Couldn't load resource {}", idFile, e);
                }
            }
        }
        return actions;
    }

    public ActionManager getActionManager() {
        return actionManager;
    }
}
