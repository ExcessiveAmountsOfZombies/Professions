package com.epherical.professions.profession.action;


public interface Action {

    ActionType getType();

    interface Builder {
        Action build();
    }
}
