package com.epherical.professions.profession.conditions;

import com.epherical.professions.Constants;
import com.epherical.professions.RegistryConstants;
import com.epherical.professions.profession.conditions.builtin.BlockStateIntegerRangeCondition;
import com.epherical.professions.profession.conditions.builtin.BlockStatePropertyAnyCondition;
import com.epherical.professions.profession.conditions.builtin.BlockStatePropertyCondition;
import com.epherical.professions.profession.conditions.builtin.FullyGrownCropCondition;
import com.epherical.professions.profession.conditions.builtin.InvertedCondition;
import com.epherical.professions.profession.conditions.builtin.ToolMatcher;
import net.minecraft.core.MappedRegistry;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static com.epherical.professions.Constants.modID;

public class ActionConditions {
    public static final DeferredRegister<ActionConditionType> CONDITIONS = DeferredRegister.create(RegistryConstants.ACTION_CONDITION_TYPE, Constants.MOD_ID);
    public static final RegistryObject<ActionConditionType> TOOL_MATCHES = CONDITIONS.register("tool_matches", create(new ToolMatcher.ToolSerializer()));
    public static final RegistryObject<ActionConditionType> INVERTED = CONDITIONS.register(("inverted"), create(new InvertedCondition.Serializer()));
    public static final RegistryObject<ActionConditionType> BLOCK_STATE_MATCHES = CONDITIONS.register(("block_state_matches"), create(new BlockStatePropertyCondition.Serializer()));
    public static final RegistryObject<ActionConditionType> BLOCK_STATE_MATCHES_ANY = CONDITIONS.register(("block_state_matches_any"), create(new BlockStatePropertyAnyCondition.Serializer()));
    public static final RegistryObject<ActionConditionType> FULLY_GROWN_CROP_CONDITION = CONDITIONS.register(("crop_fully_grown"), create(new FullyGrownCropCondition.Serializer()));
    public static final RegistryObject<ActionConditionType> BLOCKSTATE_INTEGER_RANGE = CONDITIONS.register(("blockstate_int_range"), create(new BlockStateIntegerRangeCondition.Serializer()));
    // level gate conditions, certain level to activate this action
    // advancement condition

    public static Object createGsonAdapter() {
        // TODO: this might cause issues
        return GsonAdapterFactory.builder(RegistryConstants.ACTION_CONDITION_TYPE.getSlaveMap(modID("conditions"), MappedRegistry.class), "condition", "condition", ActionCondition::getType).build();
    }

    /*public static ActionConditionType register(ResourceLocation location, Serializer<? extends ActionCondition> serializer) {
        return Registry.register(RegistryConstants.ACTION_CONDITION_TYPE, location, new ActionConditionType(serializer));
    }*/

    public static Supplier<ActionConditionType> create(Serializer<? extends ActionCondition> serializer) {
        return () -> new ActionConditionType(serializer);
    }
}
