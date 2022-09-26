package com.epherical.professions.datagen.defaults;

import com.epherical.professions.Constants;
import com.epherical.professions.datagen.NamedProfessionBuilder;
import com.epherical.professions.profession.ProfessionBuilder;
import com.epherical.professions.profession.action.builtin.blocks.PlaceBlockAction;
import com.epherical.professions.profession.editor.Append;
import com.epherical.professions.profession.modifiers.perks.Perks;
import com.epherical.professions.profession.modifiers.perks.builtin.ScalingAttributePerk;
import net.minecraft.network.chat.TextColor;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.Blocks;

import static com.epherical.professions.profession.action.Actions.PLACE_BLOCK;

public class BuilderProvider extends NamedProfessionBuilder {

    public BuilderProvider() {
        super(ProfessionBuilder.profession(
                TextColor.parseColor("#f2de00"),
                TextColor.parseColor("#FFFFFF"),
                new String[]{
                        "Earn money and experience",
                        "by placing blocks."},
                "Building", 100));
    }


    @Override
    public void addData(ProfessionBuilder builder) {
        builder.addExperienceScaling(defaultLevelParser())
                .incomeScaling(defaultIncomeParser())
                .addAction(PLACE_BLOCK, PlaceBlockAction.place()
                        .block(Blocks.STONE, Blocks.ANDESITE, Blocks.GRANITE, Blocks.DIORITE, Blocks.STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS,
                                Blocks.CRACKED_STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS, Blocks.DEEPSLATE, Blocks.DEEPSLATE_BRICKS,
                                Blocks.DEEPSLATE_TILES, Blocks.CHISELED_DEEPSLATE, Blocks.COBBLED_DEEPSLATE, Blocks.POLISHED_DEEPSLATE, Blocks.COBBLESTONE,
                                Blocks.POLISHED_BASALT, Blocks.BLACKSTONE, Blocks.GILDED_BLACKSTONE, Blocks.CHISELED_POLISHED_BLACKSTONE, Blocks.POLISHED_GRANITE,
                                Blocks.POLISHED_DIORITE, Blocks.POLISHED_ANDESITE, Blocks.CALCITE, Blocks.END_STONE_BRICKS, Blocks.CRACKED_DEEPSLATE_TILES,
                                Blocks.CRACKED_DEEPSLATE_BRICKS, Blocks.PACKED_ICE, Blocks.BLUE_ICE)
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
                        .block(Blocks.PRISMARINE, Blocks.DARK_PRISMARINE, Blocks.PRISMARINE_BRICKS, Blocks.SEA_LANTERN, Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR,
                                Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_BRICKS, Blocks.QUARTZ_PILLAR)
                        .reward(expReward(1.25))
                        .reward(moneyReward(1.25))
                        .build())
                .addAction(PLACE_BLOCK, PlaceBlockAction.place()
                        .block(Blocks.CHEST, Blocks.ENDER_CHEST, Blocks.ANVIL, Blocks.FURNACE, Blocks.BLAST_FURNACE, Blocks.SMOKER, Blocks.CAMPFIRE,
                                Blocks.SOUL_CAMPFIRE, Blocks.CRAFTING_TABLE, Blocks.ENCHANTING_TABLE, Blocks.JUKEBOX, Blocks.HOPPER, Blocks.BEACON)
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
                                Blocks.BLACK_STAINED_GLASS_PANE, Blocks.CHAIN, Blocks.EXPOSED_COPPER, Blocks.WEATHERED_COPPER, Blocks.OXIDIZED_COPPER,
                                Blocks.CUT_COPPER, Blocks.EXPOSED_CUT_COPPER, Blocks.WEATHERED_CUT_COPPER, Blocks.OXIDIZED_CUT_COPPER,
                                Blocks.WAXED_COPPER_BLOCK, Blocks.WAXED_EXPOSED_COPPER, Blocks.WAXED_WEATHERED_COPPER, Blocks.WAXED_OXIDIZED_COPPER,
                                Blocks.WAXED_CUT_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER,
                                Blocks.WAXED_OXIDIZED_CUT_COPPER)
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
                        .block(BlockTags.CARPETS)
                        .block(BlockTags.CANDLES)
                        .reward(expReward(1))
                        .reward(moneyReward(1))
                        .build());
        builder.addPerk(Perks.SCALING_ATTRIBUTE_PERK, ScalingAttributePerk.scaling()
                .level(1).attribute(Attributes.MAX_HEALTH).increaseBy(0.40));
        builder.addPerk(Perks.SCALING_ATTRIBUTE_PERK, ScalingAttributePerk.scaling()
                .level(10).attribute(Attributes.MOVEMENT_SPEED).increaseBy(0.001));
    }

    public Append.Builder createBuildingAppender() {
        Append.Builder builder = Append.Builder.appender(Constants.modID("building"));

        return builder;
    }
}
