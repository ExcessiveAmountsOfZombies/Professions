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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.epherical.professions.datagen.ProfessionDataGeneration.id;
import static com.epherical.professions.datagen.ProfessionDataGeneration.rl;

public final class SmithingActionProvider implements DataProvider {

    private static final List<SmithingCraftAction> TOOL_ACTIONS = List.of(
            new SmithingCraftAction("craft_tools_wooden", List.of(Items.WOODEN_PICKAXE, Items.WOODEN_AXE, Items.WOODEN_SHOVEL, Items.WOODEN_HOE), 40),
            new SmithingCraftAction("craft_tools_stone", List.of(Items.STONE_PICKAXE, Items.STONE_AXE, Items.STONE_SHOVEL, Items.STONE_HOE), 70),
            new SmithingCraftAction("craft_tools_iron", List.of(Items.IRON_PICKAXE, Items.IRON_AXE, Items.IRON_SHOVEL, Items.IRON_HOE), 120),
            new SmithingCraftAction("craft_tools_golden", List.of(Items.GOLDEN_PICKAXE, Items.GOLDEN_AXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_HOE), 140),
            new SmithingCraftAction("craft_tools_diamond", List.of(Items.DIAMOND_PICKAXE, Items.DIAMOND_AXE, Items.DIAMOND_SHOVEL, Items.DIAMOND_HOE), 220),
            new SmithingCraftAction("craft_tools_netherite", List.of(Items.NETHERITE_PICKAXE, Items.NETHERITE_AXE, Items.NETHERITE_SHOVEL, Items.NETHERITE_HOE), 360)
    );

    private static final List<SmithingCraftAction> ARMOR_ACTIONS = List.of(
            new SmithingCraftAction("craft_armor_leather", List.of(Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS), 70),
            new SmithingCraftAction("craft_armor_chainmail", List.of(Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS), 120),
            new SmithingCraftAction("craft_armor_iron", List.of(Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS), 180),
            new SmithingCraftAction("craft_armor_golden", List.of(Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS), 200),
            new SmithingCraftAction("craft_armor_diamond", List.of(Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS), 300),
            new SmithingCraftAction("craft_armor_netherite", List.of(Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS), 480),
            new SmithingCraftAction("craft_armor_turtle_shell", List.of(Items.TURTLE_HELMET), 220)
    );

    private static final List<SmithingCraftAction> WEAPON_ACTIONS = List.of(
            new SmithingCraftAction("craft_weapons_wooden", List.of(Items.WOODEN_SWORD), 40),
            new SmithingCraftAction("craft_weapons_stone", List.of(Items.STONE_SWORD), 70),
            new SmithingCraftAction("craft_weapons_iron", List.of(Items.IRON_SWORD), 120),
            new SmithingCraftAction("craft_weapons_golden", List.of(Items.GOLDEN_SWORD), 140),
            new SmithingCraftAction("craft_weapons_diamond", List.of(Items.DIAMOND_SWORD), 220),
            new SmithingCraftAction("craft_weapons_netherite", List.of(Items.NETHERITE_SWORD), 360)
    );

    private static final List<SmithingCraftAction> RANGED_ACTIONS = List.of(
            new SmithingCraftAction("craft_ranged_bow", List.of(Items.BOW), 90),
            new SmithingCraftAction("craft_ranged_crossbow", List.of(Items.CROSSBOW), 140)
    );

    private static final List<SmithingSmeltAction> SMELTED_ITEM_ACTIONS = List.of(
            new SmithingSmeltAction("take_smelted_copper_ingot", Items.COPPER_INGOT, 35),
            new SmithingSmeltAction("take_smelted_iron_ingot", Items.IRON_INGOT, 55),
            new SmithingSmeltAction("take_smelted_gold_ingot", Items.GOLD_INGOT, 70),
            new SmithingSmeltAction("take_smelted_diamond", Items.DIAMOND, 140),
            new SmithingSmeltAction("take_smelted_lapis_lazuli", Items.LAPIS_LAZULI, 80),
            new SmithingSmeltAction("take_smelted_emerald", Items.EMERALD, 160),
            new SmithingSmeltAction("take_smelted_netherite_scrap", Items.NETHERITE_SCRAP, 250),
            new SmithingSmeltAction("take_smelted_coal", Items.COAL, 25),
            new SmithingSmeltAction("take_smelted_quartz", Items.QUARTZ, 45),
            new SmithingSmeltAction("take_smelted_redstone", Items.REDSTONE, 40),
            new SmithingSmeltAction("take_smelted_gold_nugget", Items.GOLD_NUGGET, 20),
            new SmithingSmeltAction("take_smelted_iron_nugget", Items.IRON_NUGGET, 20)
    );

    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

