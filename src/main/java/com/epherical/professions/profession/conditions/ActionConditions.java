package com.epherical.professions.profession.conditions;

import com.epherical.professions.ProfessionConstants;
import com.epherical.professions.profession.conditions.builtin.BlockStatePropertyAnyCondition;
import com.epherical.professions.profession.conditions.builtin.BlockStatePropertyCondition;
import com.epherical.professions.profession.conditions.builtin.InvertedCondition;
import com.epherical.professions.profession.conditions.builtin.ToolMatcher;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraft.world.level.storage.loot.Serializer;

import static com.epherical.professions.ProfessionsMod.modID;

public class ActionConditions {
    public static final ActionConditionType TOOL_MATCHES = register(modID("tool_matches"), new ToolMatcher.ToolSerializer());
    public static final ActionConditionType INVERTED = register(modID("inverted"), new InvertedCondition.Serializer());
    public static final ActionConditionType BLOCK_STATE_MATCHES = register(modID("block_state_matches"), new BlockStatePropertyCondition.Serializer());
    public static final ActionConditionType BLOCK_STATE_MATCHES_ANY = register(modID("block_state_matches_any"), new BlockStatePropertyAnyCondition.Serializer());
    // level gate conditions, certain level to activate this action
    // advancement condition

    public static Object createGsonAdapter() {
        return GsonAdapterFactory.builder(ProfessionConstants.ACTION_CONDITION_TYPE, "condition", "condition", ActionCondition::getType).build();
    }

    public static ActionConditionType register(ResourceLocation location, Serializer<? extends ActionCondition> serializer) {
        return Registry.register(ProfessionConstants.ACTION_CONDITION_TYPE, location, new ActionConditionType(serializer));
    }
}
