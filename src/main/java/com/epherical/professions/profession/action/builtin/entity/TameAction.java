package com.epherical.professions.profession.action.builtin.entity;

import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.util.ActionEntry;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.world.entity.EntityType;

import java.util.List;

public class TameAction extends AbstractEntityAction {

    protected TameAction(ActionCondition[] conditions, Reward[] rewards, List<ActionEntry<EntityType<?>>> types) {
        super(conditions, rewards, types);
    }

    @Override
    public ActionType getType() {
        return Actions.TAME_ENTITY;
    }

    public static Builder tame() {
        return new Builder();
    }

    public static class Serializer extends AbstractEntityAction.Serializer<TameAction> {

        @Override
        public TameAction deserialize(JsonObject object, JsonDeserializationContext context, ActionCondition[] conditions, Reward[] rewards) {
            return new TameAction(conditions, rewards, deserializeEntities(object));
        }
    }

    public static class Builder extends AbstractEntityAction.Builder<Builder> {

        @Override
        protected Builder instance() {
            return this;
        }

        @Override
        public Action build() {
            return new TameAction(this.getConditions(), this.getRewards(), this.entries);
        }
    }
}
