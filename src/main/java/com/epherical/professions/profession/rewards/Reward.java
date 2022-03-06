package com.epherical.professions.profession.rewards;

import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.action.Action;

public interface Reward {
    RewardType getType();

    void giveReward(ProfessionContext context, Action action);

    interface Builder {
        Reward build();
    }
}
