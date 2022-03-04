package com.epherical.professions.profession.rewards;

public interface Reward {
    RewardType getType();

    void giveReward();

    interface Builder {
        Reward build();
    }
}
