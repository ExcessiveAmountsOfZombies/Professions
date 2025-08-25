package com.epherical.professions.platform;

import com.epherical.professions.core.Profession;
import com.epherical.professions.core.actions.ActionType;
import com.epherical.professions.core.conditions.ConditionType;
import com.epherical.professions.core.rewards.RewardType;
import com.epherical.professions.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;

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
    public Registry<Profession> getProfessionRegistry() {
        return null;
    }

    @Override
    public Registry<ActionType> getActionTypeRegistry() {
        return null;
    }

    @Override
    public Registry<ConditionType> getConditionTypeRegistry() {
        return null;
    }

    @Override
    public Registry<RewardType> getRewardTypeRegistry() {
        return null;
    }
}
