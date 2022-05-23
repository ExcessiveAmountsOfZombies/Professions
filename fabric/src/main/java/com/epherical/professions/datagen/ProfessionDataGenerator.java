package com.epherical.professions.datagen;

import com.epherical.octoecon.api.Economy;
import com.epherical.org.mbertoli.jfep.Parser;
import com.epherical.professions.ProfessionsFabric;
import com.epherical.professions.datapack.ProfessionLoader;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.action.builtin.blocks.BreakBlockAction;
import com.epherical.professions.profession.action.builtin.blocks.PlaceBlockAction;
import com.epherical.professions.profession.action.builtin.blocks.TntDestroyAction;
import com.epherical.professions.profession.action.builtin.entity.BreedAction;
import com.epherical.professions.profession.action.builtin.entity.KillAction;
import com.epherical.professions.profession.action.builtin.entity.TameAction;
import com.epherical.professions.profession.action.builtin.items.BrewAction;
import com.epherical.professions.profession.action.builtin.items.CraftingAction;
import com.epherical.professions.profession.action.builtin.items.EnchantAction;
import com.epherical.professions.profession.action.builtin.items.FishingAction;
import com.epherical.professions.profession.action.builtin.items.SmeltItemAction;
import com.epherical.professions.profession.action.builtin.items.TakeSmeltAction;
import com.epherical.professions.profession.action.builtin.items.TradeAction;
import com.epherical.professions.profession.conditions.builtin.BlockStatePropertyAnyCondition;
import com.epherical.professions.profession.conditions.builtin.FullyGrownCropCondition;
import com.epherical.professions.profession.conditions.builtin.ToolMatcher;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.profession.rewards.builtin.OccupationExperience;
import com.epherical.professions.profession.rewards.builtin.PaymentReward;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.io.IOException;
import java.nio.file.Path;

