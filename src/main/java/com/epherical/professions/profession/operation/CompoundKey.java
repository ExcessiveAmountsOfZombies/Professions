package com.epherical.professions.profession.operation;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class CompoundKey<T> {

    private ResourceKey<Registry<T>> registry;
    private ResourceLocation key;

    public CompoundKey(ResourceKey<Registry<T>> registry, ResourceLocation key) {
        this.registry = registry;
        this.key = key;
    }

    public ResourceKey<Registry<T>> getRegistry() {
        return registry;
    }

    public ResourceLocation getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompoundKey<?> that = (CompoundKey<?>) o;

        if (!Objects.equals(registry, that.registry)) return false;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        int result = registry != null ? registry.hashCode() : 0;
        result = 31 * result + (key != null ? key.hashCode() : 0);
        return result;
    }
}
