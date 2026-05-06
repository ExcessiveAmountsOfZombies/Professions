package com.epherical.professions.core.register;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

/**
 * One implementation per platform.  Discovered through ServiceLoader.
 */
public interface IRegistrarBackend {

    <T> T register(ResourceKey<Registry<T>> registryKey,
                             Identifier id,
                             T object);

}
