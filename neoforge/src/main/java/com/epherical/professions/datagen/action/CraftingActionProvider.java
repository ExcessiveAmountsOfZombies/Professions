package com.epherical.professions.datagen.action;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.core.Profession;
import com.epherical.professions.api.actions.Action;
import com.epherical.professions.model.actions.item.CraftingAction;
import com.epherical.professions.model.actions.item.TakeSmeltAction;
import com.epherical.professions.model.actions.rewards.OccupationExperience;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.epherical.professions.datagen.ProfessionDataGeneration.id;
import static com.epherical.professions.datagen.ProfessionDataGeneration.rl;

public final class CraftingActionProvider implements DataProvider {

    private static final List<CraftingActionDefinition> CRAFTING_ACTIONS = List.of(
            new CraftingActionDefinition(
                    "craft_decorative_wood_and_fabric",
                    28,
                    List.of(ItemTags.WOODEN_PRESSURE_PLATES, ItemTags.BANNERS, ItemTags.CANDLES, ItemTags.SIGNS, ItemTags.WOOL_CARPETS,
                            ItemTags.SLABS,
                            ItemTags.TRAPDOORS),
                    List.of(Items.PAINTING, Items.FLOWER_POT)
            ),
            new CraftingActionDefinition(
                    "craft_structural_blocks",
                    34,
                    List.of(
                            ItemTags.WALLS,
                            ItemTags.STAIRS,
                            ItemTags.FENCES,
                            ItemTags.WOODEN_DOORS,
                            ItemTags.WOODEN_TRAPDOORS,
                            ItemTags.WOODEN_BUTTONS,
                            ItemTags.BOATS,
                            ItemTags.FENCE_GATES
                    ),
                    List.of(Items.LADDER)
            ),
            new CraftingActionDefinition(
                    "craft_storage_and_basic_utility",
                    62,
                    List.of(),
                    List.of(Items.CHEST, Items.BARREL, Items.FURNACE)
            ),
            new CraftingActionDefinition(
                    "craft_rail_and_transport_systems",
                    78,
                    List.of(ItemTags.RAILS),
                    List.of(Items.MINECART)
            ),
            new CraftingActionDefinition(
                    "craft_redstone_components",
                    72,
                    List.of(),
                    List.of(
                            Items.DISPENSER,
                            Items.PISTON,
                            Items.STICKY_PISTON,
                            Items.OBSERVER,
                            Items.DROPPER,
                            Items.TRIPWIRE_HOOK,
                            Items.TRAPPED_CHEST,
                            Items.REDSTONE_TORCH,
                            Items.REDSTONE_LAMP,
                            Items.COMPARATOR,
                            Items.REPEATER,
                            Items.DAYLIGHT_DETECTOR
                    )
            ),
            new CraftingActionDefinition(
                    "craft_metal_utility_blocks",
                    96,
                    List.of(),
                    List.of(
                            Items.IRON_DOOR,
                            Items.IRON_BARS,
                            Items.HOPPER,
                            Items.CAULDRON,
                            Items.CHAIN,
                            Items.STONE_PRESSURE_PLATE,
                            Items.POLISHED_BLACKSTONE_PRESSURE_PLATE,
                            Items.LIGHT_WEIGHTED_PRESSURE_PLATE,
                            Items.HEAVY_WEIGHTED_PRESSURE_PLATE,
                            Items.LIGHTNING_ROD
                    )
            ),
            new CraftingActionDefinition(
                    "craft_precision_tools",
                    88,
                    List.of(),
                    List.of(Items.COMPASS, Items.CLOCK, Items.SPYGLASS, Items.FISHING_ROD, Items.BREWING_STAND, Items.LECTERN, Items.END_CRYSTAL)
            ),
            new CraftingActionDefinition(
                    "craft_advanced_mechanics",
                    120,
                    List.of(),
                    List.of(Items.BLAST_FURNACE, Items.TNT, Items.ANVIL, Items.JUKEBOX, Items.SHULKER_BOX)
            ),
            new CraftingActionDefinition(
                    "craft_home_rest_items",
                    55,
                    List.of(ItemTags.BEDS),
                    List.of(Items.ARMOR_STAND, Items.LEAD)
            ),
            new CraftingActionDefinition(
                    "craft_glass_and_lighting",
                    42,
                    List.of(),
                    List.of(Items.TINTED_GLASS, Items.TORCH)
            ),
            new CraftingActionDefinition(
                    "craft_colored_concrete_powder",
                    24,
                    List.of(),
                    List.of(
                            Items.WHITE_CONCRETE_POWDER,
                            Items.ORANGE_CONCRETE_POWDER,
                            Items.MAGENTA_CONCRETE_POWDER,
                            Items.LIGHT_BLUE_CONCRETE_POWDER,
                            Items.YELLOW_CONCRETE_POWDER,
                            Items.LIME_CONCRETE_POWDER,
                            Items.PINK_CONCRETE_POWDER,
                            Items.GRAY_CONCRETE_POWDER,
                            Items.LIGHT_GRAY_CONCRETE_POWDER,
                            Items.CYAN_CONCRETE_POWDER,
                            Items.BLUE_CONCRETE_POWDER,
                            Items.BROWN_CONCRETE_POWDER,
                            Items.GREEN_CONCRETE_POWDER,
                            Items.RED_CONCRETE_POWDER,
                            Items.BLACK_CONCRETE_POWDER
                    )
            ),
            new CraftingActionDefinition(
                    "craft_lodestone",
                    220,
                    List.of(),
                    List.of(Items.LODESTONE)
            ),
            new CraftingActionDefinition(
                    "craft_beacon",
                    420,
                    List.of(),
                    List.of(Items.BEACON)
            )
    );

