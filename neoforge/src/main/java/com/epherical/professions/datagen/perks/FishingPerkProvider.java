package com.epherical.professions.datagen.perks;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public final class FishingPerkProvider extends AbstractProfessionPerkProvider {

    private static final double[] LUCK_VALUES = {0.5D, 0.5D, 0.5D, 0.5D, 0.5D, 0.5D, 0.5D, 0.5D, 0.5D, 0.5D};

    public FishingPerkProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, "fishing", "Fishing", Attributes.LUCK,
                LUCK_VALUES, "luck_flat", "Gain additional luck.",
                "Angler's Fortune ",
                Items.FISHING_ROD,
                "Seasoned Angler ");
    }
}
