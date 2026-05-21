package com.epherical.professions.model.gating.requirements;

import com.epherical.professions.bootstrap.platform.Services;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.model.Occupation;
import com.mojang.serialization.Codec;

import java.util.function.BiPredicate;

public interface GateRequirement extends BiPredicate<ProfessionContext, Occupation> {

    Codec<GateRequirement> CODEC = Services.PLATFORM.getGateRequirementTypeRegistry().byNameCodec().dispatch(
            "requirement", GateRequirement::getRequirementType, GateRequirementType::codec);

    GateRequirementType getRequirementType();

    @FunctionalInterface
    interface Builder {
        GateRequirement build();
    }
}
