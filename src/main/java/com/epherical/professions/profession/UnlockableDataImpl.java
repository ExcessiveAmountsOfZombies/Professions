package com.epherical.professions.profession;

import com.epherical.professions.api.UnlockableData;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.profession.unlock.UnlockType;
import com.epherical.professions.util.Tristate;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UnlockableDataImpl implements UnlockableData {
    private final Occupation occupation;
    // i really don't like this, but I'm not sure how to handle it better.
    private final Map<Object, UnlockableValues<Unlock.Singular<?>>> unlocks;

    public UnlockableDataImpl(Occupation occupation) {
        this.occupation = occupation;
        this.unlocks = new HashMap<>();
        if (occupation.getProfession() != null) {
            for (Map.Entry<UnlockType<?>, Collection<Unlock<?>>> entry : occupation.getProfession().getUnlocks().entrySet()) {
                for (Unlock<?> unlock : entry.getValue()) {
                    for (Unlock.Singular<?> singular : unlock.convertToSingle(occupation.getProfession())) {
                        if (unlocks.containsKey(singular.getObject())) {
                            unlocks.get(singular.getObject()).addValue(singular);
                        } else {
                            unlocks.put(singular.getObject(), new UnlockableValues<>(singular));
                        }
                    }
                }
            }
        }
    }

    @Override
    public <T> UnlockableValues<Unlock.Singular<T>> getUnlock(T object) {
        return (UnlockableValues<Unlock.Singular<T>>)(UnlockableValues<?>) unlocks.get(object);
    }

    @Override
    public <T> Tristate canUse(T object) {
        // NO NO NO NO NO
        UnlockableValues<Unlock.Singular<T>> values = getUnlock(object);
        if (values == null || values.isEmpty()) {
            return Tristate.UNKNOWN;
        }
        for (Unlock.Singular<T> singular : values.getValues()) {
            if (!singular.isLocked(object, occupation.getLevel()).valid()) {
                return Tristate.FALSE;
            }
        }
        return Tristate.TRUE;
    }

    @Override
    public <T> Tristate canUse(Unlock.Singular<T> unlock, T object) {
        if (unlock == null) {
            return Tristate.UNKNOWN;
        }
        return unlock.isLocked(object, occupation.getLevel());
    }

    @Override
    public Occupation getOccupation() {
        return occupation;
    }

    @Override
    public Collection<Unlock.Singular<?>> getUnlockables() {
        return unlocks.values()
                .stream()
                .map(UnlockableValues::getValues)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public List<Unlock.Singular<?>> getUnlockedKnowledge() {
        return List.of();
        //return unlocks.values().stream().filter(singular -> singular.getUnlockLevel() <= occupation.getLevel()).collect(Collectors.toList());
    }

}
