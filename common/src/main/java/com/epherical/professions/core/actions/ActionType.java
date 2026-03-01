package com.epherical.professions.core.actions;

import com.mojang.serialization.MapCodec;

public record ActionType(MapCodec<? extends Action<?>> codec, String translationKey)  {
}
