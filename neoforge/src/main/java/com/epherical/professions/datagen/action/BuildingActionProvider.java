package com.epherical.professions.datagen.action;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.core.Profession;
import com.epherical.professions.api.actions.Action;
import com.epherical.professions.model.actions.block.BlockPlaceAction;
import com.epherical.professions.model.actions.rewards.OccupationExperience;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.epherical.professions.datagen.ProfessionDataGeneration.id;
import static com.epherical.professions.datagen.ProfessionDataGeneration.rl;

public final class BuildingActionProvider implements DataProvider {

    private static final List<BuildingActionDefinition> BUILDING_ACTIONS = List.of(
            new BuildingActionDefinition(
                    "place_foundation_and_masonry",
                    24,
                    List.of(BlockTags.BASE_STONE_OVERWORLD, BlockTags.BASE_STONE_NETHER, BlockTags.STONE_BRICKS),
                    List.of(
                            Blocks.DEEPSLATE_BRICKS, Blocks.CRACKED_DEEPSLATE_BRICKS,
                            Blocks.DEEPSLATE_TILES, Blocks.CRACKED_DEEPSLATE_TILES,
                            Blocks.CHISELED_DEEPSLATE, Blocks.COBBLED_DEEPSLATE, Blocks.POLISHED_DEEPSLATE,
                            Blocks.COBBLESTONE, Blocks.POLISHED_BASALT,
                            Blocks.BLACKSTONE, Blocks.GILDED_BLACKSTONE, Blocks.CHISELED_POLISHED_BLACKSTONE,
                            Blocks.POLISHED_GRANITE, Blocks.POLISHED_DIORITE, Blocks.POLISHED_ANDESITE,
                            Blocks.CALCITE, Blocks.END_STONE_BRICKS,
                            Blocks.PACKED_ICE, Blocks.BLUE_ICE
                    )
            ),
            new BuildingActionDefinition(
                    "place_wood_and_textile_surfaces",
                    30,
                    List.of(BlockTags.PLANKS, BlockTags.WOOL),
                    List.of()
            ),
            new BuildingActionDefinition(
                    "place_structural_variants",
                    22,
                    List.of(
                            BlockTags.SLABS, BlockTags.STAIRS, BlockTags.WALLS,
                            BlockTags.FENCE_GATES, BlockTags.FENCES,
                            BlockTags.BANNERS, BlockTags.CORALS, BlockTags.CORAL_BLOCKS, BlockTags.CORAL_PLANTS,
                            BlockTags.DOORS, BlockTags.TRAPDOORS, BlockTags.TERRACOTTA,
                            BlockTags.WOOL_CARPETS, BlockTags.CANDLES
                    ),
                    List.of()
            ),
            new BuildingActionDefinition(
                    "place_refined_blocks",
                    96,
                    List.of(),
                    List.of(
                            Blocks.BRICKS, Blocks.BOOKSHELF, Blocks.MOSSY_COBBLESTONE,
                            Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN,
                            Blocks.PRISMARINE, Blocks.DARK_PRISMARINE, Blocks.PRISMARINE_BRICKS, Blocks.SEA_LANTERN,
                            Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR,
                            Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_BRICKS, Blocks.QUARTZ_PILLAR
                    )
            ),
            new BuildingActionDefinition(
                    "place_glass_and_copper_details",
                    36,
                    List.of(),
                    List.of(
                            Blocks.GLASS, Blocks.GLASS_PANE, Blocks.IRON_BARS, Blocks.CHAIN,
                            Blocks.WHITE_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS, Blocks.MAGENTA_STAINED_GLASS,
                            Blocks.LIGHT_BLUE_STAINED_GLASS, Blocks.YELLOW_STAINED_GLASS, Blocks.LIME_STAINED_GLASS,
                            Blocks.PINK_STAINED_GLASS, Blocks.GRAY_STAINED_GLASS, Blocks.LIGHT_GRAY_STAINED_GLASS,
                            Blocks.CYAN_STAINED_GLASS, Blocks.PURPLE_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS,
                            Blocks.BROWN_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS, Blocks.RED_STAINED_GLASS,
                            Blocks.BLACK_STAINED_GLASS,
                            Blocks.WHITE_STAINED_GLASS_PANE, Blocks.ORANGE_STAINED_GLASS_PANE,
                            Blocks.MAGENTA_STAINED_GLASS_PANE, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE,
                            Blocks.YELLOW_STAINED_GLASS_PANE, Blocks.LIME_STAINED_GLASS_PANE,
                            Blocks.PINK_STAINED_GLASS_PANE, Blocks.GRAY_STAINED_GLASS_PANE,
                            Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, Blocks.CYAN_STAINED_GLASS_PANE,
                            Blocks.PURPLE_STAINED_GLASS_PANE, Blocks.BLUE_STAINED_GLASS_PANE,
                            Blocks.BROWN_STAINED_GLASS_PANE, Blocks.GREEN_STAINED_GLASS_PANE,
                            Blocks.RED_STAINED_GLASS_PANE, Blocks.BLACK_STAINED_GLASS_PANE,
                            Blocks.EXPOSED_COPPER, Blocks.WEATHERED_COPPER, Blocks.OXIDIZED_COPPER,
                            Blocks.CUT_COPPER, Blocks.EXPOSED_CUT_COPPER, Blocks.WEATHERED_CUT_COPPER, Blocks.OXIDIZED_CUT_COPPER,
                            Blocks.WAXED_COPPER_BLOCK, Blocks.WAXED_EXPOSED_COPPER, Blocks.WAXED_WEATHERED_COPPER, Blocks.WAXED_OXIDIZED_COPPER,
                            Blocks.WAXED_CUT_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER
                    )
            ),
            new BuildingActionDefinition(
                    "place_workstations_and_storage",
                    112,
                    List.of(),
                    List.of(
                            Blocks.CHEST, Blocks.ENDER_CHEST, Blocks.ANVIL,
                            Blocks.FURNACE, Blocks.BLAST_FURNACE, Blocks.SMOKER,
                            Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE, Blocks.CRAFTING_TABLE,
                            Blocks.ENCHANTING_TABLE, Blocks.JUKEBOX, Blocks.HOPPER
                    )
            ),
            new BuildingActionDefinition(
                    "place_luxury_blocks",
                    180,
                    List.of(),
                    List.of(
                            Blocks.IRON_BLOCK, Blocks.GOLD_BLOCK, Blocks.DIAMOND_BLOCK,
                            Blocks.EMERALD_BLOCK, Blocks.LAPIS_BLOCK, Blocks.NETHERITE_BLOCK,
                            Blocks.BEACON
                    )
            )
    );

    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

