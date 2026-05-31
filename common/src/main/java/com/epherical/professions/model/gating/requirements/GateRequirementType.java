package com.epherical.professions.model.gating.requirements;

import com.epherical.professions.api.actions.GateRequirement;
import com.mojang.serialization.MapCodec;

public record GateRequirementType(MapCodec<? extends GateRequirement> codec) {

}
