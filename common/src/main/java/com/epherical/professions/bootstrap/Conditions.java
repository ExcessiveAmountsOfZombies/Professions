package com.epherical.professions.bootstrap;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.core.register.PlatformBootstrap;
import com.epherical.professions.model.actions.conditions.BlockStateCondition;
import com.epherical.professions.model.actions.conditions.ConditionType;
import com.epherical.professions.model.actions.conditions.FullyGrownCropCondition;
import com.epherical.professions.model.actions.conditions.InvertedCondition;
import com.epherical.professions.model.actions.conditions.ToolMatcher;

public class Conditions {

    public static final ConditionType TOOL_MATCHES = PlatformBootstrap.register(
            ProfessionsCommon.CONDITION_REGISTRY_KEY, "tool_matches", new ConditionType(ToolMatcher.CODEC));
    public static final ConditionType INVERTED_CONDITION = PlatformBootstrap.register(
            ProfessionsCommon.CONDITION_REGISTRY_KEY, "inverted", new ConditionType(InvertedCondition.CODEC));
    public static final ConditionType BLOCK_STATE_MATCHES = PlatformBootstrap.register(
            ProfessionsCommon.CONDITION_REGISTRY_KEY, "block_state_matches", new ConditionType(BlockStateCondition.CODEC));
    public static final ConditionType FULLY_GROWN_CROP_CONDITION = PlatformBootstrap.register(
            ProfessionsCommon.CONDITION_REGISTRY_KEY, "fully_grown_crop", new ConditionType(FullyGrownCropCondition.CODEC));



    public static void bootstrap() {



    }
}
