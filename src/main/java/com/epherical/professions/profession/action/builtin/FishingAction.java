package com.epherical.professions.profession.action.builtin;

import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.AbstractAction;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.profession.rewards.RewardType;
import com.epherical.professions.profession.rewards.Rewards;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FishingAction extends AbstractAction {

    private final List<Item> items;

    protected FishingAction(ActionCondition[] conditions, Reward[] rewards, List<Item> items) {
        super(conditions, rewards);
        this.items = items;
    }

    @Override
    public boolean test(ProfessionContext professionContext) {
        ItemStack item = professionContext.getPossibleParameter(ProfessionParameter.ITEM_INVOLVED);
        return item != null && !item.isEmpty() && items.contains(item.getItem());
    }

    @Override
    public ActionType getType() {
        return Actions.FISH_ACTION;
    }

    @Override
    public List<Component> displayInformation() {
        List<Component> components = new ArrayList<>();
        Map<RewardType, Component> map = getRewardInformation();
        for (Item entity : items) {
            components.add(((TranslatableComponent) entity.getDescription()).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)).append(new TranslatableComponent(" (%s | %s & %s)",
                    map.get(Rewards.PAYMENT_REWARD),
                    map.get(Rewards.EXPERIENCE_REWARD),
                    extraRewardInformation(map))));
        }
        return components;
    }


    public static class Serializer extends ActionSerializer<FishingAction> {

        @Override
        public void serialize(@NotNull JsonObject json, FishingAction value, @NotNull JsonSerializationContext serializationContext) {
            super.serialize(json, value, serializationContext);
            JsonArray array = new JsonArray();
            for (Item item : value.items) {
                array.add(Registry.ITEM.getKey(item).toString());
            }
            json.add("items", array);
        }

        @Override
        public FishingAction deserialize(JsonObject object, JsonDeserializationContext context, ActionCondition[] conditions, Reward[] rewards) {
            JsonArray array = GsonHelper.getAsJsonArray(object, "items");
            List<Item> items = new ArrayList<>();
            for (JsonElement element : array) {
                String id = element.getAsString();
                items.add(Registry.ITEM.get(new ResourceLocation(id)));
            }
            return new FishingAction(conditions, rewards, items);
        }
    }
}
