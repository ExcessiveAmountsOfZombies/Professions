package com.epherical.professions.profession.action.builtin.items;

import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.rewards.Reward;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
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

    public static Builder fish() {
        return new Builder();
    }

    public static class Serializer extends AbstractItemAction.Serializer<FishingAction> {

        @Override
        public void serialize(@NotNull JsonObject json, FishingAction value, @NotNull JsonSerializationContext serializationContext) {
            super.serialize(json, value, serializationContext);
        }

        @Override
        public FishingAction deserialize(JsonObject object, JsonDeserializationContext context, ActionCondition[] conditions, Reward[] rewards) {
            return new FishingAction(conditions, rewards, deserializeItems(object));
        }
    }

    public static class Builder extends AbstractItemAction.Builder<FishingAction.Builder> {

        @Override
        protected Builder instance() {
            return this;
        }

        @Override
        public Action build() {
            return new FishingAction(this.getConditions(), this.getRewards(), this.items);
        }
    }
}
