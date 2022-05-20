package com.epherical.professions.profession.conditions.builtin;

import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.conditions.ActionConditionType;
import com.epherical.professions.profession.conditions.ActionConditions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;

/**
 * Based on {@link net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition}
 */
public record InvertedCondition(ActionCondition condition) implements ActionCondition {

    @Override
    public ActionConditionType getType() {
        return ActionConditions.INVERTED;
    }

    @Override
    public boolean test(ProfessionContext context) {
        return !condition.test(context);
    }

    public static Builder invert(Builder toInvert) {
        InvertedCondition inversion = new InvertedCondition(toInvert.build());
        return () -> inversion;
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<InvertedCondition> {

        @Override
        public void serialize(JsonObject json, InvertedCondition value, JsonSerializationContext serializationContext) {
            json.add("term", serializationContext.serialize(value.condition));
        }

        @Override
        public InvertedCondition deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
            ActionCondition condition = GsonHelper.getAsObject(json, "term", serializationContext, InvertedCondition.class);
            return new InvertedCondition(condition);
        }
    }
}
