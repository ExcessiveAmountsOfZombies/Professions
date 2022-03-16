package com.epherical.professions.profession.action;


import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.progression.Occupation;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Predicate;

public interface Action extends Predicate<ProfessionContext> {

    ActionType getType();

    void handleRewards(ProfessionContext context, Occupation player);

    List<Component> displayInformation();

    String actionDisplayName();

    @FunctionalInterface
    interface Builder {
        Action build();
    }
}
