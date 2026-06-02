package com.epherical.professions.datagen.action;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.core.Profession;
import com.epherical.professions.api.actions.Action;
import com.epherical.professions.model.actions.block.BlockBreakAction;
import com.epherical.professions.model.actions.block.TNTDestroyAction;
import com.epherical.professions.api.actions.Condition;
import com.epherical.professions.model.actions.conditions.InvertedCondition;
import com.epherical.professions.model.actions.conditions.ToolMatcher;
import com.epherical.professions.model.actions.rewards.OccupationExperience;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.epherical.professions.datagen.ProfessionDataGeneration.id;
import static com.epherical.professions.datagen.ProfessionDataGeneration.rl;

public final class MinerActionProvider implements DataProvider {

    private static final double TNT_EXP_MULTIPLIER = 0.2;

    private static final List<OreAction> ORE_ACTIONS = List.of(
            new OreAction("coal", BlockTags.COAL_ORES, 200),
            new OreAction("copper", BlockTags.COPPER_ORES, 400),
            new OreAction("iron", BlockTags.IRON_ORES, 650),
            new OreAction("redstone", BlockTags.REDSTONE_ORES, 600),
            new OreAction("lapis", BlockTags.LAPIS_ORES, 700),
            new OreAction("gold", BlockTags.GOLD_ORES, 900),
            new OreAction("emerald", BlockTags.EMERALD_ORES, 1000),
            new OreAction("diamond", BlockTags.DIAMOND_ORES, 1500)
    );

    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

