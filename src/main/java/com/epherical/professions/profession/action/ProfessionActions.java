package com.epherical.professions.profession.action;

import com.epherical.professions.ProfessionsMod;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.Serializer;

public class ProfessionActions {
    public static final ActionType BREAK_BLOCK = register(modID("break_block"), new BreakBlockAction());


    public static ActionType register(ResourceLocation id, Serializer<? extends Action> serializer) {
        return Registry.register(ProfessionsMod.PROFESSION_ACTIONS, id, new ActionType(serializer));
    }

    public static ResourceLocation modID(String name) {
        return new ResourceLocation("professions", name);
    }
}
