package com.epherical.professions.profession.unlock.builtin;

import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.action.builtin.items.AbstractItemAction;
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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
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
    public List<Singular<Item>> convertToSingle(Profession profession) {
        List<Singular<Item>> list = new ArrayList<>();
        for (Item realBlock : convertToReal()) {
            list.add(new ToolUnlock.Single(realBlock, level, profession.getDisplayComponent()));
        }
        return list;
    }

    @Override
    public UnlockSerializer<?> getSerializer() {
        return UnlockSerializer.TOOL_UNLOCK;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Single extends AbstractSingle<Item> {

        public Single(Item item, int level, Component professionDisplay) {
            super(item, level, professionDisplay);
        }

        @Override
        public UnlockType<Item> getType() {
            return Unlocks.TOOL_UNLOCK;
        }

        @Override
        public Component createUnlockComponent() {
            return new TranslatableComponent("Unlock use of %s at level %s",
                    ((MutableComponent) getObject().getDescription()).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)),
                    new TextComponent(String.valueOf(getUnlockLevel())).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors));
        }
    }

    public static class Builder implements Unlock.Builder<Item> {
        protected final List<ActionEntry<Item>> blocks = new ArrayList<>();
        protected int level = 2;

        public Builder item(Item... item) {
            this.blocks.add(ActionEntry.of(item));
            return this;
        }

        public Builder tag(TagKey<Item> item) {
            this.blocks.add(ActionEntry.of(item));
            return this;
        }

        public Builder level(int level) {
            this.level = level;
            return this;
        }

        @Override
        public Unlock<Item> build() {
            return new ToolUnlock(blocks, level);
        }
    }

    public static class JsonSerializer implements Serializer<ToolUnlock> {

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

    public static class NetworkSerializer implements UnlockSerializer<ToolUnlock> {

        @Override
        public ToolUnlock fromNetwork(FriendlyByteBuf buf) {
            int unlockLevel = buf.readVarInt();
            int arraySize = buf.readVarInt();
            List<ActionEntry<Item>> entries = new ArrayList<>();
            for (int i = 0; i < arraySize; i++) {
                entries.addAll(ActionEntry.fromNetwork(buf, Registry.ITEM));
            }
            return new ToolUnlock(entries, unlockLevel);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ToolUnlock unlock) {
            buf.writeVarInt(unlock.level);
            buf.writeVarInt(unlock.items.size());
            for (ActionEntry<Item> block : unlock.items) {
                block.toNetwork(buf, Registry.ITEM);
            }
        }
    }
}
