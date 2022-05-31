package com.epherical.professions;

import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.ProfessionEditorSerializer;
import com.epherical.professions.profession.ProfessionSerializer;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.conditions.ActionConditionType;
import com.epherical.professions.profession.editor.Editor;
import com.epherical.professions.profession.rewards.RewardType;
import com.epherical.professions.profession.unlock.UnlockType;
import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import static com.epherical.professions.Constants.modID;

public class RegistryConstants {

    public static final ResourceKey<Registry<ProfessionSerializer<?, ?>>> PROFESSION_TYPE_KEY = ResourceKey.createRegistryKey(modID("professions/occupation_type"));
    public static final ResourceKey<Registry<ProfessionEditorSerializer<?>>> PROFESSION_EDITOR_TYPE_KEY = ResourceKey.createRegistryKey(modID("professions/editor_type"));
    public static final ResourceKey<Registry<ActionType>> ACTION_TYPE_KEY = ResourceKey.createRegistryKey(modID("professions/actions"));
    public static final ResourceKey<Registry<ActionConditionType>> ACTION_CONDITION_KEY = ResourceKey.createRegistryKey(modID("professions/conditions"));
    public static final ResourceKey<Registry<RewardType>> REWARD_KEY = ResourceKey.createRegistryKey(modID("professions/rewards"));
    public static final ResourceKey<Registry<UnlockType<?>>> UNLOCK_KEY = ResourceKey.createRegistryKey(modID("professions/unlocks"));

    public static final Registry<ProfessionSerializer<? extends Profession, ?>> PROFESSION_SERIALIZER = FabricRegistryBuilder.from(new MappedRegistry<>(PROFESSION_TYPE_KEY,
            Lifecycle.experimental(), null)).buildAndRegister();
    public static final Registry<ProfessionEditorSerializer<? extends Editor>> PROFESSION_EDITOR_SERIALIZER = FabricRegistryBuilder.from(new MappedRegistry<>(PROFESSION_EDITOR_TYPE_KEY,
            Lifecycle.experimental(), null)).buildAndRegister();
    public static final Registry<ActionType> ACTION_TYPE = FabricRegistryBuilder.from(new MappedRegistry<>(ACTION_TYPE_KEY,
            Lifecycle.experimental(), null)).buildAndRegister();
    public static final Registry<ActionConditionType> ACTION_CONDITION_TYPE = FabricRegistryBuilder.from(new MappedRegistry<>(ACTION_CONDITION_KEY,
            Lifecycle.experimental(), null)).buildAndRegister();
    public static final Registry<RewardType> REWARDS = FabricRegistryBuilder.from(new MappedRegistry<>(REWARD_KEY,
            Lifecycle.experimental(), null)).buildAndRegister();
    public static final Registry<UnlockType<?>> UNLOCKS = FabricRegistryBuilder.from(new MappedRegistry<>(UNLOCK_KEY,
            Lifecycle.experimental(), null)).buildAndRegister();
}
