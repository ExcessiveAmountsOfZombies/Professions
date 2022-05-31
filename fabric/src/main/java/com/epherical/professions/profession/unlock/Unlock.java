package com.epherical.professions.profession.unlock;


import com.epherical.professions.util.Tristate;

import java.util.List;

public interface Unlock<T> {

    boolean isLocked(T object, int level);

    int getUnlockLevel();

    UnlockType getType();

    Class<T> getClassType();

    List<Singular<T>> convertToSingle();

    @FunctionalInterface
    interface Builder<T> {
        Unlock<T> build();
    }

    interface Singular<T> {

        Tristate isLocked(T object, int level);

        UnlockType getType();

        T getObject();

    }

}
