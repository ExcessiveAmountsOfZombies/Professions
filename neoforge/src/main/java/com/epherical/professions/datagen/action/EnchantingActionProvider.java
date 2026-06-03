package com.epherical.professions.datagen.action;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.core.Profession;
import com.epherical.professions.api.actions.Action;
import com.epherical.professions.model.actions.item.BrewAction;
import com.epherical.professions.model.actions.item.EnchantAction;
import com.epherical.professions.model.actions.rewards.OccupationExperience;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.epherical.professions.datagen.ProfessionDataGeneration.id;
import static com.epherical.professions.datagen.ProfessionDataGeneration.rl;
import static net.minecraft.world.item.enchantment.Enchantments.*;

public final class EnchantingActionProvider implements DataProvider {

    private static final List<ItemTierAction> ITEM_TIER_ACTIONS = List.of(
            new ItemTierAction("enchant_wood_leather_material_items", 45, List.of(
                    Items.WOODEN_SWORD, Items.WOODEN_AXE, Items.WOODEN_PICKAXE, Items.WOODEN_SHOVEL, Items.WOODEN_HOE,
                    Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS
            )),
            new ItemTierAction("enchant_iron_gold_chainmail_bow_material_items", 80, List.of(
                    Items.STONE_SWORD, Items.STONE_AXE, Items.STONE_PICKAXE, Items.STONE_SHOVEL, Items.STONE_HOE,
                    Items.GOLDEN_SWORD, Items.GOLDEN_AXE, Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_HOE,
                    Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS,
                    Items.BOW, Items.CROSSBOW, Items.FISHING_ROD
            )),
            new ItemTierAction("enchant_iron_items", 125, List.of(
                    Items.IRON_SWORD, Items.IRON_AXE, Items.IRON_PICKAXE, Items.IRON_SHOVEL, Items.IRON_HOE,
                    Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS,
                    Items.SHIELD, Items.TRIDENT
            )),
            new ItemTierAction("enchant_diamond_items", 190, List.of(
                    Items.DIAMOND_SWORD, Items.DIAMOND_AXE, Items.DIAMOND_PICKAXE, Items.DIAMOND_SHOVEL, Items.DIAMOND_HOE,
                    Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS
            )),
            new ItemTierAction("enchant_netherite_items", 275, List.of(
                    Items.NETHERITE_SWORD, Items.NETHERITE_AXE, Items.NETHERITE_PICKAXE, Items.NETHERITE_SHOVEL, Items.NETHERITE_HOE,
                    Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS
            ))
    );

    private static final List<EnchantmentRarityAction> ENCHANTMENT_RARITY_ACTIONS = List.of(
            new EnchantmentRarityAction("enchant_common_enchantments", 30, List.of(
                    PROTECTION, SHARPNESS, EFFICIENCY,
                    UNBREAKING, POWER, BLAST_PROTECTION,
                    FIRE_PROTECTION, PROJECTILE_PROTECTION
            )),
            new EnchantmentRarityAction("enchant_uncommon_enchantments", 65, List.of(
                    FEATHER_FALLING, FIRE_ASPECT, PUNCH,
                    LOYALTY, RESPIRATION, BANE_OF_ARTHROPODS,
                    BREACH, DENSITY, FLAME,
                    LURE, PIERCING, QUICK_CHARGE,
                    SWEEPING_EDGE
            )),
            new EnchantmentRarityAction("enchant_rare_enchantments", 120, List.of(
                    FORTUNE, LOOTING, SILK_TOUCH,
                    RIPTIDE, CHANNELING, AQUA_AFFINITY,
                    IMPALING, LUCK_OF_THE_SEA, MULTISHOT

            ))
    );

    private static final List<BrewRecord> BREW_ACTIONS = List.of(
            new BrewRecord("nether_wart", Items.NETHER_WART, 20),
            new BrewRecord("redstone_dust", Items.REDSTONE, 28),
            new BrewRecord("glowstone_dust", Items.GLOWSTONE_DUST, 36),
            new BrewRecord("fermented_spider_eye", Items.FERMENTED_SPIDER_EYE, 40),
            new BrewRecord("gunpowder", Items.GUNPOWDER, 50),
            new BrewRecord("dragon_breath", Items.DRAGON_BREATH, 220),
            new BrewRecord("sugar", Items.SUGAR, 18),
            new BrewRecord("rabbit_foot", Items.RABBIT_FOOT, 75),
            new BrewRecord("glistering_melon_slice", Items.GLISTERING_MELON_SLICE, 55),
            new BrewRecord("spider_eye", Items.SPIDER_EYE, 26),
            new BrewRecord("blaze_powder", Items.BLAZE_POWDER, 70),
            new BrewRecord("golden_carrot", Items.GOLDEN_CARROT, 65),
            new BrewRecord("ghast_tear", Items.GHAST_TEAR, 120),
            new BrewRecord("pufferfish", Items.PUFFERFISH, 45),
            new BrewRecord("magma_cream", Items.MAGMA_CREAM, 85),
            new BrewRecord("turtle_shell", Items.TURTLE_HELMET, 140),
            new BrewRecord("phantom_membrane", Items.PHANTOM_MEMBRANE, 95),
            new BrewRecord("breeze_rod", Items.BREEZE_ROD, 110),
            new BrewRecord("stone", Items.STONE, 12),
            new BrewRecord("cobweb", Items.COBWEB, 32),
            new BrewRecord("slime_block", Items.SLIME_BLOCK, 34)
    );

    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

