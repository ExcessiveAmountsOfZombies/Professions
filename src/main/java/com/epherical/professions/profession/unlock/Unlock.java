package com.epherical.professions.profession.unlock;


import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.util.ActionEntry;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Represents a(n) un/deserialized unlock. It could have one entry, or many. Can hold tags or values or both.
 */
public interface Unlock<T> {

    UnlockType<T> getType();

    List<Singular<T>> convertToSingle(Profession profession);

    UnlockSerializer<?> getSerializer();

    @Deprecated()
        // TODO; this is a result of technical debt in switching from V1 to V2 of our data loading process.
        //  ideally the action wouldn't be formed until it has its single action entry, but we'll figure out how to solve
        //  that later.
    void addActionEntry(ActionEntry<T> entry);

    @FunctionalInterface
    interface Builder<T> {
        Unlock<T> build();
    }

    /**
     * A single value from an unlock. Unlocks should not duplicate, so we can represent them with singular objects.
     */
    interface Singular<T> {

        UnlockType<T> getType();

        T getObject();

        Component getProfessionDisplay();

        Profession getProfession();

        Component createUnlockComponent();

        boolean canUse(ProfessionalPlayer player);

    }

}
