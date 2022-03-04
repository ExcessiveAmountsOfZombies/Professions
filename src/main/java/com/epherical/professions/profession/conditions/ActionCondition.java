package com.epherical.professions.profession.conditions;

import com.epherical.professions.profession.ProfessionContext;

import java.util.function.Predicate;

public interface ActionCondition extends Predicate<ProfessionContext> {
    ActionConditionType getType();

    @FunctionalInterface
    interface Builder {
        ActionCondition build();
    }

}
