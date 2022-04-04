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

public class KillAction extends AbstractEntityAction {

    protected KillAction(ActionCondition[] conditions, Reward[] rewards, List<ActionEntry<EntityType<?>>> types) {
        super(conditions, rewards, types);
    }

    @Override
    public ActionType getType() {
        return Actions.KILL_ENTITY;
    }

    public static class Serializer extends AbstractEntityAction.Serializer<KillAction> {

        @Override
        public KillAction deserialize(JsonObject object, JsonDeserializationContext context, ActionCondition[] conditions, Reward[] rewards) {
            return new KillAction(conditions, rewards, deserializeEntities(object));
        }
    }
}
