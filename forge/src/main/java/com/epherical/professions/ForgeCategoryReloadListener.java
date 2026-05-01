package com.epherical.professions;

import com.epherical.professions.registries.CategoryLoad3;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class ForgeCategoryReloadListener implements PreparableReloadListener {

    private final CategoryLoad3 delegate;
    private final HolderLookup.Provider registries;

    public ForgeCategoryReloadListener(CategoryLoad3 delegate, HolderLookup.Provider registries) {
        this.delegate = delegate;
        this.registries = registries;
    }

    @Override
    public CompletableFuture<Void> reload(@NotNull PreparationBarrier barrier,
                                          @NotNull ResourceManager resourceManager,
                                          @NotNull ProfilerFiller prepProfiler,
                                          @NotNull ProfilerFiller applyProfiler,
                                          @NotNull Executor background,
                                          @NotNull Executor gameThread) {
        return delegate.reload(registries, barrier, resourceManager, prepProfiler, applyProfiler, background, gameThread);
    }
}
