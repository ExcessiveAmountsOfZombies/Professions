package com.epherical.professions.profession.rewards;

import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.SerializerType;

public class RewardType extends SerializerType<Reward> {

    public RewardType(Serializer<? extends Reward> serializer) {
        super(serializer);
    }
}
