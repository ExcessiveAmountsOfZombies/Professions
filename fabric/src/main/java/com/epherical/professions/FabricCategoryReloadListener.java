package com.epherical.professions;

import com.epherical.professions.registries.CategoryLoad3;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class FabricCategoryReloadListener implements IdentifiableResourceReloadListener {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "categories");

    private final CategoryLoad3 delegate;
    private final HolderLookup.Provider registries;

    public FabricCategoryReloadListener(CategoryLoad3 delegate, HolderLookup.Provider registries) {
        this.delegate = delegate;
        this.registries = registries;
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    @Override
    public Collection<ResourceLocation> getFabricDependencies() {
        return List.of(ResourceReloadListenerKeys.TAGS);
    }

    @Override
    public CompletableFuture<Void> reload(@NotNull PreparableReloadListener.PreparationBarrier barrier,
                                          @NotNull ResourceManager resourceManager,
                                          @NotNull ProfilerFiller prepProfiler,
                                          @NotNull ProfilerFiller applyProfiler,
                                          @NotNull Executor background,
                                          @NotNull Executor gameThread) {
        return delegate.reload(registries, barrier, resourceManager, prepProfiler, applyProfiler, background, gameThread);
    }
}
