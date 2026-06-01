package com.epherical.professions;

import com.epherical.professions.core.register.IRegistrarBackend;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

public final class FabricRegistrarBackend implements IRegistrarBackend {

    @Override
    public <T> T register(ResourceKey<Registry<T>> registryKey, Identifier id, T object) {
        Registry.register(getRegistry(registryKey), id, object);
        return object;
    }

    private static <T> Registry<T> getRegistry(ResourceKey<Registry<T>> registryKey) {
        return FabricProfessionsMod.getRegistry(registryKey);
    }
}
