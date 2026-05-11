package com.epherical.professions.datagen.perks;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public final class MiningPerkProvider extends AbstractProfessionPerkProvider {

    private static final double[] MAX_HEALTH_VALUES = {0.5D, 0.5D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D};

    public MiningPerkProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, "mining", "Mining", Attributes.MAX_HEALTH, MAX_HEALTH_VALUES,
                "max_health_flat", "Gain additional max health.", "Heart of Stone ", Items.GOLDEN_APPLE, "Deep Delver ");
    }
}
