package com.epherical.professions.profession.unlock;

import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.SerializerType;

public class UnlockType extends SerializerType<Unlock> {

    public UnlockType(Serializer<? extends Unlock> serializer) {
        super(serializer);
    }
}
