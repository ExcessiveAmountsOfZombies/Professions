package com.epherical.professions.profession.conditions;

import com.epherical.professions.ProfessionsMod;
import com.epherical.professions.profession.conditions.builtin.ToolMatcher;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraft.world.level.storage.loot.Serializer;

import static com.epherical.professions.ProfessionsMod.modID;

public class ActionConditions {
    public static final ActionConditionType TOOL_MATCHES = register(modID("tool_matches"), new ToolMatcher.ToolSerializer());


    public static Object createGsonAdapter() {
        return GsonAdapterFactory.builder(ProfessionsMod.ACTION_CONDITION_TYPE, "conditions", "conditions", ActionCondition::getType).build();
    }

    public static ActionConditionType register(ResourceLocation location, Serializer<? extends ActionCondition> serializer) {
        return Registry.register(ProfessionsMod.ACTION_CONDITION_TYPE, location, new ActionConditionType(serializer));
    }
}
