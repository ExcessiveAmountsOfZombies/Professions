package com.epherical.professions.profession.action.builtin.items;

import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.util.ActionEntry;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class TradeAction extends AbstractItemAction {


    protected TradeAction(ActionCondition[] conditions, Reward[] rewards, List<ActionEntry<Item>> items) {
        super(conditions, rewards, items);
    }

    @Override
    public ActionType getType() {
        return Actions.VILLAGER_TRADE;
    }

    @Override
    public List<Action.Singular<Item>> convertToSingle(Profession profession) {
        List<Action.Singular<Item>> list = new ArrayList<>();
        for (Item items : getRealItems()) {
            list.add(new TradeAction.Single(items, profession));
        }
        return list;
    }

    public static Builder trade() {
        return new Builder();
    }

    @Override
    public double modifyReward(ProfessionContext context, Reward reward, double base) {
        return context.hasParameter(ProfessionParameter.ITEM_INVOLVED) ? base * context.getParameter(ProfessionParameter.ITEM_INVOLVED).getCount() : base;
    }

    public static class Serializer extends AbstractItemAction.Serializer<TradeAction> {

        @Override
        public TradeAction deserialize(JsonObject object, JsonDeserializationContext context, ActionCondition[] conditions, Reward[] rewards) {
            return new TradeAction(conditions, rewards, deserializeItems(object));
        }
    }

    public static class Builder extends AbstractItemAction.Builder<Builder> {

        @Override
        protected Builder instance() {
            return this;
        }

        @Override
        public Action build() {
            return new TradeAction(this.getConditions(), this.getRewards(), this.items);
        }
    }

    public class Single extends AbstractSingle<Item> {

        public Single(Item value, Profession profession) {
            super(value, profession);
        }

        @Override
        public ActionType getType() {
            return TradeAction.this.getType();
        }

        @Override
        public Component createActionComponent() {
            return new TranslatableComponent(getType().getTranslationKey());
        }

        @Override
        public boolean handleAction(ProfessionContext context, Occupation player) {
            return TradeAction.this.handleAction(context, player);
        }

        @Override
        public void giveRewards(ProfessionContext context, Occupation occupation) {
            TradeAction.this.giveRewards(context, occupation);
        }
    }
}
