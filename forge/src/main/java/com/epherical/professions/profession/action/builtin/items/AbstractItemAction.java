package com.epherical.professions.profession.action.builtin.items;

import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.AbstractAction;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.profession.rewards.RewardType;
import com.epherical.professions.profession.rewards.Rewards;
import com.epherical.professions.util.ActionEntry;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractItemAction extends AbstractAction {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final List<ActionEntry<Item>> items;
    @Nullable
    protected List<Item> realItems;

    protected AbstractItemAction(ActionCondition[] conditions, Reward[] rewards, List<ActionEntry<Item>> items) {
        super(conditions, rewards);
        this.items = items;
    }

    @Override
    public boolean test(ProfessionContext professionContext) {
        ItemStack item = professionContext.getPossibleParameter(ProfessionParameter.ITEM_INVOLVED);
        logAction(professionContext, item != null ? item.getDisplayName() : Component.nullToEmpty(""));
        return item != null && !item.isEmpty() && getRealItems().contains(item.getItem());
    }

    @Override
    public List<Component> displayInformation() {
        List<Component> components = new ArrayList<>();
        Map<RewardType, Component> map = getRewardInformation();
        for (Item item : getRealItems()) {
            components.add(((TranslatableComponent) item.getDescription()).setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)).append(new TranslatableComponent(" (%s | %s%s)",
                    //map.get(Rewards.PAYMENT_REWARD),
                    map.get(Rewards.EXPERIENCE_REWARD),
                    extraRewardInformation(map))));
        }
        return components;
    }

    @Override
    public List<Component> clientFriendlyInformation() {
        List<Component> components = new ArrayList<>();
        for (Item item : getRealItems()) {
            components.add(((TranslatableComponent) item.getDescription())
                    .setStyle(Style.EMPTY
                            .withColor(ProfessionConfig.descriptors)
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, allRewardInformation()))));
        }
        return components;
    }

    @Override
    public boolean internalCondition(ProfessionContext context) {
        return true;
    }

    protected List<Item> getRealItems() {
        if (realItems == null) {
            realItems = new ArrayList<>();
            for (ActionEntry<Item> item : items) {
                realItems.addAll(item.getActionValues(Registry.ITEM));
            }
        }
        return realItems;
    }

    public abstract static class Builder<T extends Builder<T>> extends AbstractAction.Builder<T> {
        protected final List<ActionEntry<Item>> items = new ArrayList<>();

        public Builder<T> item(Item... item) {
            this.items.add(ActionEntry.of(item));
            return this;
        }

        public Builder<T> item(TagKey<Item> item) {
            this.items.add(ActionEntry.of(item));
            return this;
        }
    }

    public abstract static class Serializer<T extends AbstractItemAction> extends ActionSerializer<T> {

        @Override
        public void serialize(@NotNull JsonObject json, T value, @NotNull JsonSerializationContext serializationContext) {
            super.serialize(json, value, serializationContext);
            JsonArray array = new JsonArray();
            for (ActionEntry<Item> item : value.items) {
                array.addAll(item.serialize(Registry.ITEM));
            }
            json.add("items", array);
        }

        public List<ActionEntry<Item>> deserializeItems(JsonObject object) {
            JsonArray array = GsonHelper.getAsJsonArray(object, "items");
            List<ActionEntry<Item>> items = new ArrayList<>();
            for (JsonElement element : array) {
                String id = element.getAsString();
                if (id.startsWith("#")) {
                    TagKey<Item> key = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(id.substring(1)));
                    items.add(ActionEntry.of(key));
                } else {
                    Registry.ITEM.getOptional(new ResourceLocation(id)).ifPresentOrElse(
                            item -> items.add(ActionEntry.of(item)),
                            () -> LOGGER.warn("Attempted to add unknown item {}. Was not added, but will continue processing the list.", id));
                }
            }
            return items;
        }
    }
}
