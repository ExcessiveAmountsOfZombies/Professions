package com.epherical.professions.datagen.gate;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.core.Profession;
import com.epherical.professions.model.gating.Gate;
import com.epherical.professions.model.gating.ToolGate;
import com.epherical.professions.model.gating.requirements.AdvancementRequirement;
import com.epherical.professions.model.gating.requirements.LevelRequirement;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

import static com.epherical.professions.datagen.ProfessionDataGeneration.id;
import static com.epherical.professions.datagen.ProfessionDataGeneration.rl;

public final class MiningGateProvider implements DataProvider {

    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

    public MiningGateProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "professions/gates/mining");
        this.lookupProvider = lookupProvider;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return lookupProvider.thenCompose(registries -> {
            Holder<Profession> miningProfession = registries.lookupOrThrow(ProfessionsCommon.PROFESSION_REGISTRY_KEY)
                    .get(id("mining"))
                    .orElseThrow(() -> new IllegalStateException("Missing mining profession for mining gate datagen"));

            CompletableFuture<?> ironPickaxeGate = gate("iron_pickaxe_requires_level_10", miningProfession)
                    .requirementLevel(10)
                    .target(Items.IRON_PICKAXE)
                    .save(output, registries);

            CompletableFuture<?> netheritePickaxeGate = gate("netherite_pickaxe_requires_kill_dragon", miningProfession)
                    .requirementAdvancement(ResourceLocation.parse("minecraft:end/kill_dragon"))
                    .target(Items.NETHERITE_PICKAXE)
                    .save(output, registries);

            return CompletableFuture.allOf(ironPickaxeGate, netheritePickaxeGate);
        });
    }

    @Override
    public String getName() {
        return "Professions Mining Gate Provider";
    }

    private MiningToolGateBuilder gate(String path, Holder<Profession> profession) {
        return new MiningToolGateBuilder(path, profession, pathProvider);
    }

    private static final class MiningToolGateBuilder {
        private final String path;
        private final PackOutput.PathProvider pathProvider;
        private final ToolGate.Builder builder;

        private MiningToolGateBuilder(String path, Holder<Profession> profession, PackOutput.PathProvider pathProvider) {
            this.path = path;
            this.pathProvider = pathProvider;
            this.builder = new ToolGate.Builder(profession);
        }

        private MiningToolGateBuilder requirementLevel(int level) {
            builder.requirement(new LevelRequirement.Builder().level(level));
            return this;
        }

        private MiningToolGateBuilder requirementAdvancement(ResourceLocation advancement) {
            builder.requirement(new AdvancementRequirement.Builder().advancement(advancement));
            return this;
        }

        private MiningToolGateBuilder target(Item item) {
            builder.target(item.builtInRegistryHolder().key());
            return this;
        }

        private CompletableFuture<?> save(CachedOutput output, HolderLookup.Provider registries) {
            Gate<?> gate = builder.build();
            return DataProvider.saveStable(output, registries, Gate.TYPED_CODEC, gate, pathProvider.json(rl(path)));
        }
    }
}
