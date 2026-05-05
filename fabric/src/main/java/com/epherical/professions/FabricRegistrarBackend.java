package com.epherical.professions;

import com.epherical.professions.core.register.IRegistrarBackend;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class FabricRegistrarBackend implements IRegistrarBackend {

    @Override
    public <T> T register(ResourceKey<Registry<T>> registryKey, ResourceLocation id, T object) {
        Registry.register(getRegistry(registryKey), id, object);
        return object;
    }

    @SuppressWarnings("unchecked")
    private static <T> Registry<T> getRegistry(ResourceKey<Registry<T>> registryKey) {
        Registry<?> registry = BuiltInRegistries.REGISTRY.get(registryKey.location());
        if (registry == null) {
            throw new IllegalStateException("Unable to find registry " + registryKey.location());
        }
        return (Registry<T>) registry;
    }
}
