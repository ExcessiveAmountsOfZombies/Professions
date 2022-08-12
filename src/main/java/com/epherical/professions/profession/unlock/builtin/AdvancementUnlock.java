package com.epherical.professions.profession.unlock.builtin;

import com.epherical.professions.api.ProfessionalPlayer;
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
import net.minecraft.advancements.Advancement;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.Serializer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdvancementUnlock implements Unlock<Item> {
    protected final List<ActionEntry<Item>> items;
    protected final ResourceLocation location;
    @Nullable
    protected Set<Item> real;

    public AdvancementUnlock(List<ActionEntry<Item>> items, ResourceLocation location) {
        this.items = items;
        this.location = location;
    }


    @Override
    public UnlockType<Item> getType() {
        return Unlocks.ADVANCEMENT_UNLOCK;
    }

    @Override
    public List<Singular<Item>> convertToSingle(Profession profession) {
        List<Singular<Item>> list = new ArrayList<>();
        for (Item item : convertToReal()) {
            list.add(new Single(item, location, profession));
        }
        return list;
    }

    @Override
    public UnlockSerializer<?> getSerializer() {
        return UnlockSerializer.ADVANCEMENT_UNLOCK;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Single implements Singular<Item> {
        protected final ResourceLocation value;
        protected final Item item;
        protected final Profession profession;
        protected @Nullable Advancement advancement;

        public Single(Item item, ResourceLocation location, Profession professionDisplay) {
            this.item = item;
            this.value = location;
            this.profession = professionDisplay;
        }

        @Override
        public UnlockType<Item> getType() {
            return Unlocks.ADVANCEMENT_UNLOCK;
        }

        @Override
        public Item getObject() {
            return item;
        }

        @Override
        public Component getProfessionDisplay() {
            return profession.getDisplayComponent();
        }

        @Override
        public Profession getProfession() {
            return profession;
        }

        @Override
        public Component createUnlockComponent() {
            Component toUse;
            if (advancement != null) {
                toUse = advancement.getChatComponent();
            } else {
                toUse = new TextComponent(value.toString()).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables));
            }
            // todo: translation
            return new TranslatableComponent("Achieve: - %s", toUse).setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors));
        }

        @Override
        public boolean canUse(ProfessionalPlayer player) {
            Advancement advancement = player.getPlayer().getServer().getAdvancements().getAdvancement(value);
            if (advancement != null) {
                this.advancement = advancement;
                return player.getPlayer().getAdvancements().getOrStartProgress(advancement).isDone();
            } else {
                return true;
            }
        }
    }

    public static class Builder implements Unlock.Builder<Item> {
        protected final List<ActionEntry<Item>> blocks = new ArrayList<>();
        protected ResourceLocation resourceLocation = new ResourceLocation("minecraft:story/root");

        public Builder item(Item... item) {
            this.blocks.add(ActionEntry.of(item));
            return this;
        }

        public Builder tag(TagKey<Item> item) {
            this.blocks.add(ActionEntry.of(item));
            return this;
        }

        public Builder id(ResourceLocation id) {
            this.resourceLocation = id;
            return this;
        }

        @Override
        public Unlock<Item> build() {
            return new AdvancementUnlock(blocks, resourceLocation);
        }
    }

    public static class JsonSerializer implements Serializer<AdvancementUnlock> {

        @Override
        public void serialize(JsonObject json, AdvancementUnlock value, JsonSerializationContext serializationContext) {
            JsonArray array = new JsonArray();
            for (ActionEntry<Item> block : value.items) {
                array.addAll(block.serialize(Registry.ITEM));
            }
            json.add("items", array);
            json.addProperty("advancement_id", value.location.toString());
        }

        @Override
        public AdvancementUnlock deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
            List<ActionEntry<Item>> items = AbstractItemAction.Serializer.deserializeItems(json);

            ResourceLocation level = new ResourceLocation(GsonHelper.getAsString(json, "advancement_id"));
            return new AdvancementUnlock(items, level);
        }
    }

    public static class NetworkSerializer implements UnlockSerializer<AdvancementUnlock> {

        @Override
        public AdvancementUnlock fromNetwork(FriendlyByteBuf buf) {
            ResourceLocation advancementID = buf.readResourceLocation();
            int arraySize = buf.readVarInt();
            List<ActionEntry<Item>> entries = new ArrayList<>();
            for (int i = 0; i < arraySize; i++) {
                entries.addAll(ActionEntry.fromNetwork(buf, Registry.ITEM));
            }
            return new AdvancementUnlock(entries, advancementID);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, AdvancementUnlock unlock) {
            buf.writeResourceLocation(unlock.location);
            buf.writeVarInt(unlock.items.size());
            for (ActionEntry<Item> block : unlock.items) {
                block.toNetwork(buf, Registry.ITEM);
            }
        }
    }
}
