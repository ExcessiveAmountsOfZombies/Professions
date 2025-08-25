package com.epherical.professions.platform;

import com.epherical.professions.core.Profession;
import com.epherical.professions.core.actions.ActionType;
import com.epherical.professions.core.conditions.ConditionType;
import com.epherical.professions.core.rewards.RewardType;
import com.epherical.professions.platform.services.IPlatformHelper;
import net.minecraft.core.Registry;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
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
