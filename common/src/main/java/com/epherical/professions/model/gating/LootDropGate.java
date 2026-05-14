package com.epherical.professions.model.gating;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class LootDropGate<T> extends LevelGate<T> {



    // todo; this could be an entity or a block i think.
    //  it might be easier to do this as two separate gates, but we'll have to wait until implementation to know
    //


    @Override
    public GateType getGateType() {
        return null;
    }

    @Override
    public ResourceKey<? extends Registry<T>> getRegistryKey() {
        return null;
    }
}