    public EnchantingActionProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "professions/actions/enchanting");
        this.lookupProvider = lookupProvider;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return lookupProvider.thenCompose(registries -> {
            Holder<Profession> enchantingProfession = registries.lookupOrThrow(ProfessionsCommon.PROFESSION_REGISTRY_KEY)
                    .get(id("enchanting"))
                    .orElseThrow(() -> new IllegalStateException("Missing enchanting profession for enchanting action datagen"));

            List<CompletableFuture<?>> writes = new ArrayList<>();

            ITEM_TIER_ACTIONS.stream()
                    .map(tier -> itemTierAction(tier.name(), enchantingProfession)
                            .rewardExp(tier.experience())
                            .items(tier.items())
                            .save(output, registries))
                    .forEach(writes::add);

            ENCHANTMENT_RARITY_ACTIONS.stream()
                    .map(rarity -> enchantmentRarityAction(rarity.name(), enchantingProfession)
                            .rewardExp(rarity.experience())
                            .enchantments(rarity.enchantments())
                            .save(output, registries))
                    .forEach(writes::add);

            BREW_ACTIONS.stream()
                    .map(brew -> brewAction("brew_" + brew.name(), enchantingProfession)
                            .rewardExp(brew.experience())
                            .ingredient(brew.ingredient())
                            .save(output, registries))
                    .forEach(writes::add);

            return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName() {
        return "Professions Enchanting Action Provider";
    }

    private EnchantingActionBuilder itemTierAction(String path, Holder<Profession> profession) {
        return new EnchantingActionBuilder(path, profession, pathProvider);
    }

    private EnchantingActionBuilder enchantmentRarityAction(String path, Holder<Profession> profession) {
        return new EnchantingActionBuilder(path, profession, pathProvider);
    }

    private BrewingActionBuilder brewAction(String path, Holder<Profession> profession) {
        return new BrewingActionBuilder(path, profession, pathProvider);
    }

    private record ItemTierAction(String name, double experience, List<Item> items) {
    }

    private record EnchantmentRarityAction(String name, double experience, List<ResourceKey<Enchantment>> enchantments) {
    }

    private record BrewRecord(String name, Item ingredient, double experience) {
    }

    private static final class EnchantingActionBuilder {
        private final String path;
        private final PackOutput.PathProvider pathProvider;
        private final EnchantAction.Builder builder;

        private EnchantingActionBuilder(String path, Holder<Profession> profession, PackOutput.PathProvider pathProvider) {
            this.path = path;
            this.pathProvider = pathProvider;
            this.builder = new EnchantAction.Builder(profession);
        }

        private EnchantingActionBuilder rewardExp(double exp) {
            builder.reward(new OccupationExperience.Builder().exp(exp));
            return this;
        }

        private EnchantingActionBuilder items(List<Item> items) {
            for (Item item : items) {
                builder.target(item.builtInRegistryHolder().key());
            }
            return this;
        }

        private EnchantingActionBuilder enchantments(List<ResourceKey<Enchantment>> enchantments) {
            for (ResourceKey<Enchantment> enchantment : enchantments) {
                builder.enchantment(enchantment);
            }
            return this;
        }

        private CompletableFuture<?> save(CachedOutput output, HolderLookup.Provider registries) {
            Action<?> action = builder.build();
            return DataProvider.saveStable(output, registries, Action.TYPED_CODEC, action, pathProvider.json(rl(path)));
        }
    }

    private static final class BrewingActionBuilder {
        private final String path;
        private final PackOutput.PathProvider pathProvider;
        private final BrewAction.Builder builder;

        private BrewingActionBuilder(String path, Holder<Profession> profession, PackOutput.PathProvider pathProvider) {
            this.path = path;
            this.pathProvider = pathProvider;
            this.builder = new BrewAction.Builder(profession);
        }

        private BrewingActionBuilder rewardExp(double exp) {
            builder.reward(new OccupationExperience.Builder().exp(exp));
            return this;
        }

        private BrewingActionBuilder ingredient(Item item) {
            builder.target(item.builtInRegistryHolder().key());
            return this;
        }

        private CompletableFuture<?> save(CachedOutput output, HolderLookup.Provider registries) {
            Action<?> action = builder.build();
            return DataProvider.saveStable(output, registries, Action.TYPED_CODEC, action, pathProvider.json(rl(path)));
        }
    }
}
