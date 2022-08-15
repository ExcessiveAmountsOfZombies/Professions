package com.epherical.professions.datagen;

import com.epherical.professions.Constants;
import com.epherical.professions.profession.editor.Append;
import com.epherical.professions.profession.unlock.Unlocks;
import com.epherical.professions.profession.unlock.builtin.AdvancementUnlock;
import com.epherical.professions.profession.unlock.builtin.BlockBreakUnlock;
import com.epherical.professions.profession.unlock.builtin.BlockDropUnlock;
import com.epherical.professions.profession.unlock.builtin.ToolUnlock;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import static com.epherical.professions.profession.unlock.Unlocks.*;

public abstract class CommonProvider {

    protected static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public Append.Builder createMiningAppender() {
        return Append.Builder.appender(Constants.modID("mining"))
                .addUnlock(ADVANCEMENT_UNLOCK, AdvancementUnlock.builder()
                        .id(new ResourceLocation("minecraft:adventure/adventuring_time"))
                        .tag(ItemTags.EMERALD_ORES))
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
