package com.epherical.professions.integration.cardinal;

import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentInitializer;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import net.minecraft.resources.ResourceLocation;

public class ChunkComponent implements ChunkComponentInitializer {

    public static final ComponentKey<ChunksExploredComponent> CHUNKS_EXPLORED = ComponentRegistryV3.INSTANCE.getOrCreate(
            new ResourceLocation("professions:p_expo_chunk"), ChunksExploredComponent.class);

    @Override
    public void registerChunkComponentFactories(ChunkComponentFactoryRegistry registry) {
        registry.register(CHUNKS_EXPLORED, ChunksExploredComponent::new);
    }
}
