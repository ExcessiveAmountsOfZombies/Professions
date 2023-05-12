package com.epherical.professions.profession.action;


import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.util.ActionDisplay;
import com.epherical.professions.util.ActionEntry;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Predicate;

public interface Action<T> extends Predicate<ProfessionContext> {

    ActionType getType();

    boolean handleAction(ProfessionContext context, Occupation player);

    void giveRewards(ProfessionContext context, Occupation occupation);

    List<Component> displayInformation();

    List<ActionDisplay.Icon> clientFriendlyInformation(Component actionType);

    List<Action.Singular<T>> convertToSingle(Profession profession);

    @Deprecated()
    // TODO; this is a result of technical debt in switching from V1 to V2 of our data loading process.
    //  ideally the action wouldn't be formed until it has its single action entry, but we'll figure out how to solve
    //  that later.
    void addActionEntry(ActionEntry<T> entry);

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

    interface Singular<T> {

        ActionType getType();

        T getObject();

        Component getProfessionDisplay();

        Profession getProfession();

        Component createActionComponent();

        boolean handleAction(ProfessionContext context, Occupation player);

        void giveRewards(ProfessionContext context, Occupation occupation);
    }
}
