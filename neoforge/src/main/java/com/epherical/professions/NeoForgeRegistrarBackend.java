package com.epherical.professions;

import com.epherical.professions.core.register.IRegistrarBackend;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Uses one DeferredRegister per vanilla Registry.
 */
public final class NeoForgeRegistrarBackend implements IRegistrarBackend {

    private final Map<ResourceKey<?>, List<Pending<?>>> pending = new ConcurrentHashMap<>();

    @Override
    public <T> T register(ResourceKey<Registry<T>> registryKey,
                          Identifier idPath,
                                    T factory) {


        pending.computeIfAbsent(registryKey, k -> new ArrayList<>())
                .add(new Pending<>(idPath, factory));
        return factory;
    }


    @SuppressWarnings("unchecked")
    public <T> void onRegister(RegisterEvent event) {
        ResourceKey<? extends Registry<?>> key = event.getRegistryKey();
        List<Pending<?>> list = pending.remove(key);
        if (list == null) return;

        event.register((ResourceKey<Registry<T>>) key, helper -> {
            for (Pending<?> p : list) {
                Pending<T> entry = (Pending<T>) p;
                helper.register(entry.idPath, entry.instance);
            }
        });
    }

    private record Pending<T>(Identifier idPath, T instance) {
    }


}
