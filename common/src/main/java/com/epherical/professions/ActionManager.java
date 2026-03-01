package com.epherical.professions;

import com.epherical.professions.core.Profession;
import com.epherical.professions.core.actions.Action;
import com.epherical.professions.core.actions.ActionType;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import net.minecraft.core.Holder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;

public class ActionManager {

    private static final Logger LOGGER = LogManager.getLogger();


    private Multimap<ActionType, Action<?>> actionMap = MultimapBuilder.hashKeys().hashSetValues().build();
    private Multimap<Holder<Profession>, Action<?>> professionToActionsMap = MultimapBuilder.hashKeys().hashSetValues().build();
    private Multimap<Holder<?>, Action<?>> valueToActionsMap = MultimapBuilder.hashKeys().hashSetValues().build();

    public ActionManager() {
    }

    public void reloadActions(List<Action<?>> actions) {
        actionMap.clear();
        professionToActionsMap.clear();
        valueToActionsMap.clear();

        synchronized (this) {
            for (Action<?> action : actions) {
                actionMap.put(action.getType(), action);
                professionToActionsMap.put(action.getProfession(), action);
            }
        }
    }

    public Collection<Action<?>> getActionsByType(ActionType actionType) {
        return actionMap.get(actionType);
    }

    public Collection<Action<?>> getActionsByProfession(Holder<Profession> profession) {
        return professionToActionsMap.get(profession);
    }

}


