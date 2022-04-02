package com.epherical.professions.profession.action.builtin.items;

import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.rewards.Reward;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.world.item.Item;

import java.util.List;

public class TakeSmeltAction extends AbstractItemAction {


    protected TakeSmeltAction(ActionCondition[] conditions, Reward[] rewards, List<Item> items) {
        super(conditions, rewards, items);
    }

    @Override
    public ActionType getType() {
        return Actions.TAKE_COOKED_ITEM;
    }

    public static Builder takeSmelt() {
        return new Builder();
    }

    @Override
    public double modifyReward(ProfessionContext context, Reward reward, double base) {
        return context.getParameter(ProfessionParameter.ITEM_INVOLVED).getCount() * base;
    }

    public static class Serializer extends AbstractItemAction.Serializer<TakeSmeltAction> {

        @Override
        public TakeSmeltAction deserialize(JsonObject object, JsonDeserializationContext context, ActionCondition[] conditions, Reward[] rewards) {
            return new TakeSmeltAction(conditions, rewards, deserializeItems(object));
        }
    }

    public static class Builder extends AbstractItemAction.Builder<TakeSmeltAction.Builder> {

        @Override
        protected TakeSmeltAction.Builder instance() {
            return this;
        }

        @Override
        public Action build() {
            return new TakeSmeltAction(this.getConditions(), this.getRewards(), this.items);
        }
    }
}
