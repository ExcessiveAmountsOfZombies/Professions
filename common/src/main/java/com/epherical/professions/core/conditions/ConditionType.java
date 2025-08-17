package com.epherical.professions.core.conditions;

import com.mojang.serialization.MapCodec;

public record ConditionType(MapCodec<? extends Condition> codec) {
}
