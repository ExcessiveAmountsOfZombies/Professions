package com.epherical.professions.datagen.perks;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public final class BuildingPerkProvider extends AbstractProfessionPerkProvider {

    private static final double[] MAX_HEALTH_VALUES = {1.0D, 1.0D, 2.0D, 2.0D, 2.0D, 2.0D, 2.0D, 2.0D, 2.0D, 2.0D};

    public BuildingPerkProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, "building", "Building", Attributes.MAX_HEALTH, MAX_HEALTH_VALUES,
                "max_health_flat", "Gain additional max health.", "Builder's Vitality ", Items.OAK_PLANKS, "Master Builder "
        );
    }
}
