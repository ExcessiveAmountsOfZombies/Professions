package com.epherical.professions.core.register;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * One implementation per platform.  Discovered through ServiceLoader.
 */
public interface IRegistrarBackend {

    <T> T register(ResourceKey<Registry<T>> registryKey,
                             ResourceLocation id,
                             T object);

}
