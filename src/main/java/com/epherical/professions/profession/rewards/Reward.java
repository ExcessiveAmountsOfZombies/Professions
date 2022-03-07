package com.epherical.professions.profession.rewards;

import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.progression.Occupation;

public interface Reward {
    RewardType getType();

    void giveReward(ProfessionContext context, Action action, Occupation occupation);

    interface Builder {
        Reward build();
    }
}
