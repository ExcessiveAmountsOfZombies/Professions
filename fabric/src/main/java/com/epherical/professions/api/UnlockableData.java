package com.epherical.professions.api;

import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.util.Tristate;

import java.util.Collection;
import java.util.List;

public interface UnlockableData {

    ProfessionalPlayer getPlayer();

    <T> Tristate canUse(T object);

    Collection<Unlock.Singular<?>> getUnlockables();

    List<Unlock.Singular<?>> getUnlockedKnowledge();
}
