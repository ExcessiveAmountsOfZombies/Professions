package com.epherical.professions;

import com.epherical.professions.registries.GateLoad3;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.resource.ContextAwareReloadListener;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class NeoForgeGateReloadListener extends ContextAwareReloadListener {

    private final GateLoad3 delegate;

    public NeoForgeGateReloadListener(GateLoad3 delegate) {
        this.delegate = delegate;
    }

    @Override
    public CompletableFuture<Void> reload(@NotNull SharedState sharedState,
                                          @NotNull Executor taskExecutor,
                                          @NotNull PreparationBarrier preparationBarrier,
                                          @NotNull Executor reloadExecutor) {
        return delegate.reload(getRegistryLookup(), sharedState, taskExecutor, preparationBarrier, reloadExecutor);
    }
}
