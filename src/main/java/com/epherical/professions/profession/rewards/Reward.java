package com.epherical.professions.profession.rewards;

public interface Reward {
    RewardType getType();

    boolean giveReward();

    interface Builder {
        Reward build();
    }
}
