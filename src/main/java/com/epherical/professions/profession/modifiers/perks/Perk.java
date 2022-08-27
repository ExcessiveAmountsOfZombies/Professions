package com.epherical.professions.profession.modifiers.perks;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.profession.progression.Occupation;

public interface Perk {

    PerkType getType();

    int getLevel();

    // TODO: we will have to rewrite this when we get more perks, but for now we're only supporting commands
    boolean applyPerkToPlayer(String permission, ProfessionalPlayer context, Occupation occupation);

    @FunctionalInterface
    interface Builder {
        Perk build();
    }
}