    public MinerActionProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "professions/actions/mining");
        this.lookupProvider = lookupProvider;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return lookupProvider.thenCompose(registries -> {
            Holder<Profession> miningProfession = registries.lookupOrThrow(ProfessionsCommon.PROFESSION_REGISTRY_KEY)
                    .get(id("mining"))
                    .orElseThrow(() -> new IllegalStateException("Missing mining profession for miner action datagen"));

            List<CompletableFuture<?>> writes = new ArrayList<>();
            writes.add(action("mine_stone", miningProfession)
                    .rewardExp(50.0)
                    .blocks(List.of(BlockTags.BASE_STONE_OVERWORLD, BlockTags.BASE_STONE_NETHER))
                    .block(List.of(Blocks.MOSSY_COBBLESTONE))
                    .save(output, registries));
            writes.add(tntAction("tnt_destroy_stone", miningProfession)
                    .rewardExp(50.0 * TNT_EXP_MULTIPLIER)
                    .blocks(List.of(BlockTags.BASE_STONE_OVERWORLD, BlockTags.BASE_STONE_NETHER))
                    .block(List.of(Blocks.MOSSY_COBBLESTONE))
                    .save(output, registries));

            writes.add(action("mine_terracotta", miningProfession)
                    .rewardExp(50.0)
                    .block(BlockTags.TERRACOTTA)
                    .save(output, registries));
            writes.add(tntAction("tnt_destroy_terracotta", miningProfession)
                    .rewardExp(50.0 * TNT_EXP_MULTIPLIER)
                    .block(BlockTags.TERRACOTTA)
                    .save(output, registries));

            writes.add(action("mine_sandstone", miningProfession)
                    .rewardExp(50)
                    .block(List.of(
                            Blocks.SANDSTONE,
                            Blocks.RED_SANDSTONE,
                            Blocks.CHISELED_SANDSTONE,
                            Blocks.CUT_SANDSTONE,
                            Blocks.CHISELED_RED_SANDSTONE,
                            Blocks.SMOOTH_RED_SANDSTONE
                    ))
                    .save(output, registries));
            writes.add(tntAction("tnt_destroy_sandstone", miningProfession)
                    .rewardExp(50 * TNT_EXP_MULTIPLIER)
                    .block(List.of(
                            Blocks.SANDSTONE,
                            Blocks.RED_SANDSTONE,
                            Blocks.CHISELED_SANDSTONE,
                            Blocks.CUT_SANDSTONE,
                            Blocks.CHISELED_RED_SANDSTONE,
                            Blocks.SMOOTH_RED_SANDSTONE
                    ))
                    .save(output, registries));

            writes.add(action("mine_obsidian", miningProfession)
                    .rewardExp(200)
                    .block(List.of(Blocks.OBSIDIAN))
                    .save(output, registries));

            writes.add(action("prismarine", miningProfession)
                    .rewardExp(60)
                    .block(List.of(
                            Blocks.PRISMARINE,
                            Blocks.DARK_PRISMARINE,
                            Blocks.PRISMARINE_BRICKS,
                            Blocks.PRISMARINE_SLAB
                    ))
                    .save(output, registries));
            writes.add(tntAction("tnt_destroy_prismarine", miningProfession)
                    .rewardExp(60 * TNT_EXP_MULTIPLIER)
                    .block(List.of(
                            Blocks.PRISMARINE,
                            Blocks.DARK_PRISMARINE,
                            Blocks.PRISMARINE_BRICKS,
                            Blocks.PRISMARINE_SLAB
                    ))
                    .save(output, registries));

            writes.add(action("mine_nether_bricks", miningProfession)
                    .rewardExp(75)
                    .block(List.of(
                            Blocks.NETHER_BRICKS,
                            Blocks.CRACKED_NETHER_BRICKS,
                            Blocks.NETHER_BRICK_STAIRS,
                            Blocks.NETHER_BRICK_SLAB,
                            Blocks.NETHER_BRICK_FENCE,
                            Blocks.NETHER_BRICK_WALL
                    ))
                    .save(output, registries));
            writes.add(tntAction("tnt_destroy_nether_bricks", miningProfession)
                    .rewardExp(75 * TNT_EXP_MULTIPLIER)
                    .block(List.of(
                            Blocks.NETHER_BRICKS,
                            Blocks.CRACKED_NETHER_BRICKS,
                            Blocks.NETHER_BRICK_STAIRS,
                            Blocks.NETHER_BRICK_SLAB,
                            Blocks.NETHER_BRICK_FENCE,
                            Blocks.NETHER_BRICK_WALL
                    ))
                    .save(output, registries));

            writes.add(action("mine_nether_terrain", miningProfession)
                    .rewardExp(80)
                    .block(List.of(
                            Blocks.CRIMSON_NYLIUM,
                            Blocks.WARPED_NYLIUM,
                            Blocks.MAGMA_BLOCK,
                            Blocks.GILDED_BLACKSTONE,
                            Blocks.POLISHED_BLACKSTONE_BRICKS,
                            Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS,
                            Blocks.POLISHED_BASALT
                    ))
                    .save(output, registries));
            writes.add(tntAction("tnt_destroy_nether_terrain", miningProfession)
                    .rewardExp(80 * TNT_EXP_MULTIPLIER)
                    .block(List.of(
                            Blocks.CRIMSON_NYLIUM,
                            Blocks.WARPED_NYLIUM,
                            Blocks.MAGMA_BLOCK,
                            Blocks.GILDED_BLACKSTONE,
                            Blocks.POLISHED_BLACKSTONE_BRICKS,
                            Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS,
                            Blocks.POLISHED_BASALT
                    ))
                    .save(output, registries));

            writes.add(action("mine_end_blocks", miningProfession)
                    .rewardExp(70)
                    .block(List.of(
                            Blocks.END_STONE,
                            Blocks.PURPUR_BLOCK
                    ))
                    .save(output, registries));
            writes.add(tntAction("tnt_destroy_end_blocks", miningProfession)
                    .rewardExp(70 * TNT_EXP_MULTIPLIER)
                    .block(List.of(
                            Blocks.END_STONE,
                            Blocks.PURPUR_BLOCK
                    ))
                    .save(output, registries));

            writes.add(action("mine_geode", miningProfession)
                    .rewardExp(90)
                    .block(List.of(
                            Blocks.SMOOTH_BASALT,
                            Blocks.CALCITE,
                            Blocks.DRIPSTONE_BLOCK,
                            Blocks.POINTED_DRIPSTONE,
                            Blocks.AMETHYST_BLOCK,
                            Blocks.AMETHYST_CLUSTER
                    ))
                    .save(output, registries));
            writes.add(tntAction("tnt_destroy_geode", miningProfession)
                    .rewardExp(90 * TNT_EXP_MULTIPLIER)
                    .block(List.of(
                            Blocks.SMOOTH_BASALT,
                            Blocks.CALCITE,
                            Blocks.DRIPSTONE_BLOCK,
                            Blocks.POINTED_DRIPSTONE,
                            Blocks.AMETHYST_BLOCK,
                            Blocks.AMETHYST_CLUSTER
                    ))
                    .save(output, registries));

            writes.add(action("mine_nether_quartz_ore", miningProfession)
                    .rewardExp(650)
                    .unlessSilkTouchPickaxe(registries)
                    .block(List.of(Blocks.NETHER_QUARTZ_ORE))
                    .save(output, registries));
            writes.add(tntAction("tnt_destroy_nether_quartz_ore", miningProfession)
                    .rewardExp(650 * TNT_EXP_MULTIPLIER)
                    .block(List.of(Blocks.NETHER_QUARTZ_ORE))
                    .save(output, registries));

            writes.add(action("mine_ancient_debris", miningProfession)
                    .rewardExp(5000)
                    .block(List.of(Blocks.ANCIENT_DEBRIS))
                    .save(output, registries));

            writes.add(action("mine_spawner", miningProfession)
                    .rewardExp(8000)
                    .block(List.of(Blocks.SPAWNER))
                    .save(output, registries));
            writes.add(tntAction("tnt_destroy_spawner", miningProfession)
                    .rewardExp(8000 * TNT_EXP_MULTIPLIER)
                    .block(List.of(Blocks.SPAWNER))
                    .save(output, registries));

            ORE_ACTIONS.stream()
                    .map(ore -> action("mine_ore_" + ore.name(), miningProfession)
                            .rewardExp(ore.experience())
                            .unlessSilkTouchPickaxe(registries)
                            .block(ore.tag())
                            .save(output, registries))
                    .forEach(write -> writes.add(write));
            ORE_ACTIONS.stream()
                    .map(ore -> tntAction("tnt_destroy_ore_" + ore.name(), miningProfession)
                            .rewardExp(ore.experience() * TNT_EXP_MULTIPLIER)
                            .block(ore.tag())
                            .save(output, registries))
                    .forEach(write -> writes.add(write));

            return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName() {
        return "Professions Miner Action Provider";
    }

    private MinerBlockBreakActionBuilder action(String path, Holder<Profession> profession) {
        return new MinerBlockBreakActionBuilder(path, profession, pathProvider);
    }

    private MinerTntDestroyActionBuilder tntAction(String path, Holder<Profession> profession) {
        return new MinerTntDestroyActionBuilder(path, profession, pathProvider);
    }

    private record OreAction(String name, TagKey<Block> tag, double experience) {
    }

    private static final class MinerTntDestroyActionBuilder {
        private final String path;
        private final PackOutput.PathProvider pathProvider;
        private final TNTDestroyAction.Builder builder;

        private MinerTntDestroyActionBuilder(String path, Holder<Profession> profession, PackOutput.PathProvider pathProvider) {
            this.path = path;
            this.pathProvider = pathProvider;
            this.builder = new TNTDestroyAction.Builder(profession);
        }

        private MinerTntDestroyActionBuilder rewardExp(double exp) {
            builder.reward(new OccupationExperience.Builder().exp(exp));
            return this;
        }

        private MinerTntDestroyActionBuilder block(Block block) {
            builder.target(block.builtInRegistryHolder().key());
            return this;
        }

        private MinerTntDestroyActionBuilder block(List<Block> blocks) {
            for (Block block : blocks) {
                builder.target(block.builtInRegistryHolder().key());
            }
            return this;
        }

        private MinerTntDestroyActionBuilder block(TagKey<Block> block) {
            builder.target(block);
            return this;
        }

        private MinerTntDestroyActionBuilder blocks(List<TagKey<Block>> blocks) {
            for (TagKey<Block> block : blocks) {
                builder.target(block);
            }
            return this;
        }

        private CompletableFuture<?> save(CachedOutput output, HolderLookup.Provider registries) {
            Action<?> action = builder.build();
            return DataProvider.saveStable(output, registries, Action.TYPED_CODEC, action, pathProvider.json(rl(path)));
        }
    }

    private static final class MinerBlockBreakActionBuilder {
        private final String path;
        private final PackOutput.PathProvider pathProvider;
        private final BlockBreakAction.Builder builder;

        private MinerBlockBreakActionBuilder(String path, Holder<Profession> profession, PackOutput.PathProvider pathProvider) {
            this.path = path;
            this.pathProvider = pathProvider;
            this.builder = new BlockBreakAction.Builder(profession);
        }

        private MinerBlockBreakActionBuilder rewardExp(double exp) {
            builder.reward(new OccupationExperience.Builder().exp(exp));
            return this;
        }

        private MinerBlockBreakActionBuilder condition(Condition.Builder condition) {
            builder.condition(condition);
            return this;
        }

        private MinerBlockBreakActionBuilder unlessSilkTouchPickaxe(HolderLookup.Provider registries) {
            return condition(new InvertedCondition.Builder(
                    new ToolMatcher.Builder(registries)
                            .itemTag(ItemTags.PICKAXES)
                            .withEnchantment(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))
            ));
        }

        private MinerBlockBreakActionBuilder block(Block block) {
            builder.target(block.builtInRegistryHolder().key());
            return this;
        }

        private MinerBlockBreakActionBuilder block(List<Block> blocks) {
            for (Block block : blocks) {
                builder.target(block.builtInRegistryHolder().key());
            }
            return this;
        }

        private MinerBlockBreakActionBuilder block(TagKey<Block> block) {
            builder.target(block);
            return this;
        }

        private MinerBlockBreakActionBuilder blocks(List<TagKey<Block>> blocks) {
            for (TagKey<Block> block : blocks) {
                builder.target(block);
            }
            return this;
        }

        private CompletableFuture<?> save(CachedOutput output, HolderLookup.Provider registries) {
            Action<?> action = builder.build();
            return DataProvider.saveStable(output, registries, Action.TYPED_CODEC, action, pathProvider.json(rl(path)));
        }
    }
}
