package com.epherical.professions.core.rewards;

import com.mojang.serialization.MapCodec;

public record RewardType(MapCodec<? extends Reward> codec) {
}
