package com.epherical.professions.profession.unlock.builtin;

import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.action.builtin.blocks.BlockAbstractAction;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.profession.unlock.UnlockSerializer;
import com.epherical.professions.profession.unlock.UnlockType;
import com.epherical.professions.profession.unlock.Unlocks;
import com.epherical.professions.util.ActionEntry;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.Serializer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockBreakUnlock implements Unlock<Block> {
    protected final List<ActionEntry<Block>> blocks;
    protected final int level;
    @Nullable
    protected Set<Block> realBlocks;

    public BlockBreakUnlock(List<ActionEntry<Block>> blocks, int level) {
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
    public UnlockType<Block> getType() {
        return Unlocks.BLOCK_BREAK_UNLOCK;
    }

    @Override
    public List<Singular<Block>> convertToSingle(Profession profession) {
        List<Singular<Block>> list = new ArrayList<>();
        for (Block realBlock : getRealBlocks()) {
            list.add(new Single(realBlock, level, profession));
        }
        return list;
    }

    @Override
    public UnlockSerializer<?> getSerializer() {
        return UnlockSerializer.BLOCK_BREAK_UNLOCK;
    }

    public static class Single extends AbstractSingle<Block> {

        public Single(Block block, int level, Profession professionDisplay) {
            super(block, level, professionDisplay);
        }

        @Override
        public UnlockType<Block> getType() {
            return Unlocks.BLOCK_BREAK_UNLOCK;
        }

        @Override
        public Component createUnlockComponent() {
            return new TranslatableComponent("Unlock drops for %s at level %s",
                            getObject().getName().setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)),
                            new TextComponent(String.valueOf(getUnlockLevel())).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors));
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements Unlock.Builder<Block> {
        protected final List<ActionEntry<Block>> blocks = new ArrayList<>();
        protected int level = 2;

        public Builder block(Block... item) {
            this.blocks.add(ActionEntry.of(item));
            return this;
        }

        public Builder tag(TagKey<Block> item) {
            this.blocks.add(ActionEntry.of(item));
            return this;
        }

        public Builder level(int level) {
            this.level = level;
            return this;
        }

        @Override
        public Unlock<Block> build() {
            return new BlockBreakUnlock(blocks, level);
        }
    }

    public static class JsonSerializer implements Serializer<BlockBreakUnlock> {

        @Override
        public void serialize(JsonObject json, BlockBreakUnlock value, JsonSerializationContext serializationContext) {
            JsonArray array = new JsonArray();
            for (ActionEntry<Block> block : value.blocks) {
                array.addAll(block.serialize(Registry.BLOCK));
            }
            json.add("blocks", array);
            json.addProperty("level", value.level);
        }

        @Override
        public BlockBreakUnlock deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
            List<ActionEntry<Block>> blocks = BlockAbstractAction.Serializer.deserializeBlocks(json);
            int level = GsonHelper.getAsInt(json, "level");
            return new BlockBreakUnlock(blocks, level);
        }
    }

    public static class NetworkSerializer implements UnlockSerializer<BlockBreakUnlock> {

        @Override
        public BlockBreakUnlock fromNetwork(FriendlyByteBuf buf) {
            int unlockLevel = buf.readVarInt();
            int arraySize = buf.readVarInt();
            List<ActionEntry<Block>> entries = new ArrayList<>();
            for (int i = 0; i < arraySize; i++) {
                entries.addAll(ActionEntry.fromNetwork(buf, Registry.BLOCK));
            }
            return new BlockBreakUnlock(entries, unlockLevel);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, BlockBreakUnlock unlock) {
            buf.writeVarInt(unlock.level);
            buf.writeVarInt(unlock.blocks.size());
            for (ActionEntry<Block> block : unlock.blocks) {
                block.toNetwork(buf, Registry.BLOCK);
            }
        }

    }

}
