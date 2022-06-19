package com.epherical.professions.datagen;

import com.epherical.professions.Constants;
import com.epherical.professions.profession.ProfessionBuilder;
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

import static com.epherical.professions.profession.action.Actions.*;

public class FabricProfessionProvider extends CommonProvider implements DataProvider, ProviderHelpers {

    private final DataGenerator dataGenerator;

    public FabricProfessionProvider(FabricDataGenerator dataGenerator) {
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
                .addAction(BREW_ITEM, BrewAction.brew()
                        .item(Items.NETHER_WART)
                        .reward(expReward(2))
                        .reward(moneyReward(0.5))
                        .build())
                .addAction(BREW_ITEM, BrewAction.brew()
                        .item(Items.REDSTONE, Items.GLOWSTONE_DUST)
                        .reward(expReward(2))
                        .reward(moneyReward(0.65))
                        .build())
                .addAction(BREW_ITEM, BrewAction.brew()
                        .item(Items.FERMENTED_SPIDER_EYE)
                        .reward(expReward(4))
                        .reward(moneyReward(1))
                        .build())
                .addAction(BREW_ITEM, BrewAction.brew()
                        .item(Items.GUNPOWDER)
                        .reward(expReward(4))
                        .reward(moneyReward(0.85))
                        .build())
                .addAction(BREW_ITEM, BrewAction.brew()
                        .item(Items.DRAGON_BREATH)
                        .reward(expReward(6))
                        .reward(moneyReward(1))
                        .build())
                .addAction(BREW_ITEM, BrewAction.brew()
                        .item(Items.SUGAR)
                        .reward(expReward(0.5))
                        .reward(moneyReward(0.25))
                        .build())
                .addAction(BREW_ITEM, BrewAction.brew()
                        .item(Items.RABBIT_FOOT, Items.TURTLE_HELMET, Items.PHANTOM_MEMBRANE)
                        .reward(expReward(10))
                        .reward(moneyReward(5))
                        .build())
                .addAction(BREW_ITEM, BrewAction.brew()
                        .item(Items.GLISTERING_MELON_SLICE)
                        .reward(expReward(3))
                        .reward(moneyReward(1))
                        .build())
                .addAction(BREW_ITEM, BrewAction.brew()
                        .item(Items.SPIDER_EYE, Items.MAGMA_CREAM, Items.BLAZE_POWDER)
                        .reward(expReward(2))
                        .reward(moneyReward(0.5))
                        .build())
                .addAction(BREW_ITEM, BrewAction.brew()
                        .item(Items.PUFFERFISH, Items.GOLDEN_CARROT, Items.GHAST_TEAR)
                        .reward(expReward(5))
                        .reward(moneyReward(2))
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
                .addAction(PLACE_BLOCK, PlaceBlockAction.place()
                        .block(Blocks.STONE, Blocks.ANDESITE, Blocks.GRANITE, Blocks.DIORITE, Blocks.STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS,
                                Blocks.CRACKED_STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS, Blocks.DEEPSLATE, Blocks.DEEPSLATE_BRICKS,
                                Blocks.DEEPSLATE_TILES, Blocks.CHISELED_DEEPSLATE, Blocks.COBBLED_DEEPSLATE, Blocks.POLISHED_DEEPSLATE, Blocks.COBBLESTONE,
                                Blocks.POLISHED_BASALT, Blocks.BLACKSTONE, Blocks.GILDED_BLACKSTONE, Blocks.CHISELED_POLISHED_BLACKSTONE)
                        .reward(expReward(1))
                        .reward(moneyReward(1))
                        .build())
                .addAction(PLACE_BLOCK, PlaceBlockAction.place()
                        .block(Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.ACACIA_PLANKS,
                                Blocks.DARK_OAK_PLANKS, Blocks.WARPED_PLANKS, Blocks.CRIMSON_PLANKS)
                        .reward(expReward(1.3))
                        .reward(moneyReward(1.3))
                        .build())
                .addAction(PLACE_BLOCK, PlaceBlockAction.place()
                        .block(Blocks.IRON_BLOCK, Blocks.GOLD_BLOCK, Blocks.DIAMOND_BLOCK, Blocks.EMERALD_BLOCK, Blocks.LAPIS_BLOCK, Blocks.NETHERITE_BLOCK,
                                Blocks.BRICKS, Blocks.BOOKSHELF, Blocks.MOSSY_COBBLESTONE, Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN)
                        .reward(expReward(1.5))
                        .reward(moneyReward(1.5))
                        .build())
                .addAction(PLACE_BLOCK, PlaceBlockAction.place()
                        .block(Blocks.PRISMARINE, Blocks.DARK_PRISMARINE, Blocks.SEA_LANTERN, Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR)
                        .reward(expReward(1.25))
                        .reward(moneyReward(1.25))
                        .build())
                .addAction(PLACE_BLOCK, PlaceBlockAction.place()
                        .block(Blocks.CHEST, Blocks.ENDER_CHEST, Blocks.ANVIL, Blocks.FURNACE, Blocks.BLAST_FURNACE, Blocks.SMOKER, Blocks.CAMPFIRE,
                                Blocks.SOUL_CAMPFIRE, Blocks.CRAFTING_TABLE, Blocks.ENCHANTING_TABLE, Blocks.JUKEBOX, Blocks.HOPPER)
                        .reward(expReward(1.5))
                        .reward(moneyReward(1.5))
                        .build())
                .addAction(PLACE_BLOCK, PlaceBlockAction.place()
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
                        .reward(moneyReward(1.3))
                        .build())
                .addAction(PLACE_BLOCK, PlaceBlockAction.place()
                        .block(BlockTags.WOOL)
                        .reward(expReward(1.3))
                        .reward(moneyReward(1.3))
                        .build())
                .addAction(PLACE_BLOCK, PlaceBlockAction.place()
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
                        .reward(moneyReward(1))
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
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .reward(expReward(2))
                        .reward(moneyReward(2))
                        .item(ItemTags.WOODEN_PRESSURE_PLATES)
                        .item(ItemTags.BANNERS)
                        .item(ItemTags.STAIRS)
                        .item(ItemTags.FENCES)
                        .item(ItemTags.WOODEN_DOORS)
                        .item(ItemTags.WOODEN_TRAPDOORS)
                        .item(ItemTags.WOODEN_BUTTONS)
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .reward(expReward(4))
                        .reward(moneyReward(4))
                        .item(Items.CHEST)
                        .item(Items.FURNACE)
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.ANVIL, Items.JUKEBOX, Items.TNT)
                        .reward(expReward(6))
                        .reward(moneyReward(5))
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.DISPENSER, Items.BLAST_FURNACE, Items.PISTON)
                        .reward(expReward(3))
                        .reward(moneyReward(3))
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.BEACON)
                        .reward(expReward(200))
                        .reward(moneyReward(200))
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.IRON_DOOR, Items.IRON_BARS, Items.HOPPER, Items.CAULDRON)
                        .reward(expReward(10))
                        .reward(moneyReward(10))
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.COMPARATOR, Items.COMPASS, Items.CLOCK, Items.BREWING_STAND)
                        .item(ItemTags.BEDS)
                        .reward(expReward(6))
                        .reward(moneyReward(6))
                        .build());
        ProfessionBuilder enchanting = ProfessionBuilder.profession(
                        TextColor.parseColor("#c9008d"),
                        TextColor.parseColor("#FFFFFF"),
                        new String[]{"Earn money and experience",
                                "by enchanting."},
                        "Enchanting", 100)
                .addExperienceScaling(defaultLevelParser())
                .incomeScaling(defaultIncomeParser())
                .addAction(ENCHANT_ITEM, EnchantAction.enchant()
                        .item(Items.DIAMOND_HELMET)
                        .reward(moneyReward(10))
                        .reward(expReward(10))
                        .build())
                .addAction(ENCHANT_ITEM, EnchantAction.enchant()
                        .item(Items.DIAMOND_CHESTPLATE)
                        .reward(moneyReward(16))
                        .reward(expReward(16))
                        .build())
                .addAction(ENCHANT_ITEM, EnchantAction.enchant()
                        .item(Items.DIAMOND_LEGGINGS)
                        .reward(moneyReward(14))
                        .reward(expReward(14))
                        .build())
                .addAction(ENCHANT_ITEM, EnchantAction.enchant()
                        .item(Items.DIAMOND_BOOTS)
                        .reward(moneyReward(8))
                        .reward(expReward(8))
                        .build())
                .addAction(ENCHANT_ITEM, EnchantAction.enchant()
                        .item(Items.DIAMOND_SWORD)
                        .reward(moneyReward(4))
                        .reward(expReward(4))
                        .build())
                .addAction(ENCHANT_ITEM, EnchantAction.enchant()
                        .item(Items.DIAMOND_SHOVEL)
                        .reward(moneyReward(2))
                        .reward(expReward(2))
                        .build())
                .addAction(ENCHANT_ITEM, EnchantAction.enchant()
                        .item(Items.DIAMOND_PICKAXE)
                        .reward(moneyReward(6))
                        .reward(expReward(6))
                        .build())
                .addAction(ENCHANT_ITEM, EnchantAction.enchant()
                        .item(Items.DIAMOND_AXE)
                        .reward(moneyReward(6))
                        .reward(expReward(6))
                        .build())
                .addAction(ENCHANT_ITEM, EnchantAction.enchant()
                        .item(Items.DIAMOND_CHESTPLATE)
                        .reward(moneyReward(16))
                        .reward(expReward(16))
                        .build());
        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            if (enchantment.isCurse() || enchantment.isTreasureOnly() || !enchantment.isDiscoverable()) {
                continue;
            }
            for (int i = 1; i <= enchantment.getMaxLevel(); i++) {
                enchanting.addAction(ENCHANT_ITEM, EnchantAction.enchant()
                        .enchant(enchantment, i)
                        .reward(moneyReward(2 * i))
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
                .addAction(BREAK_BLOCK, BreakBlockAction.breakBlock()
                        .block(BlockTags.CROPS)
                        .condition(FullyGrownCropCondition::new)
                        .reward(expReward(1))
                        .reward(moneyReward(1))
                        .build())
                .addAction(BREAK_BLOCK, BreakBlockAction.breakBlock()
                        .block(Blocks.COCOA)
                        .condition(BlockStatePropertyAnyCondition.checkProperties()
                                .properties(StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(BlockStateProperties.AGE_2, 2)))
                        .reward(expReward(1))
                        .reward(moneyReward(1))
                        .build())
                .addAction(BREAK_BLOCK, BreakBlockAction.breakBlock()
                        .block(Blocks.SUGAR_CANE)
                        .reward(expReward(0.75))
                        .reward(moneyReward(0.75))
                        .build())
                .addAction(BREAK_BLOCK, BreakBlockAction.breakBlock()
                        .block(Blocks.MELON, Blocks.PUMPKIN)
                        .reward(expReward(1.3))
                        .reward(moneyReward(1.3))
                        .build())
                .addAction(PLACE_BLOCK, PlaceBlockAction.place()
                        .block(BlockTags.CROPS)
                        .block(Blocks.COCOA)
                        .reward(moneyReward(0.85))
                        .reward(expReward(0.85))
                        .build())
                .addAction(BREED_ENTITY, BreedAction.breed()
                        .entity(EntityType.PIG, EntityType.COW, EntityType.MOOSHROOM,
                                EntityType.SHEEP, EntityType.RABBIT, EntityType.LLAMA,
                                EntityType.TURTLE, EntityType.OCELOT, EntityType.WOLF, EntityType.CHICKEN,
                                EntityType.HORSE)
                        .reward(expReward(5.0))
                        .reward(moneyReward(5.0))
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
                .addAction(FISH_ACTION, FishingAction.fish()
                        .item(Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH)
                        .reward(moneyReward(25))
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
                .addAction(KILL_ENTITY, KillAction.kill()
                        .entity(EntityType.PIG, EntityType.CHICKEN, EntityType.SHEEP, EntityType.COW, EntityType.MOOSHROOM, EntityType.RABBIT)
                        .reward(moneyReward(8))
                        .reward(expReward(8))
                        .build())
                .addAction(KILL_ENTITY, KillAction.kill()
                        .entity(EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER, EntityType.CAVE_SPIDER, EntityType.SHULKER)
                        .reward(moneyReward(12))
                        .reward(expReward(12))
                        .build())
                .addAction(KILL_ENTITY, KillAction.kill()
                        .entity(EntityType.BLAZE, EntityType.WITHER_SKELETON, EntityType.PIGLIN_BRUTE, EntityType.HOGLIN, EntityType.PHANTOM)
                        .reward(moneyReward(15))
                        .reward(expReward(15))
                        .build())
                .addAction(KILL_ENTITY, KillAction.kill()
                        .entity(EntityType.WITHER)
                        .reward(moneyReward(100))
                        .reward(expReward(100))
                        .build())
                .addAction(KILL_ENTITY, KillAction.kill()
                        .entity(EntityType.ENDER_DRAGON)
                        .reward(moneyReward(600))
                        .reward(expReward(600))
                        .build())
                .addAction(TAME_ENTITY, TameAction.tame()
                        .entity(EntityType.WOLF)
                        .reward(moneyReward(15))
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
                .addAction(BREAK_BLOCK, BreakBlockAction.breakBlock()
                        .block(BlockTags.IRON_ORES)
                        .condition(ToolMatcher.toolMatcher(ItemPredicate.Builder.item()
                                .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.ANY))).invert())
                        .reward(expReward(4))
                        .reward(moneyReward(4))
                        .build())
                .addAction(BREAK_BLOCK, BreakBlockAction.breakBlock()
                        .block(BlockTags.COPPER_ORES)
                        .condition(ToolMatcher.toolMatcher(ItemPredicate.Builder.item()
                                .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.ANY))).invert())
                        .reward(expReward(3.5))
                        .reward(moneyReward(3.5))
                        .build())
                .addAction(BREAK_BLOCK, BreakBlockAction.breakBlock()
                        .block(BlockTags.GOLD_ORES)
                        .condition(ToolMatcher.toolMatcher(ItemPredicate.Builder.item()
                                .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.ANY))).invert())
                        .reward(expReward(5.5))
                        .reward(moneyReward(5.5))
                        .build())
                .addAction(BREAK_BLOCK, BreakBlockAction.breakBlock()
                        .block(BlockTags.LAPIS_ORES)
                        .condition(ToolMatcher.toolMatcher(ItemPredicate.Builder.item()
                                .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.ANY))).invert())
                        .reward(expReward(5.5))
                        .reward(moneyReward(5.5))
                        .build())
                .addAction(BREAK_BLOCK, BreakBlockAction.breakBlock()
                        .block(BlockTags.COAL_ORES)
                        .condition(ToolMatcher.toolMatcher(ItemPredicate.Builder.item()
                                .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.ANY))).invert())
                        .reward(expReward(1.5))
                        .reward(moneyReward(1.5))
                        .build())
                .addAction(BREAK_BLOCK, BreakBlockAction.breakBlock()
                        .block(BlockTags.DIAMOND_ORES)
                        .condition(ToolMatcher.toolMatcher(ItemPredicate.Builder.item()
                                .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.ANY))).invert())
                        .reward(expReward(12.5))
                        .reward(moneyReward(12.5))
                        .build())
                .addAction(BREAK_BLOCK, BreakBlockAction.breakBlock()
                        .block(BlockTags.EMERALD_ORES)
                        .condition(ToolMatcher.toolMatcher(ItemPredicate.Builder.item()
                                .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.ANY))).invert())
                        .reward(expReward(14.5))
                        .reward(moneyReward(14.5))
                        .build())
                .addAction(BREAK_BLOCK, BreakBlockAction.breakBlock()
                        .block(BlockTags.REDSTONE_ORES)
                        .condition(ToolMatcher.toolMatcher(ItemPredicate.Builder.item()
                                .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.ANY))).invert())
                        .reward(expReward(4.5))
                        .reward(moneyReward(4.5))
                        .build())
                .addAction(BREAK_BLOCK, BreakBlockAction.breakBlock()
                        .block(Blocks.NETHER_QUARTZ_ORE)
                        .condition(ToolMatcher.toolMatcher(ItemPredicate.Builder.item()
                                .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.ANY))).invert())
                        .reward(moneyReward(3))
                        .reward(expReward(3))
                        .build())
                .addAction(BREAK_BLOCK, BreakBlockAction.breakBlock()
                        .block(Blocks.OBSIDIAN)
                        .reward(moneyReward(7))
                        .reward(expReward(7))
                        .build())
                .addAction(BREAK_BLOCK, BreakBlockAction.breakBlock()
                        .block(Blocks.STONE, Blocks.ANDESITE, Blocks.GRANITE, Blocks.DIORITE, Blocks.DEEPSLATE, Blocks.CALCITE,
                                Blocks.DRIPSTONE_BLOCK, Blocks.TUFF)
                        .reward(expReward(1))
                        .reward(moneyReward(1))
                        .build())
                .addAction(BREAK_BLOCK, BreakBlockAction.breakBlock()
                        .block(BlockTags.TERRACOTTA)
                        .reward(expReward(1))
                        .reward(moneyReward(1)))
                .addAction(BREAK_BLOCK, BreakBlockAction.breakBlock()
                        .block(Blocks.END_STONE)
                        .reward(expReward(1))
                        .reward(moneyReward(1)))
                .addAction(BREAK_BLOCK, BreakBlockAction.breakBlock()
                        .block(Blocks.CLAY, Blocks.GRAVEL, Blocks.SAND, Blocks.RED_SAND, Blocks.SANDSTONE, Blocks.RED_SANDSTONE,
                                Blocks.BLACKSTONE)
                        .reward(expReward(1))
                        .reward(moneyReward(1)))
                .addAction(BREAK_BLOCK, BreakBlockAction.breakBlock()
                        .block(Blocks.AMETHYST_BLOCK)
                        .reward(expReward(2))
                        .reward(moneyReward(1.5)))
                .addAction(TNT_DESTROY, TntDestroyAction.tntDestroy()
                        .block(Blocks.STONE, Blocks.ANDESITE, Blocks.GRANITE, Blocks.DIORITE, Blocks.DEEPSLATE, Blocks.TUFF, Blocks.CALCITE, Blocks.DRIPSTONE_BLOCK)
                        .reward(expReward(0.5))
                        .reward(moneyReward(0.5))
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
                .addAction(VILLAGER_TRADE, TradeAction.trade()
                        .item(Items.EMERALD, Items.DIAMOND)
                        .reward(moneyReward(7))
                        .reward(expReward(7))
                        .build())
                .addAction(VILLAGER_TRADE, TradeAction.trade()
                        .item(Items.PAPER, Items.ROTTEN_FLESH)
                        .reward(moneyReward(0.25))
                        .reward(expReward(0.25))
                        .build())
                .addAction(VILLAGER_TRADE, TradeAction.trade()
                        .item(Items.GOLD_INGOT, Items.IRON_INGOT)
                        .reward(moneyReward(3))
                        .reward(expReward(3))
                        .build())
                .addAction(VILLAGER_TRADE, TradeAction.trade()
                        .item(Items.WHEAT, Items.CARROT, Items.POTATO, Items.BEETROOT)
                        .reward(moneyReward(2))
                        .reward(expReward(2))
                        .build())
                .addAction(VILLAGER_TRADE, TradeAction.trade()
                        .item(Items.TROPICAL_FISH, Items.SALMON, Items.COD, Items.PUFFERFISH)
                        .reward(moneyReward(3))
                        .reward(expReward(3))
                        .build());
        ProfessionBuilder smithing = ProfessionBuilder.profession(
                        TextColor.parseColor("#84abad"),
                        TextColor.parseColor("#FFFFFF"),
                        new String[]{
                                "Earn money and experience",
                                "by smithing items."},
                        "Smithing", 100)
                .addExperienceScaling(defaultLevelParser())
                .incomeScaling(defaultIncomeParser())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.WOODEN_SWORD, Items.WOODEN_AXE, Items.WOODEN_HOE, Items.WOODEN_SHOVEL, Items.WOODEN_PICKAXE)
                        .reward(moneyReward(1.0))
                        .reward(expReward(1.0))
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS)
                        .reward(moneyReward(6.0))
                        .reward(expReward(6.0))
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.STONE_SWORD, Items.STONE_AXE, Items.STONE_HOE, Items.STONE_SHOVEL, Items.STONE_PICKAXE)
                        .reward(moneyReward(3))
                        .reward(expReward(3))
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS)
                        .reward(moneyReward(12.0))
                        .reward(expReward(12.0))
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.IRON_SWORD, Items.IRON_AXE, Items.IRON_HOE, Items.IRON_SHOVEL, Items.IRON_PICKAXE)
                        .reward(moneyReward(5))
                        .reward(expReward(5))
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS)
                        .reward(moneyReward(18.0))
                        .reward(expReward(18.0))
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.GOLDEN_SWORD, Items.GOLDEN_AXE, Items.GOLDEN_HOE, Items.GOLDEN_SHOVEL, Items.GOLDEN_PICKAXE)
                        .reward(moneyReward(7))
                        .reward(expReward(7))
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS)
                        .reward(moneyReward(24.0))
                        .reward(expReward(24.0))
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.DIAMOND_SWORD, Items.DIAMOND_AXE, Items.DIAMOND_HOE, Items.DIAMOND_SHOVEL, Items.DIAMOND_PICKAXE)
                        .reward(moneyReward(12))
                        .reward(expReward(12))
                        .build())
                .addAction(ON_ITEM_COOK, SmeltItemAction.smelt()
                        .item(Items.GOLD_INGOT)
                        .reward(moneyReward(2))
                        .reward(expReward(2))
                        .build())
                .addAction(TAKE_COOKED_ITEM, TakeSmeltAction.takeSmelt()
                        .item(Items.IRON_INGOT)
                        .reward(moneyReward(1.3))
                        .reward(expReward(1.3))
                        .build());
        ProfessionBuilder logging = ProfessionBuilder.profession(
                        TextColor.parseColor("#9e3011"),
                        TextColor.parseColor("#FFFFFF"),
                        new String[]{
                                "Earn money and experience",
                                "by farming trees."},
                        "Logging", 100)
                .addExperienceScaling(defaultLevelParser())
                .incomeScaling(defaultIncomeParser())
                .addAction(BREAK_BLOCK, BreakBlockAction.breakBlock()
                        .block(BlockTags.LOGS)
                        .reward(expReward(1))
                        .reward(moneyReward(1))
                        .build());

        generate(GSON, cache, alchemy.build(), createNormalPath(path, new ResourceLocation("professions:alchemy"), false));
        generate(GSON, cache, builder.build(), createNormalPath(path, new ResourceLocation("professions:building"), false));
        generate(GSON, cache, crafting.build(), createNormalPath(path, new ResourceLocation("professions:crafting"), false));
        generate(GSON, cache, enchanting.build(), createNormalPath(path, new ResourceLocation("professions:enchanting"), false));
        generate(GSON, cache, farming.build(), createNormalPath(path, new ResourceLocation("professions:farming"), false));
        generate(GSON, cache, fishing.build(), createNormalPath(path, new ResourceLocation("professions:fishing"), false));
        generate(GSON, cache, hunting.build(), createNormalPath(path, new ResourceLocation("professions:hunting"), false));
        generate(GSON, cache, mining.build(), createNormalPath(path, new ResourceLocation("professions:mining"), false));
        generate(GSON, cache, createMiningAppender().build(), createHardcoreAppenders(path, Constants.modID("appenders/mining"), false));

        generate(GSON, cache, trading.build(), createNormalPath(path, new ResourceLocation("professions:trading"), false));
        generate(GSON, cache, smithing.build(), createNormalPath(path, new ResourceLocation("professions:smithing"), false));
        generate(GSON, cache, logging.build(), createNormalPath(path, new ResourceLocation("professions:logging"), false));
    }

    @Override
    public String getName() {
        return "Professions";
    }

}
