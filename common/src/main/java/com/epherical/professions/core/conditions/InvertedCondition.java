package com.epherical.professions.core.conditions;

import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.register.Conditions;
import com.epherical.professions.platform.Services;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record InvertedCondition(Condition condition) implements Condition {

    public static final MapCodec<InvertedCondition> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    Condition.CODEC.fieldOf("term").forGetter(InvertedCondition::condition)
            ).apply(i, InvertedCondition::new));



    @Override
    public ConditionType getType() {
        return Conditions.INVERTED_CONDITION;
    }

    @Override
    public boolean test(ProfessionContext context) {
        return !condition.test(context);
    }
}
