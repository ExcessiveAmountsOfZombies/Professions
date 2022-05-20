package com.epherical.professions.profession.conditions.builtin;

import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.conditions.ActionConditionType;
import com.epherical.professions.profession.conditions.ActionConditions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public record BlockStateIntegerRangeCondition(int min, int max, String property) implements ActionCondition {

    @Override
    public ActionConditionType getType() {
        return ActionConditions.BLOCKSTATE_INTEGER_RANGE;
    }

    @Override
    public boolean test(ProfessionContext context) {
        BlockState state = context.getPossibleParameter(ProfessionParameter.THIS_BLOCKSTATE);
        if (state == null) {
            return false;
        }
        Property<?> prop = state.getBlock().getStateDefinition().getProperty(property);
        if (prop == null) {
            return false;
        }
        Object value = prop.value(state).value();
        if (!(value instanceof Integer)) {
           return false;
        }

        int intValue = (Integer) value;
        return intValue >= min && intValue <= max;
    }

    public static Builder intRange(int min, int max, String property) {
        return () -> new BlockStateIntegerRangeCondition(min, max, property);
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<BlockStateIntegerRangeCondition> {

        @Override
        public void serialize(JsonObject json, BlockStateIntegerRangeCondition value, JsonSerializationContext serializationContext) {
            json.addProperty("min", value.min);
            json.addProperty("max", value.max);
            json.addProperty("property", value.property);
        }

        public BlockStateIntegerRangeCondition deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            int min = GsonHelper.getAsInt(jsonObject, "min");
            int max = GsonHelper.getAsInt(jsonObject, "max");
            String property = GsonHelper.getAsString(jsonObject, "property");
            return new BlockStateIntegerRangeCondition(min, max, property);
        }
    }

}
