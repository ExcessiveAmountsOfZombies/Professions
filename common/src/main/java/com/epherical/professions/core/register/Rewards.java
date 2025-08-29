package com.epherical.professions.core.register;

import com.epherical.professions.core.rewards.OccupationExperience;
import com.epherical.professions.core.rewards.RewardType;

import static com.epherical.professions.CommonClass.REWARD_REGISTRY_KEY;

public class Rewards {

    public static final RewardType OCCUPATION_EXPERIENCE = PlatformBootstrap.register(
            REWARD_REGISTRY_KEY, "occupation_exp", new RewardType(OccupationExperience.CODEC));


    public static void register() {

    }
}
