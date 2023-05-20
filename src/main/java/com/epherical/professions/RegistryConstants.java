package com.epherical.professions;

import com.epherical.professions.profession.ProfessionEditorSerializer;
import com.epherical.professions.profession.ProfessionSerializer;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.conditions.ActionConditionType;
import com.epherical.professions.profession.conditions.ActionConditions;
import com.epherical.professions.profession.editor.Editor;
import com.epherical.professions.profession.modifiers.milestones.MilestoneType;
import com.epherical.professions.profession.modifiers.milestones.Milestones;
import com.epherical.professions.profession.modifiers.perks.PerkType;
import com.epherical.professions.profession.modifiers.perks.Perks;
import com.epherical.professions.profession.rewards.RewardType;
import com.epherical.professions.profession.rewards.Rewards;
import com.epherical.professions.profession.unlock.UnlockSerializer;
import com.epherical.professions.profession.unlock.UnlockType;
import com.epherical.professions.profession.unlock.Unlocks;
import com.google.common.collect.Maps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Supplier;

import static com.epherical.professions.Constants.modID;

public class RegistryConstants {

    private static final Map<ResourceLocation, Supplier<?>> LOADERS = Maps.newLinkedHashMap();

    public static final ResourceKey<Registry<ProfessionSerializer<?, ?>>> PROFESSION_TYPE_KEY = ResourceKey.createRegistryKey(modID("professions/occupation_type"));
    public static final ResourceKey<Registry<ProfessionEditorSerializer<? extends Editor>>> PROFESSION_EDITOR_TYPE_KEY = ResourceKey.createRegistryKey(modID("professions/editor_type"));
    public static final ResourceKey<Registry<ActionType>> ACTION_TYPE_KEY = ResourceKey.createRegistryKey(modID("professions/actions"));
    public static final ResourceKey<Registry<ActionConditionType>> ACTION_CONDITION_KEY = ResourceKey.createRegistryKey(modID("professions/conditions"));
    public static final ResourceKey<Registry<RewardType>> REWARD_KEY = ResourceKey.createRegistryKey(modID("professions/rewards"));
    public static final ResourceKey<Registry<UnlockType<?>>> UNLOCK_KEY = ResourceKey.createRegistryKey(modID("professions/unlocks"));
    public static final ResourceKey<Registry<UnlockSerializer<?>>> UNLOCK_TYPE_KEY = ResourceKey.createRegistryKey(modID("professions/unlock_type"));
    public static final ResourceKey<Registry<MilestoneType>> MILESTONE_TYPE_KEY = ResourceKey.createRegistryKey(modID("professions/milestone_type"));
    public static final ResourceKey<Registry<PerkType>> PERK_TYPE_KEY = ResourceKey.createRegistryKey(modID("professions/perk_type"));

    public static Registry<ProfessionSerializer<?, ?>> PROFESSION_SERIALIZER;
    public static Registry<ProfessionEditorSerializer<? extends Editor>> PROFESSION_EDITOR_SERIALIZER;
    public static Registry<ActionType> ACTION_TYPE;
    public static Registry<ActionConditionType> ACTION_CONDITION_TYPE;
    public static Registry<RewardType> REWARDS;
    public static Registry<UnlockType<?>> UNLOCKS;
    public static Registry<UnlockSerializer<?>> UNLOCK_TYPE;
    public static Registry<MilestoneType> MILESTONE_TYPE;
    public static Registry<PerkType> PERK_TYPE;


    public static void init() {}

    static {
        PROFESSION_SERIALIZER = createRegistry(PROFESSION_TYPE_KEY, Lifecycle.experimental(), registry -> {
            return ProfessionSerializer.DEFAULT_PROFESSION;
        });
        PROFESSION_EDITOR_SERIALIZER = createRegistry(PROFESSION_EDITOR_TYPE_KEY, Lifecycle.experimental(), registry -> {
            return ProfessionEditorSerializer.APPEND_EDITOR;
        });
        ACTION_TYPE = createRegistry(ACTION_TYPE_KEY, Lifecycle.experimental(), registry -> Actions.BREAK_BLOCK);
        ACTION_CONDITION_TYPE = createRegistry(ACTION_CONDITION_KEY, Lifecycle.experimental(), registry -> {
            return ActionConditions.INVERTED;
        });
        REWARDS = createRegistry(REWARD_KEY, Lifecycle.experimental(), registry -> {
            return Rewards.EXPERIENCE_REWARD;
        });
        UNLOCKS = createRegistry(UNLOCK_KEY, Lifecycle.experimental(), registry -> {
            return Unlocks.BLOCK_BREAK_UNLOCK;
        });
        UNLOCK_TYPE = createRegistry(UNLOCK_TYPE_KEY, Lifecycle.experimental(), registry -> {
            return UnlockSerializer.BLOCK_BREAK_UNLOCK;
        });
        MILESTONE_TYPE = createRegistry(MILESTONE_TYPE_KEY, Lifecycle.experimental(), registry -> {
            return Milestones.ADMIN_RAN_COMMAND;
        });
        PERK_TYPE = createRegistry(PERK_TYPE_KEY, Lifecycle.experimental(), registry -> {
            return Perks.SINGLE_ATTRIBUTE_PERK;
        });

        for (Supplier<?> value : LOADERS.values()) {
            value.get();
        }

    }

    private static <T> MappedRegistry<T> createRegistry(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle, ClassInitializer<T> initializer) {
        MappedRegistry<T> registry = new MappedRegistry<>(key, lifecycle);
        LOADERS.put(key.location(), () -> initializer.run(registry));
        return registry;
    }

    // Initializes the class so everything gets put into the registry.
    private interface ClassInitializer<T> {
        T run(Registry<T> registry);
    }

}
