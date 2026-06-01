package com.epherical.professions.platform;

import com.epherical.professions.FabricProfessionsMod;
import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.bootstrap.platform.IPlatformHelper;
import com.epherical.professions.core.Profession;
import com.epherical.professions.model.actions.ActionType;
import com.epherical.professions.model.actions.conditions.ConditionType;
import com.epherical.professions.model.actions.rewards.RewardType;
import com.epherical.professions.model.gating.GateType;
import com.epherical.professions.model.gating.requirements.GateRequirementType;
import com.epherical.professions.model.perks.PerkType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Registry<Profession> getProfessionRegistry() {
        return (Registry<Profession>) BuiltInRegistries.REGISTRY.get(ProfessionsCommon.PROFESSION_REGISTRY_KEY.location());
    }

    @Override
    public Registry<ActionType> getActionTypeRegistry() {
        return FabricProfessionsMod.ACTIONS;
    }

    @Override
    public Registry<GateType> getGateTypeRegistry() {
        return FabricProfessionsMod.GATES;
    }

    @Override
    public Registry<GateRequirementType> getGateRequirementTypeRegistry() {
        return FabricProfessionsMod.REQUIREMENTS;
    }

    @Override
    public Registry<ConditionType> getConditionTypeRegistry() {
        return FabricProfessionsMod.CONDITIONS;
    }

    @Override
    public Registry<RewardType> getRewardTypeRegistry() {
        return FabricProfessionsMod.REWARDS;
    }

    @Override
    public Registry<PerkType> getPerkTypeRegistry() {
        return FabricProfessionsMod.PERKS;
    }
}
