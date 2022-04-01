package com.epherical.professions.profession.action;


import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.rewards.Reward;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Predicate;

public interface Action extends Predicate<ProfessionContext> {

    ActionType getType();

    void handleRewards(ProfessionContext context, Occupation player);

    List<Component> displayInformation();

    void serializeDisplayToClient(FriendlyByteBuf buf);

    /**
     * Called after it has already been shown the action is successful.
     */
    double modifyReward(ProfessionContext context, Reward reward, double base);

    @FunctionalInterface
    interface Builder {
        Action build();
    }
}
