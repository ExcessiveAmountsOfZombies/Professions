package com.epherical.professions.datagen.defaults;

import com.epherical.professions.datagen.NamedProfessionBuilder;
import com.epherical.professions.profession.ProfessionBuilder;
import com.epherical.professions.profession.action.builtin.ExploreStructureAction;
import com.epherical.professions.profession.action.builtin.blocks.BreakBlockAction;
import com.epherical.professions.profession.action.builtin.blocks.TntDestroyAction;
import com.epherical.professions.profession.conditions.builtin.ToolMatcher;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.network.chat.TextColor;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;

import static com.epherical.professions.profession.action.Actions.*;

public class MiningProvider extends NamedProfessionBuilder {

    public MiningProvider() {
        super(ProfessionBuilder.profession(
                TextColor.parseColor("#666E63"),
                TextColor.parseColor("#FFFFFF"),
                new String[]{
                        "Earn money and experience",
                        "by mining ores and minerals."},
                "Mining", 100));
    }


    @Override
    public void addData(ProfessionBuilder builder) {
        builder.addExperienceScaling(defaultLevelParser())
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
                        .build())
                .addAction(EXPLORE_STRUCT, ExploreStructureAction.explore()
                        .feature(BuiltinStructures.MINESHAFT)
                        .reward(expReward(20))
                        .reward(moneyReward(8)))
                .addAction(EXPLORE_STRUCT, ExploreStructureAction.explore()
                        .feature(BuiltinStructures.MINESHAFT_MESA)
                        .reward(expReward(30))
                        .reward(moneyReward(10)));
    }
}
