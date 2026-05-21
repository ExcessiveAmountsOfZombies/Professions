package com.epherical.professions.registries;

import com.epherical.professions.GateManager;
import com.epherical.professions.model.gating.Gate;
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

public class GateLoad3 {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String PATH = "professions/gates";

    private final GateManager gateManager;

    public GateLoad3(GateManager gateManager) {
        this.gateManager = gateManager;
    }

    public CompletableFuture<Void> reload(HolderLookup.Provider registryLookup,
                                          @NotNull PreparableReloadListener.PreparationBarrier barrier,
                                          @NotNull ResourceManager resourceManager,
                                          @NotNull ProfilerFiller prepProfiler,
                                          @NotNull ProfilerFiller applyProfiler,
                                          @NotNull Executor background,
                                          @NotNull Executor gameThread) {
        gateManager.setRegistryLookup(registryLookup);
        return CompletableFuture
                .supplyAsync(() -> decodeAll(resourceManager, registryLookup), background)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(gateManager::reloadGates, gameThread);
    }

    private List<Gate<?>> decodeAll(ResourceManager manager, HolderLookup.Provider registryLookup) {
        List<Gate<?>> gates = new ArrayList<>();
        FileToIdConverter fileToIdConverter = FileToIdConverter.json(PATH);
        Map<ResourceLocation, List<Resource>> resources = fileToIdConverter.listMatchingResourceStacks(manager);
        var ops = registryLookup.createSerializationContext(JsonOps.INSTANCE);

        for (Map.Entry<ResourceLocation, List<Resource>> stackEntry : resources.entrySet()) {
            for (Resource entry : stackEntry.getValue()) {
                ResourceLocation fileId = stackEntry.getKey();
                ResourceLocation idFile = fileToIdConverter.fileToId(fileId);

                try (Reader reader = entry.openAsReader()) {
                    JsonElement element = GsonHelper.fromJson(GSON, reader, JsonElement.class);
                    Gate<?> gate = Gate.TYPED_CODEC.parse(ops, element)
                            .resultOrPartial(message -> LOGGER.error("Failed to decode gate from {}: {}", fileId, message))
                            .orElse(null);

                    if (gate != null) {
                        gate.setId(idFile);
                        LOGGER.debug("Successfully decoded gate for file: {}", fileId);
                        gates.add(gate);
                    }
                } catch (IllegalArgumentException | IOException e) {
                    LOGGER.error("Couldn't load resource {}", idFile, e);
                }
            }
        }

        return gates;
    }

    public GateManager getGateManager() {
        return gateManager;
    }
}
