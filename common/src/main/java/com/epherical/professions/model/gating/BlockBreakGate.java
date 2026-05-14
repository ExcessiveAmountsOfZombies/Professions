package com.epherical.professions.model.gating;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

public class BlockBreakGate extends LevelGate<Block> {



    @Override
    public GateType getGateType() {
        return null;
    }

    @Override
    public ResourceKey<? extends Registry<Block>> getRegistryKey() {
        return Registries.BLOCK;
    }
}
