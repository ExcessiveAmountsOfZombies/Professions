package com.epherical.professions.datagen;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.core.Profession;
import com.epherical.professions.model.actions.Action;
import com.epherical.professions.model.actions.block.BlockBreakAction;
import com.epherical.professions.model.actions.conditions.Condition;
import com.epherical.professions.model.actions.conditions.FullyGrownCropCondition;
import com.epherical.professions.model.actions.entity.BreedAction;
import com.epherical.professions.model.actions.entity.TameAction;
import com.epherical.professions.model.actions.rewards.OccupationExperience;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.epherical.professions.datagen.ProfessionDataGeneration.id;
import static com.epherical.professions.datagen.ProfessionDataGeneration.rl;

final class FarmingActionProvider implements DataProvider {

    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

    FarmingActionProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "professions/actions/farming");
        this.lookupProvider = lookupProvider;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return lookupProvider.thenCompose(registries -> {
            Holder<Profession> farmingProfession = registries.lookupOrThrow(ProfessionsCommon.PROFESSION_REGISTRY_KEY)
                    .get(id("farming"))
                    .orElseThrow(() -> new IllegalStateException("Missing farming profession for farming action datagen"));

            List<CompletableFuture<?>> writes = new ArrayList<>();

            writes.add(action("harvest_crops", farmingProfession)
                    .rewardExp(60)
                    .fullyGrownCrop()
                    .block(BlockTags.CROPS)
                    .save(output, registries));

            writes.add(action("harvest_flowers", farmingProfession)
                    .rewardExp(25)
                    .block(BlockTags.FLOWERS)
                    .save(output, registries));

            writes.add(action("clear_grass_and_ferns", farmingProfession)
                    .rewardExp(20)
                    .block(List.of(Blocks.SHORT_GRASS, Blocks.TALL_GRASS, Blocks.FERN, Blocks.LARGE_FERN))
                    .save(output, registries));

            writes.add(action("harvest_corals", farmingProfession)
                    .rewardExp(35)
                    .block(BlockTags.CORALS)
                    .block(BlockTags.CORAL_BLOCKS)
                    .block(BlockTags.WALL_CORALS)
                    .save(output, registries));


            writes.add(action("harvest_sugar_cane", farmingProfession)
                    .rewardExp(45)
                    .block(Blocks.SUGAR_CANE)
                    .save(output, registries));

            writes.add(action("harvest_cactus", farmingProfession)
                    .rewardExp(45)
                    .block(Blocks.CACTUS)
                    .save(output, registries));

            writes.add(action("harvest_nether_wart", farmingProfession)
                    .rewardExp(70)
                    .block(Blocks.NETHER_WART)
                    .save(output, registries));

            writes.add(action("harvest_melons", farmingProfession)
                    .rewardExp(80)
                    .block(Blocks.MELON)
                    .save(output, registries));

            writes.add(action("harvest_pumpkins", farmingProfession)
                    .rewardExp(90)
                    .block(Blocks.PUMPKIN)
                    .save(output, registries));

            writes.add(breedAction("breed_farm_animals", farmingProfession)
                    .rewardExp(110)
                    .entity(List.of(
                            EntityType.COW, EntityType.SHEEP, EntityType.PIG, EntityType.CHICKEN, EntityType.RABBIT,
                            EntityType.MOOSHROOM, EntityType.GOAT, EntityType.CAMEL, EntityType.LLAMA, EntityType.HORSE,
                            EntityType.DONKEY, EntityType.MULE, EntityType.BEE
                    ))
                    .save(output, registries));

            writes.add(tameAction("tame_animals", farmingProfession)
                    .rewardExp(140)
                    .entity(List.of(
                            EntityType.WOLF, EntityType.CAT, EntityType.HORSE, EntityType.DONKEY,
                            EntityType.MULE, EntityType.LLAMA, EntityType.PARROT
                    ))
                    .save(output, registries));

            return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName() {
        return "Professions Farming Action Provider";
    }

    private FarmingBlockBreakActionBuilder action(String path, Holder<Profession> profession) {
        return new FarmingBlockBreakActionBuilder(path, profession, pathProvider);
    }

    private FarmingBreedActionBuilder breedAction(String path, Holder<Profession> profession) {
        return new FarmingBreedActionBuilder(path, profession, pathProvider);
    }

    private FarmingTameActionBuilder tameAction(String path, Holder<Profession> profession) {
        return new FarmingTameActionBuilder(path, profession, pathProvider);
    }

    private static final class FarmingBlockBreakActionBuilder {
        private final String path;
        private final PackOutput.PathProvider pathProvider;
        private final BlockBreakAction.Builder builder;

        private FarmingBlockBreakActionBuilder(String path, Holder<Profession> profession, PackOutput.PathProvider pathProvider) {
            this.path = path;
            this.pathProvider = pathProvider;
            this.builder = new BlockBreakAction.Builder(profession);
        }

        private FarmingBlockBreakActionBuilder rewardExp(double exp) {
            builder.reward(new OccupationExperience.Builder().exp(exp));
            return this;
        }

        private FarmingBlockBreakActionBuilder condition(Condition.Builder condition) {
            builder.condition(condition);
            return this;
        }

        private FarmingBlockBreakActionBuilder fullyGrownCrop() {
            return condition(FullyGrownCropCondition::new);
        }

        private FarmingBlockBreakActionBuilder block(Block block) {
            builder.target(block.builtInRegistryHolder().key());
            return this;
        }

        private FarmingBlockBreakActionBuilder block(List<Block> blocks) {
            for (Block block : blocks) {
                builder.target(block.builtInRegistryHolder().key());
            }
            return this;
        }

        private FarmingBlockBreakActionBuilder block(TagKey<Block> block) {
            builder.target(block);
            return this;
        }

        private CompletableFuture<?> save(CachedOutput output, HolderLookup.Provider registries) {
            Action<?> action = builder.build();
            return DataProvider.saveStable(output, registries, Action.TYPED_CODEC, action, pathProvider.json(rl(path)));
        }
    }

    private static final class FarmingBreedActionBuilder {
        private final String path;
        private final PackOutput.PathProvider pathProvider;
        private final BreedAction.Builder builder;

        private FarmingBreedActionBuilder(String path, Holder<Profession> profession, PackOutput.PathProvider pathProvider) {
            this.path = path;
            this.pathProvider = pathProvider;
            this.builder = new BreedAction.Builder(profession);
        }

        private FarmingBreedActionBuilder rewardExp(double exp) {
            builder.reward(new OccupationExperience.Builder().exp(exp));
            return this;
        }

        private FarmingBreedActionBuilder entity(EntityType<?> entityType) {
            builder.target(entityType.builtInRegistryHolder().key());
            return this;
        }

        private FarmingBreedActionBuilder entity(List<EntityType<?>> entities) {
            for (EntityType<?> entityType : entities) {
                builder.target(entityType.builtInRegistryHolder().key());
            }
            return this;
        }

        private CompletableFuture<?> save(CachedOutput output, HolderLookup.Provider registries) {
            Action<?> action = builder.build();
            return DataProvider.saveStable(output, registries, Action.TYPED_CODEC, action, pathProvider.json(rl(path)));
        }
    }

    private static final class FarmingTameActionBuilder {
        private final String path;
        private final PackOutput.PathProvider pathProvider;
        private final TameAction.Builder builder;

        private FarmingTameActionBuilder(String path, Holder<Profession> profession, PackOutput.PathProvider pathProvider) {
            this.path = path;
            this.pathProvider = pathProvider;
            this.builder = new TameAction.Builder(profession);
        }

        private FarmingTameActionBuilder rewardExp(double exp) {
            builder.reward(new OccupationExperience.Builder().exp(exp));
            return this;
        }

        private FarmingTameActionBuilder entity(EntityType<?> entityType) {
            builder.target(entityType.builtInRegistryHolder().key());
            return this;
        }

        private FarmingTameActionBuilder entity(List<EntityType<?>> entities) {
            for (EntityType<?> entityType : entities) {
                builder.target(entityType.builtInRegistryHolder().key());
            }
            return this;
        }

        private CompletableFuture<?> save(CachedOutput output, HolderLookup.Provider registries) {
            Action<?> action = builder.build();
            return DataProvider.saveStable(output, registries, Action.TYPED_CODEC, action, pathProvider.json(rl(path)));
        }
    }
}
