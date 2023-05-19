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
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.epherical.professions.profession.unlock.UnlockSerializer.EQUIPMENT_UNLOCK;

public class EquipmentUnlock extends AbstractLevelUnlock<Item> {
    protected final List<ActionEntry<Item>> items;
    @Nullable
    protected Set<Item> real;


    public EquipmentUnlock(List<ActionEntry<Item>> items, int level) {
        super(level);
        this.items = items;
    }

    @Override
    public UnlockType<Item> getType() {
        return Unlocks.EQUIPMENT_UNLOCK;
    }

    @Override
    public List<Singular<Item>> convertToSingle(Profession profession) {
        List<Singular<Item>> list = new ArrayList<>();
        for (Item real : convertToReal()) {
            list.add(new EquipmentUnlock.Single(real, level, profession));
        }
        return list;
    }

    @Override
    public UnlockSerializer<?> getSerializer() {
        return EQUIPMENT_UNLOCK;
    }

    @Override
    public void addActionEntry(ActionEntry<Item> entry) {
        items.add(entry);
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

    public List<ActionEntry<Item>> getEntries() {
        return items;
    }

    @Override
    public Registry<Item> getRegistry() {
        return Registry.ITEM;
    }

    public static EquipmentUnlock.Builder builder() {
        return new EquipmentUnlock.Builder();
    }

    public static class Single extends AbstractLevelUnlock.AbstractSingle<Item> {

        public Single(Item item, int level, Profession professionDisplay) {
            super(item, level, professionDisplay);
        }

        @Override
        public UnlockType<Item> getType() {
            return Unlocks.EQUIPMENT_UNLOCK;
        }

        @Override
        public Component createUnlockComponent() {
            // todo: translation
            return Component.translatable("Equip - Level %s %s",
                            Component.literal(String.valueOf(level)).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)),
                            profession.getDisplayComponent())
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors));
        }
    }

    public static class Builder implements Unlock.Builder<Item> {
        protected final List<ActionEntry<Item>> items = new ArrayList<>();
        protected int level = 2;

        public EquipmentUnlock.Builder item(Item... item) {
            this.items.add(ActionEntry.of(item));
            return this;
        }

        public EquipmentUnlock.Builder tag(TagKey<Item> item) {
            this.items.add(ActionEntry.of(item));
            return this;
        }

        public EquipmentUnlock.Builder level(int level) {
            this.level = level;
            return this;
        }

        @Override
        public Unlock<Item> build() {
            return new EquipmentUnlock(items, level);
        }
    }

    public static class JsonDeserializer extends JsonUnlockSerializer<EquipmentUnlock> {

        @Override
        public void serialize(JsonObject json, EquipmentUnlock value, JsonSerializationContext serializationContext) {
            super.serialize(json, value, serializationContext);
            JsonArray array = new JsonArray();
            for (ActionEntry<Item> block : value.items) {
                array.addAll(block.serialize(Registry.ITEM));
            }
            json.add("items", array);
        }

        @Override
        public EquipmentUnlock deserialize(JsonObject json, JsonDeserializationContext context, int level) {
            List<ActionEntry<Item>> items = AbstractItemAction.Serializer.deserializeItems(json);
            return new EquipmentUnlock(items, level);
        }
    }


    public static class NetworkSerializer implements UnlockSerializer<EquipmentUnlock> {

        @Override
        public EquipmentUnlock fromNetwork(FriendlyByteBuf buf) {
            int unlockLevel = buf.readVarInt();
            int arraySize = buf.readVarInt();
            List<ActionEntry<Item>> entries = new ArrayList<>();
            for (int i = 0; i < arraySize; i++) {
                entries.addAll(ActionEntry.fromNetwork(buf, Registry.ITEM));
            }
            return new EquipmentUnlock(entries, unlockLevel);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, EquipmentUnlock unlock) {
            buf.writeVarInt(unlock.level);
            buf.writeVarInt(unlock.items.size());
            for (ActionEntry<Item> item : unlock.items) {
                item.toNetwork(buf, Registry.ITEM);
            }
        }
    }
}
