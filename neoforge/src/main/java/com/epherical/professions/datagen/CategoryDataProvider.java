package com.epherical.professions.datagen;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.core.Profession;
import com.epherical.professions.core.ProfessionCategory;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.epherical.professions.datagen.ProfessionDataGeneration.rl;

final class CategoryDataProvider implements DataProvider {

    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

    CategoryDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "professions/categories");
        this.lookupProvider = lookupProvider;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return lookupProvider.thenCompose(registries -> {
            List<ResourceKey<Profession>> professions = registries.lookupOrThrow(ProfessionsCommon.PROFESSION_REGISTRY_KEY)
                    .listElementIds()
                    .sorted(Comparator.comparing(key -> key.location().toString()))
                    .toList();

            ProfessionCategory category = new ProfessionCategory(
                    "Built-in Professions",
                    "Contains the default experience that comes with the Professions mod.",
                    TextColor.parseColor("#55FFFF").getOrThrow(),
                    professions
            );

            return DataProvider.saveStable(
                    output,
                    registries,
                    ProfessionCategory.CODEC,
                    category,
                    pathProvider.json(rl("all_professions"))
            );
        });
    }

    @Override
    public String getName() {
        return "Professions Category Provider";
    }
}
