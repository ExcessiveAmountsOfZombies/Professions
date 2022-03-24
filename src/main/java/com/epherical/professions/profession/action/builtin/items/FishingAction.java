package com.epherical.professions.profession.action.builtin.items;

import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.rewards.Reward;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FishingAction extends AbstractItemAction {


    protected FishingAction(ActionCondition[] conditions, Reward[] rewards, List<Item> items) {
        super(conditions, rewards, items);
    }

    @Override
    public ActionType getType() {
        return Actions.FISH_ACTION;
    }


    public static class Serializer extends AbstractItemAction.Serializer<FishingAction> {

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
            return new FishingAction(conditions, rewards, deserializeItems(object));
        }
    }
}
