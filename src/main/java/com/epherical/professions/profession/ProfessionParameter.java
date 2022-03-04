package com.epherical.professions.profession;

import net.minecraft.resources.ResourceLocation;

public record ProfessionParameter<T>(ResourceLocation name) {

    @Override
    public String toString() {
        return "ProfessionParameter{" +
                "name=" + name +
                '}';
    }
}
