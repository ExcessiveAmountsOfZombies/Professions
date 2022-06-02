package com.epherical.professions.profession.unlock.builtin;

import com.epherical.professions.profession.action.builtin.items.AbstractItemAction;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.Serializer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ToolUnlock implements Unlock<Item> {
    protected final List<ActionEntry<Item>> items;
    protected final int level;
    @Nullable
    protected Set<Item> real;

    public ToolUnlock(List<ActionEntry<Item>> items, int level) {
        this.items = items;
        this.level = level;
    }

    @Override
    public boolean isLocked(Item object, int level) {
        return level >= this.level && convertToReal().contains(object);
    }

    @Override
    public int getUnlockLevel() {
        return level;
    }

    @Override
    public UnlockType<Item> getType() {
        return Unlocks.TOOL_UNLOCK;
    }

    @Override
    public List<Singular<Item>> convertToSingle() {
        List<Singular<Item>> list = new ArrayList<>();
        for (Item realBlock : convertToReal()) {
            list.add(new ToolUnlock.Single(realBlock, level));
        }
        return list;
    }

    protected Set<Item> convertToReal() {
        if (real == null) {
            real = new HashSet<>();
            for (ActionEntry<Item> block : items) {
                real.addAll(block.getActionValues(Registry.ITEM));
            }
        }
        return real;
    }

    public static class Single implements Singular<Item> {
        protected final Item item;
        protected final int level;

        public Single(Item item, int level) {
            this.item = item;
            this.level = level;
        }

        @Override
        public Tristate isLocked(Item object, int level) {
            return (level >= this.level && item.equals(object)) ? Tristate.TRUE : Tristate.FALSE;
        }

        @Override
        public UnlockType<Item> getType() {
            return Unlocks.TOOL_UNLOCK;
        }

        @Override
        public Item getObject() {
            return item;
        }

        @Override
        public int getUnlockLevel() {
            return level;
        }
    }

    public static class Builder implements Unlock.Builder<Item> {
        protected final List<ActionEntry<Item>> blocks = new ArrayList<>();
        protected int level = 2;

        public Unlock.Builder<Item> block(Item... item) {
            this.blocks.add(ActionEntry.of(item));
            return this;
        }

        public Unlock.Builder<Item> item(TagKey<Item> item) {
            this.blocks.add(ActionEntry.of(item));
            return this;
        }

        public Unlock.Builder<Item> level(int level) {
            this.level = level;
            return this;
        }

        @Override
        public Unlock<Item> build() {
            return new ToolUnlock(blocks, level);
        }
    }

    public static class UnlockSerializer implements Serializer<ToolUnlock> {

        @Override
        public void serialize(JsonObject json, ToolUnlock value, JsonSerializationContext serializationContext) {
            JsonArray array = new JsonArray();
            for (ActionEntry<Item> block : value.items) {
                array.addAll(block.serialize(Registry.ITEM));
            }
            json.add("items", array);
            json.addProperty("level", value.level);
        }

        @Override
        public ToolUnlock deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
            List<ActionEntry<Item>> items = AbstractItemAction.Serializer.deserializeItems(json);
            int level = GsonHelper.getAsInt(json, "level");
            return new ToolUnlock(items, level);
        }
    }
}
