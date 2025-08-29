package com.epherical.professions.core.conditions;

import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.platform.Services;
import com.mojang.serialization.Codec;

import java.util.function.Predicate;

public interface Condition extends Predicate<ProfessionContext> {

    Codec<Condition> CODEC = Services.PLATFORM.getConditionTypeRegistry().byNameCodec().dispatch(
            "condition", Condition::getType, ConditionType::codec);

    ConditionType getType();

    @FunctionalInterface
    interface Builder {
        Condition build();
    }

}
