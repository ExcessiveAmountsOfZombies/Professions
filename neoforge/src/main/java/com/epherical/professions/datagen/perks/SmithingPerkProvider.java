package com.epherical.professions.datagen.perks;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public final class SmithingPerkProvider extends AbstractProfessionPerkProvider {

    private static final double[] ATTACK_DAMAGE_VALUES = {1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D};

    public SmithingPerkProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, "smithing", "Smithing", Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_VALUES,
                "attack_damage_flat", "Gain additional attack damage.", "Forge Might ", Items.ANVIL, "Seasoned Smith ");
    }
}
