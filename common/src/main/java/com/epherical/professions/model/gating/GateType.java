package com.epherical.professions.model.gating;

import com.mojang.serialization.MapCodec;

public record GateType(MapCodec<? extends Gate<?>> codec) {
}
