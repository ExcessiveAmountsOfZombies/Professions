package com.epherical.professions.profession.action;

import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.SerializerType;

public class ActionType extends SerializerType<Action> {

    public ActionType(Serializer<? extends Action> serializer) {
        super(serializer);
    }
}
