package com.epherical.professions.core.rewards;

import com.epherical.professions.core.actions.Action;

public record OccupationExperienceReward(double expAmount) implements Reward {



    @Override
    public RewardType getType() {
        return null;
    }

    @Override
    public void giveReward(Action actionType) {

    }
}
