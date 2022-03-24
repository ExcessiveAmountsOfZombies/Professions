package com.epherical.professions.profession.action.builtin.items;

import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.rewards.Reward;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.world.item.Item;

import java.util.List;

public class CraftingAction extends AbstractItemAction {


    protected CraftingAction(ActionCondition[] conditions, Reward[] rewards, List<Item> items) {
        super(conditions, rewards, items);
    }

    @Override
    public ActionType getType() {
        return Actions.CRAFTS_ITEM;
    }

    public static class Serializer extends AbstractItemAction.Serializer<CraftingAction> {

        @Override
        public CraftingAction deserialize(JsonObject object, JsonDeserializationContext context, ActionCondition[] conditions, Reward[] rewards) {
            return new CraftingAction(conditions, rewards, deserializeItems(object));
        }
    }
}
