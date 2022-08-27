package com.epherical.professions.profession.modifiers;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.profession.progression.Occupation;

public interface Modifiers {

    void handleLevelUp(ProfessionalPlayer player, Occupation occupation);

    boolean canUsePerk(String permission, ProfessionalPlayer player, Occupation occupation);

}
