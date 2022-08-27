package com.epherical.professions.profession.modifiers.milestones;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.profession.progression.Occupation;

public interface Milestone {

    MilestoneType getType();

    int getLevel();

    boolean giveMilestoneReward(ProfessionalPlayer context, Occupation occupation);

    @FunctionalInterface
    interface Builder {
        Milestone build();
    }


}
