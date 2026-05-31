package com.epherical.professions.model.perks;

import com.epherical.professions.api.perks.Perk;
import com.epherical.professions.bootstrap.Perks;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class PerkProfessionGainEXP extends Perk {

    public static final MapCodec<PerkProfessionGainEXP> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    COMMON.forGetter(PerkProfessionGainEXP::buildCommon))
                    .apply(i, PerkProfessionGainEXP::new)
    );

    public PerkProfessionGainEXP(Common common) {
        super(common);
    }

    @Override
    public PerkType getType() {
        return Perks.PROFESSION_EXP_GAIN;
    }

    @Override
    public Common buildCommon() {
        return Common.build(this);
    }


}

