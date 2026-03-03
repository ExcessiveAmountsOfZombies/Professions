package com.epherical.professions;

import com.epherical.professions.data.config.CommonConfig;
import com.epherical.professions.core.Profession;
import com.epherical.professions.core.actions.ActionType;
import com.epherical.professions.core.conditions.ConditionType;
import com.epherical.professions.core.register.Actions;
import com.epherical.professions.core.register.Conditions;
import com.epherical.professions.core.register.Rewards;
import com.epherical.professions.core.rewards.RewardType;
import com.epherical.professions.registries.ActionLoad3;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.io.File;

public abstract class CommonClass {

    public static final ResourceKey<Registry<ActionType>> ACTION_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "professions/actions"));
    public static final ResourceKey<Registry<ConditionType>> CONDITION_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "professions/conditions"));
    public static final ResourceKey<Registry<RewardType>> REWARD_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "professions/rewards"));


    public static final ResourceKey<Registry<Profession>> PROFESSION_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "occupations"));


    public static ActionLoad3 ACTION_LOAD2;

    public static CommonConfig config;


    public static CommonClass INSTANCE;


    public void init() {
        INSTANCE = this;
    }

    public static void register() {
        Actions.register();
        Conditions.register();
        Rewards.register();
    }

    public void buildConfig() {
        config = new CommonConfig(false, "professions.conf", getModDir());
        config.loadConfig();
    }

    public PlayerManager getPlayerManager() {
        return null;
    }

    public abstract ActionLoad3 getActionLoader();
    public abstract File getModDir();
    public abstract boolean isClientEnvironment();
}
