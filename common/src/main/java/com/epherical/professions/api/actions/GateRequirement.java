package com.epherical.professions.api.actions;

import com.epherical.professions.bootstrap.platform.Services;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.model.gating.requirements.GateRequirementType;
import com.epherical.professions.util.GateReportPredicate;
import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;

import java.util.function.BiPredicate;

public interface GateRequirement extends BiPredicate<ProfessionContext, Occupation>, GateReportPredicate<ProfessionContext, Occupation> {

    Codec<GateRequirement> CODEC = Services.PLATFORM.getGateRequirementTypeRegistry().byNameCodec().dispatch(
            "requirement", GateRequirement::getRequirementType, GateRequirementType::codec);

    GateRequirementType getRequirementType();

    Component failureMessage(Occupation occupation, ProfessionContext context);

    @FunctionalInterface
    interface Builder {
        GateRequirement build();
    }
}
