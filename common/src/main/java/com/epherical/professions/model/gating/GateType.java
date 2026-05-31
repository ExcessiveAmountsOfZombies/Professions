package com.epherical.professions.model.gating;

import com.epherical.professions.api.actions.Gate;
import com.mojang.serialization.MapCodec;

public record GateType(MapCodec<? extends Gate<?>> codec, String translationKey) {
}