    public SmithingActionProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "professions/actions/smithing");
        this.lookupProvider = lookupProvider;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return lookupProvider.thenCompose(registries -> {
            Holder<Profession> smithingProfession = registries.lookupOrThrow(ProfessionsCommon.PROFESSION_REGISTRY_KEY)
                    .get(id("smithing"))
                    .orElseThrow(() -> new IllegalStateException("Missing smithing profession for smithing action datagen"));

            List<CompletableFuture<?>> writes = new ArrayList<>();
            addCraftActions(writes, TOOL_ACTIONS, smithingProfession, output, registries);
            addCraftActions(writes, ARMOR_ACTIONS, smithingProfession, output, registries);
            addCraftActions(writes, WEAPON_ACTIONS, smithingProfession, output, registries);
            addCraftActions(writes, RANGED_ACTIONS, smithingProfession, output, registries);
            addTakeSmeltActions(writes, SMELTED_ITEM_ACTIONS, smithingProfession, output, registries);
            return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName() {
        return "Professions Smithing Action Provider";
    }

    private void addCraftActions(List<CompletableFuture<?>> writes, List<SmithingCraftAction> actions, Holder<Profession> profession, CachedOutput output, HolderLookup.Provider registries) {
        actions.stream()
                .map(craftAction -> action(craftAction.name(), profession)
                        .rewardExp(craftAction.experience())
                        .items(craftAction.items())
                        .save(output, registries))
                .forEach(write -> writes.add(write));
    }

    private void addTakeSmeltActions(List<CompletableFuture<?>> writes, List<SmithingSmeltAction> actions, Holder<Profession> profession, CachedOutput output, HolderLookup.Provider registries) {
        actions.stream()
                .map(smeltAction -> takeSmeltAction(smeltAction.name(), profession)
                        .rewardExp(smeltAction.experience())
                        .item(smeltAction.item())
                        .save(output, registries))
                .forEach(write -> writes.add(write));
    }

    private SmithingCraftingActionBuilder action(String path, Holder<Profession> profession) {
        return new SmithingCraftingActionBuilder(path, profession, pathProvider);
    }

    private TakeSmeltActionBuilder takeSmeltAction(String path, Holder<Profession> profession) {
        return new TakeSmeltActionBuilder(path, profession, pathProvider);
    }

    private record SmithingCraftAction(String name, List<Item> items, double experience) {
    }

    private record SmithingSmeltAction(String name, Item item, double experience) {
    }

    private static final class TakeSmeltActionBuilder {
        private final String path;
        private final PackOutput.PathProvider pathProvider;
        private final TakeSmeltAction.Builder builder;

        private TakeSmeltActionBuilder(String path, Holder<Profession> profession, PackOutput.PathProvider pathProvider) {
            this.path = path;
            this.pathProvider = pathProvider;
            this.builder = new TakeSmeltAction.Builder(profession);
        }

        private TakeSmeltActionBuilder rewardExp(double exp) {
            builder.reward(new OccupationExperience.Builder().exp(exp));
            return this;
        }

        private TakeSmeltActionBuilder item(Item item) {
            builder.target(item.builtInRegistryHolder().key());
            return this;
        }

        private CompletableFuture<?> save(CachedOutput output, HolderLookup.Provider registries) {
            Action<?> action = builder.build();
            return DataProvider.saveStable(output, registries, Action.TYPED_CODEC, action, pathProvider.json(rl(path)));
        }
    }

    private static final class SmithingCraftingActionBuilder {
        private final String path;
        private final PackOutput.PathProvider pathProvider;
        private final CraftingAction.Builder builder;

        private SmithingCraftingActionBuilder(String path, Holder<Profession> profession, PackOutput.PathProvider pathProvider) {
            this.path = path;
            this.pathProvider = pathProvider;
            this.builder = new CraftingAction.Builder(profession);
        }

        private SmithingCraftingActionBuilder rewardExp(double exp) {
            builder.reward(new OccupationExperience.Builder().exp(exp));
            return this;
        }

        private SmithingCraftingActionBuilder item(Item item) {
            builder.target(item.builtInRegistryHolder().key());
            return this;
        }

        private SmithingCraftingActionBuilder items(List<Item> items) {
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
