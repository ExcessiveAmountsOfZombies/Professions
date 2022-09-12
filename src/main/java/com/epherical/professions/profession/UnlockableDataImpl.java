package com.epherical.professions.profession;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.api.UnlockableData;
import com.epherical.professions.profession.modifiers.perks.Perk;
import com.epherical.professions.profession.modifiers.perks.PerkType;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.profession.unlock.UnlockType;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UnlockableDataImpl implements UnlockableData {
    private final Occupation occupation;
    // i really don't like this, but I'm not sure how to handle it better.
    private final Map<Object, UnlockableValues<Unlock.Singular<?>>> unlocks;
    private final Multimap<PerkType, Perk> perks;

    public UnlockableDataImpl(Occupation occupation) {
        this.occupation = occupation;
        this.unlocks = new HashMap<>();
        this.perks = HashMultimap.create();
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
            for (Perk perk : occupation.getProfession().getBenefits().getPerks()) {
                perks.put(perk.getType(), perk);
            }
        }
    }

    @Override
    public <T> UnlockableValues<Unlock.Singular<T>> getUnlock(T object) {
        return (UnlockableValues<Unlock.Singular<T>>)(UnlockableValues<?>) unlocks.get(object);
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

    @Override
    public Collection<Perk> getPerkByType(PerkType type) {
        return perks.get(type);
    }


    public Collection<Perk> getUnlockedPerkByType(PerkType type, ProfessionalPlayer player) {
        return perks.get(type).stream().filter(perk -> perk.canApplyPerkToPlayer("", player, occupation)).toList();
    }

    @Override
    public Collection<Perk> allPerks() {
        return perks.values();
    }

}
