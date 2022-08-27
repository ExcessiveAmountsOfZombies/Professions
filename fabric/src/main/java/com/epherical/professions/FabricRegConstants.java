package com.epherical.professions;

import com.epherical.professions.loot.UnlockCondition;
import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.MappedRegistry;

import static com.epherical.professions.ProfessionsFabric.registerLootCondition;
import static com.epherical.professions.RegistryConstants.*;

public class FabricRegConstants {

    public static void init() {
    }

    static {
        PROFESSION_SERIALIZER = FabricRegistryBuilder.from(new MappedRegistry<>(PROFESSION_TYPE_KEY, Lifecycle.experimental(), null)).buildAndRegister();
        PROFESSION_EDITOR_SERIALIZER = FabricRegistryBuilder.from(new MappedRegistry<>(PROFESSION_EDITOR_TYPE_KEY, Lifecycle.experimental(), null)).buildAndRegister();
        ACTION_TYPE = FabricRegistryBuilder.from(new MappedRegistry<>(ACTION_TYPE_KEY, Lifecycle.experimental(), null)).buildAndRegister();
        ACTION_CONDITION_TYPE = FabricRegistryBuilder.from(new MappedRegistry<>(ACTION_CONDITION_KEY, Lifecycle.experimental(), null)).buildAndRegister();
        REWARDS = FabricRegistryBuilder.from(new MappedRegistry<>(REWARD_KEY, Lifecycle.experimental(), null)).buildAndRegister();
        UNLOCKS = FabricRegistryBuilder.from(new MappedRegistry<>(UNLOCK_KEY, Lifecycle.experimental(), null)).buildAndRegister();
        UNLOCK_TYPE = FabricRegistryBuilder.from(new MappedRegistry<>(UNLOCK_TYPE_KEY, Lifecycle.experimental(), null)).buildAndRegister();
        MILESTONE_TYPE = FabricRegistryBuilder.from(new MappedRegistry<>(MILESTONE_TYPE_KEY, Lifecycle.experimental(), null)).buildAndRegister();
        PERK_TYPE = FabricRegistryBuilder.from(new MappedRegistry<>(PERK_TYPE_KEY, Lifecycle.experimental(), null)).buildAndRegister();
        Constants.UNLOCK_CONDITION = registerLootCondition("unlock_condition", new UnlockCondition.Serializer());
    }

}
