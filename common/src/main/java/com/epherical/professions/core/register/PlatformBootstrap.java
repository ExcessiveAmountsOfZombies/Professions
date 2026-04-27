package com.epherical.professions.core.register;

import com.epherical.professions.ProfessionsCommon;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class PlatformBootstrap {

    private static IRegistrarBackend backend;

    public static void init(IRegistrarBackend impl) {
        if (backend != null) throw new IllegalStateException("PlatformBootstrap already initialised");
        backend = impl;
    }


    public static IRegistrarBackend backend() {
        if (backend == null) throw new IllegalStateException("PlatformBootstrap not initialised yet");
        return backend;
    }

    public static <T> T register(ResourceKey<Registry<T>> registryKey,
                                           String path,
                                           T object) {
        return backend().register(registryKey, ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, path), object);
    }

}
