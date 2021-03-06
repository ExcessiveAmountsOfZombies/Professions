package com.epherical.professions.profession.action.builtin.items;

import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.util.ActionEntry;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.world.item.Item;

import java.util.List;

public class BrewAction extends AbstractItemAction {


    protected BrewAction(ActionCondition[] conditions, Reward[] rewards, List<ActionEntry<Item>> items) {
        super(conditions, rewards, items);
    }

    @Override
    public ActionType getType() {
        return Actions.BREW_ITEM;
    }

    @Override
    public double modifyReward(ProfessionContext context, Reward reward, double base) {
        return base;
    }

    public static Builder brew() {
        return new Builder();
    }

    public static class Serializer extends AbstractItemAction.Serializer<BrewAction> {

        @Override
        public BrewAction deserialize(JsonObject object, JsonDeserializationContext context, ActionCondition[] conditions, Reward[] rewards) {
            return new BrewAction(conditions, rewards, deserializeItems(object));
        }
    }

    public static class Builder extends AbstractItemAction.Builder<Builder> {

        @Override
        protected Builder instance() {
            return this;
        }

        @Override
        public Action build() {
            return new BrewAction(this.getConditions(), this.getRewards(), this.items);
        }
    }
}
