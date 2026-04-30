package com.epherical.professions.datagen;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.core.Profession;
import com.epherical.professions.model.actions.Action;
import com.epherical.professions.model.actions.item.FishingAction;
import com.epherical.professions.model.actions.rewards.OccupationExperience;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.epherical.professions.datagen.ProfessionDataGeneration.id;
import static com.epherical.professions.datagen.ProfessionDataGeneration.rl;

final class FishingActionProvider implements DataProvider {

    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

    FishingActionProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "professions/actions/fishing");
        this.lookupProvider = lookupProvider;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return lookupProvider.thenCompose(registries -> {
            Holder<Profession> fishingProfession = registries.lookupOrThrow(ProfessionsCommon.PROFESSION_REGISTRY_KEY)
                    .get(id("fishing"))
                    .orElseThrow(() -> new IllegalStateException("Missing fishing profession for fishing action datagen"));

            List<CompletableFuture<?>> writes = new ArrayList<>();

            writes.add(action("catch_fish", fishingProfession)
                    .rewardExp(55)
                    .item(List.of(Items.COD, Items.SALMON, Items.PUFFERFISH, Items.TROPICAL_FISH))
                    .save(output, registries));

            writes.add(action("catch_treasure", fishingProfession)
                    .rewardExp(200)
                    .item(List.of(Items.BOW, Items.ENCHANTED_BOOK, Items.NAUTILUS_SHELL, Items.NAME_TAG, Items.SADDLE))
                    .save(output, registries));

            writes.add(action("catch_junk", fishingProfession)
                    .rewardExp(28)
                    .item(List.of(
                            Items.LILY_PAD,
                            Items.BOWL,
                            Items.LEATHER,
                            Items.LEATHER_BOOTS,
                            Items.ROTTEN_FLESH,
                            Items.STICK,
                            Items.STRING,
                            Items.POTION,
                            Items.BONE,
                            Items.INK_SAC,
                            Items.TRIPWIRE_HOOK
                    ))
                    .save(output, registries));

            return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName() {
        return "Professions Fishing Action Provider";
    }

    private FishingItemActionBuilder action(String path, Holder<Profession> profession) {
        return new FishingItemActionBuilder(path, profession, pathProvider);
    }

    private static final class FishingItemActionBuilder {
        private final String path;
        private final PackOutput.PathProvider pathProvider;
        private final FishingAction.Builder builder;

        private FishingItemActionBuilder(String path, Holder<Profession> profession, PackOutput.PathProvider pathProvider) {
            this.path = path;
            this.pathProvider = pathProvider;
            this.builder = new FishingAction.Builder(profession);
        }

        private FishingItemActionBuilder rewardExp(double exp) {
            builder.reward(new OccupationExperience.Builder().exp(exp));
            return this;
        }

        private FishingItemActionBuilder item(Item item) {
            builder.target(item.builtInRegistryHolder().key());
            return this;
        }

        private FishingItemActionBuilder item(List<Item> items) {
            for (Item item : items) {
                builder.target(item.builtInRegistryHolder().key());
            }
            return this;
        }

        private CompletableFuture<?> save(CachedOutput output, HolderLookup.Provider registries) {
            Action<?> action = builder.build();
            return DataProvider.saveStable(output, registries, Action.TYPED_CODEC, action, pathProvider.json(rl(path)));
        }
    }
}
