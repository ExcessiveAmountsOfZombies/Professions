package com.epherical.professions.profession.rewards;

import com.epherical.professions.Constants;
import com.epherical.professions.RegistryConstants;
import com.epherical.professions.profession.rewards.builtin.ItemReward;
import com.epherical.professions.profession.rewards.builtin.OccupationExperience;
//import com.epherical.professions.profession.rewards.builtin.PaymentReward;
import net.minecraft.core.MappedRegistry;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static com.epherical.professions.Constants.modID;

public class Rewards {
    public static final DeferredRegister<RewardType> REWARDS = DeferredRegister.create(RegistryConstants.REWARDS, Constants.MOD_ID);
    //public static final RegistryObject<RewardType> PAYMENT_REWARD = REWARDS.register("payment", create(new PaymentReward.RewardSerializer()));
    public static final RegistryObject<RewardType> EXPERIENCE_REWARD = REWARDS.register("occupation_exp", create(new OccupationExperience.RewardSerializer()));
    public static final RegistryObject<RewardType> ITEM_REWARD = REWARDS.register("item", create(new ItemReward.RewardSerializer()));


    public static Object createGsonAdapter() {
        return GsonAdapterFactory.builder(RegistryConstants.REWARDS.getSlaveMap(modID("rewards"), MappedRegistry.class), "reward", "reward", Reward::getType).build();
    }

    /*public static RewardType register(ResourceLocation id, Serializer<? extends Reward> serializer) {
        return Registry.register(RegistryConstants.REWARDS, id, new RewardType(serializer));
    }*/

    public static Supplier<RewardType> create(Serializer<? extends Reward> serializer) {
        return () -> new RewardType(serializer);
    }
}
