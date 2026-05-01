package com.epherical.professions.data.player;

import com.epherical.professions.model.Occupation;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record PlayerOccupationData(List<Occupation> occupations, @Nullable ResourceLocation professionCategoryId) {

    public static PlayerOccupationData empty() {
        return new PlayerOccupationData(List.of(), null);
    }
}
