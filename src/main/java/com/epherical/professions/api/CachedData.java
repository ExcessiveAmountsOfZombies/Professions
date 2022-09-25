package com.epherical.professions.api;

import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.modifiers.perks.Perk;
import com.epherical.professions.profession.modifiers.perks.PerkType;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.util.SeededValueList;

import java.util.Collection;
import java.util.List;

public interface CachedData {

    <T> SeededValueList<Unlock.Singular<T>> getUnlock(T object);

    <T> SeededValueList<Action.Singular<T>> getAction(T object);

    Occupation getOccupation();

    Collection<Unlock.Singular<?>> getUnlockables();

    List<Unlock.Singular<?>> getUnlockedKnowledge();

    Collection<Perk> getPerkByType(PerkType type);

    Collection<Perk> getUnlockedPerkByType(PerkType type, ProfessionalPlayer player);

    Collection<Perk> allPerks();

    <T> SeededValueList<Action.Singular<T>> getActions();
}