    private static final List<CraftingTakeSmeltDefinition> TAKE_SMELT_ACTIONS = List.of(
            new CraftingTakeSmeltDefinition(
                    "take_smelted_basic_blocks",
                    1.5,
                    List.of(Items.DEEPSLATE, Items.GLASS, Items.STONE, Items.TERRACOTTA)),
            new CraftingTakeSmeltDefinition(
                    "take_smelted_masonry_blocks",
                    2.0,
                    List.of(
                            Items.BRICK,
                            Items.CRACKED_DEEPSLATE_BRICKS,
                            Items.CRACKED_DEEPSLATE_TILES,
                            Items.CRACKED_NETHER_BRICKS,
                            Items.CRACKED_POLISHED_BLACKSTONE_BRICKS,
                            Items.CRACKED_STONE_BRICKS,
                            Items.NETHER_BRICK,
                            Items.SMOOTH_BASALT,
                            Items.SMOOTH_RED_SANDSTONE,
                            Items.SMOOTH_SANDSTONE,
                            Items.SMOOTH_STONE)),
            new CraftingTakeSmeltDefinition(
                    "take_smelted_refined_decorative_items",
                    2.5,
                    List.of(Items.GREEN_DYE, Items.LIME_DYE, Items.POPPED_CHORUS_FRUIT, Items.SMOOTH_QUARTZ)),
            new CraftingTakeSmeltDefinition(
                    "take_smelted_glazed_terracotta",
                    3.0,
                    List.of(
                            Items.BLACK_GLAZED_TERRACOTTA,
                            Items.BLUE_GLAZED_TERRACOTTA,
                            Items.BROWN_GLAZED_TERRACOTTA,
                            Items.CYAN_GLAZED_TERRACOTTA,
                            Items.GRAY_GLAZED_TERRACOTTA,
                            Items.GREEN_GLAZED_TERRACOTTA,
                            Items.LIGHT_BLUE_GLAZED_TERRACOTTA,
                            Items.LIGHT_GRAY_GLAZED_TERRACOTTA,
                            Items.LIME_GLAZED_TERRACOTTA,
                            Items.MAGENTA_GLAZED_TERRACOTTA,
                            Items.ORANGE_GLAZED_TERRACOTTA,
                            Items.PINK_GLAZED_TERRACOTTA,
                            Items.PURPLE_GLAZED_TERRACOTTA,
                            Items.RED_GLAZED_TERRACOTTA,
                            Items.WHITE_GLAZED_TERRACOTTA,
                            Items.YELLOW_GLAZED_TERRACOTTA))
    );

    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

