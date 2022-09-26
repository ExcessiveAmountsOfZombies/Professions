package com.epherical.professions.datagen.defaults;

import com.epherical.professions.Constants;
import com.epherical.professions.datagen.NamedProfessionBuilder;
import com.epherical.professions.profession.ProfessionBuilder;
import com.epherical.professions.profession.action.builtin.ExploreStructureAction;
import com.epherical.professions.profession.action.builtin.blocks.BreakBlockAction;
import com.epherical.professions.profession.action.builtin.blocks.TntDestroyAction;
import com.epherical.professions.profession.conditions.builtin.ToolMatcher;
import com.epherical.professions.profession.editor.Append;
import com.epherical.professions.profession.modifiers.perks.Perks;
import com.epherical.professions.profession.modifiers.perks.builtin.ScalingAttributePerk;
import com.epherical.professions.profession.unlock.Unlocks;
import com.epherical.professions.profession.unlock.builtin.BlockBreakUnlock;
import com.epherical.professions.profession.unlock.builtin.BlockDropUnlock;
import com.epherical.professions.profession.unlock.builtin.ToolUnlock;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.network.chat.TextColor;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;

import static com.epherical.professions.profession.action.Actions.*;
import static com.epherical.professions.profession.unlock.Unlocks.BLOCK_BREAK_UNLOCK;
import static com.epherical.professions.profession.unlock.Unlocks.BLOCK_DROP_UNLOCK;

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
                        .reward(expReward(3))
                        .reward(moneyReward(4)))
                .addAction(EXPLORE_STRUCT, ExploreStructureAction.explore()
                        .feature(BuiltinStructures.MINESHAFT_MESA)
                        .reward(expReward(3))
                        .reward(moneyReward(4)));
        builder.addPerk(Perks.SCALING_ATTRIBUTE_PERK, ScalingAttributePerk.scaling()
                .level(1).attribute(Attributes.MAX_HEALTH).increaseBy(0.20));
        builder.addPerk(Perks.SCALING_ATTRIBUTE_PERK, ScalingAttributePerk.scaling()
                .level(10).attribute(Attributes.KNOCKBACK_RESISTANCE).increaseBy(0.002));
    }

    public Append.Builder createMiningAppender() {
        return Append.Builder.appender(Constants.modID("mining"))
                .addUnlock(BLOCK_DROP_UNLOCK, BlockDropUnlock.builder()
                        .level(2)
                        .tag(BlockTags.TERRACOTTA))
                .addUnlock(BLOCK_BREAK_UNLOCK, BlockBreakUnlock.builder()
                        .level(2)
                        .tag(BlockTags.TERRACOTTA))
                .addUnlock(BLOCK_DROP_UNLOCK, BlockDropUnlock.builder()
                        .level(3)
                        .block(Blocks.CLAY, Blocks.GRAVEL, Blocks.SAND, Blocks.RED_SAND, Blocks.SANDSTONE, Blocks.RED_SANDSTONE))
                .addUnlock(BLOCK_DROP_UNLOCK, BlockDropUnlock.builder()
                        .level(10)
                        .block(Blocks.BLACKSTONE))
                .addUnlock(BLOCK_DROP_UNLOCK, BlockDropUnlock.builder()
                        .level(7)
                        .block(Blocks.AMETHYST_BLOCK))
                .addUnlock(BLOCK_DROP_UNLOCK, BlockDropUnlock.builder()
                        .level(5)
                        .tag(BlockTags.IRON_ORES).build())
                .addUnlock(BLOCK_DROP_UNLOCK, BlockDropUnlock.builder()
                        .level(5)
                        .tag(BlockTags.REDSTONE_ORES).build())
                .addUnlock(BLOCK_DROP_UNLOCK, BlockDropUnlock.builder()
                        .level(3)
                        .tag(BlockTags.COAL_ORES).build())
                .addUnlock(BLOCK_DROP_UNLOCK, BlockDropUnlock.builder()
                        .level(3)
                        .tag(BlockTags.COPPER_ORES).build())
                .addUnlock(BLOCK_DROP_UNLOCK, BlockDropUnlock.builder()
                        .level(10)
                        .tag(BlockTags.GOLD_ORES).build())
                .addUnlock(BLOCK_DROP_UNLOCK, BlockDropUnlock.builder()
                        .level(10)
                        .block(Blocks.NETHER_QUARTZ_ORE).build())
                .addUnlock(BLOCK_DROP_UNLOCK, BlockDropUnlock.builder()
                        .level(10)
                        .tag(BlockTags.LAPIS_ORES).build())
                .addUnlock(BLOCK_DROP_UNLOCK, BlockDropUnlock.builder()
                        .level(20)
                        .tag(BlockTags.DIAMOND_ORES).build())
                .addUnlock(BLOCK_DROP_UNLOCK, BlockDropUnlock.builder()
                        .level(20)
                        .tag(BlockTags.EMERALD_ORES).build())
                .addUnlock(BLOCK_DROP_UNLOCK, BlockDropUnlock.builder()
                        .level(30)
                        .block(Blocks.ANCIENT_DEBRIS).build())
                .addUnlock(Unlocks.TOOL_UNLOCK, ToolUnlock.builder()
                        .level(5)
                        .item(Items.IRON_PICKAXE).build())
                .addUnlock(Unlocks.TOOL_UNLOCK, ToolUnlock.builder()
                        .level(12)
                        .item(Items.GOLDEN_PICKAXE).build())
                .addUnlock(Unlocks.TOOL_UNLOCK, ToolUnlock.builder()
                        .level(22)
                        .item(Items.DIAMOND_PICKAXE).build())
                .addUnlock(Unlocks.TOOL_UNLOCK, ToolUnlock.builder()
                        .level(34)
                        .item(Items.NETHERITE_PICKAXE).build());
    }
}
