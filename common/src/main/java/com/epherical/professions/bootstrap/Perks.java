package com.epherical.professions.bootstrap;

import com.epherical.professions.core.register.PlatformBootstrap;
import com.epherical.professions.model.perks.PerkAttribute;
import com.epherical.professions.model.perks.PerkType;
import com.epherical.professions.model.perks.PerkProfessionGainEXP;

import static com.epherical.professions.ProfessionsCommon.PERK_REGISTRY_KEY;

public class Perks {

    public static final PerkType ATTRIBUTE_PERK = PlatformBootstrap.register(
            PERK_REGISTRY_KEY, "attribute", new PerkType(PerkAttribute.CODEC));
    public static final PerkType PROFESSION_EXP_GAIN = PlatformBootstrap.register(
            PERK_REGISTRY_KEY, "profession_exp_gain", new PerkType(PerkProfessionGainEXP.CODEC));

    public static void bootstrap() {
    }
}

