package com.epherical.professions.datagen.action;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.core.Profession;
import com.epherical.professions.api.actions.Action;
import com.epherical.professions.model.actions.block.BlockBreakAction;
import com.epherical.professions.api.actions.Condition;
import com.epherical.professions.model.actions.conditions.InvertedCondition;
import com.epherical.professions.model.actions.conditions.ToolMatcher;
import com.epherical.professions.model.actions.rewards.OccupationExperience;
import net.minecraft.advancements.criterion.MinMaxBounds;
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
                    .save(output, registries));

            writes.add(action("mine_terracotta", miningProfession)
                    .rewardExp(50.0)
                    .block(BlockTags.TERRACOTTA)
                    .save(output, registries));

            writes.add(action("mine_sandstone", miningProfession)
                    .rewardExp(50)
                    .block(List.of(Blocks.SANDSTONE, Blocks.RED_SANDSTONE))
                    .save(output, registries));

            writes.add(action("mine_obsidian", miningProfession)
                    .rewardExp(200)
                    .block(List.of(Blocks.OBSIDIAN))
                    .save(output, registries));

            writes.add(action("prismarine", miningProfession)
                    .rewardExp(60)
                    .block(List.of(Blocks.PRISMARINE, Blocks.DARK_PRISMARINE, Blocks.PRISMARINE_BRICKS))
                    .save(output, registries));

            ORE_ACTIONS.stream()
                    .map(ore -> action("mine_ore_" + ore.name(), miningProfession)
                            .rewardExp(ore.experience())
                            .unlessSilkTouchPickaxe(registries)
                            .block(ore.tag())
                            .save(output, registries))
                    .forEach(writes::add);

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

    private record OreAction(String name, TagKey<Block> tag, double experience) {
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
