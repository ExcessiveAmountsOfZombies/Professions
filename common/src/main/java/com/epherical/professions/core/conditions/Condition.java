package com.epherical.professions.core.conditions;

import com.epherical.professions.platform.Services;
import com.mojang.serialization.Codec;

public interface Condition {

    Codec<Condition> CODEC = Services.PLATFORM.getConditionTypeRegistry().byNameCodec().dispatch(
            "condition", Condition::getType, ConditionType::codec);

    ConditionType getType();

    @FunctionalInterface
    interface Builder {
        Condition build();
    }

}
