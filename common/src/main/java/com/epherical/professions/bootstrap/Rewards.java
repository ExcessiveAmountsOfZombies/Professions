package com.epherical.professions.bootstrap;

import com.epherical.professions.core.register.PlatformBootstrap;
import com.epherical.professions.model.actions.rewards.ItemReward;
import com.epherical.professions.model.actions.rewards.OccupationExperience;
import com.epherical.professions.model.actions.rewards.RewardType;

import static com.epherical.professions.ProfessionsCommon.REWARD_REGISTRY_KEY;

public class Rewards {

    public static final RewardType OCCUPATION_EXPERIENCE = PlatformBootstrap.register(
            REWARD_REGISTRY_KEY, "occupation_exp", new RewardType(OccupationExperience.CODEC));
    public static final RewardType ITEM_REWARD = PlatformBootstrap.register(
            REWARD_REGISTRY_KEY, "item", new RewardType(ItemReward.CODEC));

    public static void bootstrap() {

    }
}
