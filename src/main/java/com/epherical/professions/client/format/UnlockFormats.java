package com.epherical.professions.client.format;

import com.epherical.professions.client.entry.NumberEntry;
import com.epherical.professions.client.entry.StringEntry;
import com.epherical.professions.profession.unlock.builtin.BlockBreakUnlock;
import com.epherical.professions.profession.unlock.builtin.BlockDropUnlock;
import com.epherical.professions.profession.unlock.builtin.ToolUnlock;
import com.epherical.professions.util.ActionEntry;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static com.epherical.professions.client.format.FormatRegistry.arrayBlockString;
import static com.epherical.professions.client.format.FormatRegistry.arrayItemString;

public class UnlockFormats {


    public static class BlockDrop implements FormatBuilder<BlockDropUnlock> {

        @Override
        public Format<BlockDropUnlock> deserializeToFormat(BlockDropUnlock blockDropUnlock) {
            return new RegularFormat<>((embed, y, width) -> new FormatEntryBuilder<BlockDropUnlock>()
                    .addEntry(arrayBlockString(embed, y, width, "blocks",
                            (o, entry) -> {
                                for (ActionEntry<Block> block : o.getBlocks()) {
                                    for (String s : block.serializeString(Registry.BLOCK)) {
                                        StringEntry<String> entry1 = entry.createEntry();
                                        entry1.deserialize(s);
                                        entry.addEntry(entry1);
                                    }
                                }
                            }, BlockDropUnlock.class))
                    .addEntry(new NumberEntry<>(embed, y, width, "level", 1, (o, entry) -> {
                        entry.setValue(String.valueOf(o.getLevel()));
                    })));
        }
    }

    public static class BlockBreak implements FormatBuilder<BlockBreakUnlock> {

        @Override
        public Format<BlockBreakUnlock> deserializeToFormat(BlockBreakUnlock blockBreakUnlock) {
            return new RegularFormat<>((embed, y, width) -> new FormatEntryBuilder<BlockBreakUnlock>()
                    .addEntry(arrayBlockString(embed, y, width, "blocks",
                            (o, entry) -> {
                                for (ActionEntry<Block> block : o.getBlocks()) {
                                    for (String s : block.serializeString(Registry.BLOCK)) {
                                        StringEntry<String> entry1 = entry.createEntry();
                                        entry1.deserialize(s);
                                        entry.addEntry(entry1);
                                    }
                                }
                            }, BlockBreakUnlock.class))
                    .addEntry(new NumberEntry<>(embed, y, width, "level", 1, (o, entry) -> {
                        entry.setValue(String.valueOf(o.getLevel()));
                    })));
        }
    }

    public static class Tool implements FormatBuilder<ToolUnlock> {

        @Override
        public Format<ToolUnlock> deserializeToFormat(ToolUnlock toolUnlock) {
            return new RegularFormat<>((embed, y, width) -> new FormatEntryBuilder<ToolUnlock>()
                    .addEntry(arrayItemString(embed, y, width, "items",
                            (o, entry) -> {
                                for (ActionEntry<Item> block : o.getItems()) {
                                    for (String s : block.serializeString(Registry.ITEM)) {
                                        StringEntry<String> entry1 = entry.createEntry();
                                        entry1.deserialize(s);
                                        entry.addEntry(entry1);
                                    }
                                }
                            }, ToolUnlock.class))
                    .addEntry(new NumberEntry<>(embed, y, width, "level", 1, (o, entry) -> {
                        entry.setValue(String.valueOf(o.getLevel()));
                    })));
        }
    }
}