    public BuildingActionProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "professions/actions/building");
        this.lookupProvider = lookupProvider;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return lookupProvider.thenCompose(registries -> {
            Holder<Profession> buildingProfession = registries.lookupOrThrow(ProfessionsCommon.PROFESSION_REGISTRY_KEY)
                    .get(id("building"))
                    .orElseThrow(() -> new IllegalStateException("Missing building profession for building action datagen"));

            List<CompletableFuture<?>> writes = new ArrayList<>();
            BUILDING_ACTIONS.stream()
                    .map(definition -> action(definition.name(), buildingProfession)
                            .rewardExp(definition.experience())
                            .tags(definition.tags())
                            .blocks(definition.blocks())
                            .save(output, registries))
                    .forEach(writes::add);

            return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName() {
        return "Professions Building Action Provider";
    }

    private BuildingPlaceActionBuilder action(String path, Holder<Profession> profession) {
        return new BuildingPlaceActionBuilder(path, profession, pathProvider);
    }

    private record BuildingActionDefinition(String name, double experience, List<TagKey<Block>> tags, List<Block> blocks) {
    }

    private static final class BuildingPlaceActionBuilder {
        private final String path;
        private final PackOutput.PathProvider pathProvider;
        private final BlockPlaceAction.Builder builder;

        private BuildingPlaceActionBuilder(String path, Holder<Profession> profession, PackOutput.PathProvider pathProvider) {
            this.path = path;
            this.pathProvider = pathProvider;
            this.builder = new BlockPlaceAction.Builder(profession);
        }

        private BuildingPlaceActionBuilder rewardExp(double exp) {
            builder.reward(new OccupationExperience.Builder().exp(exp));
            return this;
        }

        private BuildingPlaceActionBuilder tag(TagKey<Block> tag) {
            builder.target(tag);
            return this;
        }

        private BuildingPlaceActionBuilder tags(List<TagKey<Block>> tags) {
            for (TagKey<Block> tag : tags) {
                tag(tag);
            }
            return this;
        }

        private BuildingPlaceActionBuilder block(Block block) {
            builder.target(block.builtInRegistryHolder().key());
            return this;
        }

        private BuildingPlaceActionBuilder blocks(List<Block> blocks) {
            for (Block block : blocks) {
                block(block);
            }
            return this;
        }

        private CompletableFuture<?> save(CachedOutput output, HolderLookup.Provider registries) {
            Action<?> action = builder.build();
            return DataProvider.saveStable(output, registries, Action.TYPED_CODEC, action, pathProvider.json(rl(path)));
        }
    }
}
