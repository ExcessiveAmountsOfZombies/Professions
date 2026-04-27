package com.epherical.professions.api.event;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record EventKey<E extends ProfessionEvent>(ResourceLocation name, Class<E> type) {

    public EventKey {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(type, "type");
    }
}
