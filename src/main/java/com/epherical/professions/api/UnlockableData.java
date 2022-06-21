package com.epherical.professions.api;

import com.epherical.professions.profession.UnlockableValues;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.util.Tristate;

import java.util.Collection;
import java.util.List;

public interface UnlockableData {

    <T> UnlockableValues<Unlock.Singular<T>> getUnlock(T object);

    <T> Tristate canUse(T object);

    <T> Tristate canUse(Unlock.Singular<T> unlock, T object);

    Occupation getOccupation();

    Collection<Unlock.Singular<?>> getUnlockables();

    List<Unlock.Singular<?>> getUnlockedKnowledge();
}