    public CraftingActionProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "professions/actions/crafting");
        this.lookupProvider = lookupProvider;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return lookupProvider.thenCompose(registries -> {
            Holder<Profession> craftingProfession = registries.lookupOrThrow(ProfessionsCommon.PROFESSION_REGISTRY_KEY)
                    .get(id("crafting"))
                    .orElseThrow(() -> new IllegalStateException("Missing crafting profession for crafting action datagen"));

            List<CompletableFuture<?>> writes = new ArrayList<>();
            addCraftActions(writes, CRAFTING_ACTIONS, craftingProfession, output, registries);
            addTakeSmeltActions(writes, TAKE_SMELT_ACTIONS, craftingProfession, output, registries);
            return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName() {
        return "Professions Crafting Action Provider";
    }

    private void addCraftActions(
            List<CompletableFuture<?>> writes,
            List<CraftingActionDefinition> definitions,
            Holder<Profession> profession,
            CachedOutput output,
            HolderLookup.Provider registries
    ) {
        definitions.stream()
                .map(definition -> action(definition.name(), profession)
                        .rewardExp(definition.experience())
                        .tags(definition.tags())
                        .items(definition.items())
                        .save(output, registries))
                .forEach(write -> writes.add(write));
    }

    private CraftingDataActionBuilder action(String path, Holder<Profession> profession) {
        return new CraftingDataActionBuilder(path, profession, pathProvider);
    }

    private void addTakeSmeltActions(
            List<CompletableFuture<?>> writes,
            List<CraftingTakeSmeltDefinition> definitions,
            Holder<Profession> profession,
            CachedOutput output,
            HolderLookup.Provider registries
    ) {
        definitions.stream()
                .map(definition -> takeSmeltAction(definition.name(), profession)
                        .rewardExp(definition.experience())
                        .items(definition.items())
                        .save(output, registries))
                .forEach(write -> writes.add(write));
    }

    private CraftingTakeSmeltActionBuilder takeSmeltAction(String path, Holder<Profession> profession) {
        return new CraftingTakeSmeltActionBuilder(path, profession, pathProvider);
    }

    private record CraftingActionDefinition(String name, double experience, List<TagKey<Item>> tags, List<Item> items) {
    }

    private record CraftingTakeSmeltDefinition(String name, double experience, List<Item> items) {
    }

    private static final class CraftingDataActionBuilder {
        private final String path;
        private final PackOutput.PathProvider pathProvider;
        private final CraftingAction.Builder builder;

        private CraftingDataActionBuilder(String path, Holder<Profession> profession, PackOutput.PathProvider pathProvider) {
            this.path = path;
            this.pathProvider = pathProvider;
            this.builder = new CraftingAction.Builder(profession);
        }

        private CraftingDataActionBuilder rewardExp(double exp) {
            builder.reward(new OccupationExperience.Builder().exp(exp));
            return this;
        }

        private CraftingDataActionBuilder tag(TagKey<Item> tag) {
            builder.target(tag);
            return this;
        }

        private CraftingDataActionBuilder tags(List<TagKey<Item>> tags) {
            for (TagKey<Item> tag : tags) {
                tag(tag);
            }
            return this;
        }

        private CraftingDataActionBuilder item(Item item) {
            builder.target(item.builtInRegistryHolder().key());
            return this;
        }

        private CraftingDataActionBuilder items(List<Item> items) {
            for (Item item : items) {
                item(item);
            }
            return this;
        }

        private CompletableFuture<?> save(CachedOutput output, HolderLookup.Provider registries) {
            Action<?> action = builder.build();
            return DataProvider.saveStable(output, registries, Action.TYPED_CODEC, action, pathProvider.json(rl(path)));
        }
    }

    private static final class CraftingTakeSmeltActionBuilder {
        private final String path;
        private final PackOutput.PathProvider pathProvider;
        private final TakeSmeltAction.Builder builder;

        private CraftingTakeSmeltActionBuilder(String path, Holder<Profession> profession, PackOutput.PathProvider pathProvider) {
            this.path = path;
            this.pathProvider = pathProvider;
            this.builder = new TakeSmeltAction.Builder(profession);
        }

        private CraftingTakeSmeltActionBuilder rewardExp(double exp) {
            builder.reward(new OccupationExperience.Builder().exp(exp));
            return this;
        }

        private CraftingTakeSmeltActionBuilder item(Item item) {
            builder.target(item.builtInRegistryHolder().key());
            return this;
        }

        private CraftingTakeSmeltActionBuilder items(List<Item> items) {
            for (Item item : items) {
                item(item);
            }
            return this;
        }

        private CompletableFuture<?> save(CachedOutput output, HolderLookup.Provider registries) {
            Action<?> action = builder.build();
            return DataProvider.saveStable(output, registries, Action.TYPED_CODEC, action, pathProvider.json(rl(path)));
        }
    }
}
