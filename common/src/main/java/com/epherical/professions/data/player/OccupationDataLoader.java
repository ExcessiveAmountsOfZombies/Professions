package com.epherical.professions.data.player;

import com.epherical.professions.model.Occupation;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class OccupationDataLoader {

    protected final Path baseDirectory;

    protected OccupationDataLoader(Path baseDirectory) {
        this.baseDirectory = baseDirectory;
    }


    public abstract CompletableFuture<PlayerOccupationData> load(UUID uuid);

    public abstract CompletableFuture<Map<UUID, PlayerOccupationData>> loadAll();

    public abstract CompletableFuture<Void> save(UUID uuid, Collection<Occupation> occupations, @Nullable Identifier professionCategoryId);

}
