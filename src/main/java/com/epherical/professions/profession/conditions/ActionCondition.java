package com.epherical.professions.profession.conditions;

public interface ActionCondition {
    ActionConditionType getType();

    @FunctionalInterface
    interface Builder {
        ActionCondition build();
    }

}
