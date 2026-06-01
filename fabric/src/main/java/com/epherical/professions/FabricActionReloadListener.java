package com.epherical.professions;

import com.epherical.professions.registries.ActionLoad3;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class FabricActionReloadListener implements PreparableReloadListener {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "actions");

    private final ActionLoad3 delegate;
    private final HolderLookup.Provider registries;

    public FabricActionReloadListener(ActionLoad3 delegate, HolderLookup.Provider registries) {
        this.delegate = delegate;
        this.registries = registries;
    }

    @Override
    public CompletableFuture<Void> reload(@NotNull PreparableReloadListener.SharedState sharedState,
                                          @NotNull Executor taskExecutor,
                                          @NotNull PreparableReloadListener.PreparationBarrier preparationBarrier,
                                          @NotNull Executor reloadExecutor) {
        return delegate.reload(registries, sharedState, taskExecutor, preparationBarrier, reloadExecutor);
    }
}
