package com.epherical.professions.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

public interface ChunkVisited extends INBTSerializable<CompoundTag> {

    boolean hasPlayerExploredChunk(UUID uuid);

    void addPlayerToChunk(UUID uuid);
}
