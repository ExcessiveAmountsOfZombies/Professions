package com.epherical.professions.model.actions.rewards;

import com.mojang.serialization.MapCodec;

public record RewardType(MapCodec<? extends Reward<?>> codec) {
}
