package com.epherical.professions.model.gating.requirements;

import com.epherical.professions.api.actions.GateRequirement;
import com.epherical.professions.bootstrap.Requirements;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.data.config.ProfessionConfig;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.api.actions.Gate;
import com.epherical.professions.model.gating.GateReport;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

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
    public Component failureMessage(Occupation occupation, ProfessionContext context) {
        return Component.translatable("professions.gate.requirement.level.failureMessage",
                Component.literal(String.valueOf(level)).setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)),
                Component.literal(occupation.getProfession().value().displayNameRaw())
                        .setStyle(Style.EMPTY.withColor(occupation.getProfession().value().professionColor())));
    }

    @Override
    public boolean test(ProfessionContext context, Occupation occupation) {
        return occupation.getLevel() >= level;
    }

    @Override
    public void test(ProfessionContext context, Occupation occupation, GateReport gateReport, Gate<?> gate) {
        if (!test(context, occupation)) {
            gateReport.failed(this, gate, occupation, context);
        } else {
            gateReport.success(this, gate, occupation, context);
        }
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
