package com.epherical.professions;

import com.epherical.professions.data.config.CommonConfig;
import com.epherical.professions.core.Profession;
import com.epherical.professions.registries.CategoryLoad3;
import com.epherical.professions.model.actions.ActionType;
import com.epherical.professions.model.actions.conditions.ConditionType;
import com.epherical.professions.bootstrap.Actions;
import com.epherical.professions.bootstrap.Conditions;
import com.epherical.professions.bootstrap.Rewards;
import com.epherical.professions.model.actions.rewards.RewardType;
import com.epherical.professions.registries.ActionLoad3;
import com.epherical.professions.runtime.event.ProfessionEventBus;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public abstract class ProfessionsCommon {

    public static final String MOD_ID = "professions";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);
    public static final ResourceKey<Registry<ActionType>> ACTION_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MOD_ID, "professions/actions"));
    public static final ResourceKey<Registry<ConditionType>> CONDITION_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MOD_ID, "professions/conditions"));
    public static final ResourceKey<Registry<RewardType>> REWARD_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MOD_ID, "professions/rewards"));
    public static final ResourceKey<Registry<Profession>> PROFESSION_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MOD_ID, "occupations"));

    public static ProfessionsCommon INSTANCE;


    protected ActionLoad3 actionLoader;
    protected CategoryLoad3 categoryLoader;
    protected CommonConfig config;
    protected final ProfessionCategoryManager categoryManager;

    protected final ProfessionEventBus eventBus;


    public ProfessionsCommon() {
        INSTANCE = this;
        config = new CommonConfig(false, "professions.conf", getModDir());
        config.loadConfig();
        this.eventBus = new ProfessionEventBus();
        this.categoryManager = new ProfessionCategoryManager();
    }

    public static void register() {
        Actions.bootstrap();
        Conditions.bootstrap();
        Rewards.bootstrap();
    }


    public void setActionLoader(ActionLoad3 actionLoader) {
        this.actionLoader = actionLoader;
    }

    public ProfessionEventBus getEventBus() {
        return eventBus;
    }

    public ProfessionCategoryManager getCategoryManager() {
        return categoryManager;
    }

    public ActionLoad3 getActionLoader() {
        return actionLoader;
    }

    public CategoryLoad3 getCategoryLoader() {
        return categoryLoader;
    }

    public void setCategoryLoader(CategoryLoad3 categoryLoader) {
        this.categoryLoader = categoryLoader;
    }

    public abstract PlayerManager getPlayerManager();
    public abstract File getModDir();
}
