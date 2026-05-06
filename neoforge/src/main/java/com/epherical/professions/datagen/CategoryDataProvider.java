package com.epherical.professions.datagen;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.core.Profession;
import com.epherical.professions.core.ProfessionCategory;
import com.google.gson.Gson;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.epherical.professions.datagen.ProfessionDataGeneration.rl;

final class CategoryDataProvider implements DataProvider {

    private static final Gson GSON = new Gson();
    private static final String FEATURE_PERKS_ENABLED = "perksEnabled";
    private static final String FEATURE_GATES_ENABLED = "gatesEnabled";

    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

    public CategoryDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "professions/categories");
        this.lookupProvider = lookupProvider;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return lookupProvider.thenCompose(registries -> {
            List<ResourceKey<Profession>> professions = registries.lookupOrThrow(ProfessionsCommon.PROFESSION_REGISTRY_KEY)
                    .listElementIds()
                    .sorted(Comparator.comparing(key -> key.identifier().toString()))
                    .toList();

            ProfessionCategory defaultCategory = new ProfessionCategory(
                    "Built-in Professions",
                    "Contains the default experience that comes with the Professions mod.",
                    TextColor.parseColor("#55FFFF").getOrThrow(),
                    professions,
                    features(
                            FEATURE_PERKS_ENABLED, true,
                            FEATURE_GATES_ENABLED, false
                    )
            );

            ProfessionCategory hardcoreCategory = new ProfessionCategory(
                    "Hardcore Professions",
                    "Contains the default professions with progression gates enabled.",
                    TextColor.parseColor("#FF5555").getOrThrow(),
                    professions,
                    features(
                            FEATURE_PERKS_ENABLED, true,
                            FEATURE_GATES_ENABLED, true
                    )
            );

            CompletableFuture<?> defaultWrite = DataProvider.saveStable(
                    output,
                    registries,
                    ProfessionCategory.CODEC,
                    defaultCategory,
                    pathProvider.json(rl("all_professions"))
            );

            CompletableFuture<?> hardcoreWrite = DataProvider.saveStable(
                    output,
                    registries,
                    ProfessionCategory.CODEC,
                    hardcoreCategory,
                    pathProvider.json(rl("hardcore"))
            );

            return CompletableFuture.allOf(defaultWrite, hardcoreWrite);
        });
    }

    @Override
    public String getName() {
        return "Professions Category Provider";
    }

    private static Map<String, Dynamic<?>> features(Object... entries) {
        if (entries.length % 2 != 0) {
            throw new IllegalArgumentException("features(...) requires key/value pairs");
        }

        Map<String, Dynamic<?>> featureValues = new LinkedHashMap<>();
        for (int i = 0; i < entries.length; i += 2) {
            Object key = entries[i];
            if (!(key instanceof String stringKey)) {
                throw new IllegalArgumentException("Feature key at index " + i + " must be a String");
            }

            Object value = entries[i + 1];
            featureValues.put(stringKey, new Dynamic<>(JsonOps.INSTANCE, GSON.toJsonTree(value)));
        }

        return featureValues;
    }
}
