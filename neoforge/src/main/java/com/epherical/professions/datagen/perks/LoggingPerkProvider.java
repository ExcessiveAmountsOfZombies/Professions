package com.epherical.professions.datagen.perks;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public final class LoggingPerkProvider extends AbstractProfessionPerkProvider {

    private static final double[] LUCK_VALUES = {0.5D, 0.5D, 0.5D, 0.5D, 0.5D, 0.5D, 0.5D, 0.5D, 0.5D, 0.5D};

    public LoggingPerkProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, "logging", "Logging", Attributes.LUCK, LUCK_VALUES, "luck_flat",
                "Gain additional luck.", "Logger's Fortune ", Items.OAK_SAPLING, "Seasoned Logger ");
    }
}
