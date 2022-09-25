package com.epherical.professions.profession.action.builtin.items;

import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.ProfessionContext;
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

public class CraftingAction extends AbstractItemAction {


    protected CraftingAction(ActionCondition[] conditions, Reward[] rewards, List<ActionEntry<Item>> items) {
        super(conditions, rewards, items);
    }

    @Override
    public ActionType getType() {
        return Actions.CRAFTS_ITEM;
    }

    public static Builder craft() {
        return new Builder();
    }

    @Override
    public List<Action.Singular<Item>> convertToSingle(Profession profession) {
        List<Action.Singular<Item>> list = new ArrayList<>();
        for (Item realEntity : getRealItems()) {
            list.add(new CraftingAction.Single(realEntity, profession));
        }
        return list;
    }

    public static class Serializer extends AbstractItemAction.Serializer<CraftingAction> {

        @Override
        public CraftingAction deserialize(JsonObject object, JsonDeserializationContext context, ActionCondition[] conditions, Reward[] rewards) {
            return new CraftingAction(conditions, rewards, deserializeItems(object));
        }
    }

    public static class Builder extends AbstractItemAction.Builder<Builder> {

        @Override
        protected Builder instance() {
            return this;
        }

        @Override
        public Action build() {
            return new CraftingAction(this.getConditions(), this.getRewards(), this.items);
        }
    }

    public class Single extends AbstractSingle<Item> {

        public Single(Item value, Profession profession) {
            super(value, profession);
        }

        @Override
        public ActionType getType() {
            return CraftingAction.this.getType();
        }

        @Override
        public Component createActionComponent() {
            return new TranslatableComponent(getType().getTranslationKey());
        }

        @Override
        public boolean handleAction(ProfessionContext context, Occupation player) {
            return CraftingAction.this.handleAction(context, player);
        }

        @Override
        public void giveRewards(ProfessionContext context, Occupation occupation) {
            CraftingAction.this.giveRewards(context, occupation);
        }
    }
}
