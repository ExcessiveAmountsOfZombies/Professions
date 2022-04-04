package com.epherical.professions.profession.action.builtin.entity;

import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.util.ActionEntry;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.world.entity.EntityType;

import java.util.List;

public class BreedAction extends AbstractEntityAction {

    protected BreedAction(ActionCondition[] conditions, Reward[] rewards, List<ActionEntry<EntityType<?>>> types) {
        super(conditions, rewards, types);
    }

    @Override
    public ActionType getType() {
        return Actions.BREED_ENTITY;
    }

    public static class Serializer extends AbstractEntityAction.Serializer<BreedAction> {

        @Override
        public BreedAction deserialize(JsonObject object, JsonDeserializationContext context, ActionCondition[] conditions, Reward[] rewards) {
            return new BreedAction(conditions, rewards, deserializeEntities(object));
        }
    }
}
