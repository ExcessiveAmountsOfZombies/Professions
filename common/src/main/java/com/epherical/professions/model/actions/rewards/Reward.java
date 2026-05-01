package com.epherical.professions.model.actions.rewards;

import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.bootstrap.platform.Services;
import com.epherical.professions.model.actions.Action;
import com.epherical.professions.api.event.runtime.rewards.RewardEvent;
import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public interface Reward<T extends RewardEvent> {

    Codec<Reward<?>> CODEC = Services.PLATFORM.getRewardTypeRegistry().byNameCodec().dispatch(
            "reward", Reward::getType, RewardType::codec);

    RewardType getType();

    ItemStack getRewardIcon();

    Component getRewardName();

    void giveReward(T reward);

    T buildEvent(Occupation occupation, Action<?> action, ProfessionContext professionContext);

    @FunctionalInterface
    interface Builder {
        Reward<?> build();
    }
}
