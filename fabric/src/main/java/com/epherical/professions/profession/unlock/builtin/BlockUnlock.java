package com.epherical.professions.profession.unlock.builtin;

import com.epherical.professions.profession.action.builtin.blocks.BlockAbstractAction;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.profession.unlock.UnlockType;
import com.epherical.professions.profession.unlock.Unlocks;
import com.epherical.professions.util.ActionEntry;
import com.epherical.professions.util.Tristate;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.Serializer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockUnlock implements Unlock<Block> {
    protected final List<ActionEntry<Block>> blocks;
    protected final int level;
    @Nullable
    protected Set<Block> realBlocks;

    public BlockUnlock(List<ActionEntry<Block>> blocks, int level) {
        this.blocks = blocks;
        this.level = level;
    }

    @Override
    public boolean isLocked(Block object, int level) {
        return level >= this.level && getRealBlocks().contains(object);
    }

    @Override
    public int getUnlockLevel() {
        return level;
    }

    @Override
    public UnlockType getType() {
        return Unlocks.BLOCK_UNLOCK;
    }

    @Override
    public List<Singular<Block>> convertToSingle() {
        List<Singular<Block>> list = new ArrayList<>();
        for (Block realBlock : getRealBlocks()) {
            list.add(new Single(realBlock, level));
        }
        return list;
    }

    public static class Single implements Singular<Block> {
        protected final Block block;
        protected final int level;

        public Single(Block block, int level) {
            this.block = block;
            this.level = level;
        }

        @Override
        public Tristate isLocked(Block object, int level) {
            return (level >= this.level && block.equals(object)) ? Tristate.TRUE : Tristate.FALSE;
        }

        @Override
        public UnlockType getType() {
            return Unlocks.BLOCK_UNLOCK;
        }

        @Override
        public Block getObject() {
            return block;
        }

        @Override
        public int getUnlockLevel() {
            return level;
        }
    }

    protected Set<Block> getRealBlocks() {
        if (realBlocks == null) {
            realBlocks = new HashSet<>();
            for (ActionEntry<Block> block : blocks) {
                realBlocks.addAll(block.getActionValues(Registry.BLOCK));
            }
        }
        return realBlocks;
    }

    public static class Builder implements Unlock.Builder<Block> {
        protected final List<ActionEntry<Block>> blocks = new ArrayList<>();
        protected int level = 2;

        public Unlock.Builder<Block> block(Block... item) {
            this.blocks.add(ActionEntry.of(item));
            return this;
        }

        public Unlock.Builder<Block> item(TagKey<Block> item) {
            this.blocks.add(ActionEntry.of(item));
            return this;
        }

        public Unlock.Builder<Block> level(int level) {
            this.level = level;
            return this;
        }

        @Override
        public Unlock<Block> build() {
            return new BlockUnlock(blocks, level);
        }
    }

    public static class UnlockSerializer implements Serializer<BlockUnlock> {

        @Override
        public void serialize(JsonObject json, BlockUnlock value, JsonSerializationContext serializationContext) {
            JsonArray array = new JsonArray();
            for (ActionEntry<Block> block : value.blocks) {
                array.addAll(block.serialize(Registry.BLOCK));
            }
            json.add("blocks", array);
            json.addProperty("level", value.level);
        }

        @Override
        public BlockUnlock deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
            List<ActionEntry<Block>> blocks = BlockAbstractAction.Serializer.deserializeBlocks(json);
            int level = GsonHelper.getAsInt(json, "level");
            return new BlockUnlock(blocks, level);
        }
    }

}
