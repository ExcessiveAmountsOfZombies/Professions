package com.epherical.professions.profession.action.builtin.entity;

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
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class BreedAction extends AbstractEntityAction {

    protected BreedAction(ActionCondition[] conditions, Reward[] rewards, List<ActionEntry<EntityType<?>>> types) {
        super(conditions, rewards, types);
    }

    @Override
    public ActionType getType() {
        return Actions.BREED_ENTITY;
    }

    @Override
    public List<Action.Singular<EntityType<?>>> convertToSingle(Profession profession) {
        List<Action.Singular<EntityType<?>>> list = new ArrayList<>();
        for (EntityType<?> realEntity : getRealEntities()) {
            list.add(new Single(realEntity, profession));
        }
        return list;
    }

    public static Builder breed() {
        return new Builder();
    }

    public static class Serializer extends AbstractEntityAction.Serializer<BreedAction> {

        @Override
        public BreedAction deserialize(JsonObject object, JsonDeserializationContext context, ActionCondition[] conditions, Reward[] rewards) {
            return new BreedAction(conditions, rewards, deserializeEntities(object));
        }
    }

    public static class Builder extends AbstractEntityAction.Builder<Builder> {

        @Override
        protected Builder instance() {
            return this;
        }

        @Override
        public Action build() {
            return new BreedAction(this.getConditions(), this.getRewards(), this.entries);
        }
    }

    public class Single extends AbstractSingle<EntityType<?>> {

        public Single(EntityType<?> value, Profession profession) {
            super(value, profession);
        }

        @Override
        public ActionType getType() {
            return BreedAction.this.getType();
        }

        @Override
        public Component createActionComponent() {
            return new TranslatableComponent(getType().getTranslationKey());
        }

        @Override
        public boolean handleAction(ProfessionContext context, Occupation player) {
            return BreedAction.this.handleAction(context, player);
        }

        @Override
        public void giveRewards(ProfessionContext context, Occupation occupation) {
            BreedAction.this.giveRewards(context, occupation);
        }
    }
}
