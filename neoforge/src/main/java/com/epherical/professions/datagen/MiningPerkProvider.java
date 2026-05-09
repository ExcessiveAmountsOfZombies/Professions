package com.epherical.professions.datagen;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.core.Profession;
import com.epherical.professions.model.perks.Perk;
import com.epherical.professions.model.perks.PerkAttribute;
import com.epherical.professions.model.perks.PerkProfessionGainEXP;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.epherical.professions.datagen.ProfessionDataGeneration.id;
import static com.epherical.professions.datagen.ProfessionDataGeneration.rl;

final class MiningPerkProvider implements DataProvider {

    private static final int[] TIER_LEVEL_REQUIREMENTS = {100, 200, 400, 800, 1000, 1500, 2000, 4000, 5000, 10000};
    private static final double[] MAX_HEALTH_VALUES = {0.5D, 0.5D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D};
    private static final double[] EXP_GAIN_VALUES = {0.01D, 0.02D, 0.02D, 0.02D, 0.02D, 0.02D, 0.02D, 0.02D, 0.02D, 0.05D};

    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

    MiningPerkProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "professions/perks/mining");
        this.lookupProvider = lookupProvider;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return lookupProvider.thenCompose(registries -> {
            Holder<Profession> miningProfession = registries.lookupOrThrow(ProfessionsCommon.PROFESSION_REGISTRY_KEY)
                    .get(id("mining"))
                    .orElseThrow(() -> new IllegalStateException("Missing mining profession for mining perk datagen"));

            List<CompletableFuture<?>> writes = new ArrayList<>();
            for (int tierIndex = 0; tierIndex < TIER_LEVEL_REQUIREMENTS.length; tierIndex++) {
                int tierNumber = tierIndex + 1;
                int levelRequirement = TIER_LEVEL_REQUIREMENTS[tierIndex];
                String tierRoman = toRomanNumeral(tierNumber);

                double maxHealthAmount = MAX_HEALTH_VALUES[tierIndex];
                writes.add(save(output, registries, "max_health_flat_tier_" + tierNumber,
                        new PerkAttribute(new Perk.Common(
                                miningProfession,
                                maxHealthAmount,
                                Perk.ModificationStage.FLAT,
                                "Gain additional max health. (" + maxHealthAmount + ")",
                                "Heart of Stone " + tierRoman,
                                Optional.empty(),
                                Items.GOLDEN_APPLE,
                                levelRequirement
                        ), Attributes.MAX_HEALTH)));

                double expGainAmount = EXP_GAIN_VALUES[tierIndex];
                int expPercent = (int) (expGainAmount * 100);
                writes.add(save(output, registries, "profession_exp_gain_additive_tier_" + tierNumber,
                        new PerkProfessionGainEXP(new Perk.Common(
                                miningProfession,
                                expGainAmount,
                                Perk.ModificationStage.ADDITIVE,
                                "Increase profession experience gain by " + expPercent + "%.",
                                "Deep Delver " + tierRoman,
                                Optional.empty(),
                                Items.EXPERIENCE_BOTTLE,
                                levelRequirement
                        ))));
            }

            return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName() {
        return "Professions Mining Perk Provider";
    }

    private CompletableFuture<?> save(CachedOutput output, HolderLookup.Provider registries, String path, Perk perk) {
        return DataProvider.saveStable(output, registries, Perk.TYPED_CODEC, perk, pathProvider.json(rl(path)));
    }

    private static String toRomanNumeral(int value) {
        return switch (value) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> Integer.toString(value);
        };
    }
}
