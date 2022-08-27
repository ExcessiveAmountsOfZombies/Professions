package com.epherical.professions.profession.modifiers.perks;

import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.SerializerType;

public class PerkType extends SerializerType<Perk> {

    public PerkType(Serializer<? extends Perk> serializer) {
        super(serializer);
    }

}
