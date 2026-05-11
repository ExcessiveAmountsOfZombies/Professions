package com.epherical.professions.datagen.perks;

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
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.epherical.professions.datagen.ProfessionDataGeneration.id;
import static com.epherical.professions.datagen.ProfessionDataGeneration.rl;

abstract class AbstractProfessionPerkProvider implements DataProvider {

    protected static final int[] TIER_LEVEL_REQUIREMENTS = {100, 200, 400, 800, 1000, 1500, 2000, 4000, 5000, 10000};
    protected static final double[] EXP_GAIN_VALUES = {0.01D, 0.02D, 0.02D, 0.02D, 0.02D, 0.02D, 0.02D, 0.02D, 0.02D, 0.05D};

    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;
    private final String professionId;
    private final String displayName;
    private final Holder<Attribute> attribute;
    private final double[] attributeValues;
    private final String attributePathPrefix;
    private final String attributeDescriptionPrefix;
    private final String attributePerkNamePrefix;
    private final Item attributeIcon;
    private final String expPerkNamePrefix;

    protected AbstractProfessionPerkProvider(PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            String professionId, String displayName, Holder<Attribute> attribute, double[] attributeValues,
            String attributePathPrefix, String attributeDescriptionPrefix, String attributePerkNamePrefix, Item attributeIcon, String expPerkNamePrefix) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "professions/perks/" + professionId);
        this.lookupProvider = lookupProvider;
        this.professionId = professionId;
        this.displayName = displayName;
        this.attribute = attribute;
        this.attributeValues = attributeValues;
        this.attributePathPrefix = attributePathPrefix;
        this.attributeDescriptionPrefix = attributeDescriptionPrefix;
        this.attributePerkNamePrefix = attributePerkNamePrefix;
        this.attributeIcon = attributeIcon;
        this.expPerkNamePrefix = expPerkNamePrefix;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return lookupProvider.thenCompose(registries -> {
            Holder<Profession> profession = registries.lookupOrThrow(ProfessionsCommon.PROFESSION_REGISTRY_KEY)
                    .get(id(professionId))
                    .orElseThrow(() -> new IllegalStateException("Missing " + professionId + " profession for " + professionId + " perk datagen"));

            List<CompletableFuture<?>> writes = new ArrayList<>();
            for (int tierIndex = 0; tierIndex < TIER_LEVEL_REQUIREMENTS.length; tierIndex++) {
                int tierNumber = tierIndex + 1;
                int levelRequirement = TIER_LEVEL_REQUIREMENTS[tierIndex];
                String tierRoman = toRomanNumeral(tierNumber);

                double attributeAmount = attributeValues[tierIndex];
                writes.add(save(output, registries, attributePathPrefix + "_tier_" + tierNumber,
                        new PerkAttribute(new Perk.Common(
                                profession,
                                attributeAmount,
                                Perk.ModificationStage.FLAT,
                                attributeDescriptionPrefix + " (" + attributeAmount + ")",
                                attributePerkNamePrefix + tierRoman,
                                Optional.empty(),
                                attributeIcon,
                                levelRequirement
                        ), attribute)));

                double expGainAmount = EXP_GAIN_VALUES[tierIndex];
                int expPercent = (int) (expGainAmount * 100);
                writes.add(save(output, registries, "profession_exp_gain_additive_tier_" + tierNumber,
                        new PerkProfessionGainEXP(new Perk.Common(
                                profession,
                                expGainAmount,
                                Perk.ModificationStage.ADDITIVE,
                                "Increase profession experience gain by " + expPercent + "%.",
                                expPerkNamePrefix + tierRoman,
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
        return "Professions " + displayName + " Perk Provider";
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
