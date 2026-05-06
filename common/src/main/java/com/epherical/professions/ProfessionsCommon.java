package com.epherical.professions;

import com.epherical.professions.api.event.EventPhase;
import com.epherical.professions.api.event.runtime.PlayerJoinEvent;
import com.epherical.professions.api.event.runtime.perks.PerkClaimedEvent;
import com.epherical.professions.core.Profession;
import com.epherical.professions.data.config.ProfessionConfig;
import com.epherical.professions.listener.perks.PerkClaimListener;
import com.epherical.professions.listener.perks.PerkGainExperienceListener;
import com.epherical.professions.listener.perks.PerkLevelListener;
import com.epherical.professions.listener.notification.NotificationGainExperienceListener;
import com.epherical.professions.listener.notification.NotificationLevelListener;
import com.epherical.professions.listener.perks.PerkPlayerJoinListener;
import com.epherical.professions.registries.CategoryLoad3;
import com.epherical.professions.model.gating.requirements.GateRequirementType;
import com.epherical.professions.model.gating.GateType;
import com.epherical.professions.model.actions.ActionType;
import com.epherical.professions.model.actions.conditions.ConditionType;
import com.epherical.professions.bootstrap.Actions;
import com.epherical.professions.bootstrap.Conditions;
import com.epherical.professions.bootstrap.Gates;
import com.epherical.professions.bootstrap.Perks;
import com.epherical.professions.bootstrap.Requirements;
import com.epherical.professions.bootstrap.Rewards;
import com.epherical.professions.model.actions.rewards.RewardType;
import com.epherical.professions.model.perks.PerkType;
import com.epherical.professions.registries.ActionLoad3;
import com.epherical.professions.registries.GateLoad3;
import com.epherical.professions.registries.PerkLoad3;
import com.epherical.professions.api.event.runtime.ProfessionEventBus;
import com.epherical.professions.api.event.runtime.rewards.OccupationExperienceEvent;
import com.epherical.professions.api.event.runtime.rewards.OccupationLevelEvent;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public abstract class ProfessionsCommon {

    public static final String MOD_ID = "professions";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);
    public static final ResourceKey<Registry<ActionType>> ACTION_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(MOD_ID, "professions/actions"));
    public static final ResourceKey<Registry<GateType>> GATE_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MOD_ID, "professions/gates"));
    public static final ResourceKey<Registry<GateRequirementType>> REQUIREMENT_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MOD_ID, "professions/requirements"));
    public static final ResourceKey<Registry<ConditionType>> CONDITION_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(MOD_ID, "professions/conditions"));
    public static final ResourceKey<Registry<RewardType>> REWARD_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(MOD_ID, "professions/rewards"));
    public static final ResourceKey<Registry<PerkType>> PERK_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MOD_ID, "professions/perks"));
    public static final ResourceKey<Registry<Profession>> PROFESSION_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(MOD_ID, "occupations"));

    public static ProfessionsCommon INSTANCE;


    protected ActionLoad3 actionLoader;
    protected GateLoad3 gateLoader;
    protected CategoryLoad3 categoryLoader;
    protected PerkLoad3 perkLoader;
    protected ProfessionConfig config;
    protected final ProfessionCategoryManager categoryManager;
    protected final PerkManager perkManager;

    protected final ProfessionEventBus eventBus;


    public ProfessionsCommon() {
        INSTANCE = this;
        config = new ProfessionConfig(false, "professions.conf", getModDir());
        config.loadConfig();
        this.eventBus = new ProfessionEventBus();

        this.perkManager = new PerkManager();

        this.eventBus.register(OccupationLevelEvent.KEY, EventPhase.RESOLVE, new NotificationLevelListener());
        this.eventBus.register(OccupationExperienceEvent.KEY, EventPhase.APPLY, ProfessionEventBus.LAST, new NotificationGainExperienceListener());

        this.eventBus.register(OccupationExperienceEvent.KEY, EventPhase.MODIFY_EFFECTS, new PerkGainExperienceListener(getPerkManager()));
        this.eventBus.register(OccupationLevelEvent.KEY, EventPhase.RESOLVE, ProfessionEventBus.EARLY, new PerkLevelListener(getPerkManager()));
        this.eventBus.register(PlayerJoinEvent.KEY, EventPhase.RESOLVE, new PerkPlayerJoinListener(getPerkManager()));
        this.eventBus.register(PerkClaimedEvent.KEY, EventPhase.APPLY, new PerkClaimListener(getPerkManager()));

        this.categoryManager = new ProfessionCategoryManager();
    }

    public static void register() {
        Actions.bootstrap();
        Gates.bootstrap();
        Requirements.bootstrap();
        Conditions.bootstrap();
        Rewards.bootstrap();
        Perks.bootstrap();
    }

    public void registerAfterServerStarts() {

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

    public GateLoad3 getGateLoader() {
        return gateLoader;
    }

    public PerkLoad3 getPerkLoader() {
        return perkLoader;
    }

    public void setCategoryLoader(CategoryLoad3 categoryLoader) {
        this.categoryLoader = categoryLoader;
    }

    public void setGateLoader(GateLoad3 gateLoader) {
        this.gateLoader = gateLoader;
    }

    public void setPerkLoader(PerkLoad3 perkLoader) {
        this.perkLoader = perkLoader;
    }

    public PerkManager getPerkManager() {
        return perkManager;
    }

    public abstract PlayerManager getPlayerManager();
    public abstract File getModDir();
    public abstract ActionManager getActionManager();
    public abstract GateManager getGateManager();
}
