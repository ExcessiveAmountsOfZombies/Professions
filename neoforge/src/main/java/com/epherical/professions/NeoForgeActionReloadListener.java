package com.epherical.professions;

import com.epherical.professions.registries.ActionLoad3;
import net.neoforged.neoforge.resource.ContextAwareReloadListener;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class NeoForgeActionReloadListener extends ContextAwareReloadListener {

    private final ActionLoad3 delegate;

    public NeoForgeActionReloadListener(ActionLoad3 delegate) {
        this.delegate = delegate;
    }

    @Override
    public @NotNull CompletableFuture<Void> reload(@NotNull SharedState sharedState,
                                                    @NotNull Executor taskExecutor,
                                                    @NotNull PreparationBarrier preparationBarrier,
                                                    @NotNull Executor reloadExecutor) {
        return delegate.reload(getRegistryLookup(), sharedState, taskExecutor, preparationBarrier, reloadExecutor);
    }
}
