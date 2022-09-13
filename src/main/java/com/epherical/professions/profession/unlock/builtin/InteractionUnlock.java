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
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.epherical.professions.profession.unlock.UnlockSerializer.INTERACTION_UNLOCK;

public class InteractionUnlock extends AbstractLevelUnlock<Item> {
    protected final List<ActionEntry<Item>> items;
    @Nullable
    protected Set<Item> real;


    public InteractionUnlock(List<ActionEntry<Item>> items, int level) {
        super(level);
        this.items = items;
    }

    @Override
    public UnlockType<Item> getType() {
        return Unlocks.INTERACTION_UNLOCK;
    }

    @Override
    public List<Singular<Item>> convertToSingle(Profession profession) {
        List<Singular<Item>> list = new ArrayList<>();
        for (Item real : convertToReal()) {
            list.add(new InteractionUnlock.Single(real, level, profession));
        }
        return list;
    }

    @Override
    public UnlockSerializer<?> getSerializer() {
        return INTERACTION_UNLOCK;
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

    public List<ActionEntry<Item>> getItems() {
        return items;
    }

    public static InteractionUnlock.Builder builder() {
        return new InteractionUnlock.Builder();
    }

    public static class Single extends AbstractSingle<Item> {

        public Single(Item item, int level, Profession professionDisplay) {
            super(item, level, professionDisplay);
        }

        @Override
        public UnlockType<Item> getType() {
            return Unlocks.INTERACTION_UNLOCK;
        }

        @Override
        public Component createUnlockComponent() {
            // todo: translation
            return new TranslatableComponent("Interactions - Level %s %s",
                    new TextComponent(String.valueOf(level)).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)),
                    profession.getDisplayComponent())
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors));
        }
    }

    public static class Builder implements Unlock.Builder<Item> {
        protected final List<ActionEntry<Item>> items = new ArrayList<>();
        protected int level = 2;

        public InteractionUnlock.Builder item(Item... item) {
            this.items.add(ActionEntry.of(item));
            return this;
        }

        public InteractionUnlock.Builder tag(TagKey<Item> item) {
            this.items.add(ActionEntry.of(item));
            return this;
        }

        public InteractionUnlock.Builder level(int level) {
            this.level = level;
            return this;
        }

        @Override
        public Unlock<Item> build() {
            return new ToolUnlock(items, level);
        }
    }

    public static class JsonDeserializer extends JsonUnlockSerializer<InteractionUnlock> {

        @Override
        public void serialize(JsonObject json, InteractionUnlock value, JsonSerializationContext serializationContext) {
            super.serialize(json, value, serializationContext);
            JsonArray array = new JsonArray();
            for (ActionEntry<Item> block : value.items) {
                array.addAll(block.serialize(Registry.ITEM));
            }
            json.add("items", array);
        }

        @Override
        public InteractionUnlock deserialize(JsonObject json, JsonDeserializationContext context, int level) {
            List<ActionEntry<Item>> items = AbstractItemAction.Serializer.deserializeItems(json);
            return new InteractionUnlock(items, level);
        }
    }


    public static class NetworkSerializer implements UnlockSerializer<InteractionUnlock> {

        @Override
        public InteractionUnlock fromNetwork(FriendlyByteBuf buf) {
            int unlockLevel = buf.readVarInt();
            int arraySize = buf.readVarInt();
            List<ActionEntry<Item>> entries = new ArrayList<>();
            for (int i = 0; i < arraySize; i++) {
                entries.addAll(ActionEntry.fromNetwork(buf, Registry.ITEM));
            }
            return new InteractionUnlock(entries, unlockLevel);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, InteractionUnlock unlock) {
            buf.writeVarInt(unlock.level);
            buf.writeVarInt(unlock.items.size());
            for (ActionEntry<Item> item : unlock.items) {
                item.toNetwork(buf, Registry.ITEM);
            }
        }
    }
}
