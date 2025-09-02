package com.epherical.professions.core.register;

import com.epherical.professions.CommonClass;
import com.epherical.professions.core.conditions.ConditionType;
import com.epherical.professions.core.conditions.InvertedCondition;
import com.epherical.professions.core.conditions.ToolMatcher;

public class Conditions {

    public static final ConditionType TOOL_MATCHES = PlatformBootstrap.register(
            CommonClass.CONDITION_REGISTRY_KEY, "tool_matches", new ConditionType(ToolMatcher.CODEC));
    public static final ConditionType INVERTED_CONDITION = PlatformBootstrap.register(
            CommonClass.CONDITION_REGISTRY_KEY, "inverted", new ConditionType(InvertedCondition.CODEC));



    public static void register() {


    }
}
