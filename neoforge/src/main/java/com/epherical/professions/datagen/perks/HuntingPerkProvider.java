package com.epherical.professions.datagen.perks;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public final class HuntingPerkProvider extends AbstractProfessionPerkProvider {

    private static final double[] SNEAKING_SPEED_VALUES = {0.01D, 0.01D, 0.01D, 0.01D, 0.01D, 0.01D, 0.01D, 0.01D, 0.01D, 0.01D};

    public HuntingPerkProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, "hunting", "Hunting", Attributes.SNEAKING_SPEED, SNEAKING_SPEED_VALUES,
                "sneaking_speed_flat", "Gain additional sneaking speed.", "Silent Hunter ", Items.BOW, "Seasoned Hunter ");
    }
}
