package com.epherical.professions.model.actions.conditions;

import com.epherical.professions.api.actions.Condition;
import com.mojang.serialization.MapCodec;

public record ConditionType(MapCodec<? extends Condition> codec) {
}
