package com.epherical.professions;

import com.epherical.professions.core.Profession;
import com.epherical.professions.core.actions.ActionType;
import com.epherical.professions.core.conditions.ConditionType;
import com.epherical.professions.core.register.Actions;
import com.epherical.professions.core.register.Conditions;
import com.epherical.professions.core.register.Rewards;
import com.epherical.professions.core.rewards.RewardType;
import com.epherical.professions.platform.Services;
import com.epherical.professions.registries.ActionLoad2;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class CommonClass {

    public static final ResourceKey<Registry<ActionType>> ACTION_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "professions/actions"));
    public static final ResourceKey<Registry<ConditionType>> CONDITION_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "professions/conditions"));
    public static final ResourceKey<Registry<RewardType>> REWARD_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "professions/rewards"));


    public static final ResourceKey<Registry<Profession>> PROFESSION_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "occupations"));


    public static ActionLoad2 ACTION_LOAD2;


    public static void init() {

        if (Services.PLATFORM.isModLoaded("professions")) {

        }
    }

    public static void register() {
        Actions.register();
        Conditions.register();
        Rewards.register();


    }
}
