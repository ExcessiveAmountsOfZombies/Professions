package com.epherical.professions.datagen;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.core.Profession;
import com.epherical.professions.model.actions.Action;
import com.epherical.professions.model.actions.block.BlockBreakAction;
import com.epherical.professions.model.actions.conditions.Condition;
import com.epherical.professions.model.actions.conditions.ToolMatcher;
import com.epherical.professions.model.actions.rewards.OccupationExperience;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.epherical.professions.datagen.ProfessionDataGeneration.id;
import static com.epherical.professions.datagen.ProfessionDataGeneration.rl;

final class LoggingActionProvider implements DataProvider {


    private static final List<TreeAction> TREE_ACTIONS = List.of(
            new TreeAction("chop_oak_logs", 40, BlockTags.OAK_LOGS),
            new TreeAction("chop_birch_logs", 40, BlockTags.BIRCH_LOGS),
            new TreeAction("chop_spruce_logs", 40, BlockTags.SPRUCE_LOGS),
            new TreeAction("chop_jungle_logs", 40, BlockTags.JUNGLE_LOGS),
            new TreeAction("chop_cherry_logs", 40, BlockTags.CHERRY_LOGS),
            new TreeAction("chop_mangrove_logs", 40, BlockTags.MANGROVE_LOGS),
            new TreeAction("chop_dark_oak_logs", 40, BlockTags.DARK_OAK_LOGS),
            new TreeAction("chop_crimson_stems", 75, BlockTags.CRIMSON_STEMS),
            new TreeAction("chop_warped_stems", 75, BlockTags.WARPED_STEMS),
            new TreeAction("clear_nether_wart_blocks", 55, BlockTags.WART_BLOCKS),
            new TreeAction("chop_bamboo_blocks", 30, BlockTags.BAMBOO_BLOCKS)
    );

    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

    LoggingActionProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "professions/actions/logging");
        this.lookupProvider = lookupProvider;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return lookupProvider.thenCompose(registries -> {
            Holder<Profession> loggingProfession = registries.lookupOrThrow(ProfessionsCommon.PROFESSION_REGISTRY_KEY)
                    .get(id("logging"))
                    .orElseThrow(() -> new IllegalStateException("Missing logging profession for logging action datagen"));

            List<CompletableFuture<?>> writes = new ArrayList<>();
            TREE_ACTIONS.stream()
                    .map(tree -> action(tree.name(), loggingProfession)
                            .rewardExp(tree.experience())
                            /*.axeOnly(registries)*/
                            .block(tree.tag())
                            .save(output, registries))
                    .forEach(writes::add);

            return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName() {
        return "Professions Logging Action Provider";
    }

    private LoggingBlockBreakActionBuilder action(String path, Holder<Profession> profession) {
        return new LoggingBlockBreakActionBuilder(path, profession, pathProvider);
    }

    private record TreeAction(String name, double experience, TagKey<Block> tag) {
    }

    private static final class LoggingBlockBreakActionBuilder {
        private final String path;
        private final PackOutput.PathProvider pathProvider;
        private final BlockBreakAction.Builder builder;

        private LoggingBlockBreakActionBuilder(String path, Holder<Profession> profession, PackOutput.PathProvider pathProvider) {
            this.path = path;
            this.pathProvider = pathProvider;
            this.builder = new BlockBreakAction.Builder(profession);
        }

        private LoggingBlockBreakActionBuilder rewardExp(double exp) {
            builder.reward(new OccupationExperience.Builder().exp(exp));
            return this;
        }

        private LoggingBlockBreakActionBuilder condition(Condition.Builder condition) {
            builder.condition(condition);
            return this;
        }

        private LoggingBlockBreakActionBuilder axeOnly(HolderLookup.Provider registries) {
            return condition(new ToolMatcher.Builder(registries).itemTag(ItemTags.AXES));
        }

        private LoggingBlockBreakActionBuilder block(TagKey<Block> block) {
            builder.target(block);
            return this;
        }

        private CompletableFuture<?> save(CachedOutput output, HolderLookup.Provider registries) {
            Action<?> action = builder.build();
            return DataProvider.saveStable(output, registries, Action.TYPED_CODEC, action, pathProvider.json(rl(path)));
        }
    }
}
