package com.epherical.professions.core.rewards;

import com.epherical.professions.core.actions.Action;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.progression.Occupation;
import com.epherical.professions.platform.Services;
import com.mojang.serialization.Codec;

public interface Reward {

    Codec<Reward> CODEC = Services.PLATFORM.getRewardTypeRegistry().byNameCodec().dispatch(
            "reward", Reward::getType, RewardType::codec);

    RewardType getType();

    void giveReward(ProfessionContext context, Occupation occupation, Action actionType);

    @FunctionalInterface
    interface Builder {
        Reward build();
    }
}
