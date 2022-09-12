package com.epherical.professions.api;

import com.epherical.professions.profession.UnlockableValues;
import com.epherical.professions.profession.modifiers.perks.Perk;
import com.epherical.professions.profession.modifiers.perks.PerkType;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.unlock.Unlock;

import java.util.Collection;
import java.util.List;

public interface UnlockableData {

    <T> UnlockableValues<Unlock.Singular<T>> getUnlock(T object);

    Occupation getOccupation();

    Collection<Unlock.Singular<?>> getUnlockables();

    List<Unlock.Singular<?>> getUnlockedKnowledge();

    Collection<Perk> getPerkByType(PerkType type);

    Collection<Perk> getUnlockedPerkByType(PerkType type, ProfessionalPlayer player);

    Collection<Perk> allPerks();
}
