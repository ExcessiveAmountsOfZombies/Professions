package com.epherical.professions.datagen.perks;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public final class FarmingPerkProvider extends AbstractProfessionPerkProvider {

    private static final double[] MOVEMENT_SPEED_VALUES = {0.001D, 0.001D, 0.001D, 0.001D, 0.001D, 0.001D, 0.001D, 0.001D, 0.001D, 0.001D};

    public FarmingPerkProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, "farming", "Farming",
                Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_VALUES, "movement_speed_flat",
                "Gain additional movement speed.", "Field Stride ", Items.WHEAT, "Seasoned Farmer ");
    }
}
