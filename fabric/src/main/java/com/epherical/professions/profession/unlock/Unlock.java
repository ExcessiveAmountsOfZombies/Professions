package com.epherical.professions.profession.unlock;


import com.epherical.professions.util.Tristate;

import java.util.List;

/**
 * Represents a(n) un/deserialized unlock. It could have one entry, or many. Can hold tags or values or both.
 */
public interface Unlock<T> {

    boolean isLocked(T object, int level);

    int getUnlockLevel();

    UnlockType<T> getType();

    List<Singular<T>> convertToSingle();

    UnlockSerializer<?> getSerializer();

    @FunctionalInterface
    interface Builder<T> {
        Unlock<T> build();
    }

    /**
     * A single value from an unlock. Unlocks should not duplicate, so we can represent them with singular objects.
     */
    interface Singular<T> {

        Tristate isLocked(T object, int level);

        UnlockType<T> getType();

        T getObject();

        int getUnlockLevel();

    }

}
