package com.epherical.professions.profession.conditions.builtin;

import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.conditions.ActionConditionType;
import com.epherical.professions.profession.conditions.ActionConditions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FullyGrownCropCondition implements ActionCondition {


    @Override
    public ActionConditionType getType() {
        return ActionConditions.FULLY_GROWN_CROP_CONDITION;
    }

    @Override
    public boolean test(ProfessionContext context) {
        BlockState state = context.getPossibleParameter(ProfessionParameter.THIS_BLOCKSTATE);
        return state != null && state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state);
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<FullyGrownCropCondition> {

        @Override
        public void serialize(JsonObject json, FullyGrownCropCondition value, JsonSerializationContext serializationContext) {

        }

        public FullyGrownCropCondition deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return new FullyGrownCropCondition();
        }
    }

}
