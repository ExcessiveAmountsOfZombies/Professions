package com.epherical.professions;

import com.epherical.professions.registries.PerkLoad3;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.resource.ContextAwareReloadListener;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class NeoForgePerkReloadListener extends ContextAwareReloadListener {

    private final PerkLoad3 delegate;

    public NeoForgePerkReloadListener(PerkLoad3 delegate) {
        this.delegate = delegate;
    }

    @Override
    public CompletableFuture<Void> reload(@NotNull PreparableReloadListener.PreparationBarrier barrier,
                                          @NotNull ResourceManager resourceManager,
                                          @NotNull ProfilerFiller prepProfiler,
                                          @NotNull ProfilerFiller applyProfiler,
                                          @NotNull Executor background,
                                          @NotNull Executor gameThread) {
        return delegate.reload(getRegistryLookup(), barrier, resourceManager, prepProfiler, applyProfiler, background, gameThread);
    }
}

