package com.epherical.professions.core.rewards;

import com.epherical.professions.platform.Services;
import com.mojang.serialization.Codec;

public interface Reward {

    Codec<Reward> CODEC = Services.PLATFORM.getRewardTypeRegistry().byNameCodec().dispatch(
            "reward", Reward::getType, RewardType::codec);

    RewardType getType();

    @FunctionalInterface
    interface Builder {
        Reward build();
    }
}
