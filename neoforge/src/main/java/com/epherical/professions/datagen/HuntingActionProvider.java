package com.epherical.professions.datagen;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.core.Profession;
import com.epherical.professions.model.actions.Action;
import com.epherical.professions.model.actions.entity.SlayAction;
import com.epherical.professions.model.actions.entity.TameAction;
import com.epherical.professions.model.actions.rewards.OccupationExperience;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.epherical.professions.datagen.ProfessionDataGeneration.id;
import static com.epherical.professions.datagen.ProfessionDataGeneration.rl;

final class HuntingActionProvider implements DataProvider {

    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

    HuntingActionProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "professions/actions/hunting");
        this.lookupProvider = lookupProvider;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return lookupProvider.thenCompose(registries -> {
            Holder<Profession> huntingProfession = registries.lookupOrThrow(ProfessionsCommon.PROFESSION_REGISTRY_KEY)
                    .get(id("hunting"))
                    .orElseThrow(() -> new IllegalStateException("Missing hunting profession for hunting action datagen"));

            List<CompletableFuture<?>> writes = new ArrayList<>();

            writes.add(slayAction("kill_passive_animals", huntingProfession)
                    .rewardExp(100.0)
                    .entity(List.of(
                            EntityType.PIG, EntityType.CHICKEN, EntityType.SHEEP, EntityType.COW,
                            EntityType.MOOSHROOM, EntityType.RABBIT, EntityType.GOAT
                    ))
                    .save(output, registries));

            writes.add(slayAction("kill_common_hostiles", huntingProfession)
                    .rewardExp(250.0)
                    .entity(List.of(
                            EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.HUSK,
                            EntityType.DROWNED, EntityType.SKELETON, EntityType.STRAY,
                            EntityType.BOGGED, EntityType.SPIDER, EntityType.CAVE_SPIDER,
                            EntityType.CREEPER, EntityType.SLIME, EntityType.SILVERFISH,
                            EntityType.ENDERMITE
                    ))
                    .save(output, registries));

            writes.add(slayAction("kill_dangerous_hostiles", huntingProfession)
                    .rewardExp(320.0)
                    .entity(List.of(
                            EntityType.BLAZE, EntityType.WITHER_SKELETON, EntityType.PIGLIN_BRUTE,
                            EntityType.HOGLIN, EntityType.PHANTOM, EntityType.SHULKER,
                            EntityType.ENDERMAN, EntityType.MAGMA_CUBE, EntityType.PIGLIN,
                            EntityType.ZOMBIFIED_PIGLIN, EntityType.GUARDIAN, EntityType.WITCH,
                            EntityType.BREEZE, EntityType.ZOGLIN, EntityType.PILLAGER,
                            EntityType.VINDICATOR, EntityType.VEX
                    ))
                    .save(output, registries));

            writes.add(slayAction("kill_elite_hostiles", huntingProfession)
                    .rewardExp(420.0)
                    .entity(List.of(
                            EntityType.ELDER_GUARDIAN, EntityType.GHAST, EntityType.EVOKER,
                            EntityType.RAVAGER, EntityType.WARDEN
                    ))
                    .save(output, registries));

            writes.add(slayAction("kill_wither", huntingProfession)
                    .rewardExp(800)
                    .entity(EntityType.WITHER)
                    .save(output, registries));

            writes.add(slayAction("kill_ender_dragon", huntingProfession)
                    .rewardExp(1600)
                    .entity(EntityType.ENDER_DRAGON)
                    .save(output, registries));

            writes.add(tameAction("tame_wolf", huntingProfession)
                    .rewardExp(140)
                    .entity(EntityType.WOLF)
                    .save(output, registries));


            return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName() {
        return "Professions Hunting Action Provider";
    }

    private HuntingSlayActionBuilder slayAction(String path, Holder<Profession> profession) {
        return new HuntingSlayActionBuilder(path, profession, pathProvider);
    }

    private HuntingTameActionBuilder tameAction(String path, Holder<Profession> profession) {
        return new HuntingTameActionBuilder(path, profession, pathProvider);
    }

    private static final class HuntingSlayActionBuilder {
        private final String path;
        private final PackOutput.PathProvider pathProvider;
        private final SlayAction.Builder builder;

        private HuntingSlayActionBuilder(String path, Holder<Profession> profession, PackOutput.PathProvider pathProvider) {
            this.path = path;
            this.pathProvider = pathProvider;
            this.builder = new SlayAction.Builder(profession);
        }

        private HuntingSlayActionBuilder rewardExp(double exp) {
            builder.reward(new OccupationExperience.Builder().exp(exp));
            return this;
        }

        private HuntingSlayActionBuilder entity(EntityType<?> entityType) {
            builder.target(entityType.builtInRegistryHolder().key());
            return this;
        }

        private HuntingSlayActionBuilder entity(List<EntityType<?>> entities) {
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

    private static final class HuntingTameActionBuilder {
        private final String path;
        private final PackOutput.PathProvider pathProvider;
        private final TameAction.Builder builder;

        private HuntingTameActionBuilder(String path, Holder<Profession> profession, PackOutput.PathProvider pathProvider) {
            this.path = path;
            this.pathProvider = pathProvider;
            this.builder = new TameAction.Builder(profession);
        }

        private HuntingTameActionBuilder rewardExp(double exp) {
            builder.reward(new OccupationExperience.Builder().exp(exp));
            return this;
        }

        private HuntingTameActionBuilder entity(EntityType<?> entityType) {
            builder.target(entityType.builtInRegistryHolder().key());
            return this;
        }

        private CompletableFuture<?> save(CachedOutput output, HolderLookup.Provider registries) {
            Action<?> action = builder.build();
            return DataProvider.saveStable(output, registries, Action.TYPED_CODEC, action, pathProvider.json(rl(path)));
        }
    }
}
