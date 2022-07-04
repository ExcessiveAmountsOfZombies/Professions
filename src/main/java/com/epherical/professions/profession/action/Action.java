package com.epherical.professions.profession.action;


import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.util.ActionDisplay;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Predicate;

public interface Action extends Predicate<ProfessionContext> {

    ActionType getType();

    boolean handleAction(ProfessionContext context, Occupation player);

    void giveRewards(ProfessionContext context, Occupation occupation);

    List<Component> displayInformation();

    List<ActionDisplay.Icon> clientFriendlyInformation(Component actionType);

    /**
     * Called after it has already been shown the action is successful.
     */
    double modifyReward(ProfessionContext context, Reward reward, double base);

    default void logAction(ProfessionContext context, Component component) {
        context.getParameter(ProfessionParameter.ACTION_LOGGER).addAction(this, component);
    }

    @FunctionalInterface
    interface Builder {
        Action build();
    }
}
