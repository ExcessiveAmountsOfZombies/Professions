package com.epherical.professions.util.mixins;

import net.minecraftforge.common.util.LazyOptional;

public interface CapabilityMixinHelper<T> {

    LazyOptional<T> professions$getValue();

    void professions$setValue(LazyOptional<T> value);
}
