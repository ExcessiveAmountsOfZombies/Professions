package com.epherical.professions.model.actions.conditions;

import com.epherical.professions.api.actions.Condition;
import com.epherical.professions.bootstrap.Conditions;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FullyGrownCropCondition implements Condition {

    public static final MapCodec<FullyGrownCropCondition> CODEC = MapCodec.unit(FullyGrownCropCondition::new);


    @Override
    public ConditionType getType() {
        return Conditions.FULLY_GROWN_CROP_CONDITION;
    }

    @Override
    public boolean test(ProfessionContext context) {
        BlockState state = context.getPossibleParameter(ProfessionParameter.THIS_BLOCK_STATE);
        return state != null && state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state);
    }


}
