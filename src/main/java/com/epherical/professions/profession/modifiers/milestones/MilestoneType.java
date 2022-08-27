package com.epherical.professions.profession.modifiers.milestones;

import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.SerializerType;

public class MilestoneType extends SerializerType<Milestone> {

    public MilestoneType(Serializer<? extends Milestone> serializer) {
        super(serializer);
    }
}
