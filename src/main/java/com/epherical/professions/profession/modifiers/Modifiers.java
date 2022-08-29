package com.epherical.professions.profession.modifiers;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.profession.modifiers.milestones.Milestone;
import com.epherical.professions.profession.modifiers.perks.Perk;
import com.epherical.professions.profession.progression.Occupation;

public interface Modifiers {

    void handleLevelUp(ProfessionalPlayer player, Occupation occupation);

    Milestone[] getMilestones();

    Perk[] getPerks();

}
