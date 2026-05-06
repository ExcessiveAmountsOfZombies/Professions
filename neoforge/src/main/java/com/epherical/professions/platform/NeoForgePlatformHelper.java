package com.epherical.professions.platform;

import com.epherical.professions.NeoForgeProfessionsMod;
import com.epherical.professions.core.Profession;
import com.epherical.professions.model.actions.ActionType;
import com.epherical.professions.model.actions.conditions.ConditionType;
import com.epherical.professions.model.actions.rewards.RewardType;
import com.epherical.professions.model.gating.requirements.GateRequirementType;
import com.epherical.professions.model.gating.GateType;
import com.epherical.professions.model.perks.PerkType;
import com.epherical.professions.bootstrap.platform.IPlatformHelper;
import net.minecraft.core.Registry;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.getCurrent().isProduction();
    }

    @Override
    public Registry<Profession> getProfessionRegistry() {
        return NeoForgeProfessionsMod.PROFESSION_REGISTER.getRegistry().get();
    }

    @Override
    public Registry<ActionType> getActionTypeRegistry() {
        return NeoForgeProfessionsMod.ACTIONS;
    }

    @Override
    public Registry<GateType> getGateTypeRegistry() {
        return NeoForgeProfessionsMod.GATES;
    }

    @Override
    public Registry<GateRequirementType> getGateRequirementTypeRegistry() {
        return NeoForgeProfessionsMod.REQUIREMENTS;
    }

    @Override
    public Registry<ConditionType> getConditionTypeRegistry() {
        return NeoForgeProfessionsMod.CONDITIONS;
    }

    @Override
    public Registry<RewardType> getRewardTypeRegistry() {
        return NeoForgeProfessionsMod.REWARDS;
    }

    @Override
    public Registry<PerkType> getPerkTypeRegistry() {
        return NeoForgeProfessionsMod.PERKS;
    }
}
