package com.epherical.professions;

import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.ProfessionEditorSerializer;
import com.epherical.professions.profession.ProfessionSerializer;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.conditions.ActionConditionType;
import com.epherical.professions.profession.editor.Editor;
import com.epherical.professions.profession.modifiers.milestones.MilestoneType;
import com.epherical.professions.profession.modifiers.perks.PerkType;
import com.epherical.professions.profession.rewards.RewardType;
import com.epherical.professions.profession.unlock.UnlockSerializer;
import com.epherical.professions.profession.unlock.UnlockType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import static com.epherical.professions.Constants.modID;

public class RegistryConstants {

    public static final ResourceKey<Registry<ProfessionSerializer<? extends Profession, ?>>> PROFESSION_TYPE_KEY = ResourceKey.createRegistryKey(modID("professions/occupation_type"));
    public static final ResourceKey<Registry<ProfessionEditorSerializer<? extends Editor>>> PROFESSION_EDITOR_TYPE_KEY = ResourceKey.createRegistryKey(modID("professions/editor_type"));
    public static final ResourceKey<Registry<ActionType>> ACTION_TYPE_KEY = ResourceKey.createRegistryKey(modID("professions/actions"));
    public static final ResourceKey<Registry<ActionConditionType>> ACTION_CONDITION_KEY = ResourceKey.createRegistryKey(modID("professions/conditions"));
    public static final ResourceKey<Registry<RewardType>> REWARD_KEY = ResourceKey.createRegistryKey(modID("professions/rewards"));
    public static final ResourceKey<Registry<UnlockType<?>>> UNLOCK_KEY = ResourceKey.createRegistryKey(modID("professions/unlocks"));
    public static final ResourceKey<Registry<UnlockSerializer<?>>> UNLOCK_TYPE_KEY = ResourceKey.createRegistryKey(modID("professions/unlock_type"));
    public static final ResourceKey<Registry<MilestoneType>> MILESTONE_TYPE_KEY =  ResourceKey.createRegistryKey(modID("professions/milestone_type"));
    public static final ResourceKey<Registry<PerkType>> PERK_TYPE_KEY = ResourceKey.createRegistryKey(modID("professions/perk_type"));

    public static Registry<ProfessionSerializer<? extends Profession, ?>> PROFESSION_SERIALIZER;
    public static Registry<ProfessionEditorSerializer<? extends Editor>> PROFESSION_EDITOR_SERIALIZER;
    public static Registry<ActionType> ACTION_TYPE;
    public static Registry<ActionConditionType> ACTION_CONDITION_TYPE;
    public static Registry<RewardType> REWARDS;
    public static Registry<UnlockType<?>> UNLOCKS;
    public static Registry<UnlockSerializer<?>> UNLOCK_TYPE;
    public static Registry<MilestoneType> MILESTONE_TYPE;
    public static Registry<PerkType> PERK_TYPE;

}
