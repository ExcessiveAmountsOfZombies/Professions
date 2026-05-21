package com.epherical.professions.bootstrap;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.core.register.PlatformBootstrap;
import com.epherical.professions.model.gating.requirements.AdvancementRequirement;
import com.epherical.professions.model.gating.requirements.GateRequirementType;
import com.epherical.professions.model.gating.requirements.LevelRequirement;

public class Requirements {

    public static final GateRequirementType LEVEL_REQUIREMENT = PlatformBootstrap.register(
            ProfessionsCommon.REQUIREMENT_REGISTRY_KEY, "level", new GateRequirementType(LevelRequirement.CODEC));
    public static final GateRequirementType ADVANCEMENT_REQUIREMENT = PlatformBootstrap.register(
            ProfessionsCommon.REQUIREMENT_REGISTRY_KEY, "advancement", new GateRequirementType(AdvancementRequirement.CODEC));

    public static void bootstrap() {



    }
}
