package com.epherical.professions.profession.rewards;

import com.epherical.professions.RegistryConstants;
import com.epherical.professions.profession.rewards.builtin.ItemReward;
import com.epherical.professions.profession.rewards.builtin.OccupationExperience;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraft.world.level.storage.loot.Serializer;

import static com.epherical.professions.Constants.modID;

public class Rewards {
    //public static final RewardType PAYMENT_REWARD = register(modID("payment"), new PaymentReward.RewardSerializer());
    public static final RewardType EXPERIENCE_REWARD = register(modID("occupation_exp"), new OccupationExperience.RewardSerializer());
    public static final RewardType ITEM_REWARD = register(modID("item"), new ItemReward.RewardSerializer());


    public static Object createGsonAdapter() {
        return GsonAdapterFactory.builder(RegistryConstants.REWARDS, "reward", "reward", Reward::getType).build();
    }

    public static RewardType register(ResourceLocation id, Serializer<? extends Reward> serializer) {
        return Registry.register(RegistryConstants.REWARDS, id, new RewardType(serializer));
    }
}
