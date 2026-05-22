package com.epherical.professions.datagen.gate;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.core.Profession;
import com.epherical.professions.model.gating.BlockBreakGate;
import com.epherical.professions.model.gating.Gate;
import com.epherical.professions.model.gating.PlaceGate;
import com.epherical.professions.model.gating.ToolGate;
import com.epherical.professions.model.gating.requirements.AdvancementRequirement;
import com.epherical.professions.model.gating.requirements.LevelRequirement;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.epherical.professions.datagen.ProfessionDataGeneration.id;
import static com.epherical.professions.datagen.ProfessionDataGeneration.rl;

public final class GateProvider implements DataProvider {

    private static final ResourceLocation END_ROOT_ADVANCEMENT = ResourceLocation.parse("minecraft:end/root");
    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

    public GateProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "professions/gates");
        this.lookupProvider = lookupProvider;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return lookupProvider.thenCompose(registries -> {
            Holder<Profession> miningProfession = registries.lookupOrThrow(ProfessionsCommon.PROFESSION_REGISTRY_KEY)
                    .get(id("mining"))
                    .orElseThrow(() -> new IllegalStateException("Missing mining profession for mining gate datagen"));
            Holder<Profession> loggingProfession = registries.lookupOrThrow(ProfessionsCommon.PROFESSION_REGISTRY_KEY)
                    .get(id("logging"))
                    .orElseThrow(() -> new IllegalStateException("Missing logging profession for gate datagen"));
            Holder<Profession> farmingProfession = registries.lookupOrThrow(ProfessionsCommon.PROFESSION_REGISTRY_KEY)
                    .get(id("farming"))
                    .orElseThrow(() -> new IllegalStateException("Missing farming profession for gate datagen"));
            Holder<Profession> craftingProfession = registries.lookupOrThrow(ProfessionsCommon.PROFESSION_REGISTRY_KEY)
                    .get(id("crafting"))
                    .orElseThrow(() -> new IllegalStateException("Missing crafting profession for gate datagen"));

            List<CompletableFuture<?>> writes = new ArrayList<>();
            writes.add(toolGate("mining/iron_pickaxe_requires_level_25", miningProfession)
                    .requirementLevel(25)
                    .target(Items.IRON_PICKAXE)
                    .save(output, registries));
            writes.add(toolGate("mining/golden_pickaxe_requires_level_25", miningProfession)
                    .requirementLevel(25)
                    .target(Items.GOLDEN_PICKAXE)
                    .save(output, registries));
            writes.add(toolGate("mining/diamond_pickaxe_requires_level_25", miningProfession)
                    .requirementLevel(25)
                    .target(Items.DIAMOND_PICKAXE)
                    .save(output, registries));
            writes.add(toolGate("mining/netherite_pickaxe_requires_end_and_level_50", miningProfession)
                    .requirementAdvancement(END_ROOT_ADVANCEMENT)
                    .requirementLevel(50)
                    .target(Items.NETHERITE_PICKAXE)
                    .save(output, registries));
            writes.add(blockBreakGate("mining/emerald_ore_requires_level_30", miningProfession)
                    .requirementLevel(30)
                    .target(BlockTags.EMERALD_ORES)
                    .save(output, registries));
            writes.add(blockBreakGate("mining/ancient_debris_requires_end_and_level_50", miningProfession)
                    .requirementAdvancement(END_ROOT_ADVANCEMENT)
                    .requirementLevel(50)
                    .target(Blocks.ANCIENT_DEBRIS)
                    .save(output, registries));

            writes.add(toolGate("logging/iron_axe_requires_level_25", loggingProfession)
                    .requirementLevel(25)
                    .target(Items.IRON_AXE)
                    .save(output, registries));
            writes.add(toolGate("logging/golden_axe_requires_level_25", loggingProfession)
                    .requirementLevel(25)
                    .target(Items.GOLDEN_AXE)
                    .save(output, registries));
            writes.add(toolGate("logging/diamond_axe_requires_level_25", loggingProfession)
                    .requirementLevel(25)
                    .target(Items.DIAMOND_AXE)
                    .save(output, registries));
            writes.add(toolGate("logging/netherite_axe_requires_end_and_level_50", loggingProfession)
                    .requirementAdvancement(END_ROOT_ADVANCEMENT)
                    .requirementLevel(50)
                    .target(Items.NETHERITE_AXE)
                    .save(output, registries));

            writes.add(toolGate("farming/stone_hoe_requires_level_5", farmingProfession)
                    .requirementLevel(5)
                    .target(Items.STONE_HOE)
                    .save(output, registries));
            writes.add(toolGate("farming/iron_hoe_requires_level_5", farmingProfession)
                    .requirementLevel(5)
                    .target(Items.IRON_HOE)
                    .save(output, registries));
            writes.add(toolGate("farming/golden_hoe_requires_level_5", farmingProfession)
                    .requirementLevel(5)
                    .target(Items.GOLDEN_HOE)
                    .save(output, registries));
            writes.add(toolGate("farming/diamond_hoe_requires_level_5", farmingProfession)
                    .requirementLevel(5)
                    .target(Items.DIAMOND_HOE)
                    .save(output, registries));
            writes.add(toolGate("farming/netherite_hoe_requires_end_and_level_50", farmingProfession)
                    .requirementAdvancement(END_ROOT_ADVANCEMENT)
                    .requirementLevel(50)
                    .target(Items.NETHERITE_HOE)
                    .save(output, registries));

            writes.add(placeGate("crafting/beacon_requires_level_20", craftingProfession)
                    .requirementLevel(20)
                    .target(Blocks.BEACON)
                    .save(output, registries));

            return allOf(writes);
        });
    }

    @Override
    public String getName() {
        return "Professions Gate Provider";
    }

    private static CompletableFuture<?> allOf(List<CompletableFuture<?>> writes) {
        return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
    }

    private ToolGateDataBuilder toolGate(String path, Holder<Profession> profession) {
        return new ToolGateDataBuilder(path, profession, pathProvider);
    }

    private BlockBreakGateDataBuilder blockBreakGate(String path, Holder<Profession> profession) {
        return new BlockBreakGateDataBuilder(path, profession, pathProvider);
    }

    private PlaceGateDataBuilder placeGate(String path, Holder<Profession> profession) {
        return new PlaceGateDataBuilder(path, profession, pathProvider);
    }

    private static final class ToolGateDataBuilder {
        private final String path;
        private final PackOutput.PathProvider pathProvider;
        private final ToolGate.Builder builder;

        private ToolGateDataBuilder(String path, Holder<Profession> profession, PackOutput.PathProvider pathProvider) {
            this.path = path;
            this.pathProvider = pathProvider;
            this.builder = new ToolGate.Builder(profession);
        }

        private ToolGateDataBuilder requirementLevel(int level) {
            builder.requirement(new LevelRequirement.Builder().level(level));
            return this;
        }

        private ToolGateDataBuilder requirementAdvancement(ResourceLocation advancement) {
            builder.requirement(new AdvancementRequirement.Builder().advancement(advancement));
            return this;
        }

        private ToolGateDataBuilder target(Item item) {
            builder.target(item.builtInRegistryHolder().key());
            return this;
        }

        private CompletableFuture<?> save(CachedOutput output, HolderLookup.Provider registries) {
            Gate<?> gate = builder.build();
            return DataProvider.saveStable(output, registries, Gate.TYPED_CODEC, gate, pathProvider.json(rl(path)));
        }
    }

    private static final class BlockBreakGateDataBuilder {
        private final String path;
        private final PackOutput.PathProvider pathProvider;
        private final BlockBreakGate.Builder builder;

        private BlockBreakGateDataBuilder(String path, Holder<Profession> profession, PackOutput.PathProvider pathProvider) {
            this.path = path;
            this.pathProvider = pathProvider;
            this.builder = new BlockBreakGate.Builder(profession);
        }

        private BlockBreakGateDataBuilder requirementLevel(int level) {
            builder.requirement(new LevelRequirement.Builder().level(level));
            return this;
        }

        private BlockBreakGateDataBuilder requirementAdvancement(ResourceLocation advancement) {
            builder.requirement(new AdvancementRequirement.Builder().advancement(advancement));
            return this;
        }

        private BlockBreakGateDataBuilder target(Block block) {
            builder.target(block.builtInRegistryHolder().key());
            return this;
        }

        private BlockBreakGateDataBuilder target(TagKey<Block> blockTag) {
            builder.target(blockTag);
            return this;
        }

        private CompletableFuture<?> save(CachedOutput output, HolderLookup.Provider registries) {
            Gate<?> gate = builder.build();
            return DataProvider.saveStable(output, registries, Gate.TYPED_CODEC, gate, pathProvider.json(rl(path)));
        }
    }

    private static final class PlaceGateDataBuilder {
        private final String path;
        private final PackOutput.PathProvider pathProvider;
        private final PlaceGate.Builder builder;

        private PlaceGateDataBuilder(String path, Holder<Profession> profession, PackOutput.PathProvider pathProvider) {
            this.path = path;
            this.pathProvider = pathProvider;
            this.builder = new PlaceGate.Builder(profession);
        }

        private PlaceGateDataBuilder requirementLevel(int level) {
            builder.requirement(new LevelRequirement.Builder().level(level));
            return this;
        }

        private PlaceGateDataBuilder target(Block block) {
            builder.target(block.builtInRegistryHolder().key());
            return this;
        }

        private CompletableFuture<?> save(CachedOutput output, HolderLookup.Provider registries) {
            Gate<?> gate = builder.build();
            return DataProvider.saveStable(output, registries, Gate.TYPED_CODEC, gate, pathProvider.json(rl(path)));
        }
    }
}
