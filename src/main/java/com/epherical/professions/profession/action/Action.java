package com.epherical.professions.profession.action;


import com.epherical.professions.profession.ProfessionContext;

import java.util.function.Predicate;

public interface Action extends Predicate<ProfessionContext> {

    ActionType getType();

    @FunctionalInterface
    interface Builder {
        Action build();
    }
}
