package com.epherical.professions.registries;

import com.epherical.professions.ActionManager;
import com.epherical.professions.core.actions.Action;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.RegistryOps;
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

public class ActionLoad3 implements PreparableReloadListener {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String PATH = "professions/actions";

    private int loadedActionFiles;

    private final RegistryAccess access;
    private final ActionManager actionManager;

    public ActionLoad3(RegistryAccess registryAccess, ActionManager actionManager) {
        this.access = registryAccess;
        this.actionManager = actionManager;
    }


    @Override
    public CompletableFuture<Void> reload(@NotNull PreparationBarrier barrier,
                                          @NotNull ResourceManager resourceManager,
                                          @NotNull ProfilerFiller prepProfiler,
                                          @NotNull ProfilerFiller applyProfiler,
                                          @NotNull Executor background,
                                          @NotNull Executor gameThread) {


        return CompletableFuture
                .supplyAsync(() -> decodeAll(resourceManager), background)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(actionManager::reloadActions, gameThread);
    }

    private List<Action<?>> decodeAll(ResourceManager manager) {
        List<Action<?>> actions = new ArrayList<>();


        FileToIdConverter fileToIdConverter = FileToIdConverter.json(PATH);
        Map<ResourceLocation, Resource> resourceLocationResourceMap = fileToIdConverter.listMatchingResources(manager);

        for (Map.Entry<ResourceLocation, Resource> entry : resourceLocationResourceMap.entrySet()) {
            ResourceLocation fileId = entry.getKey();
            ResourceLocation idFile = fileToIdConverter.fileToId(fileId);

            try (Reader reader = entry.getValue().openAsReader()) {
                JsonElement element = GsonHelper.fromJson(GSON, reader, JsonElement.class);
                Action.TYPED_CODEC.decode(RegistryOps.create(JsonOps.INSTANCE, access), element)
                        .ifError(pairError -> {
                            LOGGER.error("Failed to decode action for file: {}", fileId);
                        })
                        .ifSuccess(actionJsonElementPair -> {
                            LOGGER.info("Successfully decoded action for file: {}", fileId);
                            Action<?> action = actionJsonElementPair.getFirst();
                            actions.add(action);
                        });


            } catch (IllegalArgumentException | IOException e) {
                // todo; better error.
                LOGGER.error("Couldn't load resource " + idFile, e);
            }
        }
        return actions;
    }

}
