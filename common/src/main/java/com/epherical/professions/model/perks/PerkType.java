package com.epherical.professions.model.perks;

import com.mojang.serialization.MapCodec;

public record PerkType(MapCodec<? extends Perk> codec) {
}

