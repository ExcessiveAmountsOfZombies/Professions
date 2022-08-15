package com.epherical.professions.capability.impl;

import com.epherical.professions.capability.ChunkVisited;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import java.util.UUID;

public class ChunkVisitedImpl implements ChunkVisited {

    public static Capability<ChunkVisited> CHUNK_VISITED_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private CompoundTag tag = new CompoundTag();

    @Override
    public CompoundTag serializeNBT() {
       CompoundTag tag = new CompoundTag();
       tag.put("prfVisited", this.tag);
       return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.tag = nbt.getCompound("prfVisited");

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
