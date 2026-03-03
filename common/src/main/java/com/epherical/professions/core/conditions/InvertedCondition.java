package com.epherical.professions.core.conditions;

import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.register.Conditions;
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

    public static class Builder implements Condition.Builder {
        private final Condition.Builder term;

        public Builder(Condition.Builder term) {
            this.term = term;
        }

        @Override
        public Condition build() {
            return new InvertedCondition(term.build());
        }
    }
}
