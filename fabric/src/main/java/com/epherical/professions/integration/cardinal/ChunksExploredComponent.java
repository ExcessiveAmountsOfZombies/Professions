package com.epherical.professions.integration.cardinal;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.UUID;

public class ChunksExploredComponent implements Component {

    private CompoundTag tag = new CompoundTag();


    public ChunksExploredComponent(ChunkAccess access) {

    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        this.tag = tag.getCompound("prfVisited");
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.put("prfVisited", this.tag);
    }

    // I'd imagine if there were to ever be a large server that used this mod
    // this method would be impractical
    public boolean hasPlayerExploredChunk(UUID uuid) {
        return tag.contains(uuid.toString());
    }

    public void addPlayerToChunk(UUID uuid) {
        tag.putBoolean(uuid.toString(), true);
    }
}
