package com.epherical.professions.model.gating.requirements;

import com.epherical.professions.bootstrap.Requirements;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.model.Occupation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record LevelRequirement(int level) implements GateRequirement {

    public static final MapCodec<LevelRequirement> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.INT.fieldOf("level").forGetter(LevelRequirement::level)
            ).apply(instance, LevelRequirement::new)
    );

    @Override
    public GateRequirementType getRequirementType() {
        return Requirements.LEVEL_REQUIREMENT;
    }

    @Override
    public boolean test(ProfessionContext context, Occupation occupation) {
        return occupation.getLevel() >= level;
    }

    public static class Builder implements GateRequirement.Builder {
        private int level;

        public Builder level(int level) {
            this.level = level;
            return this;
        }

        @Override
        public GateRequirement build() {
            return new LevelRequirement(level);
        }
    }
}
