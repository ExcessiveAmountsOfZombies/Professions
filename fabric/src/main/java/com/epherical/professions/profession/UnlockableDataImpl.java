package com.epherical.professions.profession;

import com.epherical.professions.api.UnlockableData;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.util.Tristate;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UnlockableDataImpl implements UnlockableData {
    private final Occupation occupation;
    private final Map<Object, Unlock.Singular<?>> unlocks;

    public UnlockableDataImpl(Occupation occupation) {
        this.occupation = occupation;
        this.unlocks = new HashMap<>();
        for (Map.Entry<Class<?>, Collection<Unlock<?>>> entry : occupation.getProfession().getUnlocks().entrySet()) {
            for (Unlock<?> unlock : entry.getValue()) {
                for (Unlock.Singular<?> singular : unlock.convertToSingle()) {
                    unlocks.put(singular.getObject(), singular);
                }
            }
        }
    }

    @Override
    public <T> Tristate canUse(T object) {
        Unlock.Singular<T> singular = (Unlock.Singular<T>) unlocks.get(object);
        if (singular == null) {
            return Tristate.UNKNOWN;
        }
        return singular.isLocked(object, occupation.getLevel());
    }

    @Override
    public Collection<Unlock.Singular<?>> getUnlockables() {
        return unlocks.values();
    }

    @Override
    public List<Unlock.Singular<?>> getUnlockedKnowledge() {
        return unlocks.values().stream().filter(singular -> singular.getUnlockLevel() <= occupation.getLevel()).collect(Collectors.toList());
    }

}
