package com.epherical.professions.profession.unlock;


import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.operation.AbstractOperation;
import com.epherical.professions.profession.operation.CompoundKey;
import com.epherical.professions.profession.operation.LevelRequirement;
import com.epherical.professions.profession.operation.Operator;
import com.epherical.professions.profession.unlock.builtin.AbstractLevelUnlock;
import com.epherical.professions.util.ActionEntry;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

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


    @Deprecated
    List<ActionEntry<T>> getEntries();

    @Deprecated
    Registry<T> getRegistry();

    @Deprecated()
    default void handleMigration(Map<?, ?> kMap, Profession profession) {
        // TOO LAZY TO DEAL WITH GENERIC ISSUES, JUST CAST THIS IS GETTING DELETED.
        Map<CompoundKey<T>, AbstractOperation<T>> keyToKey = (Map<CompoundKey<T>, AbstractOperation<T>>) kMap;
        for (ActionEntry<T> entry : getEntries()) {
            for (ActionEntry.Value<T> actionValue : entry.getActionValues()) {
                ResourceLocation specificObject = actionValue.getKey(getRegistry());
                CompoundKey<T> key = new CompoundKey<>((ResourceKey<Registry<T>>) getRegistry().key(), specificObject);
                AbstractOperation<T> operation = keyToKey.get(key);
                if (operation == null) {
                    operation = actionValue.createOperation(getRegistry());
                    keyToKey.put(key, operation);
                }

                Operator<Unlock<T>, List<LevelRequirement>> unlockOperator;
                if (this instanceof AbstractLevelUnlock<T> unlock) {
                    int level = unlock.getLevel();
                    unlockOperator = new Operator<>(List.of(new LevelRequirement(profession.getKey().toString(), level)), this);
                } else {
                    unlockOperator = new Operator<>(List.of(), this);
                }
                operation.addUnlock(unlockOperator);
            }
        }
    }

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
