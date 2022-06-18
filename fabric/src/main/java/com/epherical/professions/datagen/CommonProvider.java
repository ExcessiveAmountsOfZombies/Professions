package com.epherical.professions.datagen;

import com.epherical.professions.Constants;
import com.epherical.professions.profession.editor.Append;
import com.epherical.professions.profession.unlock.Unlocks;
import com.epherical.professions.profession.unlock.builtin.BlockUnlock;
import com.epherical.professions.profession.unlock.builtin.ToolUnlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import static com.epherical.professions.profession.unlock.Unlocks.BLOCK_UNLOCK;

public abstract class CommonProvider {


    public Append.Builder createMiningAppender() {
        return Append.Builder.appender(Constants.modID("mining"))
                .addUnlock(BLOCK_UNLOCK, BlockUnlock.builder()
                        .level(2)
                        .tag(BlockTags.TERRACOTTA))
                .addUnlock(BLOCK_UNLOCK, BlockUnlock.builder()
                        .level(3)
                        .block(Blocks.CLAY, Blocks.GRAVEL, Blocks.SAND, Blocks.RED_SAND, Blocks.SANDSTONE, Blocks.RED_SANDSTONE))
                .addUnlock(BLOCK_UNLOCK, BlockUnlock.builder()
                        .level(10)
                        .block(Blocks.BLACKSTONE))
                .addUnlock(BLOCK_UNLOCK, BlockUnlock.builder()
                        .level(7)
                        .block(Blocks.AMETHYST_BLOCK))
                .addUnlock(BLOCK_UNLOCK, BlockUnlock.builder()
                        .level(5)
                        .tag(BlockTags.IRON_ORES).build())
                .addUnlock(BLOCK_UNLOCK, BlockUnlock.builder()
                        .level(5)
                        .tag(BlockTags.REDSTONE_ORES).build())
                .addUnlock(BLOCK_UNLOCK, BlockUnlock.builder()
                        .level(3)
                        .tag(BlockTags.COAL_ORES).build())
                .addUnlock(BLOCK_UNLOCK, BlockUnlock.builder()
                        .level(3)
                        .tag(BlockTags.COPPER_ORES).build())
                .addUnlock(BLOCK_UNLOCK, BlockUnlock.builder()
                        .level(10)
                        .tag(BlockTags.GOLD_ORES).build())
                .addUnlock(BLOCK_UNLOCK, BlockUnlock.builder()
                        .level(10)
                        .block(Blocks.NETHER_QUARTZ_ORE).build())
                .addUnlock(BLOCK_UNLOCK, BlockUnlock.builder()
                        .level(10)
                        .tag(BlockTags.LAPIS_ORES).build())
                .addUnlock(BLOCK_UNLOCK, BlockUnlock.builder()
                        .level(20)
                        .tag(BlockTags.DIAMOND_ORES).build())
                .addUnlock(BLOCK_UNLOCK, BlockUnlock.builder()
                        .level(20)
                        .tag(BlockTags.EMERALD_ORES).build())
                .addUnlock(BLOCK_UNLOCK, BlockUnlock.builder()
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
