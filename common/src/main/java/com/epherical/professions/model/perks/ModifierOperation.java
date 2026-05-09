package com.epherical.professions.model.perks;

import com.mojang.serialization.Codec;

import java.util.Locale;

public enum ModifierOperation {
    ADDITIVE,
    MULTIPLICATIVE;

    public static final Codec<ModifierOperation> CODEC = Codec.STRING.xmap(
            value -> ModifierOperation.valueOf(value.toUpperCase(Locale.ROOT)),
            value -> value.name().toLowerCase(Locale.ROOT)
    );
}

