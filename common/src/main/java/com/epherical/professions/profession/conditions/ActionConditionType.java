package com.epherical.professions.profession.conditions;

import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.SerializerType;

public class ActionConditionType extends SerializerType<ActionCondition> {

    public ActionConditionType(Serializer<? extends ActionCondition> serializer) {
        super(serializer);
    }
}
