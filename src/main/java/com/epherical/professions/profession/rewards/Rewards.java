package com.epherical.professions.profession.rewards;

import com.epherical.professions.ProfessionsMod;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.rewards.builtin.PaymentReward;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraft.world.level.storage.loot.Serializer;

import static com.epherical.professions.ProfessionsMod.modID;

public class Rewards {
    public static final RewardType PAYMENT_REWARD = register(modID("payment"), new PaymentReward.RewardSerializer());


    public static Object createGsonAdapter() {
        return GsonAdapterFactory.builder(ProfessionsMod.ACTION_TYPE, "reward", "reward", Action::getType).build();
    }

    public static RewardType register(ResourceLocation id, Serializer<? extends Reward> serializer) {
        return Registry.register(ProfessionsMod.REWARDS, id, new RewardType(serializer));
    }
}
