package com.epherical.professions.data.player;

import com.epherical.professions.core.progression.Occupation;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class OccupationDataLoader {

    protected final Path baseDirectory;

    protected OccupationDataLoader(Path baseDirectory) {
        this.baseDirectory = baseDirectory;
    }


    public abstract CompletableFuture<List<Occupation>> load(UUID uuid);

    public abstract CompletableFuture<Map<UUID, List<Occupation>>> loadAll();

    public abstract CompletableFuture<Void> save(UUID uuid, Collection<Occupation> data);

}