public class ProfessionDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        fabricDataGenerator.addProvider(ProfessionProvider::new);
    }


    private static class ProfessionProvider implements DataProvider {

        private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

        private final DataGenerator dataGenerator;

        public ProfessionProvider(FabricDataGenerator dataGenerator) {
            this.dataGenerator = dataGenerator;
        }

        @Override
        public void run(HashCache cache) throws IOException {
            Path path = this.dataGenerator.getOutputFolder();
            ProfessionBuilder alchemy = ProfessionBuilder.profession(
                            TextColor.parseColor("#a100e0"),
                            TextColor.parseColor("#FFFFFF"),
                            new String[]{
                                    "Earn money and experience",
                                    "by brewing potions."},
                            "Alchemy", 100)
                    .addExperienceScaling(defaultLevelParser())
                    .incomeScaling(defaultIncomeParser())
                    .addAction(Actions.BREW_ITEM, BrewAction.brew()
                            .item(Items.NETHER_WART)
                            .reward(expReward(2))
                            .build())
                    .addAction(Actions.BREW_ITEM, BrewAction.brew()
                            .item(Items.REDSTONE, Items.GLOWSTONE_DUST)
                            .reward(expReward(2))
                            .build())
                    .addAction(Actions.BREW_ITEM, BrewAction.brew()
                            .item(Items.FERMENTED_SPIDER_EYE)
                            .reward(expReward(4))
                            .build())
                    .addAction(Actions.BREW_ITEM, BrewAction.brew()
                            .item(Items.GUNPOWDER)
                            .reward(expReward(4))
                            .build())
                    .addAction(Actions.BREW_ITEM, BrewAction.brew()
                            .item(Items.DRAGON_BREATH)
                            .reward(expReward(6))
                            .build())
                    .addAction(Actions.BREW_ITEM, BrewAction.brew()
                            .item(Items.SUGAR)
                            .reward(expReward(0.5))
                            .build())
                    .addAction(Actions.BREW_ITEM, BrewAction.brew()
                            .item(Items.RABBIT_FOOT, Items.TURTLE_HELMET, Items.PHANTOM_MEMBRANE)
                            .reward(expReward(10))
                            .build())
                    .addAction(Actions.BREW_ITEM, BrewAction.brew()
                            .item(Items.GLISTERING_MELON_SLICE)
                            .reward(expReward(3))
                            .build())
                    .addAction(Actions.BREW_ITEM, BrewAction.brew()
                            .item(Items.SPIDER_EYE, Items.MAGMA_CREAM, Items.BLAZE_POWDER)
                            .reward(expReward(2))
                            .build())
                    .addAction(Actions.BREW_ITEM, BrewAction.brew()
                            .item(Items.PUFFERFISH, Items.GOLDEN_CARROT, Items.GHAST_TEAR)
                            .reward(expReward(5))
                            .build());
            ProfessionBuilder builder = ProfessionBuilder.profession(
                            TextColor.parseColor("#f2de00"),
                            TextColor.parseColor("#FFFFFF"),
                            new String[]{
                                    "Earn money and experience",
                                    "by placing blocks."},
                            "Building", 100)
                    .addExperienceScaling(defaultLevelParser())
                    .incomeScaling(defaultIncomeParser())
                    .addAction(Actions.PLACE_BLOCK, PlaceBlockAction.place()
                            .block(Blocks.STONE, Blocks.ANDESITE, Blocks.GRANITE, Blocks.DIORITE, Blocks.STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS,
                                    Blocks.CRACKED_STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS, Blocks.DEEPSLATE, Blocks.DEEPSLATE_BRICKS,
                                    Blocks.DEEPSLATE_TILES, Blocks.CHISELED_DEEPSLATE, Blocks.COBBLED_DEEPSLATE, Blocks.POLISHED_DEEPSLATE, Blocks.COBBLESTONE,
                                    Blocks.POLISHED_BASALT, Blocks.BLACKSTONE, Blocks.GILDED_BLACKSTONE, Blocks.CHISELED_POLISHED_BLACKSTONE)
                            .reward(expReward(1))
                            .build())
                    .addAction(Actions.PLACE_BLOCK, PlaceBlockAction.place()
                            .block(Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.ACACIA_PLANKS,
                                    Blocks.DARK_OAK_PLANKS, Blocks.WARPED_PLANKS, Blocks.CRIMSON_PLANKS)
                            .reward(expReward(1.3))
                            .build())
                    .addAction(Actions.PLACE_BLOCK, PlaceBlockAction.place()
                            .block(Blocks.IRON_BLOCK, Blocks.GOLD_BLOCK, Blocks.DIAMOND_BLOCK, Blocks.EMERALD_BLOCK, Blocks.LAPIS_BLOCK, Blocks.NETHERITE_BLOCK,
                                    Blocks.BRICKS, Blocks.BOOKSHELF, Blocks.MOSSY_COBBLESTONE, Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN)
                            .reward(expReward(1.5))
                            .build())
                    .addAction(Actions.PLACE_BLOCK, PlaceBlockAction.place()
                            .block(Blocks.PRISMARINE, Blocks.DARK_PRISMARINE, Blocks.SEA_LANTERN, Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR)
                            .reward(expReward(1.25))
                            .build())
                    .addAction(Actions.PLACE_BLOCK, PlaceBlockAction.place()
                            .block(Blocks.CHEST, Blocks.ENDER_CHEST, Blocks.ANVIL, Blocks.FURNACE, Blocks.BLAST_FURNACE, Blocks.SMOKER, Blocks.CAMPFIRE,
                                    Blocks.SOUL_CAMPFIRE, Blocks.CRAFTING_TABLE, Blocks.ENCHANTING_TABLE, Blocks.JUKEBOX, Blocks.HOPPER)
                            .reward(expReward(1.5))
                            .build())
                    .addAction(Actions.PLACE_BLOCK, PlaceBlockAction.place()
                            .block(Blocks.GLASS, Blocks.GLASS_PANE, Blocks.IRON_BARS, Blocks.BLACK_STAINED_GLASS, Blocks.WHITE_STAINED_GLASS,
                                    Blocks.ORANGE_STAINED_GLASS, Blocks.MAGENTA_STAINED_GLASS, Blocks.LIGHT_BLUE_STAINED_GLASS, Blocks.YELLOW_STAINED_GLASS,
                                    Blocks.LIME_STAINED_GLASS, Blocks.PINK_STAINED_GLASS, Blocks.GRAY_STAINED_GLASS, Blocks.LIGHT_GRAY_STAINED_GLASS, Blocks.CYAN_STAINED_GLASS,
                                    Blocks.PURPLE_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS, Blocks.BROWN_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS,
                                    Blocks.RED_STAINED_GLASS, Blocks.BLACK_STAINED_GLASS,
                                    Blocks.WHITE_STAINED_GLASS_PANE, Blocks.ORANGE_STAINED_GLASS_PANE, Blocks.MAGENTA_STAINED_GLASS_PANE,
                                    Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Blocks.YELLOW_STAINED_GLASS_PANE, Blocks.LIME_STAINED_GLASS_PANE,
                                    Blocks.PINK_STAINED_GLASS_PANE, Blocks.GRAY_STAINED_GLASS_PANE, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE,
                                    Blocks.CYAN_STAINED_GLASS_PANE, Blocks.PURPLE_STAINED_GLASS_PANE, Blocks.BLUE_STAINED_GLASS_PANE,
                                    Blocks.BROWN_STAINED_GLASS_PANE, Blocks.GREEN_STAINED_GLASS_PANE, Blocks.RED_STAINED_GLASS_PANE,
                                    Blocks.BLACK_STAINED_GLASS_PANE)
                            .reward(expReward(1.3))
                            .build())
                    .addAction(Actions.PLACE_BLOCK, PlaceBlockAction.place()
                            .block(BlockTags.WOOL)
                            .reward(expReward(1.3))
                            .build())
                    .addAction(Actions.PLACE_BLOCK, PlaceBlockAction.place()
                            .block(BlockTags.SLABS)
                            .block(BlockTags.STAIRS)
                            .block(BlockTags.WALLS)
                            .block(BlockTags.FENCE_GATES)
                            .block(BlockTags.FENCES)
                            .block(BlockTags.BANNERS)
                            .block(BlockTags.CORALS)
                            .block(BlockTags.CORAL_BLOCKS)
                            .block(BlockTags.CORAL_PLANTS)
                            .block(BlockTags.DOORS)
                            .block(BlockTags.TRAPDOORS)
                            .block(BlockTags.TERRACOTTA)
                            .reward(expReward(1))
                            .build());
            ProfessionBuilder crafting = ProfessionBuilder.profession(
                            TextColor.parseColor("#f2a100"),
                            TextColor.parseColor("#FFFFFF"),
                            new String[]{
                                    "Earn money and experience",
                                    "by crafting."},
                            "Crafting", 100)
                    .addExperienceScaling(defaultLevelParser())
                    .incomeScaling(defaultIncomeParser())
                    .addAction(Actions.CRAFTS_ITEM, CraftingAction.craft()
                            .reward(expReward(2))
                            .item(ItemTags.WOODEN_PRESSURE_PLATES)
                            .item(ItemTags.BANNERS)
                            .item(ItemTags.STAIRS)
                            .item(ItemTags.FENCES)
                            .item(ItemTags.WOODEN_DOORS)
                            .item(ItemTags.WOODEN_TRAPDOORS)
                            .item(ItemTags.WOODEN_BUTTONS)
                            .build())
                    .addAction(Actions.CRAFTS_ITEM, CraftingAction.craft()
                            .reward(expReward(4))
                            .item(Items.CHEST)
                            .item(Items.FURNACE)
                            .build())
                    .addAction(Actions.CRAFTS_ITEM, CraftingAction.craft()
                            .item(Items.ANVIL, Items.JUKEBOX, Items.TNT)
                            .reward(expReward(6))
                            .build())
                    .addAction(Actions.CRAFTS_ITEM, CraftingAction.craft()
                            .item(Items.DISPENSER, Items.BLAST_FURNACE, Items.PISTON)
                            .reward(expReward(3))
                            .build())
                    .addAction(Actions.CRAFTS_ITEM, CraftingAction.craft()
                            .item(Items.BEACON)
                            .reward(expReward(200))
                            .build())
                    .addAction(Actions.CRAFTS_ITEM, CraftingAction.craft()
                            .item(Items.IRON_DOOR, Items.IRON_BARS, Items.HOPPER, Items.CAULDRON)
                            .reward(expReward(10))
                            .build())
                    .addAction(Actions.CRAFTS_ITEM, CraftingAction.craft()
                            .item(Items.COMPARATOR, Items.COMPASS, Items.CLOCK, Items.BREWING_STAND)
                            .item(ItemTags.BEDS)
                            .reward(expReward(6))
                            .build());
            ProfessionBuilder enchanting = ProfessionBuilder.profession(
                            TextColor.parseColor("#c9008d"),
                            TextColor.parseColor("#FFFFFF"),
                            new String[]{"Earn money and experience",
                                    "by enchanting."},
                            "Enchanting", 100)
                    .addExperienceScaling(defaultLevelParser())
                    .incomeScaling(defaultIncomeParser())
                    .addAction(Actions.ENCHANT_ITEM, EnchantAction.enchant()
                            .item(Items.DIAMOND_HELMET)
                            .reward(expReward(10))
                            .build())
                    .addAction(Actions.ENCHANT_ITEM, EnchantAction.enchant()
                            .item(Items.DIAMOND_CHESTPLATE)
                            .reward(expReward(16))
                            .build())
                    .addAction(Actions.ENCHANT_ITEM, EnchantAction.enchant()
                            .item(Items.DIAMOND_LEGGINGS)
                            .reward(expReward(14))
                            .build())
                    .addAction(Actions.ENCHANT_ITEM, EnchantAction.enchant()
                            .item(Items.DIAMOND_BOOTS)
                            .reward(expReward(8))
                            .build())
                    .addAction(Actions.ENCHANT_ITEM, EnchantAction.enchant()
                            .item(Items.DIAMOND_SWORD)
                            .reward(expReward(4))
                            .build())
                    .addAction(Actions.ENCHANT_ITEM, EnchantAction.enchant()
                            .item(Items.DIAMOND_SHOVEL)
                            .reward(expReward(2))
                            .build())
                    .addAction(Actions.ENCHANT_ITEM, EnchantAction.enchant()
                            .item(Items.DIAMOND_PICKAXE)
                            .reward(expReward(6))
                            .build())
                    .addAction(Actions.ENCHANT_ITEM, EnchantAction.enchant()
                            .item(Items.DIAMOND_AXE)
                            .reward(expReward(6))
                            .build())
                    .addAction(Actions.ENCHANT_ITEM, EnchantAction.enchant()
                            .item(Items.DIAMOND_CHESTPLATE)
                            .reward(expReward(16))
                            .build());
            for (Enchantment enchantment : Registry.ENCHANTMENT) {
                if (enchantment.isCurse() || enchantment.isTreasureOnly() || !enchantment.isDiscoverable()) {
                    continue;
                }
                for (int i = 1; i <= enchantment.getMaxLevel(); i++) {
                    enchanting.addAction(Actions.ENCHANT_ITEM, EnchantAction.enchant()
                            .enchant(enchantment, i)
                            .reward(expReward(2 * i))
                            .build());
                }
            }
            ProfessionBuilder farming = ProfessionBuilder.profession(
                            TextColor.parseColor("#107d0e"),
                            TextColor.parseColor("#FFFFFF"),
                            new String[]{
                                    "Earn money and experience",
                                    "by farming."},
                            "Farming", 100)
                    .addExperienceScaling(defaultLevelParser())
                    .incomeScaling(defaultIncomeParser())
                    .addAction(Actions.BREAK_BLOCK, BreakBlockAction.breakBlock()
                            .block(BlockTags.CROPS)
                            .condition(FullyGrownCropCondition::new)
                            .reward(expReward(1))
                            .build())
                    .addAction(Actions.BREAK_BLOCK, BreakBlockAction.breakBlock()
                            .block(Blocks.COCOA)
                            .condition(BlockStatePropertyAnyCondition.checkProperties()
                                    .properties(StatePropertiesPredicate.Builder.properties()
                                            .hasProperty(BlockStateProperties.AGE_2, 2)))
                            .reward(expReward(1))
                            .build())
                    .addAction(Actions.BREAK_BLOCK, BreakBlockAction.breakBlock()
                            .block(Blocks.SUGAR_CANE)
                            .reward(expReward(0.75))
                            .build())
                    .addAction(Actions.BREAK_BLOCK, BreakBlockAction.breakBlock()
                            .block(Blocks.MELON, Blocks.PUMPKIN)
                            .reward(expReward(1.3))
                            .build())
                    .addAction(Actions.PLACE_BLOCK, PlaceBlockAction.place()
                            .block(BlockTags.CROPS)
                            .block(Blocks.COCOA)
                            .reward(expReward(0.85))
                            .build())
                    .addAction(Actions.BREED_ENTITY, BreedAction.breed()
                            .entity(EntityType.PIG, EntityType.COW, EntityType.MOOSHROOM,
                                    EntityType.SHEEP, EntityType.RABBIT, EntityType.LLAMA,
                                    EntityType.TURTLE, EntityType.OCELOT, EntityType.WOLF, EntityType.CHICKEN,
                                    EntityType.HORSE)
                            .reward(expReward(5.0))
                            .build());
            ProfessionBuilder fishing = ProfessionBuilder.profession(
                            TextColor.parseColor("#0a91c7"),
                            TextColor.parseColor("#FFFFFF"),
                            new String[]{
                                    "Earn money and experience",
                                    "by fishing."},
                            "Fishing", 100)
                    .addExperienceScaling(defaultLevelParser())
                    .incomeScaling(defaultIncomeParser())
                    .addAction(Actions.FISH_ACTION, FishingAction.fish()
                            .item(Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH)
                            .reward(expReward(25))
                            .build());
            ProfessionBuilder hunting = ProfessionBuilder.profession(
                            TextColor.parseColor("#a6542e"),
                            TextColor.parseColor("#FFFFFF"),
                            new String[]{
                                    "Earn money and experience",
                                    "by hunting animals and killing monsters."},
                            "Hunting", 100)
                    .addExperienceScaling(defaultLevelParser())
                    .incomeScaling(defaultIncomeParser())
                    .addAction(Actions.KILL_ENTITY, KillAction.kill()
                            .entity(EntityType.PIG, EntityType.CHICKEN, EntityType.SHEEP, EntityType.COW, EntityType.MOOSHROOM, EntityType.RABBIT)
                            .reward(expReward(8))
                            .build())
                    .addAction(Actions.KILL_ENTITY, KillAction.kill()
                            .entity(EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER, EntityType.CAVE_SPIDER, EntityType.SHULKER)
                            .reward(expReward(12))
                            .build())
                    .addAction(Actions.KILL_ENTITY, KillAction.kill()
                            .entity(EntityType.BLAZE, EntityType.WITHER_SKELETON, EntityType.PIGLIN_BRUTE, EntityType.HOGLIN, EntityType.PHANTOM)
                            .reward(expReward(15))
                            .build())
                    .addAction(Actions.KILL_ENTITY, KillAction.kill()
                            .entity(EntityType.WITHER)
                            .reward(expReward(100))
                            .build())
                    .addAction(Actions.KILL_ENTITY, KillAction.kill()
                            .entity(EntityType.ENDER_DRAGON)
                            .reward(expReward(600))
                            .build())
                    .addAction(Actions.TAME_ENTITY, TameAction.tame()
                            .entity(EntityType.WOLF)
                            .reward(expReward(15))
                            .build());
            ProfessionBuilder mining = ProfessionBuilder.profession(
                            TextColor.parseColor("#666E63"),
                            TextColor.parseColor("#FFFFFF"),
                            new String[]{
                                    "Earn money and experience",
                                    "by mining ores and minerals."},
                            "Mining", 100)
                    .addExperienceScaling(defaultLevelParser())
                    .incomeScaling(defaultIncomeParser())
                    .addAction(Actions.BREAK_BLOCK, BreakBlockAction.breakBlock()
                            .block(BlockTags.IRON_ORES)
                            .condition(ToolMatcher.toolMatcher(ItemPredicate.Builder.item()
                                    .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.ANY))).invert())
                            .reward(expReward(4))
                            .build())
                    .addAction(Actions.BREAK_BLOCK, BreakBlockAction.breakBlock()
                            .block(BlockTags.COPPER_ORES)
                            .condition(ToolMatcher.toolMatcher(ItemPredicate.Builder.item()
                                    .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.ANY))).invert())
                            .reward(expReward(3.5))
                            .build())
                    .addAction(Actions.BREAK_BLOCK, BreakBlockAction.breakBlock()
                            .block(BlockTags.GOLD_ORES)
                            .condition(ToolMatcher.toolMatcher(ItemPredicate.Builder.item()
                                    .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.ANY))).invert())
                            .reward(expReward(5.5))
                            .build())
                    .addAction(Actions.BREAK_BLOCK, BreakBlockAction.breakBlock()
                            .block(BlockTags.LAPIS_ORES)
                            .condition(ToolMatcher.toolMatcher(ItemPredicate.Builder.item()
                                    .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.ANY))).invert())
                            .reward(expReward(5.5))
                            .build())
                    .addAction(Actions.BREAK_BLOCK, BreakBlockAction.breakBlock()
                            .block(BlockTags.COAL_ORES)
                            .condition(ToolMatcher.toolMatcher(ItemPredicate.Builder.item()
                                    .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.ANY))).invert())
                            .reward(expReward(1.5))
                            .build())
                    .addAction(Actions.BREAK_BLOCK, BreakBlockAction.breakBlock()
                            .block(BlockTags.DIAMOND_ORES)
                            .condition(ToolMatcher.toolMatcher(ItemPredicate.Builder.item()
                                    .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.ANY))).invert())
                            .reward(expReward(12.5))
                            .build())
                    .addAction(Actions.BREAK_BLOCK, BreakBlockAction.breakBlock()
                            .block(BlockTags.EMERALD_ORES)
                            .condition(ToolMatcher.toolMatcher(ItemPredicate.Builder.item()
                                    .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.ANY))).invert())
                            .reward(expReward(14.5))
                            .build())
                    .addAction(Actions.BREAK_BLOCK, BreakBlockAction.breakBlock()
                            .block(BlockTags.REDSTONE_ORES)
                            .condition(ToolMatcher.toolMatcher(ItemPredicate.Builder.item()
                                    .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.ANY))).invert())
                            .reward(expReward(4.5))
                            .build())
                    .addAction(Actions.BREAK_BLOCK, BreakBlockAction.breakBlock()
                            .block(Blocks.NETHER_QUARTZ_ORE)
                            .condition(ToolMatcher.toolMatcher(ItemPredicate.Builder.item()
                                    .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.ANY))).invert())
                            .reward(expReward(3))
                            .build())
                    .addAction(Actions.BREAK_BLOCK, BreakBlockAction.breakBlock()
                            .block(Blocks.OBSIDIAN)
                            .reward(expReward(7))
                            .build())
                    .addAction(Actions.BREAK_BLOCK, BreakBlockAction.breakBlock()
                            .block(Blocks.STONE, Blocks.ANDESITE, Blocks.GRANITE, Blocks.DIORITE, Blocks.DEEPSLATE)
                            .reward(expReward(1))
                            .build())
                    .addAction(Actions.TNT_DESTROY, TntDestroyAction.tntDestroy()
                            .block(Blocks.STONE, Blocks.ANDESITE, Blocks.GRANITE, Blocks.DIORITE, Blocks.DEEPSLATE)
                            .reward(expReward(0.5))
                            .build());
            ProfessionBuilder trading = ProfessionBuilder.profession(
                            TextColor.parseColor("#2dcf08"),
                            TextColor.parseColor("#FFFFFF"),
                            new String[]{
                                    "Earn money and experience",
                                    "by trading items to villagers."},
                            "Trading", 100)
                    .addExperienceScaling(defaultLevelParser())
                    .incomeScaling(defaultIncomeParser())
                    .addAction(Actions.VILLAGER_TRADE, TradeAction.trade()
                            .item(Items.EMERALD, Items.DIAMOND)
                            .reward(expReward(7))
                            .build())
                    .addAction(Actions.VILLAGER_TRADE, TradeAction.trade()
                            .item(Items.PAPER, Items.ROTTEN_FLESH)
                            .reward(expReward(0.25))
                            .build())
                    .addAction(Actions.VILLAGER_TRADE, TradeAction.trade()
                            .item(Items.GOLD_INGOT, Items.IRON_INGOT)
                            .reward(expReward(3))
                            .build())
                    .addAction(Actions.VILLAGER_TRADE, TradeAction.trade()
                            .item(Items.WHEAT, Items.CARROT, Items.POTATO, Items.BEETROOT)
                            .reward(expReward(2))
                            .build())
                    .addAction(Actions.VILLAGER_TRADE, TradeAction.trade()
                            .item(Items.TROPICAL_FISH, Items.SALMON, Items.COD, Items.PUFFERFISH)
                            .reward(expReward(3))
                            .build());
            ProfessionBuilder smithing = ProfessionBuilder.profession(
                            TextColor.parseColor("#84abad"),
                            TextColor.parseColor("#FFFFFF"),
                            new String[]{
                                    "Earn money and experience",
                                    "by smithing items." },
                            "Smithing", 100)
                    .addExperienceScaling(defaultLevelParser())
                    .incomeScaling(defaultIncomeParser())
                    .addAction(Actions.CRAFTS_ITEM, CraftingAction.craft()
                            .item(Items.WOODEN_SWORD, Items.WOODEN_AXE, Items.WOODEN_HOE, Items.WOODEN_SHOVEL, Items.WOODEN_PICKAXE)
                            .reward(expReward(1.0))
                            .build())
                    .addAction(Actions.CRAFTS_ITEM, CraftingAction.craft()
                            .item(Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS)
                            .reward(expReward(6.0))
                            .build())
                    .addAction(Actions.CRAFTS_ITEM, CraftingAction.craft()
                            .item(Items.STONE_SWORD, Items.STONE_AXE, Items.STONE_HOE, Items.STONE_SHOVEL, Items.STONE_PICKAXE)
                            .reward(expReward(3))
                            .build())
                    .addAction(Actions.CRAFTS_ITEM, CraftingAction.craft()
                            .item(Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS)
                            .reward(expReward(12.0))
                            .build())
                    .addAction(Actions.CRAFTS_ITEM, CraftingAction.craft()
                            .item(Items.IRON_SWORD, Items.IRON_AXE, Items.IRON_HOE, Items.IRON_SHOVEL, Items.IRON_PICKAXE)
                            .reward(expReward(5))
                            .build())
                    .addAction(Actions.CRAFTS_ITEM, CraftingAction.craft()
                            .item(Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS)
                            .reward(expReward(18.0))
                            .build())
                    .addAction(Actions.CRAFTS_ITEM, CraftingAction.craft()
                            .item(Items.GOLDEN_SWORD, Items.GOLDEN_AXE, Items.GOLDEN_HOE, Items.GOLDEN_SHOVEL, Items.GOLDEN_PICKAXE)
                            .reward(expReward(7))
                            .build())
                    .addAction(Actions.CRAFTS_ITEM, CraftingAction.craft()
                            .item(Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS)
                            .reward(expReward(24.0))
                            .build())
                    .addAction(Actions.CRAFTS_ITEM, CraftingAction.craft()
                            .item(Items.DIAMOND_SWORD, Items.DIAMOND_AXE, Items.DIAMOND_HOE, Items.DIAMOND_SHOVEL, Items.DIAMOND_PICKAXE)
                            .reward(expReward(12))
                            .build())
                    .addAction(Actions.ON_ITEM_COOK, SmeltItemAction.smelt()
                            .item(Items.GOLD_INGOT)
                            .reward(expReward(2))
                            .build())
                    .addAction(Actions.TAKE_COOKED_ITEM, TakeSmeltAction.takeSmelt()
                            .item(Items.IRON_INGOT)
                            .reward(expReward(1.3))
                            .build());
            ProfessionBuilder logging = ProfessionBuilder.profession(
                            TextColor.parseColor("#9e3011"),
                            TextColor.parseColor("#FFFFFF"),
                            new String[]{
                                    "Earn money and experience",
                                    "by farming trees." },
                            "Logging", 100)
                    .addExperienceScaling(defaultLevelParser())
                    .incomeScaling(defaultIncomeParser())
                    .addAction(Actions.BREAK_BLOCK, BreakBlockAction.breakBlock()
                            .block(BlockTags.LOGS)
                            .reward(expReward(1))
                            .build());


            Path alchemyProfession = createPath(path, new ResourceLocation("professions:alchemy"));
            Path builderProfession = createPath(path, new ResourceLocation("professions:building"));
            Path craftingProfession = createPath(path, new ResourceLocation("professions:crafting"));
            Path enchantingProfession = createPath(path, new ResourceLocation("professions:enchanting"));
            Path farmingProfession = createPath(path, new ResourceLocation("professions:farming"));
            Path fishingProfession = createPath(path, new ResourceLocation("professions:fishing"));
            Path huntingProfession = createPath(path, new ResourceLocation("professions:hunting"));
            Path miningProfession = createPath(path, new ResourceLocation("professions:mining"));
            Path tradingProfession = createPath(path, new ResourceLocation("professions:trading"));
            Path smithingProfession = createPath(path, new ResourceLocation("professions:smithing"));
            Path loggingProfession = createPath(path, new ResourceLocation("professions:logging"));
            DataProvider.save(GSON, cache, ProfessionLoader.serialize(alchemy.build()), alchemyProfession);
            DataProvider.save(GSON, cache, ProfessionLoader.serialize(builder.build()), builderProfession);
            DataProvider.save(GSON, cache, ProfessionLoader.serialize(crafting.build()), craftingProfession);
            DataProvider.save(GSON, cache, ProfessionLoader.serialize(enchanting.build()), enchantingProfession);
            DataProvider.save(GSON, cache, ProfessionLoader.serialize(farming.build()), farmingProfession);
            DataProvider.save(GSON, cache, ProfessionLoader.serialize(fishing.build()), fishingProfession);
            DataProvider.save(GSON, cache, ProfessionLoader.serialize(hunting.build()), huntingProfession);
            DataProvider.save(GSON, cache, ProfessionLoader.serialize(mining.build()), miningProfession);
            DataProvider.save(GSON, cache, ProfessionLoader.serialize(trading.build()), tradingProfession);
            DataProvider.save(GSON, cache, ProfessionLoader.serialize(smithing.build()), smithingProfession);
            DataProvider.save(GSON, cache, ProfessionLoader.serialize(logging.build()), loggingProfession);
        }

        private static Path createPath(Path path, ResourceLocation id) {
            String namespace = id.getNamespace();
            return path.resolve("resourcepacks/defaults/data/" + namespace + "/professions/occupations/" + id.getPath() + ".json");
        }

        @Override
        public String getName() {
            return "Professions";
        }

        public static Parser defaultLevelParser() {
            return new Parser("(91+1000)*((1.1)^(lvl-1))");
        }

        public static Parser defaultIncomeParser() {
            return new Parser("base");
        }

        public static Reward.Builder expReward(double reward) {
            return OccupationExperience.builder().exp(reward);
        }

        private static Reward.Builder moneyReward(double amount) {
            Economy economy = ProfessionsFabric.getEconomy();
            if (economy == null) {
                return PaymentReward.builder().money(amount, null);
            } else {
                return PaymentReward.builder().money(amount, economy.getDefaultCurrency());
            }
        }
    }


}
